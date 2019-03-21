package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.LightInjector;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.LightComponentConfiguration;
import by.kostevich.lightinjector.impl.bean.LightInjectContext;
import by.kostevich.lightinjector.impl.bean.PropertyDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LightInjectorFactory {

    public static LightInjector createInjector(LightInjectionModule module) {
        Set<LightComponentConfiguration> componentConfigurations =
                ModuleConfigurationReader.readComponentsConfigurations(module);

        LightInjectContext context = new LightInjectContext();
        context.setModule(module);
        context.setComponentConfigurations(componentConfigurations);

        componentConfigurations.forEach(componentConfiguration -> {
            if (context.getComponents().get(componentConfiguration.getComponentClass()) == null) {
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
            LightComponentConfiguration componentConfiguration,
            LightInjectContext context) {

        List<DependencyDefinition> dependencyDefinitions = componentConfiguration.getDependencyDefinitions();
        List<Object> dependencies = new ArrayList<>(dependencyDefinitions.size());

        dependencyDefinitions.forEach(dependencyDefinition -> {
            LightComponentConfiguration dependencyConfiguration = context.findComponentConfiguration(dependencyDefinition);

            Object dependency = context.getComponents().get(dependencyConfiguration.getComponentClass());
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
            context.addComponent(componentConfiguration.getComponentClass(), createdComponent);
            return createdComponent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
