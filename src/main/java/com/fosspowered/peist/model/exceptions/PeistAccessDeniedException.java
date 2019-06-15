package com.fosspowered.peist.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception with error code 403. */
@ExceptionAttribute(type = ExceptionType.ERROR)
@ResponseStatus(HttpStatus.FORBIDDEN)
public class PeistAccessDeniedException extends RuntimeException {
  public PeistAccessDeniedException(String message) {
    super(message);
  }
}
