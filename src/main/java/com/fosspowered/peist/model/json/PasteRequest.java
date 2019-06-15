package com.fosspowered.peist.model.json;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

/** Request object for persisting a Paste. */
@Value
@Builder
@ToString(exclude = {"pasteData", "secretKey"})
public class PasteRequest {
  private String title;

  private String author;

  private String language;

  private String secretKey;

  private Boolean isVisible;

  @NotEmpty(message = "Paste body must be present")
  private String pasteData;

  @NotNull private Integer ttl;
}
