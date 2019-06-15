package com.fosspowered.peist.util.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.fosspowered.peist.model.dao.Paste;
import com.fosspowered.peist.model.json.PasteRequest;
import com.fosspowered.peist.model.json.PasteResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testutils.DummyPaste;
import testutils.DummyPasteRequest;

class PasteDataExchangeConverterTest {
  private static PasteDataExchangeConverter converter;

  @BeforeAll
  static void init() {
    converter = new PasteDataExchangeConverter();
  }

  @Test
  void createDaoFromRequest() {
    DummyPasteRequest dummyPasteRequest = new DummyPasteRequest();
    PasteRequest pasteRequest = dummyPasteRequest.build();
    Paste paste = converter.createDaoFromRequest(pasteRequest);
    assertThat(paste).isNotNull();
    assertThat(paste.getId()).isNull();
    assertThat(paste.getUrlId()).isNull();
    assertThat(paste.getCreationDate()).isNull();
    assertThat(paste.getExpiryDate()).isNull();
    assertThat(paste)
        .isEqualToComparingOnlyGivenFields(
            pasteRequest, "title", "author", "language", "isVisible", "pasteData", "secretKey");
  }

  @Test
  void createResponseFromDao() {
    DummyPaste dummyPaste = new DummyPaste();
    Paste paste = dummyPaste.build();
    PasteResponse pasteResponse = converter.createResponseFromDao(paste);
    assertThat(pasteResponse).isNotNull();
    assertThat(pasteResponse)
        .isEqualToComparingOnlyGivenFields(
            paste,
            "urlId",
            "title",
            "author",
            "language",
            "creationDate",
            "expiryDate",
            "pasteData");
  }
}
