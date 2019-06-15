package com.fosspowered.peist.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fosspowered.peist.model.json.PasteRequest;
import com.fosspowered.peist.model.json.PasteResponse;
import com.fosspowered.peist.service.CRUDService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PasteControllerTest {
  private PasteController pasteController;

  private CRUDService mockCrudService;

  @BeforeEach
  void init() {
    mockCrudService = mock(CRUDService.class);
    pasteController = new PasteController(mockCrudService);
    given(mockCrudService.fetchRecentPastes())
        .willReturn(Collections.singletonList(PasteResponse.builder().build()));
    given(mockCrudService.fetchPaste(Mockito.anyString(), Mockito.anyString()))
        .willReturn(PasteResponse.builder().build());
    given(mockCrudService.addPaste(Mockito.any(PasteRequest.class)))
        .willReturn(PasteResponse.builder().build());
  }

  @Test
  void all_getPing_ReturnsPong() {
    String response = pasteController.getPing();
    assertThat(response).isEqualTo("pong");
  }

  @Test
  void all_getAllPaste_ReturnAllPaste() {
    List<PasteResponse> allPaste = pasteController.getAllPaste();
    assertThat(allPaste).isNotEmpty();
    verify(mockCrudService, times(1)).fetchRecentPastes();
  }

  @Test
  void whenPasteExists_getPaste_ReturnsPaste() {
    String urlId = "urlId";
    PasteResponse paste = pasteController.getPaste(urlId);
    assertThat(paste).isNotNull();
    verify(mockCrudService, times(1)).fetchPaste(eq(urlId), anyString());
  }

  @Test
  void whenPasteExists_getSecretPaste_ReturnsPaste() {
    String urlId = "urlId";
    String key = "secret";
    PasteResponse secretPaste = pasteController.getSecretPaste(urlId, key);
    assertThat(secretPaste).isNotNull();
    verify(mockCrudService, times(1)).fetchPaste(urlId, key);
  }

  @Test
  void whenPasteExistsall_addPaste_PersistsPaste() {
    PasteRequest pasteRequest = PasteRequest.builder().build();
    PasteResponse pasteResponse = pasteController.addPaste(pasteRequest);
    assertThat(pasteResponse).isNotNull();
    verify(mockCrudService, times(1)).addPaste(pasteRequest);
  }
}
