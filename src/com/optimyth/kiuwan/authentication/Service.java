package com.optimyth.kiuwan.authentication;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Service {
	
	
	public static final String ERROR_EXPIRED_TOKEN = "AUTH1";
	public static final String ERROR_UNKNOWN_USER = "AUTH2";
	public static final String ERROR_UNKNOWN_CUSTOMER = "AUTH3";
	public static final String ERROR_UNKNOWN_SECRET = "AUTH4";
	public static final String ERROR_INCORRECT_SECRET = "AUTH5";
	
	
	public static final String TOKEN_PARAM_NAME = "a";
	public static final String USER_PARAM_NAME = "b";
	public static final String LOGIN_URL_PARAM_NAME = "c";
	public static final String CLIENT_PARAM_NAME = "d";
	
	
	private static final String HMAC_ALGO = "HmacSHA256";
	private static final String SEPARATOR = ".";
	private static final String SEPARATOR_SPLITTER = "\\.";
	
	
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
		Service.secret = secret;
	}
	
	
	static public String createTokenForUser(User user) throws IOException {
		byte[] userBytes = toJSON(user);
		byte[] hash = createHmac(userBytes);
		final StringBuilder sb = new StringBuilder(170);
		sb.append(toBase64(userBytes));
		sb.append(SEPARATOR);
		sb.append(toBase64(hash));
		return sb.toString();
	}
	
	
	static public User parseUserFromToken(String secret, String token) {
		final String[] parts = token.split(SEPARATOR_SPLITTER);
		if (parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
			try {
				final byte[] userBytes = fromBase64(parts[0]);
				final byte[] hash = fromBase64(parts[1]);

				boolean validHash = Arrays.equals(createHmac(secret, userBytes), hash);
				if (validHash) {
					final User user = fromJSON(userBytes);
					if (new Date().getTime() < user.getDate()) {
						return user;
					}
				}
			} catch (IllegalArgumentException e) {
			}
		}
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
	
	static private synchronized byte[] createHmac(byte[] content) {
		return getHmac().doFinal(content);
	}
	
	static private synchronized byte[] createHmac(String secret, byte[] content) {
		return getHmac(secret).doFinal(content);
	}
}
