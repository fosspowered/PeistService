package com.fosspowered.peist.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fosspowered.peist.model.exceptions.PeistAccessDeniedException;
import com.fosspowered.peist.model.exceptions.PeistInternalException;
import com.fosspowered.peist.model.exceptions.PeistInvalidRequestException;
import com.fosspowered.peist.model.exceptions.PeistNotFoundException;
import com.fosspowered.peist.model.json.PasteRequest;
import com.fosspowered.peist.model.json.PasteResponse;
import com.fosspowered.peist.service.CRUDService;
import com.fosspowered.peist.util.converter.PasteDataExchangeConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import testutils.DummyPasteRequest;
import testutils.DummyPasteResponse;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PasteController.class)
class PasteControllerSpringTest {
  private static final String[] requestResponseColumnsToIgnore = {
    "urlId", "creationDate", "expiryDate", "secretKey"
  };

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private CRUDService crudService;

  @BeforeEach
  void setUp() {
    given(crudService.addPaste(any(PasteRequest.class)))
        .willAnswer(i -> buildResponseFromRequest((PasteRequest) i.getArguments()[0]));
  }

  @Test
  void whenPing_Normal_ReturnPong() throws Exception {
    mvc.perform(get("/paste/ping")).andExpect(content().string("pong"));
  }

  @Test
  void whenAddPaste_ValidRequest_PersistAndReturnPaste() throws Exception {
    PasteRequest pasteRequest = new DummyPasteRequest().build();

    MvcResult mvcResult =
        mvc.perform(
                post("/paste")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pasteRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    PasteResponse pasteResponse = fetchPasteResponse(mvcResult);

    assertThat(pasteResponse).isNotNull();
    assertThat(pasteResponse)
        .isEqualToIgnoringGivenFields(pasteRequest, requestResponseColumnsToIgnore);
  }

  @Test
  void whenGetPaste_PasteExists_ReturnsPaste() throws Exception {
    String urlId = "urlId";
    PasteResponse mockPasteResponse = new DummyPasteResponse().setUrlId(urlId).build();
    given(crudService.fetchPaste(eq(urlId), anyString())).willReturn(mockPasteResponse);

    MvcResult mvcResult =
        mvc.perform(get("/paste/" + urlId)).andExpect(status().isOk()).andReturn();

    PasteResponse pasteResponse = fetchPasteResponse(mvcResult);
    assertThat(pasteResponse).isEqualTo(mockPasteResponse);
  }

  @Test
  void whenGetPaste_NoPasteExists_Shows404() throws Exception {
    given(crudService.fetchPaste(anyString(), anyString())).willThrow(PeistNotFoundException.class);

    mvc.perform(get("/paste/not")).andExpect(status().isNotFound());
  }

  @Test
  void whenGetPaste_InvalidRequest_Shows400() throws Exception {
    given(crudService.fetchPaste(eq(" "), anyString())).willThrow(PeistInvalidRequestException.class);
    mvc.perform(get("/paste/ ")).andExpect(status().isBadRequest());
  }

  @Test
  void whenGetPaste_ErrorInService_Shows500() throws Exception {
    given(crudService.fetchPaste(anyString(), anyString())).willThrow(PeistInternalException.class);
    mvc.perform(get("/paste/id")).andExpect(status().isInternalServerError());
  }

  @Test
  void whenPostPasteSecret_ValidPaste_ReturnsPaste() throws Exception {
    String urlId = "urlId";
    String secretKey = "secretKey";
    PasteResponse mockPasteResponse = new DummyPasteResponse().setUrlId(urlId).build();
    given(crudService.fetchPaste(eq(urlId), eq(secretKey))).willReturn(mockPasteResponse);

    MvcResult mvcResult =
        mvc.perform(post("/paste/" + urlId).content(secretKey))
            .andExpect(status().isOk())
            .andReturn();

    PasteResponse pasteResponse = fetchPasteResponse(mvcResult);
    assertThat(pasteResponse).isEqualTo(mockPasteResponse);
  }

  @Test
  void whenPostPasteSecret_ValidPasteWrongKey_Shows403() throws Exception {
    given(crudService.fetchPaste(anyString(), anyString()))
        .willThrow(PeistAccessDeniedException.class);
    mvc.perform(post("/paste/id").content("secretKey")).andExpect(status().isForbidden());
  }

  @Test
  void whenPostPasteSecret_NoPasteExists_Shows404() throws Exception {
    given(crudService.fetchPaste(anyString(), anyString())).willThrow(PeistNotFoundException.class);

    mvc.perform(post("/paste/id").content("secretKey")).andExpect(status().isNotFound());
  }

  @Test
  void whenPostPasteSecret_InvalidRequest_Shows400() throws Exception {
    given(crudService.fetchPaste(eq(" "), anyString()))
        .willThrow(PeistInvalidRequestException.class);
    mvc.perform(post("/paste/ ").content("secretKey")).andExpect(status().isBadRequest());
  }

  @Test
  void whenPostPasteSecret_ErrorInService_Shows500() throws Exception {
    given(crudService.fetchPaste(anyString(), anyString())).willThrow(PeistInternalException.class);
    mvc.perform(post("/paste/id").content("secretKey")).andExpect(status().isInternalServerError());
  }

  private PasteResponse fetchPasteResponse(MvcResult mvcResult) throws java.io.IOException {
    return objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(), PasteResponse.class);
  }

  private PasteResponse buildResponseFromRequest(PasteRequest pasteRequest) {
    PasteDataExchangeConverter pasteDataExchangeConverter = spy(PasteDataExchangeConverter.class);
    return pasteDataExchangeConverter.createResponseFromDao(
        pasteDataExchangeConverter.createDaoFromRequest(pasteRequest));
  }
}
