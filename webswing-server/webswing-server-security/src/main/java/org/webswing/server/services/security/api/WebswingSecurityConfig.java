package org.webswing.server.services.security.api;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webswing.server.common.model.meta.ConfigField;
import org.webswing.server.common.model.meta.ConfigFieldEditorType;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;
import org.webswing.server.common.model.meta.ConfigType;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.model.meta.MetadataGenerator;
import org.webswing.server.common.util.CommonUtil;
import org.webswing.server.services.security.api.WebswingSecurityConfig.WebswingSecurityMetadataGenerator;
import org.webswing.toolkit.util.ClasspathUtil;

@ConfigType(metadataGenerator = WebswingSecurityMetadataGenerator.class)
public interface WebswingSecurityConfig {

	@ConfigField
	public String getModule();

	@ConfigField
	public List<String> getClassPath();

	@ConfigField
	@ConfigFieldEditorType(editor = EditorType.Object)
	public Map<String, Object> getConfig();

	public static class WebswingSecurityMetadataGenerator extends MetadataGenerator<WebswingSecurityConfig> {
		private static final Logger log = LoggerFactory.getLogger(WebswingSecurityMetadataGenerator.class);

		@Override
		public MetaObject getMetadata(WebswingSecurityConfig config, ClassLoader cl, Object parent) throws Exception {
			if (config.getClassPath() != null && config.getClassPath().size() > 0) {
				//need to create temporary classloader with configured classpath
				//1.resolve base dir for classpath
				String home = null;
				if (parent != null && parent.getClass().getDeclaredMethod("getHomeDir") != null) {
					try {
						Method m = parent.getClass().getDeclaredMethod("getHomeDir");
						home = (String) m.invoke(parent);
					} catch (Exception e) {
						log.error("Failed to get HomeDir from parent config", e);
					}
				} else {
					//if master
					home = ".";
				}
				File f = CommonUtil.resolveFile(".", home, null);
				if (f != null) {
					home = f.getAbsolutePath();
				}
				//2. construct the class loader 
				String classPath = CommonUtil.getClassPath(config.getClassPath());
				URL[] urls = ClasspathUtil.populateClassPath(classPath, home);
				SecurityModuleClassLoader customCL = new SecurityModuleClassLoader(urls, cl);
				try {
					return super.getMetadata(config, customCL, parent);
				} finally {
					customCL.close();
				}
			} else {
				return super.getMetadata(config, cl, parent);
			}
		}

		@Override
		public Class<?> getExplicitType(WebswingSecurityConfig config, ClassLoader cl, String propertyName, Method readMethod, Object value) throws ClassNotFoundException {
			if (propertyName.equals("config")) {
				String securityModuleClassName = BuiltInModules.getSecurityModuleClassName(config.getModule());
				if (securityModuleClassName != null) {
					Class<?> moduleClass = cl.loadClass(securityModuleClassName);
					Class<?> configClass = getConfigTypeFromConstructor(moduleClass);
					if (configClass != null) {
						return configClass;
					}
				}
			}
			return super.getExplicitType(config, cl, propertyName, readMethod, value);
		}
	}
}
