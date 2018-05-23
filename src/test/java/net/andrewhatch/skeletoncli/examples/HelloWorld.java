package net.andrewhatch.skeletoncli.examples;

import net.andrewhatch.skeletoncli.CliBuilder;
import org.apache.commons.cli.ParseException;

import java.util.function.Consumer;

public class HelloWorld {
  public static void main(String[] args) throws ParseException {
    Consumer<HelloWorldRequest> program = request -> System.out.println(request.getMessage());

    System.out.println(CliBuilder.from(args, HelloWorldRequest.class)
        .requestBean());

    final int exitStatusCode = CliBuilder.from(args, HelloWorldRequest.class)
        .run(program)
        .getExitStatusCode();

    System.exit(exitStatusCode);
  }
}
