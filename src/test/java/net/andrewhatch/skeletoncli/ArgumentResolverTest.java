package net.andrewhatch.skeletoncli;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ArgumentResolverTest {

  @Test
  public void no_parameters() {

    final ArgumentResolver<NoParameters> resolver = new ArgumentResolver<>(NoParameters.class);
    final String[] args = {};

    assertThat(resolver.resolve(args).isPresent()).isTrue();
  }

  @Test
  public void string_parameter_resolved() {

    final ArgumentResolver<SingleStringParameter> resolver = new ArgumentResolver<>(SingleStringParameter.class);
    final String message = "antidisestablishmentarianism";
    final String[] args = {
        "--message", message
    };

    Optional<SingleStringParameter> parameters = resolver.resolve(args);
    assertThat(parameters.isPresent()).isTrue();
    assertThat(parameters.get().getMessage()).isEqualTo(message);
  }

  @Test
  public void string_parameter_not_resolved() {
    final ArgumentResolver<SingleStringParameter> resolver = new ArgumentResolver<>(SingleStringParameter.class);
    final String[] args = {};

    Optional<SingleStringParameter> parameters = resolver.resolve(args);
    assertThat(parameters.isPresent()).isFalse();
  }

  public static class NoParameters {
  }

  public static class SingleStringParameter {
    private String message;

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
}
