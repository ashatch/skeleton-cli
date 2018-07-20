package net.andrewhatch.skeletoncli.options;

import net.andrewhatch.skeletoncli.options.groups.PropertyGroups;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class OptionMaker<T> {

  private final T requestObject;
  private final PropertyUtilsBean propertyUtilsBean;
  private Options options;
  private PropertyGroups<T> propertyGroups;

  public static <T> Options optionsFor(final T requestObject)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    return new OptionMaker<>(requestObject).build();
  }

  private OptionMaker(final T requestObject) {
    this.requestObject = requestObject;
    this.propertyUtilsBean = new PropertyUtilsBean();
  }

  private Options build() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    final Set<PropertyDescriptor> propertyDescriptors = propertiesForParameters(requestObject);
    this.options = new Options();
    this.propertyGroups = PropertyGroups.from(requestObject);

    propertyDescriptors.stream()
        .filter(descriptor -> !"class".equals(descriptor.getName()))
        .filter(descriptor -> !isGroupedProperty(descriptor))
        .forEach(descriptor -> this.addOptionForProperty(descriptor, requestObject));

    this.propertyGroups.groups()
        .forEach(groupName ->
            options.addOptionGroup(this.makeOptionsForGroup(groupName, requestObject)));

    return options;
  }

  private void addOptionForProperty(
      final PropertyDescriptor descriptor,
      final T requestObject
  ) {
    final Class<?> propertyType = descriptor.getPropertyType();

    if (propertyType.isEnum()) {
      options.addOptionGroup(makeSwitchProperty(descriptor));
    } else {
      options.addOption(makeBeanProperty(descriptor, requestObject));
    }

    makeBeanProperty(descriptor, requestObject);
  }

  private Option makeBeanProperty(
      final PropertyDescriptor descriptor,
      final T requestObject
  ) {
    final Class<?> propertyType = descriptor.getPropertyType();

    if (isBooleanProperty(propertyType)) {
      return makeBooleanProperty(descriptor);
    } else {
      return makeStandardProperty(descriptor, requestObject);
    }
  }

  private OptionGroup makeSwitchProperty(
      final PropertyDescriptor descriptor
  ) {
    final Class<? extends Enum> enumType = enumType(descriptor);

    final OptionGroup group = new OptionGroup();
    group.setRequired(true);

    Arrays.stream(enumType.getEnumConstants())
        .map(enumConstant -> Option.builder()
            .longOpt(enumConstant.name().toLowerCase())
            .build())
        .forEach(group::addOption);

    return group;
  }

  private Option makeStandardProperty(PropertyDescriptor descriptor, T requestObject) {
    Option.Builder builder = Option.builder()
        .longOpt(descriptor.getName())
        .hasArg(true);

    if (!propertyHasDefaultValue(descriptor, requestObject)) {
      builder = builder.required();
    } else {
      builder.desc("[default: " + defaultPropertyValue(requestObject, descriptor) + "]");
    }

    return builder.build();
  }

  private String defaultPropertyValue(
      final T requestBean,
      final PropertyDescriptor descriptor
  ) {
    try {
      return String.valueOf(propertyUtilsBean.getProperty(requestBean, descriptor.getName()));
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      return null;
    }
  }

  private boolean propertyHasDefaultValue(
      final PropertyDescriptor descriptor,
      final T requestObject
  ) {
    try {
      return propertyUtilsBean.getProperty(requestObject, descriptor.getName()) != null;
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      return false;
    }
  }

  private Option makeBooleanProperty(PropertyDescriptor descriptor) {
    return Option.builder()
        .longOpt(descriptor.getName())
        .hasArg(false)
        .build();
  }

  private boolean isBooleanProperty(Class<?> propertyType) {
    return boolean.class.equals(propertyType) || Boolean.class.equals(propertyType);
  }

  private Class<? extends Enum> enumType(PropertyDescriptor descriptor) {
    if (!descriptor.getPropertyType().isEnum()) {
      throw new IllegalArgumentException("Must be an enum");
    }

    return EnumPropertyType.of(descriptor);
  }

  private Set<PropertyDescriptor> propertiesForParameters(T requestObject)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    return Arrays.stream(new PropertyUtilsBean().getPropertyDescriptors(requestObject))
        .collect(Collectors.toSet());
  }

  private OptionGroup makeOptionsForGroup(
      String groupName,
      T requestObject
  ) {
    final OptionGroup optionGroup = new OptionGroup();
    optionGroup.setRequired(true);

    final Set<String> fields = this.propertyGroups.fields(groupName);
    fields.stream()
        .map(this::propertyDescriptor)
        .forEach(descriptor -> optionGroup.addOption(makeBeanProperty(descriptor, requestObject)));

    return optionGroup;
  }

  private PropertyDescriptor propertyDescriptor(final String fieldName) {
    try {
      return new PropertyUtilsBean().getPropertyDescriptor(this.requestObject, fieldName);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isGroupedProperty(final PropertyDescriptor descriptor) {
    return propertyGroups.groupForField(descriptor.getName())
        .isPresent();
  }

}
