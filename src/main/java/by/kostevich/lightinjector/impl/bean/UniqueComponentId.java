package by.kostevich.lightinjector.impl.bean;

import java.util.Objects;

public class UniqueComponentId {

    private Class<?> componentClass;
    private String componentName;

    public UniqueComponentId(Class<?> componentClass, String componentName) {
        this.componentClass = componentClass;
        this.componentName = componentName;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniqueComponentId that = (UniqueComponentId) o;
        return Objects.equals(componentClass, that.componentClass) &&
                Objects.equals(componentName, that.componentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentClass, componentName);
    }
}
