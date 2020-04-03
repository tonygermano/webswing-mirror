package org.webswing.server.services.rest.resources.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class BasicApplicationInfo   {
  
  private  String path;
  private  String url;
  private  String name;
  private  Integer runningInstances;
  private  Boolean enabled;

  /**
   **/
  public BasicApplicationInfo path(String path) {
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
  public BasicApplicationInfo url(String url) {
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
  public BasicApplicationInfo name(String name) {
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
  public BasicApplicationInfo runningInstances(Integer runningInstances) {
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
  public BasicApplicationInfo enabled(Boolean enabled) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicApplicationInfo basicApplicationInfo = (BasicApplicationInfo) o;
    return Objects.equals(this.path, basicApplicationInfo.path) &&
        Objects.equals(this.url, basicApplicationInfo.url) &&
        Objects.equals(this.name, basicApplicationInfo.name) &&
        Objects.equals(this.runningInstances, basicApplicationInfo.runningInstances) &&
        Objects.equals(this.enabled, basicApplicationInfo.enabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, url, name, runningInstances, enabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicApplicationInfo {\n");
    
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    runningInstances: ").append(toIndentedString(runningInstances)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
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

