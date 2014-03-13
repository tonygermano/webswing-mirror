package org.webswing.server.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.webswing.server.model.SwingApplicationDescriptor;


public class TestAppDescriptorGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();
    /**
     * @param args
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonGenerationException 
     */
    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
        Map<String,SwingApplicationDescriptor> apps= new HashMap<String, SwingApplicationDescriptor>();
        SwingApplicationDescriptor desc= new SwingApplicationDescriptor();
        apps.put("SwingSet3", desc);

        desc.setArgs("");
        desc.setVmArgs("");
        desc.setHomeDir("F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target");
        desc.setMainClass("com.sun.swingset3.SwingSet3");
desc.setClassPathEntries(Arrays.asList("f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\AppFramework.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\javaws.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\SwingSet3.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swing-worker.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swingx.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\TimingFramework.jar"));   

        mapper.defaultPrettyPrintingWriter().writeValue(new File("F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target\\webswing.config"),apps);
    }

}
