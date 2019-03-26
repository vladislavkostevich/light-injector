package by.kostevich.lightinjector.impl.bean;

import java.util.Map;
import java.util.Objects;

public class PropertyValuesHolder {
    private String propertyName;
    private PropertyType propertyType;
    private Map<String, Object> propertyEnvValues;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public Map<String, Object> getPropertyEnvValues() {
        return propertyEnvValues;
    }

    public void setPropertyEnvValues(Map<String, Object> propertyEnvValues) {
        this.propertyEnvValues = propertyEnvValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyValuesHolder that = (PropertyValuesHolder) o;
        return Objects.equals(propertyName, that.propertyName) &&
                Objects.equals(propertyType, that.propertyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName, propertyType);
    }
}
