package by.kostevich.lightinjector.impl.properties;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.impl.bean.DependencyDefinition;
import by.kostevich.lightinjector.impl.bean.InjectContext;
import by.kostevich.lightinjector.impl.bean.PropertyType;
import by.kostevich.lightinjector.impl.bean.PropertyValuesHolder;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public class PropertiesLookup {

    public static Object findPropertyValue(
            InjectContext context,
            DependencyDefinition propertyDefinition) {

        String environment = getEnvironment(context);
        String propertyName = propertyDefinition.getDependencyName();
        Class<?> propertyClass = propertyDefinition.getDependencyClass();
        String propertyDefaultValue = propertyDefinition.getDefaultValue();

        Set<PropertyValuesHolder> propertyValuesHolders = context.getPropertyValues();
        PropertyValuesHolder valuesHolder = propertyValuesHolders.stream()
                .filter(propertyValuesHolder -> propertyValuesHolder.getPropertyName().equalsIgnoreCase(propertyName))
                .findFirst().orElse(null);

        if (valuesHolder == null) {
            PropertyType propertyType = PropertyType.getByClass(propertyClass);
            return getDefaultValue(propertyDefaultValue, propertyType, propertyName, propertyClass);
        }

        Map<String, Object> valuesMap = valuesHolder.getPropertyEnvValues();
        Object propertyEnvValue = valuesMap.get(environment);
        if (propertyEnvValue == null) {
            return getDefaultValue(propertyDefaultValue, valuesHolder.getPropertyType(), propertyName, propertyClass);
        }

        return propertyEnvValue;
    }

    private static String getEnvironment(InjectContext injectContext) {
        LightInjectionModule lightInjectionModule = injectContext.getModule();
        try {
            Method getEnvNameMethod = lightInjectionModule.getClass().getDeclaredMethod("getEnvironmentName");
            getEnvNameMethod.setAccessible(true);
            return (String) getEnvNameMethod.invoke(lightInjectionModule);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getDefaultValue(String propertyDefaultValue, PropertyType propertyType, String propertyName, Class<?> propertyClass) {
        switch (propertyType) {
            case STRING:
                return propertyDefaultValue;
            case INTEGER:
                return Integer.parseInt(propertyDefaultValue);
            case BOOLEAN:
                return Boolean.parseBoolean(propertyDefaultValue);
            default:
                throw new IllegalStateException(
                        format("Property with name %s tried to be injected by unsupported type %s",
                                propertyName, propertyClass.getSimpleName())
                );
        }
    }
}
