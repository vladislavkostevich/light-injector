package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjector;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.InjectContext;

public class LightInjectorImpl implements LightInjector {

    private InjectContext context;

    private LightInjectorImpl(InjectContext context) {
        this.context = context;
    }

    @Override
    public <T> T getComponent(Class<T> componentClass) {
        return getComponent(componentClass, null);
    }

    @Override
    public <T> T getComponent(Class<T> componentClass, String componentName) {
        return (T) ContextLookup.findComponent(context, new DependencyDefinition(componentClass, componentName));
    }

}
