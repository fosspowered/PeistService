package com.fosspowered.peist.respository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fosspowered.peist.model.dao.Paste;
import com.fosspowered.peist.repository.PasteRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testutils.DummyPaste;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PasteRepositoryTest {
  @Autowired private TestEntityManager entityManager;

  @Autowired private PasteRepository pasteRepository;

  @Test
  void validUrlId_findByUrlId_returnsPaste() {
    String urlId = "urlId";

    Paste paste = new DummyPaste().setId(null).setUrlId(urlId).build();
    entityManager.persist(paste);
    entityManager.flush();

    Optional<Paste> actualPaste = pasteRepository.findByUrlId(urlId);

    assertThat(actualPaste).isPresent();
    assertThat(actualPaste).contains(paste);
  }

  @Test
  void invalidUrlId_findByUrlId_returnsEmpty() {
    Optional<Paste> invalidPaste = pasteRepository.findByUrlId("invalidId");
    assertThat(invalidPaste).isNotPresent();
  }
}
