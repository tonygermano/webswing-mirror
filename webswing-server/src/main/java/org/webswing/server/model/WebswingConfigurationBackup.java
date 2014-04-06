package org.webswing.server.model;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class WebswingConfigurationBackup {

    private Map<Date, WebswingConfiguration> backupMap = new TreeMap<Date, WebswingConfiguration>();

    public Map<Date, WebswingConfiguration> getBackupMap() {
        return backupMap;
    }

    public void setBackupMap(Map<Date, WebswingConfiguration> backupMap) {
        this.backupMap = backupMap;
    }
}
