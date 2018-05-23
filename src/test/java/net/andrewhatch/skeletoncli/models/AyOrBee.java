package net.andrewhatch.skeletoncli.models;

import net.andrewhatch.skeletoncli.options.groups.Group;

public class AyOrBee {

  @Group("mode")
  private String a;

  @Group("mode")
  private boolean b;

  public String getA() {
    return a;
  }

  public void setA(String a) {
    this.a = a;
  }

  public boolean isB() {
    return b;
  }

  public void setB(boolean b) {
    this.b = b;
  }

}

