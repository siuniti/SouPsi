package br.com.e8.soupsi.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.e8.soupsi.jpa.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "PESSOA_FISICA", schema = "AUTOGESTAO")
public class PessoaFisica extends BaseEntity {

	@Id
	@Column(name = "ID_PESSOA_FISICA", nullable = false, length = 11)
	private Long id;
	
	@Column(name = "NOME", nullable = false, length = 255)
	private String nome;
}