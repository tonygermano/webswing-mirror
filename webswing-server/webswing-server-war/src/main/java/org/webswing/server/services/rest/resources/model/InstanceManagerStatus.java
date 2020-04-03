package org.webswing.server.services.rest.resources.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class InstanceManagerStatus   {
  

public enum StatusEnum {

    STARTING(String.valueOf("Starting")), RUNNING(String.valueOf("Running")), STOPPED(String.valueOf("Stopped")), STOPPING(String.valueOf("Stopping")), ERROR(String.valueOf("Error"));


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
  private  String error;
  private  String errorDetails;

  /**
   **/
  public InstanceManagerStatus status(StatusEnum status) {
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
  public InstanceManagerStatus error(String error) {
    this.error = error;
    return this;
  }

  
  @JsonProperty("error")
  public String getError() {
    return error;
  }
  public void setError(String error) {
    this.error = error;
  }

  /**
   **/
  public InstanceManagerStatus errorDetails(String errorDetails) {
    this.errorDetails = errorDetails;
    return this;
  }

  
  @JsonProperty("errorDetails")
  public String getErrorDetails() {
    return errorDetails;
  }
  public void setErrorDetails(String errorDetails) {
    this.errorDetails = errorDetails;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstanceManagerStatus instanceManagerStatus = (InstanceManagerStatus) o;
    return Objects.equals(this.status, instanceManagerStatus.status) &&
        Objects.equals(this.error, instanceManagerStatus.error) &&
        Objects.equals(this.errorDetails, instanceManagerStatus.errorDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, error, errorDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InstanceManagerStatus {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    errorDetails: ").append(toIndentedString(errorDetails)).append("\n");
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

