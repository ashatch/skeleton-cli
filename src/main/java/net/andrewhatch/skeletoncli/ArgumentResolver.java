package net.andrewhatch.skeletoncli;

import net.andrewhatch.skeletoncli.exceptions.InvalidCommandLineException;
import net.andrewhatch.skeletoncli.exceptions.InvalidParametersClassException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

class ArgumentResolver<T> {

  private final Class<T> parametersClass;

  ArgumentResolver(
      final Class<T> parametersClass
  ) {
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
    final Set<String> propertyNames = propertiesForParameters(requestObject);
    final Options options = optionsFor(propertyNames);

    try {
      final CommandLineParser parser = new DefaultParser();
      final CommandLine commandLine = parser.parse(options, args);

      propertyNames.forEach(key ->
          this.setProperty(requestObject, key, commandLine));

      return Optional.of(requestObject);
    } catch (MissingOptionException missingOptionException) {
      usage(options);
    }

    return Optional.empty();
  }

  private Set<String> propertiesForParameters(T requestObject)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

    return new PropertyUtilsBean()
        .describe(requestObject)
        .keySet();
  }

  private void usage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "ant", options );
  }

  private void setProperty(
      final T requestObject,
      final String key,
      final CommandLine commandLine
  ) {
    try {
      BeanUtils.setProperty(requestObject, key, commandLine.getOptionValue(key));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private Options optionsFor(final Set<String> propertyNames) {
    final Options options = new Options();

    propertyNames.stream()
        .filter(key -> !"class".equals(key))
        .forEach(key -> this.addOptionForKey(options, key));

    return options;
  }

  private void addOptionForKey(
      final Options options,
      final String key
  ) {
    options.addOption(
        Option.builder()
            .longOpt(key)
            .hasArg(true)
            .required()
            .build());
  }
}
