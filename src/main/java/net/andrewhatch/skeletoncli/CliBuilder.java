package net.andrewhatch.skeletoncli;

import java.util.Optional;
import java.util.function.Consumer;

public class CliBuilder<T> {

  private final Class<T> parametersClass;
  private String[] args;

  public static <R> CliBuilder<R> from(final String[] args, final Class<R> parametersClass) {
    return new CliBuilder<>(args, parametersClass);
  }

  private CliBuilder(
      final String[] args,
      final Class<T> parametersClass
  ) {
    this.args = args;
    this.parametersClass = parametersClass;
  }

  public void run(final Consumer<T> requestConsumer) {
    new ArgumentResolver<>(parametersClass)
        .resolve(this.args)
        .ifPresent(requestConsumer);
  }

  public Optional<T> parameters() {
    return new ArgumentResolver<>(parametersClass)
        .resolve(this.args);
  }
}
