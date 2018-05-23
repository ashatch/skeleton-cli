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

  public RunStatus run(final Consumer<T> requestConsumer) {
    final Optional<T> requestOptional = new ArgumentResolver<>(parametersClass)
        .resolve(this.args);

    if (requestOptional.isPresent()) {
      requestOptional.ifPresent(requestConsumer);
      return RunStatus.OK;
    } else {
      return RunStatus.FAIL;
    }
  }

  public Optional<T> parameters() {
    return new ArgumentResolver<>(parametersClass)
        .resolve(this.args);
  }

  public enum RunStatus {
    OK(0),
    FAIL(1);

    private final int exitStatusCode;

    RunStatus(int exitStatusCode) {
      this.exitStatusCode = exitStatusCode;
    }

    public int getExitStatusCode() {
      return exitStatusCode;
    }
  }
}
