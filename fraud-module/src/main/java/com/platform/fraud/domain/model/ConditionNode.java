package com.platform.fraud.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConditionNode.Leaf.class, name = "LEAF"),
        @JsonSubTypes.Type(value = ConditionNode.Composite.class, name = "COMPOSITE")
})
public class ConditionNode {

    private String type; // LEAF or COMPOSITE

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Leaf extends ConditionNode {
        private String field;       // e.g., "amount", "geo", "velocity"
        private String operator;    // GT, LT, EQ, IN, CONTAINS, REGEX
        private Object value;       // can be Number, String, List, etc.
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Composite extends ConditionNode {
        private String operator;            // AND or OR
        private List<ConditionNode> children;
    }
}