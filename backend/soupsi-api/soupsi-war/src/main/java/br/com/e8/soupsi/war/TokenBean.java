package br.com.e8.soupsi.war;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;

//import br.com.e8.soupsi.client.util.DataUtil;
//import br.com.e8.soupsi.ejb.util.TokenUtil;
import br.com.e8.soupsi.jpa.Token;

@Named
@SessionScoped
public class TokenBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7252925018093315767L;
	
	static Map<String, Token> activeTokens = new HashMap<>();

	public void add(Token token) {
		activeTokens.put(token.getAuthorization(), token);
	}
	
	public Token isValidToken(String authorization, ServletContext context) {
		return new Token();	
		
//		Token tokenBlend = this.validateTokenBlend(authorization);
//		return tokenBlend;
	}
	
//	private Token validateTokenBlend(String authorization) {
//		Token tokenAux = activeTokens.get(authorization);
//		
//		if (tokenAux != null) {
//			if (DataUtil.getDataAtual().before(tokenAux.getDtFim())) {
//				tokenAux = updateToken(tokenAux);
//				return tokenAux;
//			} else {
//				activeTokens.remove(tokenAux.getAuthorization());
//			}
//		}
//		
//		return null;
//	}
	
//	private Token updateToken(Token token) {
//		token.setDtFim(DataUtil.addMinutes(DataUtil.getDataAtual(), TokenUtil.AUTH_LIFETIME));
//		
//		activeTokens.put(token.getAuthorization(), token);
//		
//		return token;
//	}
}