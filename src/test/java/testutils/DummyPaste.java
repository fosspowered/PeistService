package testutils;

import com.fosspowered.peist.model.dao.Paste;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Mock object creator for ${@link Paste}.
 */
@Getter
@Setter
@Accessors(chain = true)
public class DummyPaste implements DummyObject<Paste> {
  private Long id = Long.valueOf(RandomStringUtils.randomNumeric(3));
  private String urlId = UUID.randomUUID().toString();
  private String title = "This is title";
  private String author = "Michael Faraday";
  private String language = "text";
  private boolean isVisible = true;
  private LocalDateTime creationDate = LocalDateTime.now();
  private LocalDateTime expiryDate = creationDate.plusSeconds(10000);
  private String secretKey = "";
  private String pasteData = RandomStringUtils.randomAlphabetic(100);

  @Override
  public Paste build() {
    Paste paste = new Paste();
    paste.setId(id);
    paste.setUrlId(urlId);
    paste.setTitle(title);
    paste.setAuthor(author);
    paste.setLanguage(language);
    paste.setIsVisible(isVisible);
    paste.setCreationDate(creationDate);
    paste.setExpiryDate(expiryDate);
    paste.setSecretKey(secretKey);
    paste.setPasteData(pasteData);
    return paste;
  }
}
