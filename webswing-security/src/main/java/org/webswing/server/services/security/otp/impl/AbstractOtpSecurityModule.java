package org.webswing.server.services.security.otp.impl;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.modules.AbstractSecurityModule;
import org.webswing.server.services.security.otp.api.OneTimePasswordModule;

public abstract class AbstractOtpSecurityModule<T extends WebswingOtpSecurityModuleConfig>  extends AbstractSecurityModule<T> implements OneTimePasswordModule
{
	private static final Logger log = LoggerFactory.getLogger(AbstractOtpSecurityModule.class);

	public AbstractOtpSecurityModule(T config) {
		super(config);
	}

	@Override
	public AbstractWebswingUser verifyOneTimePassword(String otp) throws WebswingAuthenticationException {
		try {
			OtpTokenData token = parseOtpTokenString(otp);
			verifyTokenValid(token);
			String secret = getOtpSecret(token.getRequestorId());
			String message = getOtpMessage(token);
			String crypto = getOtpCrypto(token.getRequestorId());
			int validityInterval = getOtpValidForSec(token.getRequestorId());
			for (int i = -1; i <= 1; i++) {
				String expectedPassword = calculateTotpString(crypto, secret, message, i, validityInterval);
				if (expectedPassword.equals(token.getOneTimePassword())) {
					return createUser(token);
				}
			}
			throw new WebswingAuthenticationException("Invalid or expired OTP. ");
		} catch (Exception e) {
			throw new WebswingAuthenticationException("Failed to verify OTP. " + e.getMessage(), e);
		}
	}

	@Override
	public String generateOneTimePassword(String swingPath, String requestorId, String user, String[] roles, String[] permissions) throws WebswingAuthenticationException {
		String encoded;
		try {
			OtpTokenData token = new OtpTokenData();
			token.setUser(user);
			token.setRequestorId(requestorId);
			token.setSwingPath(swingPath);
			token.setRoles(roles);
			token.setPermissions(permissions);
			verifyTokenValid(token);
			String secret = getOtpSecret(requestorId);
			String message = getOtpMessage(token);
			String crypto = getOtpCrypto(requestorId);
			String otp = calculateTotpString(crypto, secret, message, 0, getOtpValidForSec(requestorId));
			token.setOneTimePassword(otp);
			encoded = encodeOtpToken(token);
		} catch (Exception e) {
			throw new WebswingAuthenticationException("Failed to generate OTP. " + e.getMessage(), e);
		}
		return encoded;
	}

	public void verifyTokenValid(OtpTokenData token) throws WebswingAuthenticationException {
		if (StringUtils.isEmpty(token.getUser())) {
			throw new WebswingAuthenticationException("User must not be empty.");
		}
		if (StringUtils.isEmpty(token.getRequestorId())) {
			throw new WebswingAuthenticationException("Requestor Id must not be empty.");
		}
		if (StringUtils.isEmpty(token.getSwingPath())) {
			throw new WebswingAuthenticationException("Swing Path must not be empty.");
		}
		if (getConfig().getOtpAccessConfig() == null || getConfig().getOtpAccessConfig().get(token.getRequestorId()) == null) {
			throw new WebswingAuthenticationException("RequestorId not configured.");
		}
	}

	public int getOtpValidForSec(String requestingClient) {
		Integer result = null;
		OtpAccessConfig c = getConfig().getOtpAccessConfig().get(requestingClient);
		result = c.getValidForSec();
		return result == null ? 30 : result;
	}

	public String getOtpCrypto(String requestingClient) {
		String result = null;
		OtpAccessConfig c = getConfig().getOtpAccessConfig().get(requestingClient);
		result = c.getHMacAlgo();
		return result == null ? "HmacSHA512" : result;
	}

	public String getOtpSecret(String requestingClient) throws WebswingAuthenticationException {
		String result = null;
		OtpAccessConfig c = getConfig().getOtpAccessConfig().get(requestingClient);
		result = c.getSecret();
		if (result == null) {
			log.error("Secret not found for requestor '" + requestingClient + "'");
			throw new WebswingAuthenticationException("OTP Access not configured.");
		}
		return result;
	}

	public String getOtpMessage(OtpTokenData token) {
		String permissions = arrayToString(token.getPermissions());
		String roles = arrayToString(token.getRoles());
		return "" + token.getSwingPath() + token.getUser() + roles + permissions;
	}

	private static String arrayToString(String[] array) {
		String result = "";
		if (array != null) {
			for (String item : array) {
				result += item;
			}
		}
		return result;
	}

	public OtpTokenData parseOtpTokenString(String otp) throws WebswingAuthenticationException {
		try {
			OtpTokenData token = getMapper().readValue(Base64.decodeBase64(otp), OtpTokenData.class);
			return token;
		} catch (Exception e) {
			throw new WebswingAuthenticationException("Failed to parse OTP token", e);
		}
	}

	public String encodeOtpToken(OtpTokenData token) throws WebswingAuthenticationException {
		try {
			byte[] value = getMapper().writeValueAsBytes(token);
			return Base64.encodeBase64String(value);
		} catch (Exception e) {
			throw new WebswingAuthenticationException("Failed to encode OTP token", e);
		}
	}

	public String calculateTotpString(String crypto, String secret, String message, int intervalshift, int intervalLengthSec) {
		long time = System.currentTimeMillis() / 1000 / intervalLengthSec;
		time = time + intervalshift;
		message = message + time;
		byte[] msg = message.getBytes();
		byte[] key = secret.getBytes();
		byte[] hash = hmacSha(crypto, key, msg);
		return Base64.encodeBase64String(hash);
	}

	private static byte[] hmacSha(String crypto, byte[] keyBytes, byte[] text) {
		try {
			Mac hmac;
			hmac = Mac.getInstance(crypto);
			SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
			hmac.init(macKey);
			return hmac.doFinal(text);
		} catch (GeneralSecurityException gse) {
			throw new UndeclaredThrowableException(gse);
		}
	}

	public AbstractWebswingUser createUser(OtpTokenData token) throws WebswingAuthenticationException {
		OtpAccessConfig c = getConfig().getOtpAccessConfig().get(token.getRequestorId());
		Set<String> roles = new HashSet<>();
		Set<String> permissions = new HashSet<>();
		if (token.getRoles() != null) {
			roles.addAll(Arrays.asList(token.getRoles()));
		}
		if (token.getPermissions() != null) {
			permissions.addAll(Arrays.asList(token.getPermissions()));
		}

		if (c.getAllowedUsers() != null && !c.getAllowedUsers().contains(token.getUser())) {
			throw new WebswingAuthenticationException("User '" + token.getUser() + "' is not allowed to use OTP.");
		}
		if (c.getAllowedRoles() != null) {
			roles.retainAll(c.getAllowedRoles());
		}
		if (c.getAllowedPermissions() != null) {
			permissions.retainAll(c.getAllowedPermissions());
		}

		AbstractWebswingUser user = new OtpWebswingUser(token.getUser(), roles, permissions);
		return user;
	}

}
