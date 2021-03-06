package by.kostevich.lightinjector.impl;

import by.kostevich.lightinjector.LightInjector;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.InjectContext;
import by.kostevich.lightinjector.impl.component.ComponentLookup;

public class LightInjectorImpl implements LightInjector {

    private InjectContext context;

    private LightInjectorImpl() {
    }

    @Override
    public <T> T getComponent(Class<T> componentClass) {
        return getComponent(componentClass, null);
    }

    @Override
    public <T> T getComponent(Class<T> componentClass, String componentName) {
        return (T) ComponentLookup.findComponent(context, new DependencyDefinition(componentClass, componentName));
    }

    private void setContext(InjectContext injectContext) {
        this.context = injectContext;
    }

}
