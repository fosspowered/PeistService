package com.fosspowered.peist.util.converter;

import com.fosspowered.peist.model.dao.Paste;
import com.fosspowered.peist.model.json.PasteRequest;
import com.fosspowered.peist.model.json.PasteResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/** Convert to and from Paste Request/Response objects to DAO * */
@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PasteDataExchangeConverter {

  /**
   * Convert Request into DAO.
   *
   * @param pasteRequest Paste Request.
   * @return Paste DAO object.
   */
  public Paste createDaoFromRequest(PasteRequest pasteRequest) {
    Paste paste = new Paste();
    paste.setTitle(pasteRequest.getTitle());
    paste.setAuthor(pasteRequest.getAuthor());
    paste.setLanguage(pasteRequest.getLanguage());
    paste.setSecretKey(pasteRequest.getSecretKey());
    paste.setIsVisible(pasteRequest.getIsVisible());
    paste.setPasteData(pasteRequest.getPasteData());
    return paste;
  }

  /**
   * Convert DAO into Response.
   *
   * @param paste Paste DAO object.
   * @return Paste Response.
   */
  public PasteResponse createResponseFromDao(Paste paste) {
    return PasteResponse.builder()
        .urlId(paste.getUrlId())
        .creationDate(paste.getCreationDate())
        .author(paste.getAuthor())
        .language(paste.getLanguage())
        .title(paste.getTitle())
        .expiryDate(paste.getExpiryDate())
        .pasteData(paste.getPasteData())
        .build();
  }
}
