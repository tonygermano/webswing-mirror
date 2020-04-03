package org.webswing.server.services.rest.resources.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.webswing.server.services.rest.resources.model.InstanceManagerStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class ApplicationInfo   {
  
  private  String path;
  private  String url;
  private  String name;
  private  Integer runningInstances;
  private  Boolean enabled;
  private  String message;
  private  byte[] icon;
  private  InstanceManagerStatus status;
  private  Object config;
  private  Map<String, String> variables = new HashMap<String, String>();
  private  Integer connectedInstances;
  private  Integer maxRunningInstances;
  private  Integer finishedInstances;
  private  Map<String, Map<String, BigDecimal>> stats = new HashMap<String, Map<String, BigDecimal>>();
  private  Map<String, List<String>> warnings = new HashMap<String, List<String>>();

  /**
   **/
  public ApplicationInfo path(String path) {
    this.path = path;
    return this;
  }

  
  @JsonProperty("path")
  public String getPath() {
    return path;
  }
  public void setPath(String path) {
    this.path = path;
  }

  /**
   **/
  public ApplicationInfo url(String url) {
    this.url = url;
    return this;
  }

  
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   **/
  public ApplicationInfo name(String name) {
    this.name = name;
    return this;
  }

  
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   **/
  public ApplicationInfo runningInstances(Integer runningInstances) {
    this.runningInstances = runningInstances;
    return this;
  }

  
  @JsonProperty("runningInstances")
  public Integer getRunningInstances() {
    return runningInstances;
  }
  public void setRunningInstances(Integer runningInstances) {
    this.runningInstances = runningInstances;
  }

  /**
   **/
  public ApplicationInfo enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  
  @JsonProperty("enabled")
  public Boolean getEnabled() {
    return enabled;
  }
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  /**
   **/
  public ApplicationInfo message(String message) {
    this.message = message;
    return this;
  }

  
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   **/
  public ApplicationInfo icon(byte[] icon) {
    this.icon = icon;
    return this;
  }

  
  @JsonProperty("icon")
  public byte[] getIcon() {
    return icon;
  }
  public void setIcon(byte[] icon) {
    this.icon = icon;
  }

  /**
   **/
  public ApplicationInfo status(InstanceManagerStatus status) {
    this.status = status;
    return this;
  }

  
  @JsonProperty("status")
  public InstanceManagerStatus getStatus() {
    return status;
  }
  public void setStatus(InstanceManagerStatus status) {
    this.status = status;
  }

  /**
   **/
  public ApplicationInfo config(Object config) {
    this.config = config;
    return this;
  }

  
  @JsonProperty("config")
  public Object getConfig() {
    return config;
  }
  public void setConfig(Object config) {
    this.config = config;
  }

  /**
   **/
  public ApplicationInfo variables(Map<String, String> variables) {
    this.variables = variables;
    return this;
  }

  
  @JsonProperty("variables")
  public Map<String, String> getVariables() {
    return variables;
  }
  public void setVariables(Map<String, String> variables) {
    this.variables = variables;
  }

  /**
   **/
  public ApplicationInfo connectedInstances(Integer connectedInstances) {
    this.connectedInstances = connectedInstances;
    return this;
  }

  
  @JsonProperty("connectedInstances")
  public Integer getConnectedInstances() {
    return connectedInstances;
  }
  public void setConnectedInstances(Integer connectedInstances) {
    this.connectedInstances = connectedInstances;
  }

  /**
   **/
  public ApplicationInfo maxRunningInstances(Integer maxRunningInstances) {
    this.maxRunningInstances = maxRunningInstances;
    return this;
  }

  
  @JsonProperty("maxRunningInstances")
  public Integer getMaxRunningInstances() {
    return maxRunningInstances;
  }
  public void setMaxRunningInstances(Integer maxRunningInstances) {
    this.maxRunningInstances = maxRunningInstances;
  }

  /**
   **/
  public ApplicationInfo finishedInstances(Integer finishedInstances) {
    this.finishedInstances = finishedInstances;
    return this;
  }

  
  @JsonProperty("finishedInstances")
  public Integer getFinishedInstances() {
    return finishedInstances;
  }
  public void setFinishedInstances(Integer finishedInstances) {
    this.finishedInstances = finishedInstances;
  }

  /**
   **/
  public ApplicationInfo stats(Map<String, Map<String, BigDecimal>> stats) {
    this.stats = stats;
    return this;
  }

  
  @JsonProperty("stats")
  public Map<String, Map<String, BigDecimal>> getStats() {
    return stats;
  }
  public void setStats(Map<String, Map<String, BigDecimal>> stats) {
    this.stats = stats;
  }

  /**
   **/
  public ApplicationInfo warnings(Map<String, List<String>> warnings) {
    this.warnings = warnings;
    return this;
  }

  
  @JsonProperty("warnings")
  public Map<String, List<String>> getWarnings() {
    return warnings;
  }
  public void setWarnings(Map<String, List<String>> warnings) {
    this.warnings = warnings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationInfo applicationInfo = (ApplicationInfo) o;
    return Objects.equals(this.path, applicationInfo.path) &&
        Objects.equals(this.url, applicationInfo.url) &&
        Objects.equals(this.name, applicationInfo.name) &&
        Objects.equals(this.runningInstances, applicationInfo.runningInstances) &&
        Objects.equals(this.enabled, applicationInfo.enabled) &&
        Objects.equals(this.message, applicationInfo.message) &&
        Objects.equals(this.icon, applicationInfo.icon) &&
        Objects.equals(this.status, applicationInfo.status) &&
        Objects.equals(this.config, applicationInfo.config) &&
        Objects.equals(this.variables, applicationInfo.variables) &&
        Objects.equals(this.connectedInstances, applicationInfo.connectedInstances) &&
        Objects.equals(this.maxRunningInstances, applicationInfo.maxRunningInstances) &&
        Objects.equals(this.finishedInstances, applicationInfo.finishedInstances) &&
        Objects.equals(this.stats, applicationInfo.stats) &&
        Objects.equals(this.warnings, applicationInfo.warnings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, url, name, runningInstances, enabled, message, icon, status, config, variables, connectedInstances, maxRunningInstances, finishedInstances, stats, warnings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationInfo {\n");
    
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    runningInstances: ").append(toIndentedString(runningInstances)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    config: ").append(toIndentedString(config)).append("\n");
    sb.append("    variables: ").append(toIndentedString(variables)).append("\n");
    sb.append("    connectedInstances: ").append(toIndentedString(connectedInstances)).append("\n");
    sb.append("    maxRunningInstances: ").append(toIndentedString(maxRunningInstances)).append("\n");
    sb.append("    finishedInstances: ").append(toIndentedString(finishedInstances)).append("\n");
    sb.append("    stats: ").append(toIndentedString(stats)).append("\n");
    sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
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

