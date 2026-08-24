package com.taqwa.gowaqaf.exception.custom;

import com.taqwa.gowaqaf.exception.code.ErrorCode;

public class BadRequestException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
		// TODO Auto-generated constructor stub
	}

	public BadRequestException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

}
