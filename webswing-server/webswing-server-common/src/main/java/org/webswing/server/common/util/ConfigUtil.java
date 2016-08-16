package org.webswing.server.common.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.model.meta.MetadataGenerator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConfigUtil {
	private static final Logger log = LoggerFactory.getLogger(ConfigUtil.class);

	public static MetaObject getConfigMetadata(Object o, ClassLoader cl) throws Exception {
		MetadataGenerator<Object> generator = new MetadataGenerator<Object>();
		return generator.getMetadata(o, cl);
	}

	public static <T> T instantiateConfig(Map<String, Object> c, final Class<T> clazz, final Object... context) {
		if (c == null) {
			c = new HashMap();
		}
		final Map<String, Object> config = c;
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				BeanInfo info = Introspector.getBeanInfo(method.getDeclaringClass());
				PropertyDescriptor[] pds = info.getPropertyDescriptors();
				if (method.getName().equals("getValueAs") && method.getParameterTypes().length == 2 && args[0] instanceof String && args[1] instanceof Class) {
					String s = (String) args[0];
					Class c = (Class) args[1];
					Object o = config.get(s);
					Map<String, Object> subConfig = (Map<String, Object>) (o != null && o instanceof HashMap ? o : new HashMap());
					return instantiateConfig(subConfig, c, context);
				}
				for (PropertyDescriptor pd : pds) {
					if (pd.getReadMethod().equals(method)) {
						for (Object o : context) {
							if (method.getReturnType().isAssignableFrom(o.getClass())) {
								return o;
							}
						}
						Object value = config.get(pd.getName());
						if (value != null) {
							if (ClassUtils.isAssignable(value.getClass(), method.getReturnType(), true)) {
								if (value instanceof Map) {
									Class generic = getGenericClass(method.getGenericReturnType(), 1);
									if (generic != null && generic.isInterface()) {
										Map valueMap = (Map) value;
										Map resultMap = new HashMap();
										for (Object key : valueMap.keySet()) {
											Object entryValue = valueMap.get(key);
											resultMap.put(key, instantiateConfig((Map<String, Object>) entryValue, generic, context));
										}
										return resultMap;
									}
								}
								if (value instanceof List) {
									Class generic = getGenericClass(method.getGenericReturnType(), 0);
									if (generic != null && generic.isInterface()) {
										List valuelist = (List) value;
										List resultList = new ArrayList();
										for (Object item : valuelist) {
											resultList.add(instantiateConfig((Map<String, Object>) item, generic, context));
										}
										return resultList;
									}
								}
								return value;
							} else if (value instanceof Map && method.getReturnType().isInterface()) {
								return instantiateConfig((Map) value, method.getReturnType(), context);
							} else if (value instanceof String && method.getReturnType().isEnum()) {
								return Enum.valueOf((Class<Enum>) method.getReturnType(), (String) value);
							} else {
								log.error("Invalid configuration. Type of " + clazz.getName() + "." + pd.getName() + " is not " + method.getReturnType());
								return null;
							}
						}
					}
				}
				return null;
			}
		});
	}

	private static Class<?> getGenericClass(Type genericType, int index) {
		if (genericType instanceof ParameterizedType) {
			Type[] generics = ((ParameterizedType) genericType).getActualTypeArguments();
			if (generics != null && generics[index] instanceof Class) {
				return (Class<?>) generics[index];
			}
		}
		return null;
	}
}
