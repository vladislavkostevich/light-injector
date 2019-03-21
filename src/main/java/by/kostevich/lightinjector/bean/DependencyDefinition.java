package by.kostevich.lightinjector.bean;

public class DependencyDefinition {
    private Class<?> dependencyClass;
    private String dependencyName;

    public DependencyDefinition(Class<?> dependencyClass, String dependencyName) {
        this.dependencyClass = dependencyClass;
        this.dependencyName = dependencyName;
    }

    public Class<?> getDependencyClass() {
        return dependencyClass;
    }

    public String getDependencyName() {
        return dependencyName;
    }
}
