package net.andrewhatch.skeletoncli;

import net.andrewhatch.skeletoncli.exceptions.InvalidCommandLineException;
import net.andrewhatch.skeletoncli.exceptions.InvalidParametersClassException;
import net.andrewhatch.skeletoncli.options.OptionMaker;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class ArgumentResolver<T> {

  private final Class<T> parametersClass;

  ArgumentResolver(final Class<T> parametersClass) {
    this.parametersClass = parametersClass;
  }

  Optional<T> resolve(final String[] args) throws InvalidParametersClassException, InvalidCommandLineException {
    try {
      return resolveOrThrowException(args);
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
      throw new InvalidParametersClassException(e);
    } catch (ParseException e) {
      throw new InvalidCommandLineException(e);
    }
  }

  private Optional<T> resolveOrThrowException(final String[] args)
      throws IllegalAccessException, InstantiationException,
      ParseException, InvocationTargetException, NoSuchMethodException {

    final T requestObject = parametersClass.newInstance();
    final Set<PropertyDescriptor> propertyDescriptors = propertiesForParameters(requestObject);

    final Options options = OptionMaker.optionsFor(propertyDescriptors);

    try {
      new ArgumentPopulator<>().populateBean(requestObject, propertyDescriptors, options, args);
      return Optional.of(requestObject);

    } catch (MissingOptionException missingOptionException) {
      usage(options);
    } catch (InvalidCommandLineException ice) {
      usage(ice.getMessage(), options);
    }

    return Optional.empty();
  }

  private Set<PropertyDescriptor> propertiesForParameters(T requestObject)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    return Arrays.stream(new PropertyUtilsBean().getPropertyDescriptors(requestObject))
      .collect(Collectors.toSet());
  }

  private void usage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "ant", options );
  }

  private void usage(String message, Options options) {
    final String header = message != null
        ? String.format("%s\n\n", message)
        : "";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "cmd", header, options, "");
  }


}
