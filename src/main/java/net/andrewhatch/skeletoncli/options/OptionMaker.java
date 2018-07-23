package net.andrewhatch.skeletoncli.options;

import net.andrewhatch.skeletoncli.model.RequestModel;
import net.andrewhatch.skeletoncli.options.groups.PropertyGroups;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

public class OptionMaker<T> {

  private final RequestModel<T> requestModel;
  private Options options;
  private PropertyGroups<T> propertyGroups;

  public static <T> Options optionsFor(final RequestModel<T> requestObject)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    return new OptionMaker<>(requestObject).build();
  }

  private OptionMaker(final RequestModel<T> requestModel) {
    this.requestModel = requestModel;
  }

  private Options build() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    this.options = new Options();
    this.propertyGroups = PropertyGroups.from(requestModel.getRequestObject());

    requestModel.getProperties().values().stream()
        .filter(descriptor -> !"class".equals(descriptor.getName()))
        .filter(descriptor -> !isGroupedProperty(descriptor))
        .forEach(descriptor -> this.addOptionForProperty(descriptor, requestModel));

    this.propertyGroups.groups()
        .forEach(groupName ->
            options.addOptionGroup(this.makeOptionsForGroup(groupName, requestModel)));

    return options;
  }

  private void addOptionForProperty(
      final PropertyDescriptor descriptor,
      final RequestModel<T> requestModel
  ) {
    final Class<?> propertyType = descriptor.getPropertyType();

    if (propertyType.isEnum()) {
      options.addOptionGroup(makeSwitchProperty(descriptor));
    } else {
      options.addOption(makeBeanProperty(descriptor, requestModel));
    }

    makeBeanProperty(descriptor, requestModel);
  }

  private Option makeBeanProperty(
      final PropertyDescriptor descriptor,
      final RequestModel<T> requestModel
  ) {
    final Class<?> propertyType = descriptor.getPropertyType();

    if (isBooleanProperty(propertyType)) {
      return makeBooleanProperty(descriptor);
    } else {
      return makeStandardProperty(descriptor, requestModel);
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

  private Option makeStandardProperty(
      final PropertyDescriptor descriptor,
      final RequestModel<T> requestModel
  ) {
    final String propertyName = descriptor.getName();
    Option.Builder builder = Option.builder()
        .longOpt(propertyName)
        .hasArg(true);

    if (!requestModel.hasDefaultPropertyValue(propertyName)) {
      builder = builder.required();
    } else {
      builder.desc("[default: " + requestModel.defaultPropertyValue(propertyName) + "]");
    }

    return builder.build();
  }

  private Option makeBooleanProperty(final PropertyDescriptor descriptor) {
    return Option.builder()
        .longOpt(descriptor.getName())
        .hasArg(false)
        .build();
  }

  private boolean isBooleanProperty(final Class<?> propertyType) {
    return boolean.class.equals(propertyType) || Boolean.class.equals(propertyType);
  }

  private Class<? extends Enum> enumType(final PropertyDescriptor descriptor) {
    if (!descriptor.getPropertyType().isEnum()) {
      throw new IllegalArgumentException("Must be an enum");
    }

    return EnumPropertyType.of(descriptor);
  }

  private OptionGroup makeOptionsForGroup(
      final String groupName,
      final RequestModel<T> requestModel
  ) {
    final OptionGroup optionGroup = new OptionGroup();
    optionGroup.setRequired(true);

    final Set<String> fields = this.propertyGroups.fields(groupName);
    fields.stream()
        .map(requestModel::propertyDescriptor)
        .forEach(descriptor -> optionGroup.addOption(makeBeanProperty(descriptor, requestModel)));

    return optionGroup;
  }


  private boolean isGroupedProperty(final PropertyDescriptor descriptor) {
    return propertyGroups.groupForField(descriptor.getName())
        .isPresent();
  }

}
