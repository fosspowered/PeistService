package com.fosspowered.peist.util.metrics;

/** Interface which is used to publish service level metrics. */
public interface MetricPublisher {

  /**
   * Publish metric, incrementing the metric by 1.
   *
   * @param metricName Name of the metric.
   */
  void incrementMetric(String metricName);

  /**
   * Publish metric, decrementing the metric by 1.
   *
   * @param metricName Name of the metric.
   */
  void decrementMetric(String metricName);

  /**
   * Publish metric, recording latency metric.
   *
   * @param metricName Name of the metric.
   */
  void recordExecutionTime(String metricName, long timeTaken);

  /**
   * Publish metric, recording gauge..
   *
   * @param metricName Name of the metric.
   */
  void gauge(String metricName, long value);
}
