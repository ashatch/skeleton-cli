package net.andrewhatch.skeletoncli.examples.helloworld;

import net.andrewhatch.skeletoncli.options.groups.Group;

public class HelloWorldRequest {
  private String message;

  @Group("mode")
  private String a;

  @Group("mode")
  private String b;

  public HelloWorldRequest() {}

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
