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
  private final Config config;

  RequestResolver(final Class<T> requestClass) {
    this(requestClass, new Config());
  }

  RequestResolver(final Class<T> requestClass, final Config config) {
    this.requestClass = requestClass;
    this.config = config;
  }

  Optional<T> resolve(final String[] args) throws InvalidParametersClassException, InvalidCommandLineException {
    try {
      return resolveOrThrowException(args);
    } catch (IllegalAccessException | InstantiationException
        | InvocationTargetException | NoSuchMethodException | ParseException e
    ) {
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
    usage("", options);
  }

  private void usage(String message, Options options) {
    final String header = message != null
        ? String.format("%s\n\n", message)
        : "";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        this.config.getCommandName(),
        header,
        options,
        this.config.getFooter());
  }

  static class Config {
    private String commandName = "cmd";
    private String footer = "";

    Config withCommandName(final String name) {
      setCommandName(name);
      return this;
    }

    Config withFooter(final String footer) {
      setFooter(footer);
      return this;
    }

    String getCommandName() {
      return commandName;
    }

    void setCommandName(String commandName) {
      this.commandName = commandName;
    }

    String getFooter() {
      return footer;
    }

    void setFooter(String footer) {
      this.footer = footer;
    }
  }
}
