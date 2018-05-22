package net.andrewhatch.skeletoncli;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import net.andrewhatch.skeletoncli.models.AyOrBee;
import net.andrewhatch.skeletoncli.models.BooleanSwitchParameter;
import net.andrewhatch.skeletoncli.models.NoParameters;
import net.andrewhatch.skeletoncli.models.OneOrTheOther;
import net.andrewhatch.skeletoncli.models.ParametersNumericTypes;
import net.andrewhatch.skeletoncli.models.PathParameters;
import net.andrewhatch.skeletoncli.models.SingleStringParameter;

import org.junit.Test;

import java.nio.file.Files;
import java.util.Optional;

public class ArgumentResolverTest {

  @Test
  public void no_parameters() {
    final ArgumentResolver<NoParameters> resolver = new ArgumentResolver<>(NoParameters.class);
    final String[] args = {};

    assertThat(resolver.resolve(args)).isPresent();
  }

  @Test
  public void string_parameter_resolved() {
    final ArgumentResolver<SingleStringParameter> resolver = new ArgumentResolver<>(SingleStringParameter.class);

    final String message = "antidisestablishmentarianism";
    final String[] args = {
        "--message", message
    };
    Optional<SingleStringParameter> parameters = resolver.resolve(args);

    assertThat(parameters).isPresent();
    assertThat(parameters.get().getMessage()).isEqualTo(message);
  }

  @Test
  public void string_parameter_not_resolved() {
    final ArgumentResolver<SingleStringParameter> resolver = new ArgumentResolver<>(SingleStringParameter.class);

    final String[] args = {};

    Optional<SingleStringParameter> parameters = resolver.resolve(args);
    assertThat(parameters).isNotPresent();
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
    assertThat(parameters).isPresent();
    assertThat(parameters.get().getInteger()).isEqualTo(integerArgument);
    assertThat(parameters.get().getFloatType()).isEqualTo(floatArgument);
    assertThat(parameters.get().getDoubleType()).isEqualTo(doubleArgument);
  }

  @Test
  public void existing_path_parameter() {
    final ArgumentResolver<PathParameters> resolver = new ArgumentResolver<>(PathParameters.class);

    final String[] args = {
        "--mustExist", "."
    };

    final Optional<PathParameters> parameters = resolver.resolve(args);
    assertThat(parameters).isPresent();
    assertThat(Files.exists(parameters.get().getMustExist())).isTrue();
  }

  @Test
  public void missing_path_parameter() {
    final ArgumentResolver<PathParameters> resolver = new ArgumentResolver<>(PathParameters.class);

    final String[] args = {
        "--mustExist", "this_directory_does_not_exist"
    };

    final Optional<PathParameters> parameters = resolver.resolve(args);
    assertThat(parameters).isNotPresent();
  }

  @Test
  public void boolean_switch() {
    final ArgumentResolver<BooleanSwitchParameter> resolver = new ArgumentResolver<>(BooleanSwitchParameter.class);

    final String[] argsOn = {
        "--on"
    };

    final Optional<BooleanSwitchParameter> paramsOn = resolver.resolve(argsOn);
    assertThat(paramsOn).isPresent();
    assertThat(paramsOn.get().isOn()).isTrue();

    final String[] argsOff = {};

    final Optional<BooleanSwitchParameter> paramsOff = resolver.resolve(argsOff);
    assertThat(paramsOff).isPresent();
    assertThat(paramsOff.get().isOn()).isFalse();
  }

  @Test
  public void either_this_or_that() {
    final ArgumentResolver<OneOrTheOther> resolver = new ArgumentResolver<>(OneOrTheOther.class);

    final String[] argsStdin = {
        "--stdin"
    };

    final Optional<OneOrTheOther> stdin = resolver.resolve(argsStdin);
    assertThat(stdin).isPresent();
    assertThat(stdin.get().getMode()).isEqualTo(OneOrTheOther.Mode.STDIN);

    final String[] argsIds = {
        "--ids"
    };

    final Optional<OneOrTheOther> ids = resolver.resolve(argsIds);
    assertThat(ids).isPresent();
    assertThat(ids.get().getMode()).isEqualTo(OneOrTheOther.Mode.IDS);

    final String[] emptyArgs = {};

    final Optional<OneOrTheOther> empty = resolver.resolve(emptyArgs);
    assertThat(empty.isPresent()).isFalse();
  }

  @Test
  public void either_a_or_b() {
    final ArgumentResolver<AyOrBee> resolver = new ArgumentResolver<>(AyOrBee.class);

    final String[] pickA = {
      "--a", "thing"
    };

    final Optional<AyOrBee> resolverForA = resolver.resolve(pickA);

    assertThat(resolverForA).isPresent();
    assertThat(resolverForA.get().getA()).isNotNull();
    assertThat(resolverForA.get().getB()).isNull();

    final String[] pickB = {
        "--b", "thing"
    };

    final Optional<AyOrBee> resolverForB = resolver.resolve(pickB);

    assertThat(resolverForB).isPresent();
    assertThat(resolverForB.get().getA()).isNull();
    assertThat(resolverForB.get().getB()).isNotNull();
  }
}
