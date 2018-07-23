package net.andrewhatch.skeletoncli;

import net.andrewhatch.skeletoncli.exceptions.InvalidCommandLineException;
import net.andrewhatch.skeletoncli.exceptions.InvalidRequestClassException;
import net.andrewhatch.skeletoncli.model.RequestModel;
import net.andrewhatch.skeletoncli.model.RequestModelBuilder;
import net.andrewhatch.skeletoncli.options.OptionMaker;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

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

  Optional<T> resolve(final String[] args) throws InvalidRequestClassException, InvalidCommandLineException {
    try {
      return resolveOrThrowException(args);
    } catch (IllegalAccessException | InstantiationException
        | InvocationTargetException | NoSuchMethodException | ParseException e) {

      return Optional.empty();
    }
  }

  private Optional<T> resolveOrThrowException(final String[] args)
      throws IllegalAccessException, InstantiationException,
      ParseException, InvocationTargetException, NoSuchMethodException {

    final T requestObject = requestClass.newInstance();
    final RequestModel<T> requestModel = RequestModelBuilder.build(requestObject);
    final Options options = OptionMaker.optionsFor(requestModel);

    try {
      return new RequestBeanPopulator<T>()
          .populateBean(requestModel, options, args);

    } catch (InvalidCommandLineException ice) {
      usage(ice.getMessage(), options);
    }

    return Optional.empty();
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
