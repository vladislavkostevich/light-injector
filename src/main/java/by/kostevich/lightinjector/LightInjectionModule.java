package by.kostevich.lightinjector;

import by.kostevich.lightinjector.impl.bean.PropertyType;
import by.kostevich.lightinjector.impl.bean.UniqueComponentId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class LightInjectionModule {

    private Set<UniqueComponentId> componentIds = new HashSet<>();
    private List<PropertiesConfigurer> propertiesConfigurers = new ArrayList<>();

    protected abstract void configureInjections();

    protected void defineComponent(Class<?> componentClass) {
        componentIds.add(new UniqueComponentId(componentClass, componentClass.getSimpleName()));
    }

    protected void defineComponent(Class<?> componentClass, String componentName) {
        componentIds.add(new UniqueComponentId(componentClass, componentName));
    }

    protected PropertiesConfigurer<String> defineStringProperty(String propertyName) {
        PropertiesConfigurer<String> propertiesConfigurer =
                new PropertiesConfigurer<>(propertyName, PropertyType.STRING);
        propertiesConfigurers.add(propertiesConfigurer);
        return propertiesConfigurer;
    }

    protected PropertiesConfigurer<Integer> defineIntProperty(String propertyName) {
        PropertiesConfigurer<Integer> propertiesConfigurer =
                new PropertiesConfigurer<>(propertyName, PropertyType.INTEGER);
        propertiesConfigurers.add(propertiesConfigurer);
        return propertiesConfigurer;
    }

    protected PropertiesConfigurer<Boolean> defineBooleanProperty(String propertyName) {
        PropertiesConfigurer<Boolean> propertiesConfigurer =
                new PropertiesConfigurer<>(propertyName, PropertyType.BOOLEAN);
        propertiesConfigurers.add(propertiesConfigurer);
        return propertiesConfigurer;
    }

    protected String getEnvironmentName() {
        return System.getenv("APP_ENV");
    }

    public static class PropertiesConfigurer<T> {
        private String propertyName;
        private PropertyType propertyType;
        private Map<String, Object> valuesMap = new HashMap<>();

        private PropertiesConfigurer(String propertyName, PropertyType propertyType) {
            this.propertyName = propertyName;
            this.propertyType = propertyType;
        }

        public PropertyEnvValueConfigurer forEnv(String envName) {
            return new PropertyEnvValueConfigurer(envName);
        }

        public String getPropertyName() {
            return propertyName;
        }

        public PropertyType getPropertyType() {
            return propertyType;
        }

        public class PropertyEnvValueConfigurer {

            private String envName;

            private PropertyEnvValueConfigurer(String envName) {
                this.envName = envName;
            }

            public PropertiesConfigurer<T> withValue(T value) {
                valuesMap.put(envName, value);
                return PropertiesConfigurer.this;
            }
        }
    }

}
