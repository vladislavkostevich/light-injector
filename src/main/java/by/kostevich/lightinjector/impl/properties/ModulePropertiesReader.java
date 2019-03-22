package by.kostevich.lightinjector.impl.properties;

import by.kostevich.lightinjector.LightInjectionModule;
import by.kostevich.lightinjector.LightInjectionModule.PropertiesConfigurer;
import by.kostevich.lightinjector.impl.bean.PropertyType;
import by.kostevich.lightinjector.impl.bean.PropertyValuesHolder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ModulePropertiesReader {

    public static Set<PropertyValuesHolder> readProperties(LightInjectionModule module) {
        List<PropertiesConfigurer> propertiesConfigurers = getPropertiesConfigurers(module);

        return propertiesConfigurers.stream()
                .map(propertiesConfigurer -> {
                    String propertyName = propertiesConfigurer.getPropertyName();
                    PropertyType propertyType = propertiesConfigurer.getPropertyType();
                    Map<String, Object> valuesMap = getValuesMap(propertiesConfigurer);

                    PropertyValuesHolder propertyValuesHolder = new PropertyValuesHolder();
                    propertyValuesHolder.setPropertyName(propertyName);
                    propertyValuesHolder.setPropertyType(propertyType);
                    propertyValuesHolder.setPropertyEnvValues(valuesMap);
                    return propertyValuesHolder;
                })
                .collect(Collectors.toSet());
    }

    private static List<PropertiesConfigurer> getPropertiesConfigurers(LightInjectionModule module) {
        try {
            Field propertiesConfigurersField = LightInjectionModule.class.getDeclaredField("propertiesConfigurers");
            propertiesConfigurersField.setAccessible(true);
            return (List<PropertiesConfigurer>) propertiesConfigurersField.get(module);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> getValuesMap(PropertiesConfigurer propertiesConfigurer) {
        try {
            Field valuesMapField = propertiesConfigurer.getClass().getDeclaredField("valuesMap");
            valuesMapField.setAccessible(true);
            return (Map<String, Object>) valuesMapField.get(propertiesConfigurer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
