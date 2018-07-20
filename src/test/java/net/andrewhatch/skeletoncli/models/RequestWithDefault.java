package net.andrewhatch.skeletoncli.models;

public class RequestWithDefault {
  private String notDefaulted;
  private String thisIsDefaulted = "abc";

  public String getNotDefaulted() {
    return notDefaulted;
  }

  public void setNotDefaulted(String notDefaulted) {
    this.notDefaulted = notDefaulted;
  }

  public String getThisIsDefaulted() {
    return thisIsDefaulted;
  }

  public void setThisIsDefaulted(String thisIsDefaulted) {
    this.thisIsDefaulted = thisIsDefaulted;
  }
}
