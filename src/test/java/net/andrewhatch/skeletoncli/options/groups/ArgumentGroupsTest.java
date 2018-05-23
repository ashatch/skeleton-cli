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
        PropertyGroups.from(request)
            .groups();

    assertThat(groups).containsExactly("mode");
  }

  @Test
  public void fields() {
    final AyOrBee request = new AyOrBee();

    final Set<String> fields =
        PropertyGroups.from(request)
            .fields("mode");

    assertThat(fields).containsExactly("a", "b");
  }

  @Test
  public void groupForField() {
    final AyOrBee request = new AyOrBee();

    final PropertyGroups<AyOrBee> propertyGroups = PropertyGroups.from(request);

    assertThat(propertyGroups.groupForField("a")).isPresent();
    assertThat(propertyGroups.groupForField("a").get()).isEqualTo("mode");
    assertThat(propertyGroups.groupForField("b")).isPresent();
    assertThat(propertyGroups.groupForField("b").get()).isEqualTo("mode");
    assertThat(propertyGroups.groupForField("z")).isNotPresent();
  }
}
