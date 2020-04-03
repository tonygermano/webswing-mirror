package org.webswing.server.services.rest.resources.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class LogRequest   {
  
  private  Boolean backwards;
  private  Long offset;
  private  Long max;
  private  String instanceId;

  /**
   **/
  public LogRequest backwards(Boolean backwards) {
    this.backwards = backwards;
    return this;
  }

  
  @JsonProperty("backwards")
  public Boolean getBackwards() {
    return backwards;
  }
  public void setBackwards(Boolean backwards) {
    this.backwards = backwards;
  }

  /**
   **/
  public LogRequest offset(Long offset) {
    this.offset = offset;
    return this;
  }

  
  @JsonProperty("offset")
  public Long getOffset() {
    return offset;
  }
  public void setOffset(Long offset) {
    this.offset = offset;
  }

  /**
   **/
  public LogRequest max(Long max) {
    this.max = max;
    return this;
  }

  
  @JsonProperty("max")
  public Long getMax() {
    return max;
  }
  public void setMax(Long max) {
    this.max = max;
  }

  /**
   **/
  public LogRequest instanceId(String instanceId) {
    this.instanceId = instanceId;
    return this;
  }

  
  @JsonProperty("instanceId")
  public String getInstanceId() {
    return instanceId;
  }
  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogRequest logRequest = (LogRequest) o;
    return Objects.equals(this.backwards, logRequest.backwards) &&
        Objects.equals(this.offset, logRequest.offset) &&
        Objects.equals(this.max, logRequest.max) &&
        Objects.equals(this.instanceId, logRequest.instanceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(backwards, offset, max, instanceId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogRequest {\n");
    
    sb.append("    backwards: ").append(toIndentedString(backwards)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
    sb.append("    instanceId: ").append(toIndentedString(instanceId)).append("\n");
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

