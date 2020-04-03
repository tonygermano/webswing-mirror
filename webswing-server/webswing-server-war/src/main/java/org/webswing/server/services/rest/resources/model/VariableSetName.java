package org.webswing.server.services.rest.resources.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets VariableSetName
 */
public enum VariableSetName {
  
  BASIC("Basic"),
  
  SWINGINSTANCE("SwingInstance"),
  
  SWINGAPP("SwingApp");

  private String value;

  VariableSetName(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static VariableSetName fromValue(String value) {
    for (VariableSetName b : VariableSetName.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

