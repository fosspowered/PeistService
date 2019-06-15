package com.fosspowered.peist.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception with error code 404. */
@ExceptionAttribute(type = ExceptionType.ERROR)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PeistNotFoundException extends RuntimeException {
  public PeistNotFoundException(String message) {
    super(message);
  }
}
