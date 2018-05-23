package net.andrewhatch.skeletoncli.options;

import java.beans.PropertyDescriptor;

public class EnumPropertyType {

  @SuppressWarnings("unchecked")
  public static Class<? extends Enum> of(final PropertyDescriptor enumPropertyDescriptor) {
    if (!enumPropertyDescriptor.getPropertyType().isEnum()) {
      throw new IllegalArgumentException();
    }

    return (Class<? extends Enum>) enumPropertyDescriptor.getPropertyType();
  }
}
