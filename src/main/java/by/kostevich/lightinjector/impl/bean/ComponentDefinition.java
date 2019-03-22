package by.kostevich.lightinjector.impl.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ComponentDefinition {

    private UniqueComponentId componentId;

    private Constructor<?> componentConstructor;
    private Method componentCreationMethod;

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

    public Method getComponentCreationMethod() {
        return componentCreationMethod;
    }

    public void setComponentCreationMethod(Method componentCreationMethod) {
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
        return Objects.equals(componentId, that.componentId) &&
                Objects.equals(componentConstructor, that.componentConstructor) &&
                Objects.equals(componentCreationMethod, that.componentCreationMethod) &&
                Objects.equals(dependencyDefinitions, that.dependencyDefinitions) &&
                Objects.equals(componentSuperClasses, that.componentSuperClasses) &&
                Objects.equals(componentInterfaces, that.componentInterfaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, componentConstructor, componentCreationMethod, dependencyDefinitions, componentSuperClasses, componentInterfaces);
    }
}
