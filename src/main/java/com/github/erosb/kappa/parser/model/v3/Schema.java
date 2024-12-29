package com.github.erosb.kappa.parser.model.v3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.erosb.jsonsKema.CompositeSchema;
import com.github.erosb.jsonsKema.ItemsSchema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.SchemaVisitor;
import com.github.erosb.jsonsKema.TypeSchema;
import com.github.erosb.kappa.core.exception.DecodeException;
import com.github.erosb.kappa.core.model.OAIContext;
import com.github.erosb.kappa.core.model.v3.OAI3SchemaKeywords;
import com.github.erosb.kappa.core.util.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Schema
  extends AbsExtendedRefOpenApiSchema<Schema> {
  // additionalProperties field is processed by specific getters/setters
  @JsonIgnore
  private Schema additionalProperties;
  @JsonIgnore
  private Boolean additionalPropertiesAllowed;
  @JsonProperty(OAI3SchemaKeywords.DEFAULT)
  private Object defaultValue;
  private String description;
  private Boolean deprecated;
  private Discriminator discriminator;
  @JsonProperty(OAI3SchemaKeywords.ENUM)
  private List<Object> enums;
  private Object example;
  private Boolean exclusiveMaximum;
  private Boolean exclusiveMinimum;
  private ExternalDocs externalDocs;
  private String format;
  @JsonProperty(OAI3SchemaKeywords.ITEMS)
  private Schema itemsSchema;
  private Number maximum;
  private Number minimum;
  private Integer maxItems;
  private Integer minItems;
  private Integer maxLength;
  private Integer minLength;
  private Integer maxProperties;
  private Integer minProperties;
  private Number multipleOf;
  @JsonProperty(OAI3SchemaKeywords.NOT)
  private Schema notSchema;
  private Boolean nullable;
  private String pattern;
  private Map<String, Schema> properties;
  @JsonProperty(OAI3SchemaKeywords.REQUIRED)
  private List<String> requiredFields;
  @JsonProperty(OAI3SchemaKeywords.ALLOF)
  private List<Schema> allOfSchemas;
  @JsonProperty(OAI3SchemaKeywords.ANYOF)
  private List<Schema> anyOfSchemas;
  @JsonProperty(OAI3SchemaKeywords.ONEOF)
  private List<Schema> oneOfSchemas;
  private Boolean readOnly;
  private Boolean writeOnly;
  private String type;
  private String title;
  private Boolean uniqueItems;
  private Xml xml;
  @JsonIgnore
  private com.github.erosb.jsonsKema.Schema skema;

  public Schema() {
  }

  private Schema(com.github.erosb.jsonsKema.Schema skema) {
    this.skema = skema;
  }

  @JsonIgnore
  public void setSkema(com.github.erosb.jsonsKema.Schema skema) {
    this.skema = skema;
  }

  // Title
  public String getTitle() {
    return title;
  }

  public Schema setTitle(String title) {
    this.title = title;
    return this;
  }

  // MultipleOf
  public Number getMultipleOf() {
    return multipleOf;
  }

  public Schema setMultipleOf(Number multipleOf) {
    this.multipleOf = multipleOf;
    return this;
  }

  // Maximum
  public Number getMaximum() {
    return maximum;
  }

  public Schema setMaximum(Number maximum) {
    this.maximum = maximum;
    return this;
  }

  // ExclusiveMaximum
  public Boolean getExclusiveMaximum() {
    return exclusiveMaximum;
  }

  public boolean isExclusiveMaximum() {
    return Boolean.TRUE.equals(exclusiveMaximum);
  }

  public Schema setExclusiveMaximum(Boolean exclusiveMaximum) {
    this.exclusiveMaximum = exclusiveMaximum;
    return this;
  }

  // Minimum
  public Number getMinimum() {
    return minimum;
  }

  public Schema setMinimum(Number minimum) {
    this.minimum = minimum;
    return this;
  }

  // ExclusiveMinimum
  public Boolean getExclusiveMinimum() {
    return exclusiveMinimum;
  }

  public boolean isExclusiveMinimum() {
    return Boolean.TRUE.equals(exclusiveMinimum);
  }

  public Schema setExclusiveMinimum(Boolean exclusiveMinimum) {
    this.exclusiveMinimum = exclusiveMinimum;
    return this;
  }

  // MaxLength
  public Integer getMaxLength() {
    return maxLength;
  }

  public Schema setMaxLength(Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  // MinLength
  public Integer getMinLength() {
    return minLength;
  }

  public Schema setMinLength(Integer minLength) {
    this.minLength = minLength;
    return this;
  }

  // Pattern
  public String getPattern() {
    return pattern;
  }

  public Schema setPattern(String pattern) {
    this.pattern = pattern;
    return this;
  }

  // MaxItems
  public Integer getMaxItems() {
    return maxItems;
  }

  public Schema setMaxItems(Integer maxItems) {
    this.maxItems = maxItems;
    return this;
  }

  // MinItems
  public Integer getMinItems() {
    return minItems;
  }

  public Schema setMinItems(Integer minItems) {
    this.minItems = minItems;
    return this;
  }

  // UniqueItems
  public Boolean getUniqueItems() {
    return uniqueItems;
  }

  public boolean isUniqueItems() {
    return Boolean.TRUE.equals(uniqueItems);
  }

  public Schema setUniqueItems(Boolean uniqueItems) {
    this.uniqueItems = uniqueItems;
    return this;
  }

  // MaxProperties
  public Integer getMaxProperties() {
    return maxProperties;
  }

  public Schema setMaxProperties(Integer maxProperties) {
    this.maxProperties = maxProperties;
    return this;
  }

  // MinProperties
  public Integer getMinProperties() {
    return minProperties;
  }

  public Schema setMinProperties(Integer minProperties) {
    this.minProperties = minProperties;
    return this;
  }

  // RequiredField
  public List<String> getRequiredFields() {
    return requiredFields;
  }

  public Schema setRequiredFields(List<String> requiredFields) {
    this.requiredFields = requiredFields;
    return this;
  }

  public boolean hasRequiredFields() {
    return requiredFields != null;
  }

  public Schema addRequiredField(String requiredField) {
    requiredFields = listAdd(requiredFields, requiredField);
    return this;
  }

  public Schema insertRequiredField(int index, String value) {
    requiredFields = listAdd(requiredFields, index, value);
    return this;
  }

  public Schema removeRequiredField(String value) {
    listRemove(requiredFields, value);
    return this;
  }

  // Enum
  public List<Object> getEnums() {
    return enums;
  }

  public Schema setEnums(List<Object> enums) {
    this.enums = enums;
    return this;
  }

  public boolean hasEnums() {
    return enums != null;
  }

  public Schema addEnum(Object value) {
    enums = listAdd(enums, value);
    return this;
  }

  public Schema insertEnum(int index, Object value) {
    enums = listAdd(enums, index, value);
    return this;
  }

  public Schema removeEnum(Object value) {
    listRemove(enums, value);
    return this;
  }

  // Type
  public String getType() {
    return type;
  }

  @JsonIgnore
  public String getSupposedType(OAIContext context) {
    getSkema(context);
    String result = skema.accept(new SchemaVisitor<String>() {
      @Override
      public String visitTypeSchema(@NotNull TypeSchema schema) {
        return schema.getType().getValue();
      }

      @Override
      public String visitItemsSchema(@NotNull ItemsSchema schema) {
        return "array";
      }

      @Override
      public String visitPropertySchema(@NotNull String property, @NotNull com.github.erosb.jsonsKema.Schema schema) {
        return "object";
      }
    });
    return result;

    //    // Ensure we're not in a $ref schema
    //    final Schema schema = getFlatSchema(context);
    //    assert schema != null;
    //
    //    if (schema.type != null) {
    //      return schema.type;
    //    }
    //
    //    // Deduce type from other properties
    //    if (schema.getProperties() != null) {
    //      return OAI3SchemaKeywords.TYPE_OBJECT;
    //    } else if (schema.getItemsSchema() != null) {
    //      return OAI3SchemaKeywords.TYPE_ARRAY;
    //    } else if (schema.getFormat() != null) {
    //      // Deduce type from format
    //      switch (schema.getFormat()) {
    //        case OAI3SchemaKeywords.FORMAT_INT32:
    //        case OAI3SchemaKeywords.FORMAT_INT64:
    //          return OAI3SchemaKeywords.TYPE_INTEGER;
    //        case OAI3SchemaKeywords.FORMAT_FLOAT:
    //        case OAI3SchemaKeywords.FORMAT_DOUBLE:
    //          return OAI3SchemaKeywords.TYPE_NUMBER;
    //        default:
    //          return OAI3SchemaKeywords.TYPE_STRING;
    //      }
    //    }
    //
    //    return null;
  }

  public com.github.erosb.jsonsKema.Schema getSkema(OAIContext context) {
    if (skema == null) {
      try {
        JsonNode rawJson = TreeUtil.json.convertValue(this, JsonNode.class);
        if (context != null && rawJson instanceof ObjectNode) {
          ObjectNode obj = (ObjectNode) rawJson;
          obj.set("components", context.getBaseDocument().get("components"));
        }
        SchemaLoader loader = context == null
          ? new SchemaLoader(rawJson.toPrettyString())
          : new SchemaLoader(rawJson.toPrettyString(), context.getBaseUrl().toURI());
        skema = loader.load();
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
    return skema;
  }

  public Schema setType(String type) {
    this.type = type;
    return this;
  }

  // AllOfSchema
  public List<Schema> getAllOfSchemas() {
    return allOfSchemas;
  }

  public Schema setAllOfSchemas(List<Schema> value) {
    this.allOfSchemas = value;
    return this;
  }

  public boolean hasAllOfSchemas() {
    return allOfSchemas != null && !allOfSchemas.isEmpty();
  }

  public Schema addAllOfSchema(Schema value) {
    allOfSchemas = listAdd(allOfSchemas, value);
    return this;
  }

  public Schema insertAllOfSchema(int index, Schema value) {
    allOfSchemas = listAdd(allOfSchemas, index, value);
    return this;
  }

  public Schema removeAllOfSchema(Schema value) {
    listRemove(allOfSchemas, value);
    return this;
  }

  // OneOfSchema
  public List<Schema> getOneOfSchemas() {
    return oneOfSchemas;
  }

  public Schema setOneOfSchemas(List<Schema> oneOfSchemas) {
    this.oneOfSchemas = oneOfSchemas;
    return this;
  }

  public boolean hasOneOfSchemas() {
    return oneOfSchemas != null && !oneOfSchemas.isEmpty();
  }

  public Schema addOneOfSchema(Schema value) {
    oneOfSchemas = listAdd(oneOfSchemas, value);
    return this;
  }

  public Schema insertOneOfSchema(int index, Schema value) {
    oneOfSchemas = listAdd(oneOfSchemas, index, value);
    return this;
  }

  public Schema removeOneOfSchema(Schema value) {
    listRemove(oneOfSchemas, value);
    return this;
  }

  // AnyOfSchema
  public List<Schema> getAnyOfSchemas() {
    return anyOfSchemas;
  }

  public Schema setAnyOfSchemas(List<Schema> anyOfSchemas) {
    this.anyOfSchemas = anyOfSchemas;
    return this;
  }

  public boolean hasAnyOfSchemas() {
    return anyOfSchemas != null && !anyOfSchemas.isEmpty();
  }

  public Schema addAnyOfSchema(Schema value) {
    anyOfSchemas = listAdd(anyOfSchemas, value);
    return this;
  }

  public Schema insertAnyOfSchema(int index, Schema anyOfSchema) {
    anyOfSchemas = listAdd(anyOfSchemas, index, anyOfSchema);
    return this;
  }

  public Schema removeAnyOfSchema(Schema value) {
    listRemove(anyOfSchemas, value);
    return this;
  }

  // NotSchema
  public Schema getNotSchema() {
    return notSchema;
  }

  public Schema setNotSchema(Schema notSchema) {
    this.notSchema = notSchema;
    return this;
  }

  // ItemsSchema
  public Schema getItemsSchema() {
    if (skema != null) {
      return skema.accept(new SchemaVisitor<Schema>() {
        @Override
        public Schema visitItemsSchema(@NotNull ItemsSchema schema) {
          return new Schema(schema.getItemsSchema());
        }
      });
    }
    return itemsSchema;
  }

  public Schema setItemsSchema(Schema itemsSchema) {
    this.itemsSchema = itemsSchema;
    return this;
  }

  // Property
  public Map<String, Schema> getProperties() {
    if (skema != null) {
      return skema.accept(new SchemaVisitor<Map<String, Schema>>() {
        @Override
        public Map<String, Schema> visitCompositeSchema(@NotNull CompositeSchema schema) {
          Map<String, Schema> rval = new HashMap<>(schema.getPropertySchemas().size());
          for (Map.Entry<String, com.github.erosb.jsonsKema.Schema> entry : schema.getPropertySchemas().entrySet()) {
            Schema mapped = new Schema(entry.getValue());
            rval.put(entry.getKey(), mapped);
          }
          return rval.isEmpty() ? super.visitCompositeSchema(schema) : rval;
        }
      });
    }
    return properties;
  }

  public Schema setProperties(Map<String, Schema> properties) {
    this.properties = properties;
    return this;
  }

  public boolean hasProperty(String name) {
    return mapHas(properties, name) || skema.accept(new SchemaVisitor<Boolean>() {
      @Override
      public Boolean visitPropertySchema(@NotNull String property, @NotNull com.github.erosb.jsonsKema.Schema schema) {
        return property.equals(name) ? true : null;
      }
    }) == Boolean.TRUE;
  }

  public Schema getProperty(String name) {
    return getProperties().get(name);
  }

  public Schema setProperty(String name, Schema property) {
    if (properties == null) {
      properties = new HashMap<>();
    }
    properties.put(name, property);
    return this;
  }

  public Schema removeProperty(String name) {
    mapRemove(properties, name);
    return this;
  }

  // AdditionalProperties
  public Schema getAdditionalProperties() {
    return additionalProperties;
  }

  public Schema setAdditionalProperties(Schema additionalProperties) {
    this.additionalProperties = additionalProperties;
    additionalPropertiesAllowed = (additionalProperties == null);
    return this;
  }

  @JsonProperty(value = OAI3SchemaKeywords.ADDITIONALPROPERTIES, access = JsonProperty.Access.WRITE_ONLY)
  private void setMappedAdditionalProperties(JsonNode additionalProperties)
    throws JsonProcessingException {
    if (additionalProperties.isBoolean()) {
      setAdditionalPropertiesAllowed(additionalProperties.booleanValue());
    } else if (additionalProperties.isObject()) {
      setAdditionalProperties(TreeUtil.json.treeToValue(additionalProperties, Schema.class));
    }
  }

  @JsonProperty(value = OAI3SchemaKeywords.ADDITIONALPROPERTIES, access = JsonProperty.Access.READ_ONLY)
  private Object getMappedAdditionalProperties() {
    if (hasAdditionalProperties()) {
      return getAdditionalProperties();
    } else if (getAdditionalPropertiesAllowed() != null) {
      return getAdditionalPropertiesAllowed();
    }

    return null;
  }

  public boolean hasAdditionalProperties() {
    return additionalProperties != null;
  }

  public Boolean getAdditionalPropertiesAllowed() {
    return additionalPropertiesAllowed;
  }

  public boolean isAdditionalPropertiesAllowed() {
    return additionalPropertiesAllowed == null || Boolean.TRUE.equals(additionalPropertiesAllowed);
  }

  public Schema setAdditionalPropertiesAllowed(Boolean additionalPropertiesAllowed) {
    this.additionalPropertiesAllowed = additionalPropertiesAllowed;
    additionalProperties = null;

    return this;
  }

  // Description
  public String getDescription() {
    return description;
  }

  public Schema setDescription(String description) {
    this.description = description;
    return this;
  }

  // Format
  public String getFormat() {
    return format;
  }

  public Schema setFormat(String format) {
    this.format = format;
    return this;
  }

  // Default
  public Object getDefault() {
    return defaultValue;
  }

  public Schema setDefault(Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  // Nullable
  public Boolean getNullable() {
    return nullable;
  }

  public boolean isNullable() {
    return Boolean.TRUE.equals(nullable);
  }

  public Schema setNullable(Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  // Discriminator
  public Discriminator getDiscriminator() {
    return discriminator;
  }

  public Schema setDiscriminator(Discriminator discriminator) {
    this.discriminator = discriminator;
    return this;
  }

  // ReadOnly
  public Boolean getReadOnly() {
    return readOnly;
  }

  public boolean isReadOnly() {
    return Boolean.TRUE.equals(readOnly);
  }

  public Schema setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  // WriteOnly
  public Boolean getWriteOnly() {
    return writeOnly;
  }

  public boolean isWriteOnly() {
    return Boolean.TRUE.equals(writeOnly);
  }

  public Schema setWriteOnly(Boolean writeOnly) {
    this.writeOnly = writeOnly;
    return this;
  }

  // Xml
  public Xml getXml() {
    return xml;
  }

  public Schema setXml(Xml xml) {
    this.xml = xml;
    return this;
  }

  // ExternalDocs
  public ExternalDocs getExternalDocs() {
    return externalDocs;
  }

  public Schema setExternalDocs(ExternalDocs externalDocs) {
    this.externalDocs = externalDocs;
    return this;
  }

  // Example
  public Object getExample() {
    return example;
  }

  public Schema setExample(Object example) {
    this.example = example;
    return this;
  }

  // Deprecated
  public Boolean getDeprecated() {
    return deprecated;
  }

  public boolean isDeprecated() {
    return Boolean.TRUE.equals(deprecated);
  }

  public Schema setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  @Override
  public Schema copy() {
    Schema copy = new Schema(skema);

    if (isRef()) {
      copy.setRef(getRef());
      copy.setCanonicalRef(getCanonicalRef());
    } else {
      copy.setTitle(getTitle());
      copy.setMultipleOf(getMultipleOf());
      copy.setMaximum(getMaximum());
      copy.setExclusiveMaximum(getExclusiveMaximum());
      copy.setMinimum(getMinimum());
      copy.setExclusiveMinimum(getExclusiveMinimum());
      copy.setMaxLength(getMaxLength());
      copy.setMinLength(getMinLength());
      copy.setPattern(getPattern());
      copy.setMaxItems(getMaxItems());
      copy.setMinItems(getMinItems());
      copy.setUniqueItems(getUniqueItems());
      copy.setMaxProperties(getMaxProperties());
      copy.setMinProperties(getMinProperties());
      copy.setRequiredFields(copySimpleList(getRequiredFields()));
      copy.setEnums(copySimpleList(getEnums()));
      copy.setType(getType());

      copy.setAllOfSchemas(copyList(getAllOfSchemas()));
      copy.setOneOfSchemas(copyList(getOneOfSchemas()));
      copy.setAnyOfSchemas(copyList(getAnyOfSchemas()));

      copy.setNotSchema(copyField(getNotSchema()));
      copy.setItemsSchema(copyField(getItemsSchema()));
      copy.setProperties(copyMap(getProperties()));

      if (hasAdditionalProperties()) {
        copy.setAdditionalProperties(copyField(getAdditionalProperties()));
      } else if (getAdditionalPropertiesAllowed() != null) {
        copy.setAdditionalPropertiesAllowed(getAdditionalPropertiesAllowed());
      }

      copy.setDescription(getDescription());
      copy.setFormat(getFormat());
      copy.setDefault(getDefault());
      copy.setNullable(getNullable());
      copy.setDiscriminator(copyField(getDiscriminator()));
      copy.setReadOnly(getReadOnly());
      copy.setWriteOnly(getWriteOnly());
      copy.setXml(copyField(getXml()));
      copy.setExternalDocs(copyField(getExternalDocs()));
      copy.setExample(getExample());
      copy.setDeprecated(getDeprecated());
      copy.setExtensions(copySimpleMap(getExtensions()));
    }

    return copy;
  }
}
