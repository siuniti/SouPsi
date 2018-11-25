package br.com.e8.soupsi.ejb.business;

public class IntegrityConstraintViolationException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1782491378374060599L;
	
	public IntegrityConstraintViolationException() {
		super("O registro possui relacionamentos e não pode ser excluído");
	}
}