package net.andrewhatch.skeletoncli.model;

import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RequestModelBuilder {

  private static PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

  public static <T> RequestModel<T> build(final T requestObject)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

    final Map<String, PropertyDescriptor> properties = propertiesForRequest(requestObject);
    Map<String, Object> requestDefaults = requestDefaults(requestObject, properties);
    return new RequestModel<>(requestObject, properties, requestDefaults);
  }

  private static <T> Map<String, Object> requestDefaults(
      final T requestObject,
      final Map<String, PropertyDescriptor> properties
  ) {

    final Map<String, Object> defaults = new HashMap<>();

    for (String propertyName : properties.keySet()) {
      try {
        final Object value = propertyUtilsBean.getProperty(requestObject, propertyName);
        if (value != null) {
          defaults.put(propertyName, value);
        }
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    return defaults;
  }

  private static <T> Map<String, PropertyDescriptor> propertiesForRequest(T requestObject)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    return Arrays.stream(propertyUtilsBean.getPropertyDescriptors(requestObject))
        .collect(
            Collectors.toMap(PropertyDescriptor::getName,
            Function.identity()));

  }
}
