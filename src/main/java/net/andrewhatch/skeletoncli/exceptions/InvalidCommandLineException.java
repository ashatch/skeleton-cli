package net.andrewhatch.skeletoncli.exceptions;

public class InvalidCommandLineException extends RuntimeException {
  public InvalidCommandLineException(final String message) {
    super(message);
  }
}
