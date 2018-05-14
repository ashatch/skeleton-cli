package net.andrewhatch.skeletoncli;

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

class ArgumentResolver<T> {

  Optional<T> resolve(
      final Class<T> parametersClass,
      final String[] args
  ) throws
      IllegalAccessException,
      InstantiationException,
      InvocationTargetException,
      NoSuchMethodException,
      ParseException {

    T requestObject = parametersClass.newInstance();
    final Options options = optionsFor(requestObject);

    try {
      final CommandLineParser parser = new DefaultParser();
      final CommandLine commandLine = parser.parse(options, args);

      new PropertyUtilsBean()
          .describe(requestObject)
          .keySet()
          .forEach(key -> this.setProperty(requestObject, key, commandLine));

      return Optional.of(requestObject);
    } catch (final MissingOptionException moe) {
      usage(options);
    }

    return Optional.empty();
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

  private Options optionsFor(final Object bean)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

    final Options options = new Options();

    new PropertyUtilsBean()
        .describe(bean)
        .keySet()
        .stream()
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
