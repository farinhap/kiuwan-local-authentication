/**
 * copyright (c) 2011-2017 Optimyth Software Technologies. All rights reserved.
 */

package com.kiuwan.authentication;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

public class TokenHandler {
	
	
	public static final String ERROR_EXPIRED_TOKEN = "ERROR_EXPIRED_TOKEN";
	public static final String ERROR_UNKNOWN_USER = "ERROR_UNKNOWN_USER";
	public static final String ERROR_UNKNOWN_CUSTOMER = "ERROR_UNKNOWN_CUSTOMER";
	public static final String ERROR_UNKNOWN_SECRET = "ERROR_UNKNOWN_SECRET";
	public static final String ERROR_INCORRECT_SECRET = "ERROR_INCORRECT_SECRET";
	
	
	public static final String TOKEN_PARAM_NAME = "a";
	public static final String USER_PARAM_NAME = "b";
	public static final String LOGIN_URL_PARAM_NAME = "c";
	public static final String CLIENT_PARAM_NAME = "d";
	
	
	private static final String HMAC_ALGO = "HmacSHA256";
	private static final String SEPARATOR = ".";
	private static final String SEPARATOR_SPLITTER = "\\.";
	
	/**
	 * The logger.
	 */
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(TokenHandler.class);
	
	private static String secret = null;
	private static Mac hmac = null;
	
	
	private static Mac getHmac(String secret) {
		try {
			Mac hmac = Mac.getInstance(HMAC_ALGO);
			hmac.init(new SecretKeySpec(secret.getBytes(), HMAC_ALGO));
			return hmac;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalStateException("failed to initialize HMAC: " + e.getMessage(), e);
		}
	}
	
	private static Mac getHmac() {
		if (hmac == null) {
			try {
				hmac = Mac.getInstance(HMAC_ALGO);
				hmac.init(new SecretKeySpec(secret.getBytes(), HMAC_ALGO));
			} catch (NoSuchAlgorithmException | InvalidKeyException e) {
				throw new IllegalStateException("failed to initialize HMAC: " + e.getMessage(), e);
			}
		}
		
		return hmac;
	}
	
	static public void initSecret(String secret) {
		TokenHandler.secret = secret;
	}
	
	
	static public String createTokenForUser(User user) throws IOException {
		byte[] userBytes = toJSON(user);
		byte[] hash = createHmac(user.getUsername());
		final StringBuilder sb = new StringBuilder(170);
		sb.append(toBase64(userBytes));
		sb.append(SEPARATOR);
		sb.append(toBase64(hash));
		return sb.toString();
	}
	
	
	static public User parseUserFromToken(String secret, String token) {
		final String[] parts = token.split(SEPARATOR_SPLITTER);
		String newHash = "";
		if (parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
			try {
				final byte[] userBytes = fromBase64(parts[0]);
				logger.debug("User: {}", new String(userBytes));

				User user = fromJSON(userBytes);
				newHash = toBase64(createHmac(secret, user.getUsername()));
				
				logger.info ("Compare token, newHash: {}, parts[1]: {}: ", newHash, parts[1]);		
				boolean validHash = newHash.equals(parts[1]);
				if (validHash) {
					return user;
				}
			} catch (Exception e) {
				logger.error("[EXCEPTION]: ", e);
			}
		}
		
		logger.info ("Invalid Token, secret: {}, token: {}, newHash: {}: ", secret, token, newHash);		
		return null;
	}
	
	static private byte[] toJSON(User user) throws IOException {
		try {
			return new ObjectMapper().writeValueAsBytes(user);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	static private User fromJSON(final byte[] userBytes) {
		try {
			return new ObjectMapper().readValue(new ByteArrayInputStream(userBytes), User.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	static private String toBase64(byte[] content) {
		return DatatypeConverter.printBase64Binary(content);
	}

	static private byte[] fromBase64(String content) {
		return DatatypeConverter.parseBase64Binary(content);
	}
	
	static private synchronized byte[] createHmac(String key) {
		return getHmac().doFinal(key.getBytes());
	}
	
	static private synchronized byte[] createHmac(String secret, String content) {
		return getHmac(secret).doFinal(content.getBytes());
	}
}
