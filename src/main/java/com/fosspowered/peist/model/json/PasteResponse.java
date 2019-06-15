package com.fosspowered.peist.model.json;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

/** Response object when a Paste is fetched. */
@Value
@Builder
@ToString
public class PasteResponse {
  private String urlId;

  private String title;

  private String author;

  private String language;

  private LocalDateTime creationDate;

  private LocalDateTime expiryDate;

  private String pasteData;
}
