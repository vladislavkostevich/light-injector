package by.kostevich.lightinjector;

import by.kostevich.lightinjector.bean.DependencyDefinition;
import by.kostevich.lightinjector.bean.LightInjectContext;

public class LightInjectorImpl implements LightInjector {

    private LightInjectContext context;

    private LightInjectorImpl(LightInjectContext context) {
        this.context = context;
    }

    @Override
    public <T> T getComponent(Class<T> componentClass) {
        return (T) context.findComponent(new DependencyDefinition(componentClass, null));
    }

    @Override
    public <T> T getComponent(Class<T> componentClass, String componentName) {
        return (T) context.findComponent(new DependencyDefinition(componentClass, componentName));
    }

}
