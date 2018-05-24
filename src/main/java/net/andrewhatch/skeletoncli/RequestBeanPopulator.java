package net.andrewhatch.skeletoncli;

import net.andrewhatch.skeletoncli.exceptions.InvalidCommandLineException;
import net.andrewhatch.skeletoncli.exceptions.InvalidRequestClassException;
import net.andrewhatch.skeletoncli.options.EnumPropertyType;

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

class RequestBeanPopulator<T> {

  void populateBean(
      final T requestBean,
      final Set<PropertyDescriptor> propertyDescriptors,
      final Options options,
      final String[] args
  ) throws InvalidCommandLineException, InvalidRequestClassException {
    try {
      final CommandLineParser parser = new DefaultParser();
      final CommandLine commandLine = parser.parse(options, args);

      for (PropertyDescriptor descriptor : propertyDescriptors) {
        this.applyBeanProperty(requestBean, descriptor, commandLine);
      }

    } catch (final ParseException parseException) {
      throw new InvalidCommandLineException(parseException.getMessage());
    } catch (final IllegalAccessException | InvocationTargetException e) {
      throw new InvalidRequestClassException(e);
    }
  }

  private void applyBeanProperty(
      final T requestBean,
      final PropertyDescriptor propertyDescriptor,
      final CommandLine commandLine
  ) throws InvocationTargetException, IllegalAccessException {

    final Class<?> propertyType = propertyDescriptor.getPropertyType();
    final String propertyName = propertyDescriptor.getName();

    if (Path.class.equals(propertyType)) {
      applyPathProperty(requestBean, propertyName, commandLine);
    } else if (boolean.class.equals(propertyType) || Boolean.class.equals(propertyType)) {
      applyBooleanSwitchProperty(requestBean, propertyName, commandLine);
    } else if (propertyType.isEnum()) {
      applyEnumTypeProperty(requestBean, propertyName, propertyDescriptor, commandLine);
    } else {
      BeanUtils.setProperty(
          requestBean,
          propertyName,
          commandLine.getOptionValue(propertyName));
    }
  }

  private void applyEnumTypeProperty(
      final T requestBean,
      final String propertyName,
      final PropertyDescriptor propertyDescriptor,
      final CommandLine commandLine
  ) throws InvocationTargetException, IllegalAccessException {

    final Enum[] enumConstants = EnumPropertyType.of(propertyDescriptor)
        .getEnumConstants();

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
        requestBean,
        propertyName,
        enumValue);
  }

  private void applyBooleanSwitchProperty(
      final T requestObject,
      final String propertyName,
      final CommandLine commandLine
  ) throws InvocationTargetException, IllegalAccessException {

    boolean hasOption = commandLine.hasOption(propertyName);
    BeanUtils.setProperty(requestObject, propertyName, hasOption);
  }

  private void applyPathProperty(
      final T requestObject,
      final String key,
      final CommandLine commandLine
  ) throws IllegalAccessException, InvocationTargetException {

    final Path pathProperty = Paths.get(commandLine.getOptionValue(key));
    if (!Files.exists(pathProperty)) {
      throw new InvalidCommandLineException(
          String.format("Path \"%s\" must exist", String.valueOf(pathProperty)));
    }
    BeanUtils.setProperty(requestObject, key, pathProperty);
  }
}
