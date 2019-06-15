package com.fosspowered.peist.model.exceptions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * {@code ExceptionAttribute} annotation is used to annotate the exception class whether it's FAULT,
 * ERROR, or FAILURE. This annotation is used by metric interceptor to publish metrics.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionAttribute {
  /**
   * Type of the Exception - FAULT, ERROR, or FAILURE.
   *
   * @return Type of exception.
   */
  ExceptionType type();
}
