package com.fosspowered.peist.util.metrics;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MetricPublisherTest {
  private static MetricPublisher metricPublisher;

  @BeforeAll
  static void init() {
    metricPublisher =
        new StatsDMetricPublisher("prefix", "devo", "india", "localhost", 8125, false);
  }

  @Test
  void test() {
    String metricName = "metricName";
    metricPublisher.incrementMetric(metricName);
    metricPublisher.decrementMetric(metricName);
    metricPublisher.recordExecutionTime(metricName, 100L);
    metricPublisher.gauge(metricName, 500);
  }
}
