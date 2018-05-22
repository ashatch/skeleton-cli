package net.andrewhatch.skeletoncli.examples;

import net.andrewhatch.skeletoncli.options.groups.Group;

public class HelloWorldParameters {
  private String message;

  @Group("mode")
  private String a;

  @Group("mode")
  private String b;

  public HelloWorldParameters() {}

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

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
