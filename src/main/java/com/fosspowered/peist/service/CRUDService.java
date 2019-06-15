package com.fosspowered.peist.service;

import com.fosspowered.peist.model.dao.Paste;
import com.fosspowered.peist.model.exceptions.PeistInvalidRequestException;
import com.fosspowered.peist.model.exceptions.PeistNotFoundException;
import com.fosspowered.peist.model.json.PasteRequest;
import com.fosspowered.peist.model.json.PasteResponse;
import com.fosspowered.peist.repository.PasteRepository;
import com.fosspowered.peist.util.converter.PasteDataExchangeConverter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service layer for CRUD operations on Paste. */
@Service
@Log4j2
public class CRUDService {
  private final PasteRepository pasteRepository;
  private final PasteDataExchangeConverter pasteDataExchangeConverter;
  private final EncryptService encryptService;

  @Autowired
  CRUDService(
      PasteRepository pasteRepository,
      PasteDataExchangeConverter pasteDataExchangeConverter,
      EncryptService encryptService) {
    this.pasteRepository = pasteRepository;
    this.pasteDataExchangeConverter = pasteDataExchangeConverter;
    this.encryptService = encryptService;
  }

  /**
   * Persists Paste to the system.
   *
   * @param request Paste Request object containing all paste details.
   * @return Paste Response object.
   */
  public PasteResponse addPaste(PasteRequest request) {
    Paste paste = pasteDataExchangeConverter.createDaoFromRequest(request);
    evaluatePaste(request, paste);
    pasteRepository.save(paste);
    encryptService.decrypt(paste);
    return pasteDataExchangeConverter.createResponseFromDao(paste);
  }

  /**
   * Fetch Paste by Url ID and secret key.
   *
   * @param urlId URL ID for the paste.
   * @param key Secret key (passphrase) for the paste.
   * @return Paste Response.
   */
  public PasteResponse fetchPaste(String urlId, String key) {
    Paste paste = fetchPasteFromDB(urlId);
    paste.setSecretKey(key);
    encryptService.decrypt(paste);
    return pasteDataExchangeConverter.createResponseFromDao(paste);
  }

  /**
   * Fetch all visible pastes.
   *
   * @return All visible paste response objects.
   */
  public List<PasteResponse> fetchRecentPastes() {
    List<Paste> pasteList = new ArrayList<>(this.pasteRepository.findAll());
    List<Paste> visiblePastes =
        pasteList.stream().filter(Paste::getIsVisible).collect(Collectors.toList());
    visiblePastes.forEach(encryptService::decrypt);
    return visiblePastes.stream()
        .map(pasteDataExchangeConverter::createResponseFromDao)
        .collect(Collectors.toList());
  }

  private Paste fetchPasteFromDB(String urlId) {
    if (StringUtils.isBlank(urlId)) {
      String message = "Invalid request as urlId is invalid: " + urlId;
      log.error(message);
      throw new PeistInvalidRequestException(message);
    }
    Optional<Paste> paste = this.pasteRepository.findByUrlId(urlId);
    if (paste.isPresent()) {
      return paste.get();
    } else {
      throw new PeistNotFoundException("Paste not found for urlId: " + urlId);
    }
  }

  private void evaluatePaste(PasteRequest request, Paste paste) {
    LocalDateTime creationDate = LocalDateTime.now();
    LocalDateTime expiryDate = creationDate.plusSeconds(request.getTtl());

    paste.setIsVisible(paste.getIsVisible() && StringUtils.isBlank(request.getSecretKey()));
    paste.setUrlId(UUID.randomUUID().toString());
    paste.setCreationDate(creationDate);
    paste.setExpiryDate(expiryDate);

    encryptService.encrypt(paste);
  }
}
