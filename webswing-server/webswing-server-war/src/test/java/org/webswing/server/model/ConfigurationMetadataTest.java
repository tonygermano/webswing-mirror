package org.webswing.server.model;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.webswing.server.common.model.Config;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.common.model.SwingConfig.SessionMode;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueBoolean;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueNumber;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueObject;
import org.webswing.server.common.model.meta.ConfigFieldDefaultValueString;
import org.webswing.server.common.model.meta.ConfigFieldEditorType.EditorType;
import org.webswing.server.common.model.meta.MetaField;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.util.ConfigUtil;
import org.webswing.server.services.security.api.BuiltInModules;
import org.webswing.server.services.security.extension.onetimeurl.OtpAccessConfig;

@SuppressWarnings("unchecked")
public class ConfigurationMetadataTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testConfigurationLoad() throws Exception {
		Map<String, Object> readValue = mapper.readValue(this.getClass().getClassLoader().getResourceAsStream("swingConfig1.json"), Map.class);
		SecuredPathConfig spc = ConfigUtil.instantiateConfig(readValue, SecuredPathConfig.class);
		SwingConfig c = spc.getSwingConfig();
		assertTrue(spc.getPath(), "/ss3".equals(spc.getPath()));
		assertTrue("${user.dir}/demo/SwingSet3/SwingSet3.jar".equals(c.getClassPathEntries().get(0)));
		assertTrue(c.getFontConfig().get("test1").equals("value1"));
		assertTrue(c.getMaxClients() == 2);
		assertTrue(c.getSessionMode().equals(SessionMode.CONTINUE_FOR_BROWSER));
		assertTrue(c.isAllowStealSession());
		assertTrue(c.getUploadMaxSize() == 5.0);
	}

	@Test
	public void testMetadataGenerator() throws Exception {
		Map<String, Object> readValue = mapper.readValue(this.getClass().getClassLoader().getResourceAsStream("swingConfig1.json"), Map.class);
		SecuredPathConfig c = ConfigUtil.instantiateConfig(readValue, SecuredPathConfig.class);
		MetaObject configMetadata = ConfigUtil.getConfigMetadata(c, this.getClass().getClassLoader());
		
		//@ConfigFieldEditorType(editor = EditorType.Object, className = "org.webswing.server.services.security.api.WebswingSecurityConfig")
		for (MetaField f : configMetadata.getFields()) {
			if (f.getName().equals("security")) {
				assertTrue(f.getType().equals(EditorType.Object));
				assertTrue(f.getValue() instanceof MetaObject);
			}
		}
		
		//@ConfigFieldOrder({ "path", "homeDir", "webFolder", "icon", "security", "swingConfig"})
		assertTrue(configMetadata.getFields().get(0).getName().equals("path"));
		assertTrue(configMetadata.getFields().get(1).getName().equals("homeDir"));
		assertTrue(configMetadata.getFields().get(2).getName().equals("webFolder"));
		assertTrue(configMetadata.getFields().get(4).getName().equals("security"));
		
	}

	@Test
	public void testDefaultValues() throws Exception {
		TestDefaultConfig c = ConfigUtil.instantiateConfig(null, TestDefaultConfig.class);
		assertTrue("defaultValue".equals(c.getString()));
		assertTrue(BuiltInModules.INHERITED.equals(c.getEnum()));
		assertTrue(c.getBool());
		assertTrue(c.getBoolean());
		assertTrue(1 == c.getI());
		assertTrue(1 == c.getD());
		assertTrue(1 == c.getF());
		assertTrue(1 == c.getInteger());
		assertTrue(1 == c.getDouble());
		assertTrue(1 == c.getFloat());
		assertTrue(c.getConfig() != null);
		assertTrue(c.getMap() != null);
		assertTrue(c.getObject() != null);
		assertTrue(c.getConfigNull() == null);
		assertTrue(c.getMapNull() == null);
		assertTrue(c.getObjectNull() == null);
	}
	
	@Test
	public void testDefaultValueGenerator() throws Exception {
		OtpAccessConfig c = ConfigUtil.instantiateConfig(null, OtpAccessConfig.class);
		assertTrue(c.getSecret()!=null);
	}
	

	@Test
	public void testNumberTypeGenerator() throws Exception {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("uploadMaxSize", new Integer(1));
		SwingConfig c = ConfigUtil.instantiateConfig(map, SwingConfig.class);
		assertTrue(c.getUploadMaxSize()==1);
	}
	

	public static interface TestDefaultConfig extends Config {
		@ConfigFieldDefaultValueString("defaultValue")
		String getString();

		@ConfigFieldDefaultValueString("INHERITED")
		BuiltInModules getEnum();

		@ConfigFieldDefaultValueBoolean(true)
		boolean getBool();

		@ConfigFieldDefaultValueBoolean(true)
		Boolean getBoolean();

		@ConfigFieldDefaultValueNumber(1)
		double getD();

		@ConfigFieldDefaultValueNumber(1)
		Double getDouble();

		@ConfigFieldDefaultValueNumber(1)
		int getI();

		@ConfigFieldDefaultValueNumber(1)
		Integer getInteger();

		@ConfigFieldDefaultValueNumber(1)
		float getF();

		@ConfigFieldDefaultValueNumber(1)
		Float getFloat();

		@ConfigFieldDefaultValueObject
		TestDefaultConfig getConfig();

		@ConfigFieldDefaultValueObject(HashMap.class)
		Map<String, Object> getMap();

		@ConfigFieldDefaultValueObject
		Object getObject();

		TestDefaultConfig getConfigNull();

		Map<String, Object> getMapNull();

		Object getObjectNull();
	}
}
