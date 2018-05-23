package net.andrewhatch.skeletoncli;

import java.util.Optional;
import java.util.function.Consumer;

public class CliBuilder<T> {

  private final Class<T> requestClass;
  private String[] args;
  private String commandName = "cli";
  private String footer = "";

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

  public CliBuilder<T> withCommandName(final String name) {
    this.commandName = name;
    return this;
  }

  public CliBuilder<T> withFooter(final String footer) {
    this.footer = footer;
    return this;
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
    final RequestResolver.Config config = new RequestResolver.Config()
        .withCommandName(commandName)
        .withFooter(footer);

    return new RequestResolver<>(requestClass, config)
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
