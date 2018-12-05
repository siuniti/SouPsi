package br.com.e8.soupsi.client;

import javax.ejb.Local;

import br.com.e8.soupsi.client.base.BaseService;
import br.com.e8.soupsi.jpa.PessoaFisica;

@Local
public interface PessoaFisicaService extends BaseService<PessoaFisica> {}