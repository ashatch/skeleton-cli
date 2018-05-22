package net.andrewhatch.skeletoncli.models;

import net.andrewhatch.skeletoncli.options.groups.Group;

public class AyOrBee {

  @Group("mode")
  private String a;

  @Group("mode")
  private String b;

  public String getA() {
    return a;
  }

  public void setA(String a) {
    this.a = a;
  }

  public String getB() {
    return b;
  }

  public void setB(String b) {
    this.b = b;
  }
}

