package br.com.e8.soupsi.ejb.business;

import javax.ejb.ApplicationException;

import lombok.Getter;

@ApplicationException(rollback = true)
public class BusinessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1782491378374060599L;
	
	@Getter
	private String message;
	
	public BusinessException(String message) {
		this.message = message;
	}
}