package testutils;

import com.fosspowered.peist.model.json.PasteResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;

/** Mock object creator for ${@link PasteResponse} */
@Getter
@Setter
@Accessors(chain = true)
public class DummyPasteResponse implements DummyObject<PasteResponse> {
  private String title = "On the origin of species";
  private String author = "Charles Darwin";
  private LocalDateTime creationDate = LocalDateTime.now();
  private LocalDateTime expiryDate = creationDate.plusSeconds(3600);
  private String language = "text";
  private String urlId = UUID.randomUUID().toString();
  private String pasteData = RandomStringUtils.randomAlphabetic(100);

  @Override
  public PasteResponse build() {

    return PasteResponse.builder()
        .title(title)
        .author(author)
        .creationDate(creationDate)
        .expiryDate(expiryDate)
        .language(language)
        .urlId(urlId)
        .pasteData(pasteData)
        .build();
  }
}
