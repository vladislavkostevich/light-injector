package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.LightInjector;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.ComponentDefinition;
import by.kostevich.lightinjector.impl.bean.InjectContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LightInjectorFactory {

    public static LightInjector createInjector(LightInjectionModule module) {
        Set<ComponentDefinition> componentConfigurations =
                ModuleConfigurationReader.readComponentsConfigurations(module);

        InjectContext context = new InjectContext();
        context.setModule(module);
        context.setComponentDefinitions(componentConfigurations);

        componentConfigurations.forEach(componentConfiguration -> {
            if (context.getComponents().get(componentConfiguration.getComponentId()) == null) {
                createComponent(componentConfiguration, context);
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
            ComponentDefinition dependencyConfiguration =
                    ContextLookup.findComponentConfiguration(context, dependencyDefinition);

            Object dependency = context.getComponents().get(dependencyConfiguration.getComponentId());
            if (dependency == null) {
                dependency = createComponent(dependencyConfiguration, context);
            }
            dependencies.add(dependency);
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
