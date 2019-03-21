package by.kostevich.lightinjector.impl.bean;

public class DependencyDefinition {
    private Class<?> dependencyClass;
    private String dependencyName;
    private boolean isProperty;
    private String defaultValue;

    public DependencyDefinition(Class<?> dependencyClass, String dependencyName, boolean isProperty, String defaultValue) {
        this.dependencyClass = dependencyClass;
        this.dependencyName = dependencyName;
        this.isProperty = isProperty;
        this.defaultValue = defaultValue;
    }

    public Class<?> getDependencyClass() {
        return dependencyClass;
    }

    public String getDependencyName() {
        return dependencyName;
    }

    public boolean isProperty() {
        return isProperty;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
