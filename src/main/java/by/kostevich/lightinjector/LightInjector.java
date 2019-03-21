package by.kostevich.lightinjector;

import by.kostevich.lightinjector.factory.LightInjectorFactory;

public interface LightInjector {

    <T> T getComponent(Class<T> componentClass);

    <T> T getComponent(Class<T> componentClass, String componentName);

    static LightInjector create(LightInjectionModule lightInjectionModule) {
        return LightInjectorFactory.createInjector(lightInjectionModule);
    }
}
