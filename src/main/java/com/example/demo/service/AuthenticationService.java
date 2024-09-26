package com.example.demo.service;

import java.text.ParseException;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
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

@Service
public class AuthenticationService {

	@Autowired
	private UserRepository userrepository;

	@NonFinal
	@Value("${jwt.signerKey}")
	private String SIGNER_KEY;

	public TokenDTO authentication(LoginDTO dto) {
		if (userrepository.existsByUsername(dto.getUsername()) == false) {
//			ném ra một ngoại lệ + message tương ứng
			throw new AppException(ErrorCode.USER_NOT_EXIST);
		}
		List<UserEntity> userentity = (List<UserEntity>) userrepository.findOneByUsername(dto.getUsername());
		TokenDTO token = new TokenDTO();

//		Tiến hành kiểm tra dữ liệu đã mã hóa
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		boolean result = false;
		for (UserEntity userEntity2 : userentity) {
			result = passwordEncoder.matches(dto.getPassword(), userEntity2.getPassword());
			if (result == true) {
				break;
			}
		}

//		TH mat khau k dung nem ra ngoại lẹ
		if (result == false) {
			throw new AppException(ErrorCode.AUTHENTICATION_FAIL);
		}

//	Thành công thì tạo một TOKEN
		String token_value = generateToken(dto);
		
		token.setToken(token_value);
		token.setAuthenticated(result);

		return token;

	}

	private String generateToken(LoginDTO userdto) {
//		Đầu tiên tạo header với thuật toán HS512
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

//		Tiếp theo tạo claimSet để cho vào payload

		JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
	            .issuer("https://example.com")
	            .subject(userdto.getUsername())
	            .audience("https://yourdomain.com")
	            .expirationTime(new Date(new Date().getTime() + 60 * 1000 * 30)) // hết hạn sau 30 phút
	            .notBeforeTime(new Date())
	            .issueTime(new Date())
	            .jwtID(UUID.randomUUID().toString())
	            .claim("custom_claim", "custom value")
	            .build();
		
		Payload payload = new Payload(claimSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header,payload);
//		Sau khi đầy đủ dữ liêu ta tiến hành ký, hash Token
		
		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		}
		catch(JOSEException e) {
            throw new RuntimeException("K thể tạo TOKEN");
		}
	}
	
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
		
//		kiểm tra TOKEN đã hết hạn hay chưa
		Date experiTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//		kiểm tra thời gian hết hạn phải sau thời điêmr hiện tại
		boolean check_experiTime = experiTime.after(new Date());

// gán dữ liệu để return
		token_valid_respone.setValid(result_verified&&check_experiTime);
		
		return token_valid_respone;
		
		
	}

}
