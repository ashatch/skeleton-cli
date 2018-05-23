package net.andrewhatch.skeletoncli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CliBuilderTest {

  @Test
  public void consumer_is_executed_for_valid_request() {
    final String[] argv = {};

    CliBuilder.from(argv, SimpleRequest.class).run(consumer -> assertThat(consumer).isNotNull());
  }

  @Test
  public void request_object_is_present_for_valid_request() {
    final String[] argv = {};

    assertThat(CliBuilder.from(argv, SimpleRequest.class).requestBean()).isPresent();
  }

  @Test
  public void exit_status_code_is_zero_on_success() {
    final String[] argv = {
        "--myoption", "myvalue"
    };

    final int exitStatusCode = CliBuilder.from(argv, RequestWithRequiredProperty.class)
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

    final int exitStatusCode = CliBuilder.from(argv, RequestWithRequiredProperty.class)
        .run(consumer -> {
          // do nothing
        })
        .getExitStatusCode();

    assertThat(exitStatusCode).isNotEqualTo(0);
  }

  public static class SimpleRequest {}

  public static class RequestWithRequiredProperty {
    private String myoption;

    public String getMyoption() {
      return myoption;
    }

    public void setMyoption(String myoption) {
      this.myoption = myoption;
    }
  }
}