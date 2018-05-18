package net.andrewhatch.skeletoncli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CliBuilderTest {

  @Test
  public void consumer_is_executed() {
    final String[] argv = {};
    CliBuilder.from(argv, Args.class).run(consumer -> assertThat(consumer).isNotNull());
  }

  @Test
  public void parameters_are_present() {
    final String[] argv = {};

    assertThat(CliBuilder.from(argv, Args.class).parameters()).isPresent();
  }

  public static class Args {}
}