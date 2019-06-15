package com.fosspowered.peist.repository;

import com.fosspowered.peist.model.dao.Paste;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Database access layer for Paste. */
@Repository
public interface PasteRepository extends JpaRepository<Paste, Long> {

  /**
   * Find the paste by its respective urlId.
   *
   * @param urlId Url for the paste object.
   * @return Paste if present otherwise empty.
   */
  Optional<Paste> findByUrlId(String urlId);
}
