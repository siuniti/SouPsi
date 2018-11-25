package br.com.e8.soupsi.ejb.business;

public class UnauthorizedException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1782491378374060599L;
	
	public UnauthorizedException() {
		super("Não autorizado");
	}
}