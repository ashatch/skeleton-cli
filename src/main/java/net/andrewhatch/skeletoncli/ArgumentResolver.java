package net.andrewhatch.skeletoncli;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.cli.*;

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
      ParseException
  {
    T t = parametersClass.newInstance();
    final Options options = optionsFor(t);

    try {
      final CommandLineParser parser = new DefaultParser();
      final CommandLine commandLine = parser.parse(options, args);

      new PropertyUtilsBean()
          .describe(t)
          .keySet()
          .forEach(key -> this.setProperty(t, key, commandLine));

      return Optional.of(t);
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
      final T t,
      final String key,
      final CommandLine commandLine
  ) {
    try {
      BeanUtils.setProperty(t, key, commandLine.getOptionValue(key));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private Options optionsFor(final Object bean)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
  {
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
