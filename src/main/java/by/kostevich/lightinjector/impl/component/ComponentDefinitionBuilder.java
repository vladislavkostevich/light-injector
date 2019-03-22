package by.kostevich.lightinjector.impl.component;

import by.kostevich.lightinjector.annotations.LightName;
import by.kostevich.lightinjector.annotations.LightProperty;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.ComponentDefinition;
import by.kostevich.lightinjector.impl.bean.UniqueComponentId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ComponentDefinitionBuilder {

    public static ComponentDefinition withConstructorCreation(
            Class<?> componentClass, String componentName, Constructor<?> componentConstructor) {

        ComponentDefinition componentConfiguration =
                create(componentClass, componentName, componentConstructor.getParameters());

        componentConstructor.setAccessible(true);
        componentConfiguration.setComponentConstructor(componentConstructor);

        return componentConfiguration;
    }

    public static ComponentDefinition withMethodCreation(
            Class<?> componentClass, String componentName, Method componentCreationMethod) {

        ComponentDefinition componentConfiguration =
                create(componentClass, componentName, componentCreationMethod.getParameters());

        componentCreationMethod.setAccessible(true);
        componentConfiguration.setComponentCreationMethod(componentCreationMethod);

        return componentConfiguration;
    }

    private static ComponentDefinition create(
            Class<?> componentClass, String componentName, Parameter[] creationParameters) {

        ComponentDefinition configuration = new ComponentDefinition();
        configuration.setComponentId(new UniqueComponentId(componentClass, componentName));
        configuration.setDependencyDefinitions(resolveDependencyDefinitions(creationParameters));
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
                .map(parameter -> {
                    Class<?> dependencyType = parameter.getType();
                    LightProperty lightPropertyAnnotation = parameter.getAnnotation(LightProperty.class);
                    if (lightPropertyAnnotation != null) {
                        String propertyName = lightPropertyAnnotation.value();
                        String propertyDefaultValue = lightPropertyAnnotation.defaultValue();
                        return new DependencyDefinition(dependencyType, propertyName, true, propertyDefaultValue);
                    }

                    String dependencyName = null;
                    LightName lightNameAnnotation = parameter.getAnnotation(LightName.class);
                    if (lightNameAnnotation != null) {
                        dependencyName = lightNameAnnotation.value();
                    }
                    return new DependencyDefinition(dependencyType, dependencyName, false, null);
                }).collect(Collectors.toList());
    }
}
