package net.andrewhatch.skeletoncli.options.groups;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertyGroups<T> {

  private final List<Field> groupFields;

  private PropertyGroups(T obj) {
    this.groupFields = FieldUtils.getFieldsListWithAnnotation(obj.getClass(), Group.class);
  }

  public static <R> PropertyGroups<R> from(R obj) {
    return new PropertyGroups<>(obj);
  }

  public Set<String> groups() {
    final Map<String, List<Field>> fieldsByGroupName = this.groupFields.stream()
        .collect(Collectors.groupingBy(field -> field.getAnnotation(Group.class).value()));

    return fieldsByGroupName.keySet();
  }

  public Set<String> fields(final String groupName) {
    return this.groupFields.stream()
        .filter(field -> groupName.equals(field.getAnnotation(Group.class).value()))
        .map(Field::getName)
        .collect(Collectors.toSet());
  }

  public Optional<String> groupForField(final String fieldName) {
    return groupFields.stream()
        .filter(field -> fieldName.equals(field.getName()))
        .map(field -> field.getAnnotation(Group.class).value())
        .findFirst();
  }
}
