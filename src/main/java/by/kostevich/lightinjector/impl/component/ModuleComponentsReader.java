package by.kostevich.lightinjector.impl.component;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.annotations.LightComponent;
import by.kostevich.lightinjector.annotations.LightInject;
import by.kostevich.lightinjector.impl.bean.ComponentDefinition;
import by.kostevich.lightinjector.impl.bean.UniqueComponentId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ModuleComponentsReader {

    public static Set<ComponentDefinition> readComponents(LightInjectionModule module) {
        Set<ComponentDefinition> componentConfigurations = new HashSet<>();

        Set<UniqueComponentId> componentIds = getModuleComponentsConfig(module);
        componentIds.forEach(componentId -> {
            try {
                Class<?> componentClass = componentId.getComponentClass();
                String componentName = componentId.getComponentName();

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

    private static Set<UniqueComponentId> getModuleComponentsConfig(LightInjectionModule module) {
        try {
            Method configureMethod = module.getClass().getDeclaredMethod("configureInjections");
            configureMethod.setAccessible(true);
            configureMethod.invoke(module);

            Field componentIdsField = module.getClass().getDeclaredField("componentIds");
            componentIdsField.setAccessible(true);
            return (Set<UniqueComponentId>) componentIdsField.get(module);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
