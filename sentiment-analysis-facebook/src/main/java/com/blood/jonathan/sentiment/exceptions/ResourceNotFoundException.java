package com.blood.jonathan.sentiment.exceptions;

/**
 * @author Jonathan Blood
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
