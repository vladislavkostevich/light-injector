package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.annotations.LightName;
import by.kostevich.lightinjector.annotations.LightProperty;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.LightComponentConfiguration;
import by.kostevich.lightinjector.impl.bean.PropertyDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ComponentConfigurationCreator {

    public static LightComponentConfiguration withConstructorCreation(
            Class<?> componentClass, String componentName, Constructor<?> componentConstructor) {

        LightComponentConfiguration componentConfiguration =
                create(componentClass, componentName, componentConstructor.getParameters());

        componentConstructor.setAccessible(true);
        componentConfiguration.setComponentConstructor(componentConstructor);

        return componentConfiguration;
    }

    public static LightComponentConfiguration withMethodCreation(
            Class<?> componentClass, String componentName, Method componentCreationMethod) {

        LightComponentConfiguration componentConfiguration =
                create(componentClass, componentName, componentCreationMethod.getParameters());

        componentCreationMethod.setAccessible(true);
        componentConfiguration.setComponentCreationMethod(componentCreationMethod);

        return componentConfiguration;
    }

    private static LightComponentConfiguration create(
            Class<?> componentClass, String componentName, Parameter[] creationParameters) {

        LightComponentConfiguration configuration = new LightComponentConfiguration();
        configuration.setComponentClass(componentClass);
        configuration.setComponentName(componentName);
        configuration.setDependencyDefinitions(resolveDependencyDefinitions(creationParameters));
        configuration.setPropertyDefinitions(resolvePropertyDefinitions(creationParameters));
        configuration.setComponentSuperClasses(resolveComponentSuperClasses(componentClass));
        configuration.setComponentInterfaces(
                resolveComponentInterfaces(componentClass, configuration.getComponentSuperClasses()));
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
        return Arrays.stream(parameters)
                .filter(parameter -> parameter.getAnnotation(LightProperty.class) == null)
                .map(parameter -> {
                    Class<?> dependencyType = parameter.getType();
                    String dependencyName = null;
                    LightName lightNameAnnotation = parameter.getAnnotation(LightName.class);
                    if (lightNameAnnotation != null) {
                        dependencyName = lightNameAnnotation.value();
                    }
                    return new DependencyDefinition(dependencyType, dependencyName);
                }).collect(Collectors.toList());
    }

    private static List<PropertyDefinition> resolvePropertyDefinitions(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .filter(parameter -> parameter.getAnnotation(LightProperty.class) != null)
                .map(parameter -> {
                    Class<?> propertyType = parameter.getType();
                    LightProperty lightPropertyAnnotation = parameter.getAnnotation(LightProperty.class);
                    String propertyName = lightPropertyAnnotation.value();
                    String propertyDefaultValue = lightPropertyAnnotation.defaultValue();
                    return new PropertyDefinition(propertyName, propertyType, propertyDefaultValue);
                }).collect(Collectors.toList());
    }
}
