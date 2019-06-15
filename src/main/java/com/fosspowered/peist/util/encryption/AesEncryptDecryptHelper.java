package com.fosspowered.peist.util.encryption;

import static com.fosspowered.peist.util.encryption.AesKeySpecBuilder.ENCRYPTION_ALGORITHM;

import com.fosspowered.peist.model.exceptions.PeistAccessDeniedException;
import com.fosspowered.peist.model.exceptions.PeistInternalException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/** Helper class to encrypt/decrypt using AES. */
@NoArgsConstructor
@Component
@Log4j2
public class AesEncryptDecryptHelper {
  /**
   * Encrypt the text.
   *
   * @param secretKeySpec Secret key object.
   * @param text Plain Text to be encrypted.
   * @return Cipher Text
   */
  public String encrypt(SecretKeySpec secretKeySpec, String text) {
    try {
      Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
      byte[] cipherBytes = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(cipherBytes);
    } catch (InvalidKeyException
        | IllegalBlockSizeException
        | BadPaddingException
        | NoSuchAlgorithmException
        | NoSuchPaddingException e) {
      String message = "Error while processing";
      log.error(message, e);
      throw new PeistInternalException(message);
    }
  }

  /**
   * Decrypt the text.
   *
   * @param secretKeySpec Secret key object.
   * @param cipherText Cipher text to be decrypted.
   * @return Plain Text.
   */
  public String decrypt(SecretKeySpec secretKeySpec, String cipherText) {
    try {
      Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
      return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
    } catch (BadPaddingException e) {
      throw new PeistAccessDeniedException("Access denied for paste.");
    } catch (NoSuchAlgorithmException
        | InvalidKeyException
        | NoSuchPaddingException
        | IllegalBlockSizeException e) {
      String message = "Error while processing";
      log.error(message, e);
      throw new PeistInternalException(message);
    }
  }
}
