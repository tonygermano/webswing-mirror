package org.webswing.server.services.security.extension.onetimeurl;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.services.security.api.AbstractWebswingUser;
import org.webswing.server.services.security.api.WebswingAction;
import org.webswing.server.services.security.api.WebswingAuthenticationException;
import org.webswing.server.services.security.extension.api.SecurityModuleExtension;
import org.webswing.server.services.security.modules.AbstractExtendableSecurityModule;
import org.webswing.server.services.security.modules.AbstractSecurityModule;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class OneTimeUrlSecurityExtension extends SecurityModuleExtension<OneTimeUrlSecurityExtensionConfig> {
	private static final String SECURITY_TOKEN = "securityToken";
	private static final Logger log = LoggerFactory.getLogger(OneTimeUrlSecurityExtension.class);

	public OneTimeUrlSecurityExtension(OneTimeUrlSecurityExtensionConfig config) {
		super(config);
	}

	@Override
	public AbstractWebswingUser doSufficientPreValidation(AbstractExtendableSecurityModule<?> m, HttpServletRequest request, HttpServletResponse response) throws WebswingAuthenticationException {
		Map<String, Object> requestData = m.getLoginRequest(request);
		if (requestData != null && requestData.containsKey(SECURITY_TOKEN)) {
			AbstractWebswingUser result = verifyOneTimePassword((String) requestData.get(SECURITY_TOKEN));
			return result;
		}
		return null;
	}

	@Override
	public boolean serveAuthenticated(AbstractWebswingUser user, String path, HttpServletRequest req, HttpServletResponse res) {
		if (path.equals("/oneTimeUrl") && user.isPermitted(WebswingAction.rest_getOneTimePassword.name())) {
			String requestor = req.getParameter("requestorId");
			String userName = req.getParameter("user");
			String roles = req.getParameter("roles");
			String permissions = req.getParameter("permissions");
			String[] rolesArray = roles != null ? roles.split(",") : null;
			String[] permissionsArray = permissions != null ? permissions.split(",") : null;
			List<String[]> attributes = new ArrayList<>();
			for (String paramName : req.getParameterMap().keySet()) {
				attributes.add(new String[] { paramName, req.getParameter(paramName) });
			}
			String[][] attribArray = attributes.toArray(new String[attributes.size()][]);
			try {
				String otp = generateOneTimePassword(requestor, userName, rolesArray, permissionsArray, attribArray);
				res.getWriter().print(otp);
				res.flushBuffer();
				res.getWriter().close();
			} catch (Exception e) {
				log.error("Failed to generate OneTimeUrl", e);
				try {
					res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
				} catch (IOException e1) {
				}
			}
			return true;
		} else {
			return false;
		}
	}

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

	public String generateOneTimePassword(String requestorId, String user, String[] roles, String[] permissions, String[][] attributes) throws WebswingAuthenticationException {
		String encoded;
		try {
			OtpTokenData token = new OtpTokenData();
			token.setUser(user);
			token.setRequestorId(requestorId);
			token.setAttributes(attributes);
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
		if (getConfig().getApiKeys() == null || getConfigForRequestor(getConfig(), token.getRequestorId()) == null) {
			throw new WebswingAuthenticationException("RequestorId not configured.");
		}
	}

	public static OtpAccessConfig getConfigForRequestor(OneTimeUrlSecurityExtensionConfig config, String requestorId) {
		if (config != null) {
			for (OtpAccessConfig oac : config.getApiKeys()) {
				if (StringUtils.equals(requestorId, oac.getRequestorId())) {
					return oac;
				}
			}
		}
		return null;
	}

	public int getOtpValidForSec(String requestingClient) {
		Integer result = null;
		OtpAccessConfig c = getConfigForRequestor(getConfig(), requestingClient);
		result = c.getValidForSec();
		return result == null ? 30 : result;
	}

	public String getOtpCrypto(String requestingClient) {
		String result = null;
		OtpAccessConfig c = getConfigForRequestor(getConfig(), requestingClient);
		result = c.getHMacAlgo();
		return result == null ? "HmacSHA512" : result;
	}

	public String getOtpSecret(String requestingClient) throws WebswingAuthenticationException {
		String result = null;
		OtpAccessConfig c = getConfigForRequestor(getConfig(), requestingClient);
		result = c.getSecret();
		if (result == null) {
			log.error("Secret not found for requestor '" + requestingClient + "'");
			throw new WebswingAuthenticationException("OTP Access not configured.");
		}
		return result;
	}

	public static String getOtpMessage(OtpTokenData token) {
		String attributes = arrayToString(token.getAttributes());
		String permissions = arrayToString(token.getPermissions());
		String roles = arrayToString(token.getRoles());
		return "" + token.getUser() + attributes + roles + permissions;
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

	private static String arrayToString(String[][] array) {
		String result = "";
		if (array != null) {
			for (String[] item : array) {
				result += arrayToString(item);
			}
		}
		return result;
	}

	public OtpTokenData parseOtpTokenString(String otp) throws WebswingAuthenticationException {
		try {
			OtpTokenData token = AbstractSecurityModule.getMapper().readValue(Base64.decodeBase64(otp), OtpTokenData.class);
			return token;
		} catch (Exception e) {
			throw new WebswingAuthenticationException("Failed to parse OTP token", e);
		}
	}

	public static String encodeOtpToken(OtpTokenData token) throws WebswingAuthenticationException {
		try {
			byte[] value = AbstractSecurityModule.getMapper().writeValueAsBytes(token);
			return Base64.encodeBase64String(value);
		} catch (Exception e) {
			throw new WebswingAuthenticationException("Failed to encode OTP token", e);
		}
	}

	public static String calculateTotpString(String crypto, String secret, String message, int intervalshift, int intervalLengthSec) {
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
		OtpAccessConfig c = getConfigForRequestor(getConfig(), token.getRequestorId());
		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
		Set<String> roles = new HashSet<>();
		Set<String> permissions = new HashSet<>();

		if (token.getAttributes() != null) {
			for (String[] attribute : token.getAttributes()) {
				attributes.put(attribute[0], attribute[1]);
			}
		}
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

		AbstractWebswingUser user = new OtpWebswingUser(token.getUser(), attributes, roles, permissions);
		return user;
	}

	public static void main(String[] args) throws WebswingAuthenticationException, JsonGenerationException, JsonMappingException, IOException {
		OtpTokenData token = new OtpTokenData();
		token.setUser("john");
		token.setRequestorId("myWebPage");
		String secret = "5gV4gchlFZEwmXHNLgGj";
		String crypto = "HmacSHA512";
		String[][] attrs = new String[][] { new String[] { "attr1", "value1" }, new String[] { "attr2", "value2" } };

		String encoded;
		token.setAttributes(attrs);
		token.setRoles(new String[] {});
		token.setPermissions(new String[] {});
		String message = getOtpMessage(token);
		String otp = calculateTotpString(crypto, secret, message, 0, 30);
		token.setOneTimePassword(otp);
		encoded = encodeOtpToken(token);
		long time = System.currentTimeMillis() / 1000 / 30;
		time = time + 0;
		message = message + time;
		System.out.println("message: " + message);
		System.out.println("response: " + AbstractSecurityModule.getMapper().writeValueAsString(token));
		System.out.println("http://localhost:8080/swingset3/?securityToken=" + encoded);
	}

	/* PHP snippet to generate One time URL
	<?php
	$secret =  "5gV4gchlFZEwmXHNLgGj";
	$requestorId = 'myWebPage';
	$swingPath="/swingset3";
	$userName = "john";
	$userAttributes = array(
	array("attr1","value1"),
	array("attr2","value2")
	);
	$roles = array();
	$permissions = array();
	
	$intervalSec = 30;
	
	
	//message = swingPath + username + attributes + roles + permissions + time
	$message = $swingPath.$userName;
	foreach ($userAttributes as $value) {
	  $message = $message .$value[0].$value[1];
	}
	foreach ($roles as $value) {
	  $message = $message . $value;
	}
	foreach ($permissions as $value) {
	  $message = $message . $value;
	}
	$message = $message.round(microtime(true)/$intervalSec);
	
	$otp = base64_encode(hash_hmac("sha512",$message,$secret,true));
	$response = (object)array(
	    'swingPath' => $swingPath,
	    'requestorId' => $requestorId,
		'user' => $userName,
		'attributes' => $userAttributes,
		'roles' => $roles,
		'permissions' => $permissions,
		'oneTimePassword' => $otp
	);
	$token=json_encode($response,JSON_UNESCAPED_SLASHES);
	$b64token=base64_encode($token);
	$url='http://localhost:8080/swingset3/?securityToken='.$b64token;
	echo 'message: '.$message;
	echo '<br>';
	echo 'response: '.$token;
	echo '<br>';
	echo '<a href = "'.$url.'" target="_blank">'.$url.'</a>';
	?>
	*/

}
