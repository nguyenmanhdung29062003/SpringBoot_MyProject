package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
	USER_EXISTED(404, "Người dùng này đã tồn tại", HttpStatus.BAD_REQUEST), 
	PASS_ERROR(505, "Mật khẩu phải tối thiểu 8 ký tự", HttpStatus.BAD_REQUEST),
	USER_NOT_EXIST(500, "Không tìm thấy người dùng này",HttpStatus.NOT_FOUND),
	AUTHENTICATION_FAIL(500,"Xác thực thất bại", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(403,"Bạn không có quyền truy cập trang này", HttpStatus.FORBIDDEN)
	;
	
	private HttpStatusCode statusCode;
	private int code;
	private String message;
	
	
	
	public HttpStatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	private ErrorCode() {
	}

	private ErrorCode(int code, String message, HttpStatusCode statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode=statusCode;
	}

}
