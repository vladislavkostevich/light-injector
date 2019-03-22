package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.annotations.LightComponent;
import by.kostevich.lightinjector.annotations.LightInject;
import by.kostevich.lightinjector.impl.bean.ComponentDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModuleConfigurationReader {

    public static Set<ComponentDefinition> readComponentsConfigurations(LightInjectionModule module) {
        Set<ComponentDefinition> componentConfigurations = new HashSet<>();

        Map<String, Class<?>> moduleConfigs = getModuleComponentsConfig(module);
        moduleConfigs.forEach((componentName, componentClass) -> {
            try {
                Constructor<?> injectionConstructor = Arrays.stream(componentClass.getConstructors())
                        .filter(constructor -> constructor.getAnnotation(LightInject.class) != null)
                        .findAny().orElse(null);
                if (injectionConstructor == null) {
                    injectionConstructor = componentClass.getConstructor();
                }

                ComponentDefinition componentConfiguration = ComponentDefinitionBuilder
                        .withConstructorCreation(componentClass, componentName, injectionConstructor);
                componentConfigurations.add(componentConfiguration);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });

        Arrays.stream(module.getClass().getMethods())
                .filter(method -> method.getAnnotation(LightComponent.class) != null)
                .forEach(method -> {
                    LightComponent lightComponentAnnotation = method.getAnnotation(LightComponent.class);
                    String componentName = lightComponentAnnotation.name().isEmpty() ?
                            method.getReturnType().getSimpleName() : lightComponentAnnotation.name();
                    ComponentDefinition componentConfiguration = ComponentDefinitionBuilder
                            .withMethodCreation(method.getReturnType(), componentName, method);
                    componentConfigurations.add(componentConfiguration);
                });

        return componentConfigurations;
    }

    private static Map<String, Class<?>> getModuleComponentsConfig(LightInjectionModule module) {
        try {
            Method configureMethod = module.getClass().getDeclaredMethod("configureInjections");
            configureMethod.setAccessible(true);
            configureMethod.invoke(module);

            Method getComponentsNamesAndClassesMethod = module.getClass().getDeclaredMethod("getComponentsNamesAndClasses");
            getComponentsNamesAndClassesMethod.setAccessible(true);
            return (Map<String, Class<?>>) getComponentsNamesAndClassesMethod.invoke(module);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
