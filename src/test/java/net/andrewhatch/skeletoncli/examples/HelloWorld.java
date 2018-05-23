package net.andrewhatch.skeletoncli.examples;

import net.andrewhatch.skeletoncli.CliBuilder;
import org.apache.commons.cli.ParseException;

import java.util.function.Consumer;

public class HelloWorld {
  public static void main(String[] args) throws ParseException {
    Consumer<HelloWorldParameters> program = parameters -> System.out.println(parameters.getMessage());

    System.out.println(CliBuilder.from(args, HelloWorldParameters.class).parameters());

    final int exitStatusCode = CliBuilder.from(args, HelloWorldParameters.class)
        .run(program)
        .getExitStatusCode();

    System.exit(exitStatusCode);
  }
}
