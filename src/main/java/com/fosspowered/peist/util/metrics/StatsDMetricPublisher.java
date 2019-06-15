package com.fosspowered.peist.util.metrics;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** StatsD implementation of the Metric Publisher. */
@Component
@Log4j2
public class StatsDMetricPublisher implements MetricPublisher {
  public static final String METRIC_DELIMITER = ".";

  private static final String PREFIX_FORMAT = // "%s|%s|%s
      "%s" + METRIC_DELIMITER + "%s" + METRIC_DELIMITER + "%s";

  private final StatsDClient statsD;

  StatsDMetricPublisher(
      @Value("${statsd.prefix}") String servicePrefix,
      @Value("${stage}") String stage,
      @Value("${region}") String region,
      @Value("${statsd.host}") String host,
      @Value("${statsd.port}") Integer port,
      @Value("${statsd.enabled}") Boolean isEnabled) {

    if (isEnabled) {
      String prefix = String.format(PREFIX_FORMAT, stage, region, servicePrefix);
      this.statsD = new NonBlockingStatsDClient(prefix, host, port);
      log.info("Sending statsd metrics to: {}, {}", host, port);
    } else {
      this.statsD = new NoOpStatsDClient();
      log.info("No ops statsd metric publisher initialized");
    }
  }

  @Override
  public void incrementMetric(String metricName) {
    statsD.incrementCounter(metricName);
  }

  @Override
  public void decrementMetric(String metricName) {
    statsD.incrementCounter(metricName);
  }

  @Override
  public void recordExecutionTime(String metricName, long timeTaken) {
    statsD.recordExecutionTime(metricName, timeTaken);
  }

  @Override
  public void gauge(String metricName, long value) {
    statsD.gauge(metricName, value);
  }
}
