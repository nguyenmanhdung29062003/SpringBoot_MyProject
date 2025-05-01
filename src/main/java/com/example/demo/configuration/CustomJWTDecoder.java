package com.example.demo.configuration;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.demo.dto.IntrospectRequest;
import com.example.demo.dto.IntrospectResponse;
import com.example.demo.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
public class CustomJWTDecoder  implements JwtDecoder{
	@Value("${jwt.signerKey}")
	private String SIGNER_KEY;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	private NimbusJwtDecoder nimbusJwtDecoder = null;
	
	@Override
	public Jwt decode(String token) throws JwtException
	{
		try
		{
			IntrospectRequest introspectRequest = new IntrospectRequest();
			
			introspectRequest.setToken(token);
			
			//kiểm tra xem token còn dùng đc k hay đã logout hay chưa
			IntrospectResponse response = authenticationService.introspect(introspectRequest);
			
			
			//Lombok tự động sinh code cho các getter/setter trong class IntrospectRequest do sd @Data
			//Với thuộc tính kiểu boolean, Lombok tuân theo quy ước JavaBeans và tạo getter với tiền tố "is" thay vì "get"
			//Trong trường hợp của bạn, Lombok tự động tạo phương thức isValid() thay vì getValid()
			log.warn("KET QUA RESPONE" + response.isValid());
			if(!response.isValid()) {
				throw new JwtException("Token is invalid");
			}
			
			
		}
		catch(JOSEException | ParseException e) {
			throw new JwtException(e.getMessage());
		}
		
		//nếu TOKEN vẫn dùng 
		//kiểm tra Nimbus có null hay không
		if(Objects.isNull(nimbusJwtDecoder)) {
			//bao gồm 2 tham số : secretKey và Thuật toán
			SecretKeySpec secretkey = new SecretKeySpec(SIGNER_KEY.getBytes(),"HS512");
			
			nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretkey).macAlgorithm(MacAlgorithm.HS512).build();
		}
		
		return nimbusJwtDecoder.decode(token);
	}
}
