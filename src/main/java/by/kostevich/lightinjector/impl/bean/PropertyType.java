package by.kostevich.lightinjector.impl.bean;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum PropertyType {
    STRING(String.class), INTEGER(Integer.class, int.class), BOOLEAN(Boolean.class, boolean.class);

    private Set<Class<?>> supportedPropertyClasses;

    PropertyType(Class<?>... supportedPropertyClasses) {
        this.supportedPropertyClasses = Arrays.stream(supportedPropertyClasses).collect(Collectors.toSet());
    }

    public static PropertyType getByClass(Class<?> propertyClass) {
        return Arrays.stream(values())
                .filter(propertyType -> propertyType.supportedPropertyClasses.contains(propertyClass))
                .findFirst().orElse(null);
    }
}
