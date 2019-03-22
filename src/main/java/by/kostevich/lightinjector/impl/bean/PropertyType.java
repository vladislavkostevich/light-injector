package by.kostevich.lightinjector.impl.bean;

import java.util.Arrays;

public enum PropertyType {
    STRING(String.class), INTEGER(Integer.class), BOOLEAN(Boolean.class);

    private Class<?> propertyClass;

    PropertyType(Class<?> propertyClass) {
        this.propertyClass = propertyClass;
    }

    public static PropertyType getByClass(Class<?> propertyClass) {
        return Arrays.stream(values())
                .filter(propertyType -> propertyType.propertyClass.equals(propertyClass))
                .findFirst().orElse(null);
    }
}
