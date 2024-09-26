package com.example.demo.exception;


//Phải kế thừa để ném ra ngoại lệ theo Format mà mình mong muốn
public class AppException extends RuntimeException {
	private ErrorCode errorcode;

	public ErrorCode getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(ErrorCode errorcode) {
		this.errorcode = errorcode;
	}

	public AppException(ErrorCode errorcode) {
		super(errorcode.getMessage());
		this.errorcode = errorcode;
	}

}
