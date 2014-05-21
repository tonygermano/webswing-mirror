package org.webswing.server.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.webswing.model.server.SwingApplicationDescriptor;
import org.webswing.model.server.WebswingConfiguration;

public class TestAppDescriptorGenerator {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * @param args
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonGenerationException 
     */
    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
        List<SwingApplicationDescriptor> apps = new ArrayList<SwingApplicationDescriptor>();
        SwingApplicationDescriptor desc = new SwingApplicationDescriptor();
        apps.add(desc);
        desc.setName("SwingSet3");
        desc.setArgs("");
        desc.setVmArgs("-Xmx128m");
        desc.setHomeDir("F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target");
        desc.setMainClass("com.sun.swingset3.SwingSet3");
        desc.setClassPathEntries(Arrays.asList("f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\AppFramework.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\javaws.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\SwingSet3.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swing-worker.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\swingx.jar", "f:\\DATA\\Workspaces\\sources\\webswing-1.1\\swinglib\\TimingFramework.jar"));
        desc.setMaxClients(1);

        SwingApplicationDescriptor desc1 = new SwingApplicationDescriptor();
        desc1.setName("Printing");
        desc1.setArgs("");
        desc1.setVmArgs("");
        desc1.setHomeDir("f:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\print");
        desc1.setMainClass("Main");
        desc1.setClassPathEntries(Arrays.asList("print.jar"));
        desc1.setMaxClients(1);
        desc1.setAuthorization(true);
        desc1.setDebug(true);
        apps.add(desc1);

        WebswingConfiguration config = new WebswingConfiguration();
        config.setApplications(apps);
        mapper.defaultPrettyPrintingWriter().writeValue(new File("F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target\\webswing.config"), config);

        PrintWriter out = new PrintWriter("F:\\DATA\\Workspaces\\play\\WebSwingServer2.0.git\\webswing\\webswing-server\\target\\user.properties");
        out.write("user.admin = pwd,admin\nuser.user=pwd,Printing\nuser.guest=guest");
        out.close();
    }

}
