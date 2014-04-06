package org.webswing.server.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.webswing.server.model.SwingApplicationDescriptor;
import org.webswing.server.model.WebswingConfiguration;


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
        desc.setVmArgs("-Xmx128m");
        desc.setHomeDir("F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target");
        desc.setMainClass("com.sun.swingset3.SwingSet3");
desc.setClassPathEntries(Arrays.asList("f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\AppFramework.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\javaws.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\SwingSet3.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swing-worker.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swingx.jar"
        ,"f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\TimingFramework.jar"));   
        desc.setMaxClients(1);

        SwingApplicationDescriptor desc1=new SwingApplicationDescriptor();
        desc1.setArgs("");
        desc1.setVmArgs("");
        desc1.setHomeDir("f:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\print");
        desc1.setMainClass("Main");
        desc1.setClassPathEntries(Arrays.asList("print.jar"));
        desc1.setMaxClients(1);

        apps.put("Printing", desc1);
        
        
        WebswingConfiguration config= new WebswingConfiguration();
        config.setSwingDebugEnabled(false);
        config.setApplications(apps);
        mapper.defaultPrettyPrintingWriter().writeValue(new File("F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target\\webswing.config"),config);
        
        
        PrintWriter out = new PrintWriter("F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target\\user.properties");
        out.write("user.admin = pwd,admin\nuser.user=pwd,Printing");
        out.close();
    }

}
