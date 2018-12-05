package br.com.e8.soupsi.ejb;

import javax.ejb.Stateless;

import br.com.e8.soupsi.client.PessoaFisicaService;
import br.com.e8.soupsi.ejb.base.BaseServiceImpl;
import br.com.e8.soupsi.jpa.PessoaFisica;

@Stateless
public class PessoaFisicaServiceImpl extends BaseServiceImpl<PessoaFisica> implements PessoaFisicaService {}