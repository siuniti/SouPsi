package br.com.e8.soupsi.ejb.type;

public enum OperationType {

	LIKE(" LIKE "),
	NOT_EQUAL(" != "),
	EQUAL_CASE_SENSITIVE(" = "),
	BETWEEN(" BETWEEN "),
	GREATER(" > "),
	GREATER_THAN(" >= "),
	LESS(" < "),
	LESS_THAN(" <= "),
	IN(" IN "),
	IS_NOT_NULL(" IS NOT NULL ");
	
	private String operation;
	
	private OperationType(String operation) {
		this.operation = operation;
	}
	
	public String getOperation() {
		return this.operation;
	}
}