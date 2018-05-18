package net.andrewhatch.skeletoncli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Set;

public class OptionMaker {

  private final Set<PropertyDescriptor> propertyNames;

  public static Options optionsFor(final Set<PropertyDescriptor> propertyNames) {
    return new OptionMaker(propertyNames).build();
  }

  private OptionMaker(Set<PropertyDescriptor> propertyNames) {
    this.propertyNames = propertyNames;
  }

  private Options build() {
    final Options options = new Options();

    propertyNames.stream()
        .filter(descriptor -> !"class".equals(descriptor.getName()))
        .forEach(descriptor -> this.addOptionForProperty(options, descriptor));

    return options;
  }

  private void addOptionForProperty(
      final Options options,
      final PropertyDescriptor descriptor
  ) {
    final Class<?> propertyType = descriptor.getPropertyType();

    if (isBooleanProperty(propertyType)) {
      addBooleanProperty(options, descriptor);
    } else if (propertyType.isEnum()) {
      addSwitchProperty(options, descriptor);
    } else {
      addStandardProperty(options, descriptor);
    }
  }

  private void addSwitchProperty(
      final Options options,
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

    options.addOptionGroup(group);
  }

  private void addStandardProperty(Options options, PropertyDescriptor descriptor) {
    options.addOption(
        Option.builder()
            .longOpt(descriptor.getName())
            .hasArg(true)
            .required()
            .build());
  }

  private void addBooleanProperty(Options options, PropertyDescriptor descriptor) {
    options.addOption(
        Option.builder()
            .longOpt(descriptor.getName())
            .hasArg(false)
            .build());
  }

  private boolean isBooleanProperty(Class<?> propertyType) {
    return boolean.class.equals(propertyType) || Boolean.class.equals(propertyType);
  }

  private Class<? extends Enum> enumType(PropertyDescriptor descriptor) {
    if (!descriptor.getPropertyType().isEnum()) {
      throw new IllegalArgumentException("Must be an enum");
    }

    return (Class<? extends Enum>) descriptor.getPropertyType();
  }
}
