package com.fosspowered.peist.controller;

import com.fosspowered.peist.aspect.MetricInterceptor;
import com.fosspowered.peist.model.json.PasteRequest;
import com.fosspowered.peist.model.json.PasteResponse;
import com.fosspowered.peist.service.CRUDService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Spring REST Controller for Paste. */
@RestController
@SuppressWarnings("WeakerAccess")
public class PasteController {

  private final CRUDService crudService;

  PasteController(CRUDService crudService) {
    this.crudService = crudService;
  }

  /**
   * Health check for controller.
   *
   * @return 'pong' in plain response.
   */
  @MetricInterceptor
  @RequestMapping("/paste/ping")
  public String getPing() {
    return "pong";
  }

  /**
   * Get all recent paste in the system which are visible.
   *
   * @return Recent pastes in the system which are visible.
   */
  @MetricInterceptor
  @GetMapping("/paste")
  public List<PasteResponse> getAllPaste() {
    return this.crudService.fetchRecentPastes();
  }

  /**
   * Get paste by (URL) id.
   *
   * @param id URL identifier.
   * @return Paste persisted with the specific URL id if present otherwise 404.
   */
  @MetricInterceptor
  @GetMapping("/paste/{id}")
  public PasteResponse getPaste(@PathVariable String id) {
    return this.crudService.fetchPaste(id, "");
  }

  /**
   * Get paste by (URL) id and passing the secret key.
   *
   * @param id URL identifier.
   * @param request Request body which contains the secret key.
   * @return Paste persisted with the specific URL id if present otherwise 404. 403 if wrong key.
   */
  @MetricInterceptor
  @PostMapping(value = "/paste/{id}")
  public PasteResponse getSecretPaste(@PathVariable String id, @RequestBody String request) {
    return this.crudService.fetchPaste(id, request);
  }

  /**
   * Upload the paste.
   *
   * @param pasteRequest Request body of the paste containing details like author, language, text.
   * @return Paste persisted with newly created URL id.
   */
  @MetricInterceptor
  @PostMapping(value = "/paste")
  @ResponseStatus(HttpStatus.CREATED)
  public PasteResponse addPaste(@Valid @RequestBody PasteRequest pasteRequest) {
    return this.crudService.addPaste(pasteRequest);
  }
}
