package by.kostevich.lightinjector.impl.bean;

import by.kostevich.lightinjector.annotations.LightName;
import by.kostevich.lightinjector.annotations.LightProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class LightComponentConfiguration {

    private Class<?> componentClass;
    private String componentName;

    private Constructor<?> componentConstructor;
    private Method componentCreationMethod;

    private List<DependencyDefinition> dependencyDefinitions;
    private List<PropertyDefinition> propertyDefinitions;

    private Set<Class<?>> componentSuperClasses;
    private Set<Class<?>> componentInterfaces;

    public LightComponentConfiguration() {
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public String getComponentName() {
        return componentName;
    }

    public Constructor<?> getComponentConstructor() {
        return componentConstructor;
    }

    public Method getComponentCreationMethod() {
        return componentCreationMethod;
    }

    public List<DependencyDefinition> getDependencyDefinitions() {
        return dependencyDefinitions;
    }

    public List<PropertyDefinition> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    public Set<Class<?>> getComponentSuperClasses() {
        return componentSuperClasses;
    }

    public Set<Class<?>> getComponentInterfaces() {
        return componentInterfaces;
    }

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setComponentConstructor(Constructor<?> componentConstructor) {
        this.componentConstructor = componentConstructor;
    }

    public void setComponentCreationMethod(Method componentCreationMethod) {
        this.componentCreationMethod = componentCreationMethod;
    }

    public void setDependencyDefinitions(List<DependencyDefinition> dependencyDefinitions) {
        this.dependencyDefinitions = dependencyDefinitions;
    }

    public void setPropertyDefinitions(List<PropertyDefinition> propertyDefinitions) {
        this.propertyDefinitions = propertyDefinitions;
    }

    public void setComponentSuperClasses(Set<Class<?>> componentSuperClasses) {
        this.componentSuperClasses = componentSuperClasses;
    }

    public void setComponentInterfaces(Set<Class<?>> componentInterfaces) {
        this.componentInterfaces = componentInterfaces;
    }

    public boolean isCreatedByConstructor() {
        return componentCreationMethod == null;
    }
}
