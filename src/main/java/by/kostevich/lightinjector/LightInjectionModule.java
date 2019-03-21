package by.kostevich.lightinjector;

import java.util.HashMap;
import java.util.Map;

public abstract class LightInjectionModule {

    private Map<String, Class<?>> componentsNamesAndClasses = new HashMap<>();

    protected abstract void configureInjections();

    protected void defineComponent(Class<?> componentClass) {
        componentsNamesAndClasses.put(componentClass.getSimpleName(), componentClass);
    }

    protected void defineComponent(Class<?> componentClass, String componentName) {
        componentsNamesAndClasses.put(componentName, componentClass);
    }

    public Map<String, Class<?>> getComponentsNamesAndClasses() {
        return componentsNamesAndClasses;
    }

}
