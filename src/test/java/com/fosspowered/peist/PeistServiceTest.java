package com.fosspowered.peist;

import static org.mockito.Mockito.atLeastOnce;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fosspowered.peist.util.metrics.MetricPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PeistService.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PeistServiceTest {
  @Autowired private MockMvc mvc;

  @MockBean private MetricPublisher metricPublisher;

  @Test
  void test() throws Exception {
    mvc.perform(get("/paste/ping")).andExpect(content().string("pong"));
    Mockito.verify(metricPublisher, atLeastOnce())
        .recordExecutionTime(Mockito.anyString(), Mockito.anyLong());
    Mockito.verify(metricPublisher, atLeastOnce())
        .incrementMetric(Mockito.anyString());
  }

  @Test
  void errorTest() throws Exception {
    mvc.perform(get("/paste/invalidId")).andExpect(status().isNotFound());
  }
}
