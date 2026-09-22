package com.libreria.universidad.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String msg) {
    super(msg);
  }
}
