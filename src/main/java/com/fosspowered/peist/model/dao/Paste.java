package com.fosspowered.peist.model.dao;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Model object for the paste. */
@Entity
@NoArgsConstructor
@Setter
@Getter
public class Paste {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String urlId;

  private String title;

  private String author;

  private String language;

  private LocalDateTime creationDate;

  private Boolean isVisible;

  private LocalDateTime expiryDate;

  private String pasteData;

  @Transient private String secretKey;
}
