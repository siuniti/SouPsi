package br.com.e8.soupsi.ejb.base;

import br.com.e8.soupsi.ejb.type.OperationType;
import lombok.Getter;

@Getter
public class OperationValue {
	
	private OperationType operation;
	private Object value;
	private Object value2;
	
	public OperationValue(OperationType operation) {
		this.operation = operation;
	}
	
	public OperationValue(OperationType operation, Object value) {
		this.operation = operation;
		this.value = value;
	}
	
	public OperationValue(OperationType operation, Object value1, Object value2) {
		this.operation = operation;
		this.value = value1;
		this.value2 = value2;
	}
}