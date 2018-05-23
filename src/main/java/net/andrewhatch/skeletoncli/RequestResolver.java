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

class RequestResolver<T> {

  private final Class<T> requestClass;

  RequestResolver(final Class<T> requestClass) {
    this.requestClass = requestClass;
  }

  Optional<T> resolve(final String[] args) throws InvalidParametersClassException, InvalidCommandLineException {
    try {
      return resolveOrThrowException(args);
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
      return Optional.empty();
    } catch (ParseException e) {
      return Optional.empty();
    }
  }

  private Optional<T> resolveOrThrowException(final String[] args)
      throws IllegalAccessException, InstantiationException,
      ParseException, InvocationTargetException, NoSuchMethodException {

    final T requestObject = requestClass.newInstance();
    final Set<PropertyDescriptor> propertyDescriptors = propertiesForRequest(requestObject);

    final Options options = OptionMaker.optionsFor(requestObject);

    try {
      new RequestBeanPopulator<>().populateBean(requestObject, propertyDescriptors, options, args);
      return Optional.of(requestObject);

    } catch (MissingOptionException missingOptionException) {
      usage(options);

    } catch (InvalidCommandLineException ice) {
      usage(ice.getMessage(), options);

    }

    return Optional.empty();
  }

  private Set<PropertyDescriptor> propertiesForRequest(T requestObject)
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
