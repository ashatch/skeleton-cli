package net.andrewhatch.skeletoncli.examples.helloworld;

import net.andrewhatch.skeletoncli.CliBuilder;

public class GetRequestBean {
  public static void main(String[] args) {
    CliBuilder.from(args, HelloWorldRequest.class)
        .requestBean()
        .ifPresent(System.out::println);
  }
}
