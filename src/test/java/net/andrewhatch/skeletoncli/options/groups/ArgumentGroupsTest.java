package net.andrewhatch.skeletoncli.options.groups;


import static org.assertj.core.api.Assertions.assertThat;

import net.andrewhatch.skeletoncli.models.AyOrBee;

import org.junit.Test;

import java.util.Set;

public class ArgumentGroupsTest {

  @Test
  public void groups() {
    final AyOrBee request = new AyOrBee();

    final Set<String> groups =
        ArgumentGroups.from(request)
            .groups();

    assertThat(groups).containsExactly("mode");
  }

  @Test
  public void fields() {
    final AyOrBee request = new AyOrBee();

    final Set<String> fields =
        ArgumentGroups.from(request)
            .fields("mode");

    assertThat(fields).containsExactly("a", "b");
  }

  @Test
  public void groupForField() {
    final AyOrBee request = new AyOrBee();

    final ArgumentGroups<AyOrBee> argumentGroups = ArgumentGroups.from(request);

    assertThat(argumentGroups.groupForField("a")).isPresent();
    assertThat(argumentGroups.groupForField("a").get()).isEqualTo("mode");
    assertThat(argumentGroups.groupForField("b")).isPresent();
    assertThat(argumentGroups.groupForField("b").get()).isEqualTo("mode");
    assertThat(argumentGroups.groupForField("z")).isNotPresent();
  }
}
