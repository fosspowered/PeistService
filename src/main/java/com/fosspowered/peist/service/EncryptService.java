package com.fosspowered.peist.service;

import com.fosspowered.peist.model.dao.Paste;
import com.fosspowered.peist.util.encryption.AesEncryptDecryptHelper;
import com.fosspowered.peist.util.encryption.AesKeySpecBuilder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service which performs encryption and decryption of the paste body. */
@Service
class EncryptService {
  private final String systemKey;

  private final AesEncryptDecryptHelper aesEncryptDecryptHelper;
  private final AesKeySpecBuilder aesKeySpecBuilder;

  @Autowired
  EncryptService(
      AesEncryptDecryptHelper aesEncryptDecryptHelper,
      AesKeySpecBuilder aesKeySpecBuilder,
      @Value("${system-key") String systemKey) {
    this.systemKey = systemKey;
    this.aesEncryptDecryptHelper = aesEncryptDecryptHelper;
    this.aesKeySpecBuilder = aesKeySpecBuilder;
  }

  /**
   * Encrypt the paste object's body.
   *
   * @param paste Paste DAO object with plain text.
   */
  void encrypt(Paste paste) {
    SecretKeySpec secretKeySpec = buildKey(paste);
    String text = paste.getPasteData();
    String cipherText = aesEncryptDecryptHelper.encrypt(secretKeySpec, text);
    paste.setPasteData(cipherText);
  }

  /**
   * Decrypt the paste object's body.
   *
   * @param paste Paste DAO object with cipher text.
   */
  void decrypt(Paste paste) {
    SecretKeySpec secretKeySpec = buildKey(paste);
    String cipherText = paste.getPasteData();
    String text = aesEncryptDecryptHelper.decrypt(secretKeySpec, cipherText);
    paste.setPasteData(text);
  }

  private SecretKeySpec buildKey(Paste paste) {
    LocalDateTime creationDate = paste.getCreationDate();
    String secretKey = paste.getSecretKey();
    String keyStr =
        String.format(
            "%s%s%d",
            systemKey,
            secretKey != null ? secretKey : "",
            creationDate.atZone(ZoneOffset.UTC).toEpochSecond());
    return aesKeySpecBuilder.build(keyStr);
  }
}
