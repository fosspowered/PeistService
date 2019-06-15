package com.fosspowered.peist.util.encryption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fosspowered.peist.model.exceptions.PeistAccessDeniedException;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.h2.security.SHA256;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AesEncryptDecryptHelperTest {
  private static AesEncryptDecryptHelper helper;

  @BeforeAll
  static void init() {
    helper = new AesEncryptDecryptHelper();
  }

  @Test
  void encryptionDecryptionWithCorrectKey() {
    String plainText = "Plain Text";

    byte[] key = "myKey".getBytes(StandardCharsets.UTF_8);
    SecretKeySpec secretKeySpec = new SecretKeySpec(SHA256.getHash(key, false), "AES");

    String cipherText = helper.encrypt(secretKeySpec, plainText);
    assertThat(cipherText).isNotEqualTo(plainText);

    String fetchedPlainText = helper.decrypt(secretKeySpec, cipherText);
    assertThat(fetchedPlainText).isNotEqualTo(cipherText);

    assertThat(fetchedPlainText).isEqualTo(plainText);
  }

  @Test
  void encryptionDecryptionWithWrongKey() {
    String plainText = "Plain Text";

    byte[] key = "myKey".getBytes(StandardCharsets.UTF_8);
    SecretKeySpec secretKeySpec = new SecretKeySpec(SHA256.getHash(key, false), "AES");

    String cipherText = helper.encrypt(secretKeySpec, plainText);
    assertThat(cipherText).isNotEqualTo(plainText);

    byte[] decryptKey = "wrongKey".getBytes(StandardCharsets.UTF_8);
    SecretKeySpec decryptSecretKeySpec =
        new SecretKeySpec(SHA256.getHash(decryptKey, false), "AES");

    assertThatThrownBy(() -> helper.decrypt(decryptSecretKeySpec, cipherText))
        .isExactlyInstanceOf(PeistAccessDeniedException.class)
        .hasMessageContaining("Access denied");
  }
}
