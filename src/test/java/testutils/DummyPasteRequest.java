package testutils;

import com.fosspowered.peist.model.json.PasteRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;

/** Mock object creator for ${@link PasteRequest}. */
@Getter
@Setter
@Accessors(chain = true)
public class DummyPasteRequest implements DummyObject<PasteRequest> {
  private String title = "The title";
  private String author = "James Clerk Maxwell";
  private String language = "text";
  private int ttl = 3600;
  private String secretKey = "";
  private String pasteData = RandomStringUtils.randomAlphabetic(100);
  boolean isVisible = true;

  @Override
  public PasteRequest build() {
    return PasteRequest.builder()
        .title(title)
        .author(author)
        .language(language)
        .ttl(ttl)
        .secretKey(secretKey)
        .pasteData(pasteData)
        .isVisible(isVisible)
        .build();
  }
}
