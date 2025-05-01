package com.example.demo.service;

import java.text.ParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.mapstruct.ap.shaded.freemarker.template.utility.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.IntrospectRequest;
import com.example.demo.dto.IntrospectResponse;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.TokenDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.InvalidatedToken;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.InvalidatedTokenRepository;
import com.example.demo.repository.RolerRepository;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationService {
	
	@Autowired
	private UserRepository userrepository;

	@Autowired
	private InvalidatedTokenRepository invalidatedTokenRepository;

	@NonFinal
	@Value("${jwt.signerKey}")
	private String SIGNER_KEY;
	
	@NonFinal
	@Value("${jwt.validDuration}")
	private long VALID_DURATION;
	
	@NonFinal
	@Value("${jwt.refreshDuration}")
	private long REFRESH_DURATION;

	public TokenDTO authentication(UserDTO dto) {
		if (userrepository.existsByUsername(dto.getUsername()) == false) {
//			ném ra một ngoại lệ + message tương ứng
			throw new AppException(ErrorCode.USER_NOT_EXIST);
		}
		// tìm ds user thông qua username
		List<UserEntity> userentity = (List<UserEntity>) userrepository.findOneByUsername(dto.getUsername());
		TokenDTO token = new TokenDTO();

//		Tiến hành kiểm tra dữ liệu đã mã hóa
		UserEntity userEntity1 = null;

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

		boolean result = false;

		for (UserEntity userEntity2 : userentity) {
			result = passwordEncoder.matches(dto.getPassword(), userEntity2.getPassword());

			if (result == true) {
				userEntity1 = userEntity2;

				break;
			}
		}

//		TH mat khau k dung nem ra ngoại lẹ
		if (result == false) {
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);
		}

//	Thành công thì tạo một TOKEN
		String token_value = generateToken(userEntity1);

		token.setToken(token_value);
		token.setAuthenticated(result);

		return token;

	}

	// định dạng cho scope để lưu vào TOKEN, lưu các Permission
	public String buildScope(UserEntity dto) {
		// ngăn cách nhau bằng khoảng trắng
		StringJoiner stringJoiner = new StringJoiner(" ");
		// kiểm tra có role hay chưa
		if (!dto.getRoles().isEmpty()) {
			dto.getRoles().forEach(s -> stringJoiner.add("ROLE_" + s.getName()));
			dto.getRoles().forEach(s -> {
				if (!s.getPermissions().isEmpty()) {
					s.getPermissions().forEach(y -> stringJoiner.add(y.getName()));
				}

			});
		}
		return stringJoiner.toString();
	}

	// thiết lập role
	public String buildRoles(UserEntity entity) {
		// ngăn cách nhau bằng khoảng trắng
		StringJoiner stringJoiner = new StringJoiner(" ");
		if (!entity.getRoles().isEmpty()) {
			entity.getRoles().forEach(s -> stringJoiner.add("ROLE_" + s.getName()));
		}

		return stringJoiner.toString();

	}

	private String generateToken(UserEntity userdto) {
//		Đầu tiên tạo header với thuật toán HS512
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		log.warn(VALID_DURATION+"");
		log.warn(REFRESH_DURATION+"");
		
//		Tiếp theo tạo claimSet để cho vào payload
		JWTClaimsSet claimSet = new JWTClaimsSet.Builder().issuer("https://example.com")

				.subject(userdto.getUsername()).audience("https://yourdomain.com")
				
				

				.expirationTime(new Date(new Date().getTime() + VALID_DURATION * 1000)) // thời điểm hết hạn TOKEN được chỉ định

				.notBeforeTime(new Date()).issueTime(new Date())
				// UUID là tạo ID cho TOKEN gồm chuỗi 32 ký tự đc tạo ngẫu nhiên

				.jwtID(UUID.randomUUID().toString())
				// .claim("roles", buildRoles(userdto))
				.claim("scope", buildScope(userdto)).claim("userId", userdto.getId()) // Thêm userId vào claim
				.build();

		Payload payload = new Payload(claimSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header, payload);
//		Sau khi đầy đủ dữ liêu ta tiến hành ký, hash Token

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException("K thể tạo TOKEN");
		}
	}

	// Refresh TOKEN
	public IntrospectRequest refreshToken(IntrospectRequest tokenDTO) throws JOSEException, ParseException {
		// verify token
		String token = tokenDTO.getToken();

		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signJWT = SignedJWT.parse(token);

		boolean result_verified = signJWT.verify(verifier);// kiểm tra xác thực true/ false

		//kiểm tra thời gian refresh
		//tức lây thời điêmr phát hành + thêm thời gian REFRESH_DURATION = refresh TOKEN
		Date experiTime = Date.from(signJWT.getJWTClaimsSet().getIssueTime().toInstant().plusSeconds(REFRESH_DURATION)); 
				
		boolean check_experiTime = experiTime.after(new Date());

		// nếu TOKEN đã hết hạn hoặc lỗi
		if (!(result_verified && check_experiTime)) {
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);
		}

		// nếu TOKEN đã có trong table
		if (invalidatedTokenRepository.existsById(signJWT.getJWTClaimsSet().getJWTID())) {
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);
		}

		// nếu TOKEN còn dùng đc mà muốn LRefresh thì tiền hành cho nó vào table Logout 
		String idTOKEN = signJWT.getJWTClaimsSet().getJWTID();
		Date expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

		// tiến hành insert vô table
		InvalidatedToken tokenentity = new InvalidatedToken(idTOKEN, expiryTime);
		invalidatedTokenRepository.save(tokenentity);
		
		//tao token mới
		String username = signJWT.getJWTClaimsSet().getSubject();
		
		List<UserEntity> user = userrepository.findOneByUsername(username);
		
		if(user.isEmpty()) {
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);
		}
		
		String newToken = generateToken(user.get(0));
		
		IntrospectRequest new_token_intro = new IntrospectRequest();
		
		new_token_intro.setToken(newToken);
		
		
		return new_token_intro;
	}

	// nhận vào 1 TOKEN và khi logout thì chỉ ta thêm thông tin TOKEN đó vào bảng
	// tblInvalidatedTOKEN mà ta đã tạo trong Entity để lưu các TOKEN hết hạn
	public IntrospectResponse logout(IntrospectRequest requestToken) throws JOSEException, ParseException {
		IntrospectResponse token_valid_respone = new IntrospectResponse();

//		lấy token
		String token = requestToken.getToken();
		log.warn("da den day2");
//		xác minh chữ lý, .getBytes() chuyển đổi chuỗi thành một mảng byte
//		MACVerifier là lớp sd để xác minh chữ ký dựa trên thuật toán  MAC
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

//		SignedJWT là lớp đại diên cho JWT đã được ký cho phép truy cập các thành phần khác như header, payload, sign_nature
//		phân tích chuỗi mã thông báo thành một đối tượng SignedJWT		
		SignedJWT signedJWT = SignedJWT.parse(token);
		boolean result_verified = signedJWT.verify(verifier);// kiểm tra xác thực true/ false

//		kiểm tra TOKEN đã hết hạn hay chưa
		Date experiTime = Date.from(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plusSeconds(REFRESH_DURATION)); 
//		kiểm tra thời gian hết hạn phải sau thời điêmr hiện tại
		boolean check_experiTime = experiTime.after(new Date());

		log.warn("da den day2");
		// nếu TOKEN đã hết hạn hoặc lỗi
		if (!(result_verified && check_experiTime)) {
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);
		}

		// nếu TOKEN đã có trong table
		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);
		}

		// nếu TOKEN còn dùng đc mà muốn Logout thì tiền hành cho nó vào table
		String idTOKEN = signedJWT.getJWTClaimsSet().getJWTID();
		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		// tiến hành insert vô table
		InvalidatedToken tokenentity = new InvalidatedToken(idTOKEN, expiryTime);
		invalidatedTokenRepository.save(tokenentity);

		token_valid_respone.setValid(true);
		return token_valid_respone;

	}

	
	
	
	
	
	// HÀM KIỂM TRA TOKEN ĐÃ LOGOUT HAY HẾT HẠN HAY >> CÒN DÙNG ĐƯỢC KHÔNG
	public IntrospectResponse introspect(IntrospectRequest requestToken) throws JOSEException, ParseException {
		IntrospectResponse token_valid_respone = new IntrospectResponse();

//		lấy token
		String token = requestToken.getToken();
//		xác minh chữ lý, .getBytes() chuyển đổi chuỗi thành một mảng byte
//		MACVerifier là lớp sd để xác minh chữ ký dựa trên thuật toán  MAC
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

//		SignedJWT là lớp đại diên cho JWT đã được ký cho phép truy cập các thành phần khác như header, payload, sign_nature
//		phân tích chuỗi mã thông báo thành một đối tượng SignedJWT		
		SignedJWT signedJWT = SignedJWT.parse(token);
		boolean result_verified = signedJWT.verify(verifier);// kiểm tra xác thực true/ false
		log.warn("KIEM TRA XAC THUC TOKEN" + result_verified);

//		kiểm tra TOKEN đã hết hạn hay chưa
		Date experiTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//		kiểm tra thời gian hết hạn phải sau thời điêmr hiện tại
		boolean check_experiTime = experiTime.after(new Date());
		log.warn("KIEM TRA HAN SD TOKEN" + check_experiTime);

		boolean check_logout = invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID());
		log.warn("KIEM TRA LOGOUT TOKEN" + check_logout);

		if (check_logout == false) {
			token_valid_respone.setValid(result_verified && check_experiTime);
		} else {
			token_valid_respone.setValid(false);
		}

// gán dữ liệu để return

		return token_valid_respone;

	}

	private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
		// xác minh chữ lý, .getBytes() chuyển đổi chuỗi thành một mảng byte
//		MACVerifier là lớp sd để xác minh chữ ký dựa trên thuật toán  MAC
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

//		SignedJWT là lớp đại diên cho JWT đã được ký cho phép truy cập các thành phần khác như header, payload, sign_nature
//		phân tích chuỗi mã thông báo thành một đối tượng SignedJWT	
		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);

		if (!(verified && expiryTime.after(new Date())))
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);

		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);

		return signedJWT;
	}

}
