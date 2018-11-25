package br.com.e8.soupsi.ejb.util;

import java.util.Random;

import br.com.e8.soupsi.client.util.DataUtil;
import br.com.e8.soupsi.jpa.Token;

public class TokenUtil {
	
	public static final int AUTH_LIFETIME = 30;
	
	private static String generateAuthorization() throws Exception {
		Integer valueRandom = new Random().nextInt();
		String hash = HashUtil.hashToMD5(valueRandom.toString());
		
		return hash;
	}
	
	public static Token createToken(Boolean permanecerLogado) throws Exception {
		String authorization = generateAuthorization();
		
		Token token = new Token();
		token.setAuthorization(authorization);
		token.setDtInicio(DataUtil.getDataAtual());
		token.setDtFim(DataUtil.addMinutes(token.getDtInicio(), AUTH_LIFETIME));
		token.setPermanecerLogado(permanecerLogado);
		
		return token;
	}
}