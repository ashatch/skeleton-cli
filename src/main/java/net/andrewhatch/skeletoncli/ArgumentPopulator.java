package net.andrewhatch.skeletoncli;

import net.andrewhatch.skeletoncli.exceptions.InvalidCommandLineException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

class ArgumentPopulator<T> {

  void populateBean(
      final T requestObject,
      final Set<PropertyDescriptor> propertyDescriptors,
      final Options options,
      final String[] args
  ) throws ParseException {
    final CommandLineParser parser = new DefaultParser();
    final CommandLine commandLine = parser.parse(options, args);

    propertyDescriptors.forEach(descriptor ->
        this.setProperty(requestObject, descriptor, commandLine));
  }

  private void setProperty(
      final T requestObject,
      final PropertyDescriptor propertyDescriptor,
      final CommandLine commandLine
  ) {
    try {
      final Class<?> propertyType = propertyDescriptor.getPropertyType();
      final String propertyName = propertyDescriptor.getName();

      if (Path.class.equals(propertyType)) {
        resolvePathArgument(requestObject, propertyName, commandLine);
      } else if (boolean.class.equals(propertyType) || Boolean.class.equals(propertyType)) {
        resolveBooleanSwitch(requestObject, propertyName, commandLine);
      } else if (propertyType.isEnum()) {
        resolveEnumType(requestObject, propertyName, propertyDescriptor, commandLine);
      } else {
        BeanUtils.setProperty(
            requestObject,
            propertyName,
            commandLine.getOptionValue(propertyName));
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private void resolveEnumType(
      final T requestObject,
      final String propertyName,
      final PropertyDescriptor propertyDescriptor,
      final CommandLine commandLine
  ) throws InvocationTargetException, IllegalAccessException {
    final Enum[] enumConstants = enumConstants(propertyDescriptor);

    final String matchingCommandLineOption = Arrays.stream(enumConstants)
        .map(String::valueOf)
        .map(String::toLowerCase)
        .filter(commandLine::hasOption)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Could not resolve enum"));

    final Enum enumValue = Arrays.stream(enumConstants)
        .filter(c -> matchingCommandLineOption.equals(c.name().toLowerCase()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Could not resolve enum value"));

    BeanUtils.setProperty(
        requestObject,
        propertyName,
        enumValue);
  }

  private void resolveBooleanSwitch(
      final T requestObject,
      final String propertyName,
      final CommandLine commandLine
  ) throws InvocationTargetException, IllegalAccessException {
    boolean hasOption = commandLine.hasOption(propertyName);
    BeanUtils.setProperty(requestObject, propertyName, hasOption);
  }

  private void resolvePathArgument(
      T requestObject,
      String key,
      CommandLine commandLine
  ) throws IllegalAccessException, InvocationTargetException {

    final Path argumentPath = Paths.get(commandLine.getOptionValue(key));
    if (!Files.exists(argumentPath)) {
      throw new InvalidCommandLineException(
          String.format("Path \"%s\" must exist", String.valueOf(argumentPath)));
    }
    BeanUtils.setProperty(requestObject, key, argumentPath);
  }

  @SuppressWarnings("unchecked")
  private Enum[] enumConstants(PropertyDescriptor propertyDescriptor) {
    return ((Class<? extends Enum>) propertyDescriptor.getPropertyType()).getEnumConstants();
  }
}
