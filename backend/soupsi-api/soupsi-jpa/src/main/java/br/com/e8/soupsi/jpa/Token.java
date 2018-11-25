package br.com.e8.soupsi.jpa;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Token {
	
	private String authorization;
	private Date dtInicio;
	private Date dtFim;
	private boolean permanecerLogado;
}