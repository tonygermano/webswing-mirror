package sk.viktor;

import javax.swing.UIManager;

import sk.viktor.ignored.special.JComboBoxUIWrapper;
import sk.viktor.server.SwingServer;


public class Main {
    public static final String comboboxUI="WebSwing.comboboxUiWrapper";
    public static void main(String[] args) throws Exception {
        UIManager.put(comboboxUI, JComboBoxUIWrapper.class.getCanonicalName());
        SwingServer.startServer();
    }
    
    
    
  
}
