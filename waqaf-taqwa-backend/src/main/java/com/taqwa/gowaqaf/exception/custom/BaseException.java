package com.taqwa.gowaqaf.exception.custom;

import com.taqwa.gowaqaf.exception.code.ErrorCode;

public class BaseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ErrorCode errorCode;

	public BaseException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
	}

	public BaseException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
