package com.fosspowered.peist.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception with error code 400. */
@ExceptionAttribute(type = ExceptionType.ERROR)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PeistInvalidRequestException extends RuntimeException {
  public PeistInvalidRequestException(String message) {
    super(message);
  }
}
