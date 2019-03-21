package by.kostevich.lightinjector.factory;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.annotations.LightComponent;
import by.kostevich.lightinjector.annotations.LightInject;
import by.kostevich.lightinjector.bean.LightComponentConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ModuleConfigurationReader {

    public static Set<LightComponentConfiguration> readComponentsConfigurations(LightInjectionModule module) {
        Set<LightComponentConfiguration> componentConfigurations = new HashSet<>();

        initModule(module);
        module.getComponentsNamesAndClasses().forEach((componentName, componentClass) -> {
            try {
                Constructor<?> injectionConstructor = Arrays.stream(componentClass.getConstructors())
                        .filter(constructor -> constructor.getAnnotation(LightInject.class) != null)
                        .findAny().orElse(null);
                if (injectionConstructor == null) {
                    injectionConstructor = componentClass.getConstructor();
                }

                LightComponentConfiguration componentConfiguration = LightComponentConfiguration
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
                    LightComponentConfiguration componentConfiguration = LightComponentConfiguration
                            .withMethodCreation(method.getReturnType(), componentName, method);
                    componentConfigurations.add(componentConfiguration);
                });

        return componentConfigurations;
    }

    private static void initModule(LightInjectionModule module) {
        try {
            Method configureMethod = module.getClass().getDeclaredMethod("configureInjections");
            configureMethod.setAccessible(true);
            configureMethod.invoke(module);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
