package by.kostevich.lightinjector.impl.bean;

public class PropertyDefinition {
    private String name;
    private Class<?> type;
    private String defaultValue;

    public PropertyDefinition(String name, Class<?> type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
