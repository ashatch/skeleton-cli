package net.andrewhatch.skeletoncli.examples.helloworld;

import net.andrewhatch.skeletoncli.CliBuilder;

import org.apache.commons.cli.ParseException;

import java.util.function.Consumer;

public class HelloWorld {
  public static void main(String[] args) throws ParseException {

    Consumer<HelloWorldRequest> program = request -> System.out.println(request.getMessage());

    final int exitStatusCode = CliBuilder.from(args, HelloWorldRequest.class)
        .withCommandName("helloworld")
        .withFooter("a hello world production")
        .run(program)
        .getExitStatusCode();

    System.exit(exitStatusCode);
  }
}
