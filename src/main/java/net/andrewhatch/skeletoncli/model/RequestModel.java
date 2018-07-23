package net.andrewhatch.skeletoncli.model;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RequestModel<T> {
  private final Map<String, PropertyDescriptor> properties;
  private final Map<String, Object> requestDefaults;
  private final T requestObject;

  public RequestModel(T requestObject, Map<String, PropertyDescriptor> properties, Map<String, Object> requestDefaults) {
    this.requestObject = requestObject;
    this.properties = properties;
    this.requestDefaults = requestDefaults;
  }

  public Map<String, PropertyDescriptor> getProperties() {
    return properties;
  }

  public T getRequestObject() {
    return requestObject;
  }

  public PropertyDescriptor propertyDescriptor(final String fieldName) {
    try {
      return new PropertyUtilsBean().getPropertyDescriptor(this.requestObject, fieldName);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean hasDefaultPropertyValue(String propertyName) {
    return requestDefaults.containsKey(propertyName);
  }

  public Object defaultPropertyValue(String propertyName) {
    return requestDefaults.get(propertyName);
  }

  public RequestModel set(
      final String key,
      final Object value
  ) throws InvocationTargetException, IllegalAccessException {
    BeanUtils.setProperty(
        requestObject,
        key,
        value);

    return this;
  }
}
