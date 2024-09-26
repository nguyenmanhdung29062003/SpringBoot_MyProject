package com.example.demo;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.apiresponse.ApiResponse;
import com.example.demo.dto.IntrospectRequest;
import com.example.demo.dto.IntrospectResponse;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.TokenDTO;
import com.example.demo.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;

@RestController
public class AuthenticationAPI {
	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/login")
	public ApiResponse<TokenDTO> authenticate(@RequestBody @Valid LoginDTO dto){
		
		ApiResponse<TokenDTO> apires = new ApiResponse<TokenDTO>();
		apires.setCode(200);
		apires.setMessage("success");
		apires.setResult(authenticationService.authentication(dto));
		
		return apires;
		
	}
	
	@PostMapping("/introspect")
	public ApiResponse<IntrospectResponse> validateToken(@RequestBody IntrospectRequest tokenrequest) throws JOSEException, ParseException
	{
		ApiResponse<IntrospectResponse> apries = new ApiResponse<IntrospectResponse>();
		IntrospectResponse introRespone = authenticationService.introspect(tokenrequest);
		if(introRespone.isValid()==true)
		{
			apries.setCode(200);
			apries.setMessage("success");
		}
		else {
			apries.setCode(404);
			apries.setMessage("false");
		}
		apries.setResult(authenticationService.introspect(tokenrequest));
		
		
		return apries;
		
	}
}
