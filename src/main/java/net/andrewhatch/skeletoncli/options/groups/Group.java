package net.andrewhatch.skeletoncli.options.groups;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Group {
  String value();
}
