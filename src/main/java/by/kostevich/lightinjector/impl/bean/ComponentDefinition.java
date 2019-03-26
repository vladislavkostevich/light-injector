package by.kostevich.lightinjector.impl.bean;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ComponentDefinition {

    private UniqueComponentId componentId;

    private Constructor<?> componentConstructor;
    private ComponentCreationMethod componentCreationMethod;

    private List<DependencyDefinition> dependencyDefinitions;

    private Set<Class<?>> componentSuperClasses;
    private Set<Class<?>> componentInterfaces;

    public UniqueComponentId getComponentId() {
        return componentId;
    }

    public void setComponentId(UniqueComponentId componentId) {
        this.componentId = componentId;
    }

    public Constructor<?> getComponentConstructor() {
        return componentConstructor;
    }

    public void setComponentConstructor(Constructor<?> componentConstructor) {
        this.componentConstructor = componentConstructor;
    }

    public ComponentCreationMethod getComponentCreationMethod() {
        return componentCreationMethod;
    }

    public void setComponentCreationMethod(ComponentCreationMethod componentCreationMethod) {
        this.componentCreationMethod = componentCreationMethod;
    }

    public List<DependencyDefinition> getDependencyDefinitions() {
        return dependencyDefinitions;
    }

    public void setDependencyDefinitions(List<DependencyDefinition> dependencyDefinitions) {
        this.dependencyDefinitions = dependencyDefinitions;
    }

    public Set<Class<?>> getComponentSuperClasses() {
        return componentSuperClasses;
    }

    public void setComponentSuperClasses(Set<Class<?>> componentSuperClasses) {
        this.componentSuperClasses = componentSuperClasses;
    }

    public Set<Class<?>> getComponentInterfaces() {
        return componentInterfaces;
    }

    public void setComponentInterfaces(Set<Class<?>> componentInterfaces) {
        this.componentInterfaces = componentInterfaces;
    }

    public boolean isCreatedByConstructor() {
        return componentCreationMethod == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentDefinition that = (ComponentDefinition) o;
        return Objects.equals(componentId, that.componentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId);
    }
}
