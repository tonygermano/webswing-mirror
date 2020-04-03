package org.webswing.server.services.rest.resources.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.webswing.server.services.rest.resources.model.MetaField;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class MetaObject   {
  
  private  String message;
  private  List<MetaField> fields = new ArrayList<MetaField>();
  private  Map<String, Object> data = new HashMap<String, Object>();

  /**
   **/
  public MetaObject message(String message) {
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
  public MetaObject fields(List<MetaField> fields) {
    this.fields = fields;
    return this;
  }

  
  @JsonProperty("fields")
  public List<MetaField> getFields() {
    return fields;
  }
  public void setFields(List<MetaField> fields) {
    this.fields = fields;
  }

  /**
   **/
  public MetaObject data(Map<String, Object> data) {
    this.data = data;
    return this;
  }

  
  @JsonProperty("data")
  public Map<String, Object> getData() {
    return data;
  }
  public void setData(Map<String, Object> data) {
    this.data = data;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetaObject metaObject = (MetaObject) o;
    return Objects.equals(this.message, metaObject.message) &&
        Objects.equals(this.fields, metaObject.fields) &&
        Objects.equals(this.data, metaObject.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, fields, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetaObject {\n");
    
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    fields: ").append(toIndentedString(fields)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

