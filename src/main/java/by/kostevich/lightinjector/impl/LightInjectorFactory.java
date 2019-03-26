package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.LightInjector;
import by.kostevich.lightinjector.impl.bean.ComponentCreationMethod;
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

import static java.lang.String.format;

public class LightInjectorFactory {

    public static LightInjector createInjector(LightInjectionModule module) {
        long injectionContextCreationStartTime = System.currentTimeMillis();

        InjectContext context = InjectContextCreator.createContext(module);

        Set<ComponentDefinition> componentDefinitions = context.getComponentDefinitions();

        long componentsCreationStartTime = System.currentTimeMillis();
        componentDefinitions.forEach(componentDefinition -> {
            if (context.getComponents().get(componentDefinition.getComponentId()) == null) {
                createComponent(componentDefinition, context);
            }
        });
        System.out.println(format("[DEBUG] LightInject - All components creation completed in %s ms", System.currentTimeMillis() - componentsCreationStartTime));


        LightInjectorImpl lightInjector = context.getLightInjector();
        setContextToInjector(lightInjector, context);

        System.out.println(format("[DEBUG] LightInject - Overall context creation completed in %s ms", System.currentTimeMillis() - injectionContextCreationStartTime));

        return lightInjector;
    }

    private static Object createComponent(
            ComponentDefinition componentConfiguration,
            InjectContext context) {

        List<DependencyDefinition> dependencyDefinitions = componentConfiguration.getDependencyDefinitions();
        List<Object> dependencies = new ArrayList<>(dependencyDefinitions.size());

        dependencyDefinitions.forEach(dependencyDefinition -> {
            if (isInjector(dependencyDefinition)) {
                dependencies.add(context.getLightInjector());
                return;
            }

            if (dependencyDefinition.isProperty()) {
                Object propertyValue = PropertiesLookup.findPropertyValue(context, dependencyDefinition);
                dependencies.add(propertyValue);
                return;
            }

            ComponentDefinition dependencyComponentDefinition =
                    ComponentLookup.findComponentDefinition(context, dependencyDefinition);
            Object dependencyComponent = context.getComponents().get(dependencyComponentDefinition.getComponentId());
            if (dependencyComponent == null) {
                dependencyComponent = createComponent(dependencyComponentDefinition, context);
            }
            dependencies.add(dependencyComponent);
        });

        try {
            Object createdComponent;
            if (componentConfiguration.isCreatedByConstructor()) {
                Constructor<?> creationConstructor = componentConfiguration.getComponentConstructor();
                createdComponent = creationConstructor.newInstance(dependencies.toArray());
            } else {
                ComponentCreationMethod creationMethod = componentConfiguration.getComponentCreationMethod();
                createdComponent = creationMethod.getJavaMethod().invoke(creationMethod.getModule(), dependencies.toArray());
            }
            context.addComponent(componentConfiguration.getComponentId(), createdComponent);

            System.out.println(format(
                    "[DEBUG] LightInject - Component created [Class = %s, Name = %s]",
                    componentConfiguration.getComponentId().getComponentClass().getSimpleName(),
                    componentConfiguration.getComponentId().getComponentName()
            ));
            return createdComponent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setContextToInjector(LightInjectorImpl lightInjector, InjectContext context) {
        try {
            Method setContextMethod = lightInjector.getClass().getDeclaredMethod("setContext", InjectContext.class);
            setContextMethod.setAccessible(true);
            setContextMethod.invoke(lightInjector, context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isInjector(DependencyDefinition dependencyDefinition) {
        return dependencyDefinition.getDependencyClass().equals(LightInjector.class);
    }
}
