package by.kostevich.lightinjector.bean;

import by.kostevich.lightinjector.annotations.LightName;

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
    private Set<Class<?>> componentSuperClasses;
    private Set<Class<?>> componentInterfaces;

    private LightComponentConfiguration() {
    }

    public static LightComponentConfiguration withConstructorCreation(
            Class<?> componentClass, String componentName, Constructor<?> componentConstructor) {

        LightComponentConfiguration configuration = new LightComponentConfiguration();
        configuration.componentClass = componentClass;
        configuration.componentName = componentName;
        configuration.componentConstructor = componentConstructor;
        configuration.dependencyDefinitions = resolveDependencyDefinitions(componentConstructor.getParameters());
        configuration.componentSuperClasses = resolveComponentSuperClasses(componentClass);
        configuration.componentInterfaces =
                resolveComponentInterfaces(componentClass, configuration.componentSuperClasses);

        configuration.componentConstructor.setAccessible(true);
        return configuration;
    }

    public static LightComponentConfiguration withMethodCreation(
            Class<?> componentClass, String componentName, Method componentCreationMethod) {

        LightComponentConfiguration configuration = new LightComponentConfiguration();
        configuration.componentClass = componentClass;
        configuration.componentName = componentName;
        configuration.componentCreationMethod = componentCreationMethod;
        configuration.dependencyDefinitions = resolveDependencyDefinitions(componentCreationMethod.getParameters());
        configuration.componentSuperClasses = resolveComponentSuperClasses(componentClass);
        configuration.componentInterfaces =
                resolveComponentInterfaces(componentClass, configuration.componentSuperClasses);

        configuration.componentCreationMethod.setAccessible(true);
        return configuration;
    }

    private static Set<Class<?>> resolveComponentSuperClasses(Class<?> componentClass) {
        Set<Class<?>> superClasses = new HashSet<>();

        if (componentClass.isInterface()) {
            return superClasses;
        }

        Class<?> currentClass = componentClass;
        while (!Object.class.equals(currentClass.getSuperclass())) {
            superClasses.add(currentClass.getSuperclass());
            currentClass = currentClass.getSuperclass();
        }

        return superClasses;
    }

    private static Set<Class<?>> resolveComponentInterfaces(Class<?> componentClass, Set<Class<?>> componentSuperClasses) {
        Set<Class<?>> componentInterfaces = new HashSet<>();

        componentInterfaces.addAll(asList(componentClass.getInterfaces()));
        componentSuperClasses.forEach(
                componentSuperClass -> componentInterfaces.addAll(asList(componentSuperClass.getInterfaces())));

        return componentInterfaces;
    }

    private static List<DependencyDefinition> resolveDependencyDefinitions(Parameter[] parameters) {
        return Arrays.stream(parameters).map(parameter -> {
            Class<?> dependencyType = parameter.getType();
            String dependencyName = null;
            LightName lightNameAnnotation = parameter.getAnnotation(LightName.class);
            if (lightNameAnnotation != null) {
                dependencyName = lightNameAnnotation.value();
            }
            return new DependencyDefinition(dependencyType, dependencyName);
        }).collect(Collectors.toList());
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

    public Set<Class<?>> getComponentSuperClasses() {
        return componentSuperClasses;
    }

    public Set<Class<?>> getComponentInterfaces() {
        return componentInterfaces;
    }

    public boolean isCreatedByConstructor() {
        return componentCreationMethod == null;
    }
}
