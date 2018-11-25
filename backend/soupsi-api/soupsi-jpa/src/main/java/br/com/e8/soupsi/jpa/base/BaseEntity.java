package br.com.e8.soupsi.jpa.base;

import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) 
public abstract class BaseEntity {

	public abstract Long getId();
	public abstract void setId(Long id);
	
	@Transient
	public boolean isNew() {
		return this.getId() == null || this.getId().longValue() == 0;
	}
}