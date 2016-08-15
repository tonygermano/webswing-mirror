package org.webswing.server.model;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.webswing.server.common.model.SecuredPathConfig;
import org.webswing.server.common.model.SwingConfig;
import org.webswing.server.common.model.SwingConfig.SessionMode;
import org.webswing.server.common.model.meta.MetaObject;
import org.webswing.server.common.util.ConfigUtil;

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
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(c));
	}

	@Test
	public void testMetadataGenerator() throws Exception {
		Map<String, Object> readValue = mapper.readValue(this.getClass().getClassLoader().getResourceAsStream("swingConfig1.json"), Map.class);
		SwingConfig c = ConfigUtil.instantiateConfig(readValue, SwingConfig.class);
		MetaObject configMetadata = ConfigUtil.getConfigMetadata(c, this.getClass().getClassLoader());
		System.out.println(configMetadata);

	}
}
