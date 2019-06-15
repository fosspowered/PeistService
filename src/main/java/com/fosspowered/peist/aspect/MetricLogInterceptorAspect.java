package com.fosspowered.peist.aspect;

import static com.fosspowered.peist.util.metrics.StatsDMetricPublisher.METRIC_DELIMITER;

import com.fosspowered.peist.model.exceptions.ExceptionAttribute;
import com.fosspowered.peist.model.exceptions.ExceptionType;
import com.fosspowered.peist.util.metrics.MetricPublisher;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This Aspect intercepts all public methods annotated with {@code MetricInterceptor} and performs
 * the actions of logging and publish metrics.
 */
@Aspect
@Component
@Log4j2
public class MetricLogInterceptorAspect {
  private static final String API_METRIC_FORMAT = "%s" + METRIC_DELIMITER + "%s";
  private static final String SUCCESS_LOG_FORMAT = "Request: {}, Response: {}";
  private static final String ERROR_LOG_FORMAT = "Request: {}, Exception: {}";
  private static final String LATENCY_LOG_FORMAT = "{} took {} ms";

  private static final String SUCCESS_SUFFIX = "Success";
  private static final String TIME_SUFFIX = "Time";
  private static final String FAULT_SUFFIX = "Fault";
  private static final String ERROR_SUFFIX = "Error";
  private static final String FAILURE_SUFFIX = "Failure";
  private static final String EXCEPTION_SUFFIX = "Exception";

  private final MetricPublisher metricPublisher;

  @Autowired
  MetricLogInterceptorAspect(MetricPublisher metricPublisher) {
    this.metricPublisher = metricPublisher;
  }

  @Around(
      "@annotation(com.fosspowered.peist.aspect.MetricInterceptor) && execution(public * *(..))")
  public Object time(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    long start = System.currentTimeMillis();

    Object[] args = proceedingJoinPoint.getArgs();
    String apiName =
        String.format(
            API_METRIC_FORMAT,
            proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName(),
            proceedingJoinPoint.getSignature().getName());

    Object value;
    try {
      value = proceedingJoinPoint.proceed();
      log.info(SUCCESS_LOG_FORMAT, Arrays.toString(args), value);
      metricPublisher.incrementMetric(String.format(API_METRIC_FORMAT, apiName, SUCCESS_SUFFIX));
    } catch (Throwable t) {
      metricPublisher.incrementMetric(String.format(API_METRIC_FORMAT, apiName, EXCEPTION_SUFFIX));
      metricPublisher.incrementMetric(
          String.format(API_METRIC_FORMAT, apiName, getExceptionMetricSuffix(t)));
      log.info(ERROR_LOG_FORMAT, Arrays.toString(args), t.getMessage());
      throw t;
    } finally {
      long duration = System.currentTimeMillis() - start;
      log.info(LATENCY_LOG_FORMAT, apiName, duration);
      metricPublisher.recordExecutionTime(
          String.format(API_METRIC_FORMAT, apiName, TIME_SUFFIX), duration);
    }

    return value;
  }

  private String getExceptionMetricSuffix(Throwable t) {
    Class<? extends Throwable> clazz = t.getClass();
    if (!clazz.isAnnotationPresent(ExceptionAttribute.class)) {
      return FAILURE_SUFFIX;
    }
    ExceptionAttribute exceptionAttribute = clazz.getAnnotation(ExceptionAttribute.class);
    ExceptionType type = exceptionAttribute.type();
    switch (type) {
      case FAULT:
        return FAULT_SUFFIX;
      case ERROR:
        return ERROR_SUFFIX;
      case FAILURE:
      default:
        return FAILURE_SUFFIX;
    }
  }
}
