package com.fosspowered.peist.util.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/** Helper class to generate hashed key for encryption/decryption. */
@NoArgsConstructor
@Component
public class AesKeySpecBuilder {
  static final String ENCRYPTION_ALGORITHM = "AES";

  private static final String DIGEST_ALGORITHM = "SHA-256";

  /**
   * Build private key.
   *
   * @param keyStr Pass Phrase.
   * @return Hashed key.
   */
  public SecretKeySpec build(String keyStr) {
    try {
      MessageDigest sha = MessageDigest.getInstance(DIGEST_ALGORITHM);
      return new SecretKeySpec(
          sha.digest(keyStr.getBytes(StandardCharsets.UTF_8)), ENCRYPTION_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError("Error while building key for AES algorithm", e);
    }
  }
}
