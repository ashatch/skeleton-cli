package net.andrewhatch.skeletoncli.models;

import java.nio.file.Path;

public class PathParameters {
  private Path mustExist;

  public Path getMustExist() {
    return mustExist;
  }

  public void setMustExist(Path mustExist) {
    this.mustExist = mustExist;
  }
}
