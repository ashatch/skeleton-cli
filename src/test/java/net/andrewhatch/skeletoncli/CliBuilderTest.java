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

  @Test
  public void exit_status_code_is_zero_on_success() {
    final String[] argv = {
        "--myoption", "myvalue"
    };

    final int exitStatusCode = CliBuilder.from(argv, ArgsWithRequiredField.class)
        .run(consumer -> {
          // do nothing
        })
        .getExitStatusCode();

    assertThat(exitStatusCode).isEqualTo(0);
  }

  @Test
  public void exit_status_code_is_non_zero_on_fail() {
    final String[] argv = {
        "--myoption"
    };

    final int exitStatusCode = CliBuilder.from(argv, ArgsWithRequiredField.class)
        .run(consumer -> {
          // do nothing
        })
        .getExitStatusCode();

    assertThat(exitStatusCode).isNotEqualTo(0);
  }

  public static class Args {}

  public static class ArgsWithRequiredField {
    private String myoption;

    public String getMyoption() {
      return myoption;
    }

    public void setMyoption(String myoption) {
      this.myoption = myoption;
    }
  }
}