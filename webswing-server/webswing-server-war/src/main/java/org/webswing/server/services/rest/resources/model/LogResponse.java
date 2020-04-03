package org.webswing.server.services.rest.resources.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class LogResponse   {
  
  private  Long startOffset;
  private  Long endOffset;
  private  String log;

  /**
   **/
  public LogResponse startOffset(Long startOffset) {
    this.startOffset = startOffset;
    return this;
  }

  
  @JsonProperty("startOffset")
  public Long getStartOffset() {
    return startOffset;
  }
  public void setStartOffset(Long startOffset) {
    this.startOffset = startOffset;
  }

  /**
   **/
  public LogResponse endOffset(Long endOffset) {
    this.endOffset = endOffset;
    return this;
  }

  
  @JsonProperty("endOffset")
  public Long getEndOffset() {
    return endOffset;
  }
  public void setEndOffset(Long endOffset) {
    this.endOffset = endOffset;
  }

  /**
   **/
  public LogResponse log(String log) {
    this.log = log;
    return this;
  }

  
  @JsonProperty("log")
  public String getLog() {
    return log;
  }
  public void setLog(String log) {
    this.log = log;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogResponse logResponse = (LogResponse) o;
    return Objects.equals(this.startOffset, logResponse.startOffset) &&
        Objects.equals(this.endOffset, logResponse.endOffset) &&
        Objects.equals(this.log, logResponse.log);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startOffset, endOffset, log);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogResponse {\n");
    
    sb.append("    startOffset: ").append(toIndentedString(startOffset)).append("\n");
    sb.append("    endOffset: ").append(toIndentedString(endOffset)).append("\n");
    sb.append("    log: ").append(toIndentedString(log)).append("\n");
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

