package net.andrewhatch.skeletoncli;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import net.andrewhatch.skeletoncli.models.AyOrBee;
import net.andrewhatch.skeletoncli.models.BooleanSwitchRequest;
import net.andrewhatch.skeletoncli.models.RequestWithDefault;
import net.andrewhatch.skeletoncli.models.RequestWithNoProperties;
import net.andrewhatch.skeletoncli.models.OneOrTheOther;
import net.andrewhatch.skeletoncli.models.RequestWithNumericTypes;
import net.andrewhatch.skeletoncli.models.RequestWithPath;
import net.andrewhatch.skeletoncli.models.RequestWithSingleString;

import org.junit.Test;

import java.nio.file.Files;
import java.util.Optional;

public class RequestResolverTest {

  @Test
  public void empty_properties_request_works() {
    final RequestResolver<RequestWithNoProperties> resolver = new RequestResolver<>(RequestWithNoProperties.class);
    final String[] argsv = {};

    assertThat(resolver.resolve(argsv)).isPresent();
  }

  @Test
  public void string_property_resolves_with_correct_value() {
    final RequestResolver<RequestWithSingleString> resolver = new RequestResolver<>(RequestWithSingleString.class);

    final String message = "antidisestablishmentarianism";
    final String[] argsv = {
        "--message", message
    };
    Optional<RequestWithSingleString> requestObject = resolver.resolve(argsv);

    assertThat(requestObject).isPresent();
    assertThat(requestObject.get().getMessage()).isEqualTo(message);
  }

  @Test
  public void missing_property_is_not_resolved() {
    final RequestResolver<RequestWithSingleString> resolver = new RequestResolver<>(RequestWithSingleString.class);

    final String[] args = {};

    Optional<RequestWithSingleString> request = resolver.resolve(args);
    assertThat(request).isNotPresent();
  }

  @Test
  public void numeric_properties() {
    final RequestResolver<RequestWithNumericTypes> resolver = new RequestResolver<>(RequestWithNumericTypes.class);

    int integerProperty = 314159;
    float floatProperty = 3.14159f;
    double doubleProperty = 3.141;
    final String[] args = {
        "--integer", String.valueOf(integerProperty),
        "--floatType", String.valueOf(floatProperty),
        "--doubleType", String.valueOf(doubleProperty)
    };

    Optional<RequestWithNumericTypes> request = resolver.resolve(args);
    assertThat(request).isPresent();
    assertThat(request.get().getInteger()).isEqualTo(integerProperty);
    assertThat(request.get().getFloatType()).isEqualTo(floatProperty);
    assertThat(request.get().getDoubleType()).isEqualTo(doubleProperty);
  }

  @Test
  public void existing_path_property() {
    final RequestResolver<RequestWithPath> resolver = new RequestResolver<>(RequestWithPath.class);

    final String[] args = {
        "--mustExist", "."
    };

    final Optional<RequestWithPath> request = resolver.resolve(args);
    assertThat(request).isPresent();
    assertThat(Files.exists(request.get().getMustExist())).isTrue();
  }

  @Test
  public void missing_path_property() {
    final RequestResolver<RequestWithPath> resolver = new RequestResolver<>(RequestWithPath.class);

    final String[] args = {
        "--mustExist", "this_directory_does_not_exist"
    };

    final Optional<RequestWithPath> request = resolver.resolve(args);
    assertThat(request).isNotPresent();
  }

  @Test
  public void boolean_switch() {
    final RequestResolver<BooleanSwitchRequest> resolver = new RequestResolver<>(BooleanSwitchRequest.class);

    final String[] argsOn = {
        "--on"
    };

    final Optional<BooleanSwitchRequest> requestWithOn = resolver.resolve(argsOn);
    assertThat(requestWithOn).isPresent();
    assertThat(requestWithOn.get().isOn()).isTrue();

    final String[] argsOff = {};

    final Optional<BooleanSwitchRequest> requestWithoutOn = resolver.resolve(argsOff);
    assertThat(requestWithoutOn).isPresent();
    assertThat(requestWithoutOn.get().isOn()).isFalse();
  }

  @Test
  public void either_this_or_that() {
    final RequestResolver<OneOrTheOther> resolver = new RequestResolver<>(OneOrTheOther.class);

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
    final RequestResolver<AyOrBee> resolver = new RequestResolver<>(AyOrBee.class);

    final String[] pickA = {
      "--a", "thing"
    };

    final Optional<AyOrBee> resolverForA = resolver.resolve(pickA);

    assertThat(resolverForA).isPresent();
    assertThat(resolverForA.get().getA()).isNotNull();
    assertThat(resolverForA.get().isB()).isFalse();

    final String[] pickB = {
        "--b"
    };

    final Optional<AyOrBee> resolverForB = resolver.resolve(pickB);

    assertThat(resolverForB).isPresent();
    assertThat(resolverForB.get().getA()).isNull();
    assertThat(resolverForB.get().isB()).isNotNull();
  }

  @Test
  public void test_default_remains_intact() {
    final RequestResolver<RequestWithDefault> resolver = new RequestResolver<>(RequestWithDefault.class);

    final String[] pickA = {
        "--notDefaulted", "def"
    };

    final Optional<RequestWithDefault> request = resolver.resolve(pickA);

    assertThat(request).isPresent();
    assertThat(request.get().getThisIsDefaulted()).isEqualTo("abc");
    assertThat(request.get().getNotDefaulted()).isEqualTo("def");
  }

  @Test
  public void can_override_default() {
    final RequestResolver<RequestWithDefault> resolver = new RequestResolver<>(RequestWithDefault.class);

    final String[] pickA = {
        "--notDefaulted", "def",
        "--thisIsDefaulted", "123"
    };

    final Optional<RequestWithDefault> request = resolver.resolve(pickA);

    assertThat(request).isPresent();
    assertThat(request.get().getThisIsDefaulted()).isEqualTo("123");
    assertThat(request.get().getNotDefaulted()).isEqualTo("def");
  }
}
