package com.taqwa.gowaqaf.exception.custom;

import com.taqwa.gowaqaf.exception.code.ErrorCode;

public class ResourceNotFoundException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(ErrorCode errorCode) {
		super(errorCode);
		// TODO Auto-generated constructor stub
	}

	public ResourceNotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

}
