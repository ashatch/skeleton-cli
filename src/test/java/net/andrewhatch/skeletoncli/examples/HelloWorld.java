package net.andrewhatch.skeletoncli.examples;

import net.andrewhatch.skeletoncli.CliBuilder;
import org.apache.commons.cli.ParseException;

import java.util.function.Consumer;

public class HelloWorld {
  public static void main(String[] args) throws ParseException {
    Consumer<HelloWorldParameters> program = parameters -> System.out.println(parameters.getMessage());

    CliBuilder.from(args, HelloWorldParameters.class)
        .run(program);
  }
}
