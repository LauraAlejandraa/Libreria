package com.libreria.universidad.exception;

public class DuplicateResourceException extends RuntimeException {
  public DuplicateResourceException(String msg) {
    super(msg);
  }
}
