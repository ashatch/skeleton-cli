package net.andrewhatch.skeletoncli;

import net.andrewhatch.skeletoncli.exceptions.InvalidCommandLineException;
import net.andrewhatch.skeletoncli.exceptions.InvalidRequestClassException;
import net.andrewhatch.skeletoncli.model.RequestModel;
import net.andrewhatch.skeletoncli.options.EnumPropertyType;

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
import java.util.Optional;

class RequestBeanPopulator<T> {

  Optional<T> populateBean(
      final RequestModel<T> requestBean,
      final Options options,
      final String[] args
  ) throws InvalidCommandLineException, InvalidRequestClassException {
    try {
      final CommandLineParser parser = new DefaultParser();
      final CommandLine commandLine = parser.parse(options, args);

      for (PropertyDescriptor descriptor : requestBean.getProperties().values()) {
        this.applyBeanProperty(requestBean, descriptor, commandLine);
      }

      return Optional.ofNullable(requestBean.getRequestObject());

    } catch (final ParseException parseException) {
      throw new InvalidCommandLineException(parseException.getMessage());
    } catch (final IllegalAccessException | InvocationTargetException e) {
      throw new InvalidRequestClassException(e);
    }
  }

  private void applyBeanProperty(
      final RequestModel<T> requestModel,
      final PropertyDescriptor propertyDescriptor,
      final CommandLine commandLine
  ) throws InvocationTargetException, IllegalAccessException {

    final Class<?> propertyType = propertyDescriptor.getPropertyType();
    final String propertyName = propertyDescriptor.getName();

    String optionValue = commandLine.getOptionValue(propertyName);

    if (optionValue == null && requestModel.hasDefaultPropertyValue(propertyName)) {
      optionValue = String.valueOf(requestModel.defaultPropertyValue(propertyName));
    }

    if (Path.class.equals(propertyType)) {
      applyPathProperty(requestModel, propertyName, commandLine);
    } else if (boolean.class.equals(propertyType) || Boolean.class.equals(propertyType)) {
      applyBooleanSwitchProperty(requestModel, propertyName, commandLine);
    } else if (propertyType.isEnum()) {
      applyEnumTypeProperty(requestModel, propertyName, propertyDescriptor, commandLine);
    } else {
      requestModel.set(propertyName, optionValue);
    }
  }

  private void applyEnumTypeProperty(
      final RequestModel<T> requestModel,
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

    requestModel.set(propertyName, enumValue);
  }

  private void applyBooleanSwitchProperty(
      final RequestModel<T> requestModel,
      final String propertyName,
      final CommandLine commandLine
  ) throws InvocationTargetException, IllegalAccessException {

    boolean hasOption = commandLine.hasOption(propertyName);
    requestModel.set(propertyName, hasOption);
  }

  private void applyPathProperty(
      final RequestModel<T> requestModel,
      final String key,
      final CommandLine commandLine
  ) throws IllegalAccessException, InvocationTargetException {

    final Path pathProperty = Paths.get(commandLine.getOptionValue(key));

    if (!Files.exists(pathProperty)) {
      throw new InvalidCommandLineException(
          String.format("Path \"%s\" must exist", String.valueOf(pathProperty)));
    }

    requestModel.set(key, pathProperty);
  }
}
