package com.fosspowered.peist.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception with error code 500. */
@ExceptionAttribute(type = ExceptionType.FAULT)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PeistInternalException extends RuntimeException {
  public PeistInternalException(String message) {
    super(message);
  }
}
