package com.fosspowered.peist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fosspowered.peist.model.dao.Paste;
import com.fosspowered.peist.model.exceptions.PeistAccessDeniedException;
import com.fosspowered.peist.model.exceptions.PeistInvalidRequestException;
import com.fosspowered.peist.model.exceptions.PeistNotFoundException;
import com.fosspowered.peist.model.json.PasteRequest;
import com.fosspowered.peist.model.json.PasteResponse;
import com.fosspowered.peist.repository.PasteRepository;
import com.fosspowered.peist.util.converter.PasteDataExchangeConverter;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import testutils.DummyPaste;
import testutils.DummyPasteRequest;

class CRUDServiceTest {
  private static final String[] requestResponseColumnsToIgnore = {
    "urlId", "creationDate", "expiryDate", "secretKey"
  };

  private static final String[] responseDaoFieldsToIgnore = {"id", "secretKey", "isVisible"};

  private CRUDService crudService;

  private PasteRepository pasteRepository;
  private EncryptService encryptService;

  @BeforeEach
  void setUp() {
    pasteRepository = mock(PasteRepository.class);
    encryptService = mock(EncryptService.class);
    PasteDataExchangeConverter pasteDataExchangeConverter = spy(PasteDataExchangeConverter.class);

    when(pasteRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    doNothing().when(encryptService).encrypt(any());

    crudService = new CRUDService(pasteRepository, pasteDataExchangeConverter, encryptService);
  }

  @Test
  void all_addPaste_EncryptsAndPersistsPaste() {
    PasteRequest pasteRequest = new DummyPasteRequest().build();

    PasteResponse pasteResponse = crudService.addPaste(pasteRequest);

    assertThat(pasteResponse).isNotNull();
    assertThat(pasteResponse.getUrlId()).isNotEmpty();
    LocalDateTime now = LocalDateTime.now();
    assertThat(pasteResponse.getCreationDate()).isBefore(now).isAfter(now.minusSeconds(5));
    LocalDateTime localDateTime = now.plusSeconds(pasteRequest.getTtl());
    assertThat(pasteResponse.getCreationDate())
        .isBefore(localDateTime)
        .isAfter(now.minusSeconds(5));
    assertThat(pasteResponse)
        .isEqualToIgnoringGivenFields(pasteRequest, requestResponseColumnsToIgnore);

    verify(encryptService, times(1)).encrypt(any());
    verify(encryptService, times(1)).decrypt(any());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "test")
  void pasteExists_fetchPaste_DecryptsAndFetchesPaste(String secretKey) {
    String urlId = "urlId";
    Paste paste = new DummyPaste().setUrlId(urlId).setSecretKey(secretKey).build();
    when(pasteRepository.findByUrlId(eq(urlId))).thenReturn(Optional.of(paste));

    PasteResponse pasteResponse = crudService.fetchPaste(urlId, secretKey);

    assertThat(pasteResponse).isNotNull();
    assertThat(pasteResponse).isEqualToIgnoringGivenFields(paste, responseDaoFieldsToIgnore);
    verify(encryptService, times(1)).decrypt(eq(paste));
  }

  @Test
  void wrongKey_fetchPaste_ThrowException() {
    doThrow(PeistAccessDeniedException.class).when(encryptService).decrypt(any(Paste.class));
    when(pasteRepository.findByUrlId(any())).thenReturn(Optional.of(new DummyPaste().build()));

    assertThatThrownBy(() -> crudService.fetchPaste("urlId", "wrongKey"))
        .isExactlyInstanceOf(PeistAccessDeniedException.class);
  }

  @Test
  void pasteNotExists_fetchPaste_ThrowException() {
    when(pasteRepository.findByUrlId(anyString())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> crudService.fetchPaste("urlId", ""))
        .isExactlyInstanceOf(PeistNotFoundException.class);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "  "})
  void invalidRequest_fetchPaste_ThrowException(String url) {
    assertThatThrownBy(() -> crudService.fetchPaste(url, ""))
        .isExactlyInstanceOf(PeistInvalidRequestException.class);
  }

  @Test
  void all_fetchAllPastes_ReturnsPastes() {
    int size = 5;
    List<Paste> dummyPasteList = new DummyPaste().setVisible(true).buildList(size);
    when(pasteRepository.findAll()).thenReturn(dummyPasteList);

    List<PasteResponse> pasteResponses = crudService.fetchRecentPastes();

    assertThat(pasteResponses).isNotEmpty().hasSize(size);

    final List<Paste> sortedPasteList =
        dummyPasteList.stream()
            .sorted(Comparator.comparing(Paste::getUrlId))
            .collect(Collectors.toList());
    final List<PasteResponse> sortedPasteResponseList =
        pasteResponses.stream()
            .sorted(Comparator.comparing(PasteResponse::getUrlId))
            .collect(Collectors.toList());
    for (int i = 0; i < size; i++) {
      assertThat(sortedPasteResponseList.get(i))
          .isNotNull()
          .isEqualToIgnoringGivenFields(sortedPasteList.get(i), responseDaoFieldsToIgnore);
    }
  }
}
