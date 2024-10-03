package com.example.demo.exception;

public enum ErrorCode {
	USER_EXISTED(404, "Người dùng này đã tồn tại"), 
	PASS_ERROR(505, "Mật khẩu phải tối thiểu 8 ký tự"),
	USER_NOT_EXIST(500, "Không tìm thấy người dùng này"),
	AUTHENTICATION_FAIL(500,"Xác thực thất bại"),
	;

	private int code;
	private String message;

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	private ErrorCode() {
	}

	private ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
