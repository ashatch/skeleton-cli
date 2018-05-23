package net.andrewhatch.skeletoncli;

import java.util.Optional;
import java.util.function.Consumer;

public class CliBuilder<T> {

  private final Class<T> requestClass;
  private String[] args;

  public static <R> CliBuilder<R> from(final String[] args, final Class<R> requestClass) {
    return new CliBuilder<>(args, requestClass);
  }

  private CliBuilder(
      final String[] args,
      final Class<T> requestClass
  ) {
    this.args = args;
    this.requestClass = requestClass;
  }

  public RunStatus run(final Consumer<T> requestConsumer) {
    final Optional<T> requestOptional = requestBean();

    if (requestOptional.isPresent()) {
      requestOptional.ifPresent(requestConsumer);
      return RunStatus.OK;
    } else {
      return RunStatus.FAIL;
    }
  }

  public Optional<T> requestBean() {
    return new RequestResolver<>(requestClass)
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
