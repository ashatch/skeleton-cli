package net.andrewhatch.skeletoncli;

import org.apache.commons.cli.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Consumer;

public class CliBuilder<T> {

  private final Class<T> parametersClass;
  private String[] args;

  public static <R> CliBuilder from(
      final String[] args,
      final Class<R> parametersClass
  ) {
    return new CliBuilder<>(args, parametersClass);
  }

  private CliBuilder(
      final String[] args,
      final Class<T> parametersClass
  ) {
    this.args = args;
    this.parametersClass = parametersClass;
  }

  public void run(Consumer<T> requestConsumer) throws ParseException {
    try {
      Optional<T> request = new ArgumentResolver<T>()
          .resolve(parametersClass, this.args);

      request.ifPresent(requestConsumer);

    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}