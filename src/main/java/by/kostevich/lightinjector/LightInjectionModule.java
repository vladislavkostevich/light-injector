package by.kostevich.lightinjector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LightInjectionModule {

    private Map<String, Class<?>> componentsNamesAndClasses = new HashMap<>();
    private List<PropertiesConfigurer> propertiesConfigurers = new ArrayList<>();

    protected abstract void configureInjections();

    protected void defineComponent(Class<?> componentClass) {
        componentsNamesAndClasses.put(componentClass.getSimpleName(), componentClass);
    }

    protected void defineComponent(Class<?> componentClass, String componentName) {
        componentsNamesAndClasses.put(componentName, componentClass);
    }

    protected <T> PropertiesConfigurer<T> defineProperty(String propertyName, Class<T> propertyType) {
        PropertiesConfigurer<T> propertiesConfigurer = new PropertiesConfigurer<>();
        propertiesConfigurers.add(propertiesConfigurer);
        return propertiesConfigurer;
    }

    private Map<String, Class<?>> getComponentsNamesAndClasses() {
        return componentsNamesAndClasses;
    }

    private List<PropertiesConfigurer> getPropertiesConfigurers() {
        return propertiesConfigurers;
    }

    public class PropertiesConfigurer<T> {

        private Map<String, T> valuesMap;

        public PropertyEnvValueConfigurer forEnv(String envName) {
            return new PropertyEnvValueConfigurer(envName);
        }

        public class PropertyEnvValueConfigurer {

            private String envName;

            public PropertyEnvValueConfigurer(String envName) {
                this.envName = envName;
            }

            public PropertiesConfigurer withValue(T value) {
                valuesMap.put(envName, value);
                return PropertiesConfigurer.this;
            }
        }
    }

}
