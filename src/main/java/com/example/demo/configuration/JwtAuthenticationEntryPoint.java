package com.example.demo.configuration;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.example.demo.apiresponse.ApiResponse;
import com.example.demo.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// TODO Auto-generated method stub
		//HttpServletResponse là đối tượng mà để respose những cái nội dung mà ta mong muốn
		//đầu tiên lấy ErrorCode ra trước
		ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAIL;
		
		//trả về HttpStatus Code
		response.setStatus(errorCode.getStatusCode().value());
		
		//set trả về nội dung kiểu JSON
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		
		//trả về responseBody() theo chuẩn ApiRespose mà ta đã tạo
		ApiResponse apires = new ApiResponse();
		apires.setCode(errorCode.getCode());
		apires.setMessage(errorCode.getMessage());
		
		//get write() là viết cái nội dung mà ta cần trả, thường là string
		//tuy nhiên của ta đang là Object nên ta cần sửa thành String bằng ObjectMapper để convert ApiResponse về String
		
		ObjectMapper objectMapper = new ObjectMapper();
        try {
            response.getWriter().write(objectMapper.writeValueAsString(apires));;
        } catch (Exception e) {
            e.printStackTrace();
        }
		
      //sau khi set đầy đủ các thông tin ta sẽ tiến hành commit cái response này
		response.flushBuffer();
		
	}

}
