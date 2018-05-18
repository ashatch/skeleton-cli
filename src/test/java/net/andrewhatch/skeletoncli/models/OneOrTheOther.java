package net.andrewhatch.skeletoncli.models;

public class OneOrTheOther {
  private Mode mode;

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public enum Mode {
    STDIN,
    IDS
  }
}
