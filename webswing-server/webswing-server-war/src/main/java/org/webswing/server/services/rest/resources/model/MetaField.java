package org.webswing.server.services.rest.resources.model;

import java.util.ArrayList;
import java.util.List;
import org.webswing.server.services.rest.resources.model.VariableSetName;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public class MetaField   {
  
  private  String name;

public enum TabEnum {

    GENERAL(String.valueOf("General")), JAVA(String.valueOf("Java")), EXTENSIONS(String.valueOf("Extensions")), SERVER(String.valueOf("Server")), SWING(String.valueOf("Swing")), SESSION(String.valueOf("Session")), FEATURESGS(String.valueOf("FeaturesgS"));


    private String value;

    TabEnum (String v) {
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
    public static TabEnum fromValue(String value) {
        for (TabEnum b : TabEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

  private  TabEnum tab;
  private  String label;
  private  String description;
  private  Boolean discriminator;
  private  VariableSetName variables;
  private  List<String> presets = new ArrayList<String>();

public enum TypeEnum {

    STRING(String.valueOf("String")), NUMBER(String.valueOf("Number")), BOOLEAN(String.valueOf("Boolean")), OBJECT(String.valueOf("Object")), STRINGLIST(String.valueOf("StringList")), STRINGMAP(String.valueOf("StringMap")), OBJECTLIST(String.valueOf("ObjectList")), OBJECTLISTASTABLE(String.valueOf("ObjectListAsTable")), OBJECTMAP(String.valueOf("ObjectMap")), GENERIC(String.valueOf("Generic"));


    private String value;

    TypeEnum (String v) {
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
    public static TypeEnum fromValue(String value) {
        for (TypeEnum b : TypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

  private  TypeEnum type;
  private  List<MetaField> tableColumns = new ArrayList<MetaField>();
  private  Object value;

  /**
   **/
  public MetaField name(String name) {
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
  public MetaField tab(TabEnum tab) {
    this.tab = tab;
    return this;
  }

  
  @JsonProperty("tab")
  public TabEnum getTab() {
    return tab;
  }
  public void setTab(TabEnum tab) {
    this.tab = tab;
  }

  /**
   **/
  public MetaField label(String label) {
    this.label = label;
    return this;
  }

  
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   **/
  public MetaField description(String description) {
    this.description = description;
    return this;
  }

  
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   **/
  public MetaField discriminator(Boolean discriminator) {
    this.discriminator = discriminator;
    return this;
  }

  
  @JsonProperty("discriminator")
  public Boolean getDiscriminator() {
    return discriminator;
  }
  public void setDiscriminator(Boolean discriminator) {
    this.discriminator = discriminator;
  }

  /**
   **/
  public MetaField variables(VariableSetName variables) {
    this.variables = variables;
    return this;
  }

  
  @JsonProperty("variables")
  public VariableSetName getVariables() {
    return variables;
  }
  public void setVariables(VariableSetName variables) {
    this.variables = variables;
  }

  /**
   **/
  public MetaField presets(List<String> presets) {
    this.presets = presets;
    return this;
  }

  
  @JsonProperty("presets")
  public List<String> getPresets() {
    return presets;
  }
  public void setPresets(List<String> presets) {
    this.presets = presets;
  }

  /**
   **/
  public MetaField type(TypeEnum type) {
    this.type = type;
    return this;
  }

  
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }
  public void setType(TypeEnum type) {
    this.type = type;
  }

  /**
   * Self-embedded MetaField
   **/
  public MetaField tableColumns(List<MetaField> tableColumns) {
    this.tableColumns = tableColumns;
    return this;
  }

  
  @JsonProperty("tableColumns")
  public List<MetaField> getTableColumns() {
    return tableColumns;
  }
  public void setTableColumns(List<MetaField> tableColumns) {
    this.tableColumns = tableColumns;
  }

  /**
   **/
  public MetaField value(Object value) {
    this.value = value;
    return this;
  }

  
  @JsonProperty("value")
  public Object getValue() {
    return value;
  }
  public void setValue(Object value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetaField metaField = (MetaField) o;
    return Objects.equals(this.name, metaField.name) &&
        Objects.equals(this.tab, metaField.tab) &&
        Objects.equals(this.label, metaField.label) &&
        Objects.equals(this.description, metaField.description) &&
        Objects.equals(this.discriminator, metaField.discriminator) &&
        Objects.equals(this.variables, metaField.variables) &&
        Objects.equals(this.presets, metaField.presets) &&
        Objects.equals(this.type, metaField.type) &&
        Objects.equals(this.tableColumns, metaField.tableColumns) &&
        Objects.equals(this.value, metaField.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, tab, label, description, discriminator, variables, presets, type, tableColumns, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetaField {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    tab: ").append(toIndentedString(tab)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    discriminator: ").append(toIndentedString(discriminator)).append("\n");
    sb.append("    variables: ").append(toIndentedString(variables)).append("\n");
    sb.append("    presets: ").append(toIndentedString(presets)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    tableColumns: ").append(toIndentedString(tableColumns)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

