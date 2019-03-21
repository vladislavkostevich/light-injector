package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjector;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.LightInjectContext;

public class LightInjectorImpl implements LightInjector {

    private LightInjectContext context;

    private LightInjectorImpl(LightInjectContext context) {
        this.context = context;
    }

    @Override
    public <T> T getComponent(Class<T> componentClass) {
        return getComponent(componentClass, null);
    }

    @Override
    public <T> T getComponent(Class<T> componentClass, String componentName) {
        return (T) context.findComponent(new DependencyDefinition(componentClass, componentName));
    }

}
