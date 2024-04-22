package se.kth.breaking_changes;

import japicmp.model.JApiBehavior;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtReference;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a method-level breaking change
 * Result from the comparison of two versions of an API
  */
@lombok.Getter
@lombok.Setter
public class ApiChange {

    private String Element;

    private String category;

    private String description;

    private String name;

    private String newLongName;

    private JApiChangeStatus changeType;

    private JApiCompatibilityChange compatibilityChange;

    private AbstractApiChange reference;

    Set<ApiChange> newVariants;

    String instruction;

    public ApiChange(String category, String name) {
        this.category = category;
        this.name = name;
    }

    public ApiChange(String category, String name, String newLongName, JApiChangeStatus changeType, ApiMetadata newVersion, ApiMetadata oldVersion, JApiBehavior behavior, String instruction) {
        this.category = category;
        this.name = name;
        this.newLongName = newLongName;
        this.changeType = changeType;
        this.instruction = instruction;
    }

    public ApiChange() {
    }

    @Override
    public String toString() {
        return "ApiChange{" +
                "category='" + category.toString() + '\'' +
                ", name='" + name + '\'' +
                ", newLongName='" + newLongName + '\'' +
                ", changeType=" + changeType.toString() +
                ", instruction='" + instruction + '\'' +
                ", compatibilityChange=" + compatibilityChange.getType() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiChange apiChange = (ApiChange) o;
        return category.equals(apiChange.category) &&
                name.equals(apiChange.name)
                && apiChange.getReference().getFullQualifiedName().equals(this.getReference().getFullQualifiedName())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, name);
    }
}
