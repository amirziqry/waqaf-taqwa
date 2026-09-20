package com.taqwa.gowaqaf.exception.custom;

import com.taqwa.gowaqaf.exception.code.ErrorCode;

public class InternalServerErrorException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InternalServerErrorException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

}
