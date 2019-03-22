package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.LightInjector;
import by.kostevich.lightinjector.impl.bean.ComponentDefinition;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.InjectContext;
import by.kostevich.lightinjector.impl.component.ComponentLookup;
import by.kostevich.lightinjector.impl.properties.PropertiesLookup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LightInjectorFactory {

    public static LightInjector createInjector(LightInjectionModule module) {
        InjectContext context = InjectContextCreator.createContext(module);

        Set<ComponentDefinition> componentDefinitions = context.getComponentDefinitions();

        componentDefinitions.forEach(componentDefinition -> {
            if (context.getComponents().get(componentDefinition.getComponentId()) == null) {
                createComponent(componentDefinition, context);
            }
        });

        try {
            Constructor<?> injectorConstructor = LightInjectorImpl.class.getDeclaredConstructors()[0];
            injectorConstructor.setAccessible(true);
            return (LightInjector) injectorConstructor.newInstance(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object createComponent(
            ComponentDefinition componentConfiguration,
            InjectContext context) {

        List<DependencyDefinition> dependencyDefinitions = componentConfiguration.getDependencyDefinitions();
        List<Object> dependencies = new ArrayList<>(dependencyDefinitions.size());

        dependencyDefinitions.forEach(dependencyDefinition -> {
            if (dependencyDefinition.isProperty()) {
                Object propertyValue = PropertiesLookup.findPropertyValue(context, dependencyDefinition);
                dependencies.add(propertyValue);
            } else {
                ComponentDefinition dependencyComponentDefinition =
                        ComponentLookup.findComponentDefinition(context, dependencyDefinition);
                Object dependencyComponent = context.getComponents().get(dependencyComponentDefinition.getComponentId());
                if (dependencyComponent == null) {
                    dependencyComponent = createComponent(dependencyComponentDefinition, context);
                }
                dependencies.add(dependencyComponent);
            }
        });

        try {
            Object createdComponent;
            if (componentConfiguration.isCreatedByConstructor()) {
                Constructor<?> creationConstructor = componentConfiguration.getComponentConstructor();
                createdComponent = creationConstructor.newInstance(dependencies.toArray());
            } else {
                Method creationJavaMethod = componentConfiguration.getComponentCreationMethod();
                createdComponent = creationJavaMethod.invoke(context.getModule(), dependencies.toArray());
            }
            context.addComponent(componentConfiguration.getComponentId(), createdComponent);
            return createdComponent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
