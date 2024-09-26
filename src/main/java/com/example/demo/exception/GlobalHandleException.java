package com.example.demo.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.apiresponse.ApiResponse;


@RestControllerAdvice
//để agree tất cả các Exception vào một chỗ để xử lý
public class GlobalHandleException {
//	Tiến hành xử lý Exception
//	Xử lý AppExpception
	@ExceptionHandler(value=AppException.class)
	ResponseEntity<ApiResponse> handlingRuntimeException(AppException ex)
	{
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setCode(ex.getErrorcode().getCode());
		apiResponse.setMessage(ex.getErrorcode().getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}
	
	@ExceptionHandler(value= MethodArgumentNotValidException.class)
	ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException ex){
		
		String enum_key = ex.getFieldError().getDefaultMessage();
		ApiResponse apiResponse = new ApiResponse();
		
		apiResponse.setCode(ErrorCode.valueOf(enum_key).getCode());
		apiResponse.setMessage(ErrorCode.valueOf(enum_key).getMessage());
		
		return ResponseEntity.badRequest().body(apiResponse);
	}
	
}
