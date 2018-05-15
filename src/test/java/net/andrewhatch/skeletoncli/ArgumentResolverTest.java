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

  @Test
  public void numeric_parameters() {
    final ArgumentResolver<ParametersNumericTypes> resolver = new ArgumentResolver<>(ParametersNumericTypes.class);
    int integerArgument = 314159;
    float floatArgument = 3.14159f;
    double doubleArgument = 3.141;
    final String[] args = {
        "--integer", String.valueOf(integerArgument),
        "--floatType", String.valueOf(floatArgument),
        "--doubleType", String.valueOf(doubleArgument)
    };

    Optional<ParametersNumericTypes> parameters = resolver.resolve(args);
    assertThat(parameters.isPresent()).isTrue();
    assertThat(parameters.get().getInteger()).isEqualTo(integerArgument);
    assertThat(parameters.get().getFloatType()).isEqualTo(floatArgument);
    assertThat(parameters.get().getDoubleType()).isEqualTo(doubleArgument);
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

  public static class ParametersNumericTypes {
    private int integer;
    private float floatType;
    private double doubleType;

    public int getInteger() {
      return integer;
    }

    public void setInteger(int integer) {
      this.integer = integer;
    }

    public float getFloatType() {
      return floatType;
    }

    public void setFloatType(float floatType) {
      this.floatType = floatType;
    }

    public double getDoubleType() {
      return doubleType;
    }

    public void setDoubleType(double doubleType) {
      this.doubleType = doubleType;
    }
  }
}
