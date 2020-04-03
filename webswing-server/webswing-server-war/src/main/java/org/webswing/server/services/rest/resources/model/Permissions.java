package org.webswing.server.services.rest.resources.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Permissions map. * key - string * value - boolean 
 **/
public class Permissions   {
  
  private  Boolean dashboard;
  private  Boolean configView;
  private  Boolean configSwingEdit;
  private  Boolean sessions;
  private  Boolean configEdit = false;
  private  Boolean start = false;
  private  Boolean stop = false;
  private  Boolean remove = false;
  private  Boolean create = false;
  private  Boolean logsView = false;

  /**
   **/
  public Permissions dashboard(Boolean dashboard) {
    this.dashboard = dashboard;
    return this;
  }

  
  @JsonProperty("dashboard")
  public Boolean getDashboard() {
    return dashboard;
  }
  public void setDashboard(Boolean dashboard) {
    this.dashboard = dashboard;
  }

  /**
   **/
  public Permissions configView(Boolean configView) {
    this.configView = configView;
    return this;
  }

  
  @JsonProperty("configView")
  public Boolean getConfigView() {
    return configView;
  }
  public void setConfigView(Boolean configView) {
    this.configView = configView;
  }

  /**
   **/
  public Permissions configSwingEdit(Boolean configSwingEdit) {
    this.configSwingEdit = configSwingEdit;
    return this;
  }

  
  @JsonProperty("configSwingEdit")
  public Boolean getConfigSwingEdit() {
    return configSwingEdit;
  }
  public void setConfigSwingEdit(Boolean configSwingEdit) {
    this.configSwingEdit = configSwingEdit;
  }

  /**
   **/
  public Permissions sessions(Boolean sessions) {
    this.sessions = sessions;
    return this;
  }

  
  @JsonProperty("sessions")
  public Boolean getSessions() {
    return sessions;
  }
  public void setSessions(Boolean sessions) {
    this.sessions = sessions;
  }

  /**
   **/
  public Permissions configEdit(Boolean configEdit) {
    this.configEdit = configEdit;
    return this;
  }

  
  @JsonProperty("configEdit")
  public Boolean getConfigEdit() {
    return configEdit;
  }
  public void setConfigEdit(Boolean configEdit) {
    this.configEdit = configEdit;
  }

  /**
   **/
  public Permissions start(Boolean start) {
    this.start = start;
    return this;
  }

  
  @JsonProperty("start")
  public Boolean getStart() {
    return start;
  }
  public void setStart(Boolean start) {
    this.start = start;
  }

  /**
   **/
  public Permissions stop(Boolean stop) {
    this.stop = stop;
    return this;
  }

  
  @JsonProperty("stop")
  public Boolean getStop() {
    return stop;
  }
  public void setStop(Boolean stop) {
    this.stop = stop;
  }

  /**
   **/
  public Permissions remove(Boolean remove) {
    this.remove = remove;
    return this;
  }

  
  @JsonProperty("remove")
  public Boolean getRemove() {
    return remove;
  }
  public void setRemove(Boolean remove) {
    this.remove = remove;
  }

  /**
   **/
  public Permissions create(Boolean create) {
    this.create = create;
    return this;
  }

  
  @JsonProperty("create")
  public Boolean getCreate() {
    return create;
  }
  public void setCreate(Boolean create) {
    this.create = create;
  }

  /**
   **/
  public Permissions logsView(Boolean logsView) {
    this.logsView = logsView;
    return this;
  }

  
  @JsonProperty("logsView")
  public Boolean getLogsView() {
    return logsView;
  }
  public void setLogsView(Boolean logsView) {
    this.logsView = logsView;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Permissions permissions = (Permissions) o;
    return Objects.equals(this.dashboard, permissions.dashboard) &&
        Objects.equals(this.configView, permissions.configView) &&
        Objects.equals(this.configSwingEdit, permissions.configSwingEdit) &&
        Objects.equals(this.sessions, permissions.sessions) &&
        Objects.equals(this.configEdit, permissions.configEdit) &&
        Objects.equals(this.start, permissions.start) &&
        Objects.equals(this.stop, permissions.stop) &&
        Objects.equals(this.remove, permissions.remove) &&
        Objects.equals(this.create, permissions.create) &&
        Objects.equals(this.logsView, permissions.logsView);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dashboard, configView, configSwingEdit, sessions, configEdit, start, stop, remove, create, logsView);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Permissions {\n");
    
    sb.append("    dashboard: ").append(toIndentedString(dashboard)).append("\n");
    sb.append("    configView: ").append(toIndentedString(configView)).append("\n");
    sb.append("    configSwingEdit: ").append(toIndentedString(configSwingEdit)).append("\n");
    sb.append("    sessions: ").append(toIndentedString(sessions)).append("\n");
    sb.append("    configEdit: ").append(toIndentedString(configEdit)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    stop: ").append(toIndentedString(stop)).append("\n");
    sb.append("    remove: ").append(toIndentedString(remove)).append("\n");
    sb.append("    create: ").append(toIndentedString(create)).append("\n");
    sb.append("    logsView: ").append(toIndentedString(logsView)).append("\n");
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

