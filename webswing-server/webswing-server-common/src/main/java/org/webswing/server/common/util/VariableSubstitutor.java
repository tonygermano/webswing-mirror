package org.webswing.server.common.util;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.webswing.Constants;
import org.webswing.server.common.model.SecuredPathConfig;

public class VariableSubstitutor {

	private Map<String, String> customVars;
	private StrSubstitutor subs;

	public static VariableSubstitutor basic() {
		return new VariableSubstitutor(new HashMap<String, String>());
	}

	public static VariableSubstitutor forSwingInstance(SecuredPathConfig config, String user, Map<String, Serializable> userAttributes, String sessionId, String clientIp, String locale, String timeZone, String customArgs) {
		Map<String, String> result = new HashMap<String, String>();
		if (user != null) {
			result.put(Constants.USER_NAME_SUBSTITUTE, user);
		}
		if (userAttributes != null) {
			for (Entry<String, Serializable> e : userAttributes.entrySet()) {
				try {
					result.put(Constants.USER_NAME_SUBSTITUTE + "." + e.getKey(), e.getValue().toString());
				} catch (Exception e1) {
					//ignore
				}
			}
		}
		if (sessionId != null) {
			result.put(Constants.SESSION_ID_SUBSTITUTE, sessionId);
		}
		if (clientIp != null) {
			result.put(Constants.SESSION_IP_SUBSTITUTE, clientIp);
		}
		if (locale != null) {
			result.put(Constants.SESSION_LOCALE_SUBSTITUTE, locale);
		}
		if (timeZone != null) {
			result.put(Constants.SESSION_TIMEZONE_SUBSTITUTE, timeZone);
		}
		if (customArgs != null) {
			result.put(Constants.SESSION_CUSTOMARGS_SUBSTITUTE, customArgs);
			try {
				Map<String, Object> map = WebswingObjectMapper.get().readValue(customArgs, Map.class);
				flatten(Constants.SESSION_CUSTOMARGS_SUBSTITUTE, map.entrySet().stream()).forEach(e -> result.put(e.getKey(), String.valueOf(e.getValue())));
			} catch (JsonProcessingException e) {
				//ignore
			}
		}

		VariableSubstitutor substitutor = forSwingApp(config);
		substitutor.extendCustomVars(result);
		return substitutor;
	}

	private static Stream<Entry<String, Object>> flatten(String prefix, Stream<Entry<String, Object>> stream) {
		return stream.flatMap(entry -> {
			if (entry.getValue() !=null && entry.getValue() instanceof Map) {
				return flatten(prefix + "." +entry.getKey(), ((Map<String, Object>)entry.getValue()).entrySet().stream());
			} else {
				return Stream.of(new AbstractMap.SimpleEntry<>(prefix + "." +entry.getKey(),entry.getValue()));
			}
		});
	}

	public static VariableSubstitutor forSwingApp(SecuredPathConfig config) {
		Map<String, String> result = new HashMap<String, String>();
		if (config != null) {
			result.put(Constants.APP_HOME_FOLDER_SUBSTITUTE, config.getHomeDir());
			result.put(Constants.APP_CONTEXT_PATH_SUBSTITUTE, config.getPath());
		}
		return new VariableSubstitutor(result);
	}


	public VariableSubstitutor(Map<String, String> customVars) {
		this.customVars = customVars;
		this.subs = new StrSubstitutor(new StrLookup<String>() {

			@Override
			public String lookup(String key) {
				String value = VariableSubstitutor.this.customVars.get(key);
				if (value != null) {
					return value;
				}
				value = System.getProperty(key);
				if (value != null) {
					return value;
				}
				value = System.getenv(key);
				if (value != null) {
					return value;
				}
				return null;
			}
		});
	}

	public String replace(String string) {
		return subs.replace(string);
	}

	public Map<String, String> getVariableMap() {
		Map<String, String> result = new HashMap<String, String>();
		result.putAll(System.getenv());
		for (final String name : System.getProperties().stringPropertyNames()) {
			result.put(name, System.getProperties().getProperty(name));
		}
		result.putAll(customVars);
		return result;
	}

	private void extendCustomVars(Map<String, String> customVars){
		this.customVars.putAll(customVars);
	}

}
