package org.webswing.server.services.rest.resources.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class SwingSession   {
  
  private  Long serialVersionUID;
  private  String id;
  private  String user;
  private  String userIp;
  private  String userOs;
  private  String userBrowser;
  private  String application;
  private  String applicationPath;
  private  Long startedAt;
  private  Long endedAt;
  private  Boolean connected;
  private  Boolean applet;
  private  Long disconnectedSince;
  private  Boolean recorded;
  private  String recordingFile;
  private  Map<String, Object> stats = new HashMap<String, Object>();
  private  Map<String, BigDecimal> metrics = new HashMap<String, BigDecimal>();

public enum StatusEnum {

    NOT_STARTED(String.valueOf("NOT_STARTED")), EXITING(String.valueOf("EXITING")), RUNNING(String.valueOf("RUNNING")), FORCE_KILLED(String.valueOf("FORCE_KILLED")), FINISHED(String.valueOf("FINISHED"));


    private String value;

    StatusEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
        for (StatusEnum b : StatusEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

  private  StatusEnum status;
  private  List<String> warnings = new ArrayList<String>();
  private  List<String> warningHistory = new ArrayList<String>();
  private  Map<String, String> threadDumps = new HashMap<String, String>();
  private  String applicationUrl;
  private  Boolean loggingEnabled;

  /**
   **/
  public SwingSession serialVersionUID(Long serialVersionUID) {
    this.serialVersionUID = serialVersionUID;
    return this;
  }

  
  @JsonProperty("serialVersionUID")
  public Long getSerialVersionUID() {
    return serialVersionUID;
  }
  public void setSerialVersionUID(Long serialVersionUID) {
    this.serialVersionUID = serialVersionUID;
  }

  /**
   **/
  public SwingSession id(String id) {
    this.id = id;
    return this;
  }

  
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   **/
  public SwingSession user(String user) {
    this.user = user;
    return this;
  }

  
  @JsonProperty("user")
  public String getUser() {
    return user;
  }
  public void setUser(String user) {
    this.user = user;
  }

  /**
   **/
  public SwingSession userIp(String userIp) {
    this.userIp = userIp;
    return this;
  }

  
  @JsonProperty("userIp")
  public String getUserIp() {
    return userIp;
  }
  public void setUserIp(String userIp) {
    this.userIp = userIp;
  }

  /**
   **/
  public SwingSession userOs(String userOs) {
    this.userOs = userOs;
    return this;
  }

  
  @JsonProperty("userOs")
  public String getUserOs() {
    return userOs;
  }
  public void setUserOs(String userOs) {
    this.userOs = userOs;
  }

  /**
   **/
  public SwingSession userBrowser(String userBrowser) {
    this.userBrowser = userBrowser;
    return this;
  }

  
  @JsonProperty("userBrowser")
  public String getUserBrowser() {
    return userBrowser;
  }
  public void setUserBrowser(String userBrowser) {
    this.userBrowser = userBrowser;
  }

  /**
   **/
  public SwingSession application(String application) {
    this.application = application;
    return this;
  }

  
  @JsonProperty("application")
  public String getApplication() {
    return application;
  }
  public void setApplication(String application) {
    this.application = application;
  }

  /**
   **/
  public SwingSession applicationPath(String applicationPath) {
    this.applicationPath = applicationPath;
    return this;
  }

  
  @JsonProperty("applicationPath")
  public String getApplicationPath() {
    return applicationPath;
  }
  public void setApplicationPath(String applicationPath) {
    this.applicationPath = applicationPath;
  }

  /**
   **/
  public SwingSession startedAt(Long startedAt) {
    this.startedAt = startedAt;
    return this;
  }

  
  @JsonProperty("startedAt")
  public Long getStartedAt() {
    return startedAt;
  }
  public void setStartedAt(Long startedAt) {
    this.startedAt = startedAt;
  }

  /**
   **/
  public SwingSession endedAt(Long endedAt) {
    this.endedAt = endedAt;
    return this;
  }

  
  @JsonProperty("endedAt")
  public Long getEndedAt() {
    return endedAt;
  }
  public void setEndedAt(Long endedAt) {
    this.endedAt = endedAt;
  }

  /**
   **/
  public SwingSession connected(Boolean connected) {
    this.connected = connected;
    return this;
  }

  
  @JsonProperty("connected")
  public Boolean getConnected() {
    return connected;
  }
  public void setConnected(Boolean connected) {
    this.connected = connected;
  }

  /**
   **/
  public SwingSession applet(Boolean applet) {
    this.applet = applet;
    return this;
  }

  
  @JsonProperty("applet")
  public Boolean getApplet() {
    return applet;
  }
  public void setApplet(Boolean applet) {
    this.applet = applet;
  }

  /**
   **/
  public SwingSession disconnectedSince(Long disconnectedSince) {
    this.disconnectedSince = disconnectedSince;
    return this;
  }

  
  @JsonProperty("disconnectedSince")
  public Long getDisconnectedSince() {
    return disconnectedSince;
  }
  public void setDisconnectedSince(Long disconnectedSince) {
    this.disconnectedSince = disconnectedSince;
  }

  /**
   **/
  public SwingSession recorded(Boolean recorded) {
    this.recorded = recorded;
    return this;
  }

  
  @JsonProperty("recorded")
  public Boolean getRecorded() {
    return recorded;
  }
  public void setRecorded(Boolean recorded) {
    this.recorded = recorded;
  }

  /**
   **/
  public SwingSession recordingFile(String recordingFile) {
    this.recordingFile = recordingFile;
    return this;
  }

  
  @JsonProperty("recordingFile")
  public String getRecordingFile() {
    return recordingFile;
  }
  public void setRecordingFile(String recordingFile) {
    this.recordingFile = recordingFile;
  }

  /**
   **/
  public SwingSession stats(Map<String, Object> stats) {
    this.stats = stats;
    return this;
  }

  
  @JsonProperty("stats")
  public Map<String, Object> getStats() {
    return stats;
  }
  public void setStats(Map<String, Object> stats) {
    this.stats = stats;
  }

  /**
   **/
  public SwingSession metrics(Map<String, BigDecimal> metrics) {
    this.metrics = metrics;
    return this;
  }

  
  @JsonProperty("metrics")
  public Map<String, BigDecimal> getMetrics() {
    return metrics;
  }
  public void setMetrics(Map<String, BigDecimal> metrics) {
    this.metrics = metrics;
  }

  /**
   **/
  public SwingSession status(StatusEnum status) {
    this.status = status;
    return this;
  }

  
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }
  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  /**
   **/
  public SwingSession warnings(List<String> warnings) {
    this.warnings = warnings;
    return this;
  }

  
  @JsonProperty("warnings")
  public List<String> getWarnings() {
    return warnings;
  }
  public void setWarnings(List<String> warnings) {
    this.warnings = warnings;
  }

  /**
   **/
  public SwingSession warningHistory(List<String> warningHistory) {
    this.warningHistory = warningHistory;
    return this;
  }

  
  @JsonProperty("warningHistory")
  public List<String> getWarningHistory() {
    return warningHistory;
  }
  public void setWarningHistory(List<String> warningHistory) {
    this.warningHistory = warningHistory;
  }

  /**
   **/
  public SwingSession threadDumps(Map<String, String> threadDumps) {
    this.threadDumps = threadDumps;
    return this;
  }

  
  @JsonProperty("threadDumps")
  public Map<String, String> getThreadDumps() {
    return threadDumps;
  }
  public void setThreadDumps(Map<String, String> threadDumps) {
    this.threadDumps = threadDumps;
  }

  /**
   **/
  public SwingSession applicationUrl(String applicationUrl) {
    this.applicationUrl = applicationUrl;
    return this;
  }

  
  @JsonProperty("applicationUrl")
  public String getApplicationUrl() {
    return applicationUrl;
  }
  public void setApplicationUrl(String applicationUrl) {
    this.applicationUrl = applicationUrl;
  }

  /**
   **/
  public SwingSession loggingEnabled(Boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
    return this;
  }

  
  @JsonProperty("loggingEnabled")
  public Boolean getLoggingEnabled() {
    return loggingEnabled;
  }
  public void setLoggingEnabled(Boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SwingSession swingSession = (SwingSession) o;
    return Objects.equals(this.serialVersionUID, swingSession.serialVersionUID) &&
        Objects.equals(this.id, swingSession.id) &&
        Objects.equals(this.user, swingSession.user) &&
        Objects.equals(this.userIp, swingSession.userIp) &&
        Objects.equals(this.userOs, swingSession.userOs) &&
        Objects.equals(this.userBrowser, swingSession.userBrowser) &&
        Objects.equals(this.application, swingSession.application) &&
        Objects.equals(this.applicationPath, swingSession.applicationPath) &&
        Objects.equals(this.startedAt, swingSession.startedAt) &&
        Objects.equals(this.endedAt, swingSession.endedAt) &&
        Objects.equals(this.connected, swingSession.connected) &&
        Objects.equals(this.applet, swingSession.applet) &&
        Objects.equals(this.disconnectedSince, swingSession.disconnectedSince) &&
        Objects.equals(this.recorded, swingSession.recorded) &&
        Objects.equals(this.recordingFile, swingSession.recordingFile) &&
        Objects.equals(this.stats, swingSession.stats) &&
        Objects.equals(this.metrics, swingSession.metrics) &&
        Objects.equals(this.status, swingSession.status) &&
        Objects.equals(this.warnings, swingSession.warnings) &&
        Objects.equals(this.warningHistory, swingSession.warningHistory) &&
        Objects.equals(this.threadDumps, swingSession.threadDumps) &&
        Objects.equals(this.applicationUrl, swingSession.applicationUrl) &&
        Objects.equals(this.loggingEnabled, swingSession.loggingEnabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serialVersionUID, id, user, userIp, userOs, userBrowser, application, applicationPath, startedAt, endedAt, connected, applet, disconnectedSince, recorded, recordingFile, stats, metrics, status, warnings, warningHistory, threadDumps, applicationUrl, loggingEnabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SwingSession {\n");
    
    sb.append("    serialVersionUID: ").append(toIndentedString(serialVersionUID)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    userIp: ").append(toIndentedString(userIp)).append("\n");
    sb.append("    userOs: ").append(toIndentedString(userOs)).append("\n");
    sb.append("    userBrowser: ").append(toIndentedString(userBrowser)).append("\n");
    sb.append("    application: ").append(toIndentedString(application)).append("\n");
    sb.append("    applicationPath: ").append(toIndentedString(applicationPath)).append("\n");
    sb.append("    startedAt: ").append(toIndentedString(startedAt)).append("\n");
    sb.append("    endedAt: ").append(toIndentedString(endedAt)).append("\n");
    sb.append("    connected: ").append(toIndentedString(connected)).append("\n");
    sb.append("    applet: ").append(toIndentedString(applet)).append("\n");
    sb.append("    disconnectedSince: ").append(toIndentedString(disconnectedSince)).append("\n");
    sb.append("    recorded: ").append(toIndentedString(recorded)).append("\n");
    sb.append("    recordingFile: ").append(toIndentedString(recordingFile)).append("\n");
    sb.append("    stats: ").append(toIndentedString(stats)).append("\n");
    sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
    sb.append("    warningHistory: ").append(toIndentedString(warningHistory)).append("\n");
    sb.append("    threadDumps: ").append(toIndentedString(threadDumps)).append("\n");
    sb.append("    applicationUrl: ").append(toIndentedString(applicationUrl)).append("\n");
    sb.append("    loggingEnabled: ").append(toIndentedString(loggingEnabled)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

