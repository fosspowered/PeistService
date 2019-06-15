package com.fosspowered.peist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fosspowered.peist.model.dao.Paste;
import com.fosspowered.peist.model.exceptions.PeistAccessDeniedException;
import com.fosspowered.peist.util.encryption.AesEncryptDecryptHelper;
import com.fosspowered.peist.util.encryption.AesKeySpecBuilder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutils.DummyPaste;

class EncryptServiceTest {
  private static final String systemKey = "systemKey";

  private static EncryptService encryptService;

  private AesKeySpecBuilder aesKeySpecBuilder;
  private AesEncryptDecryptHelper aesEncryptDecryptHelper;

  @BeforeEach
  void setUp() {
    aesEncryptDecryptHelper = mock(AesEncryptDecryptHelper.class);
    aesKeySpecBuilder = spy(AesKeySpecBuilder.class);
    encryptService = new EncryptService(aesEncryptDecryptHelper, aesKeySpecBuilder, systemKey);
  }

  @Test
  void secretKey_encrypt_EncryptSuccessfully() {
    String pasteData = RandomStringUtils.randomAlphabetic(100);
    String cipherText = RandomStringUtils.randomAlphanumeric(50);
    String secretKey = "secretKey";
    LocalDateTime creationDate = LocalDateTime.now();

    String key =
        String.format(
            "%s%s%s", systemKey, secretKey, creationDate.atZone(ZoneOffset.UTC).toEpochSecond());

    when(aesEncryptDecryptHelper.encrypt(any(SecretKeySpec.class), eq(pasteData)))
        .thenReturn(cipherText);

    Paste paste =
        new DummyPaste()
            .setPasteData(pasteData)
            .setSecretKey(secretKey)
            .setCreationDate(creationDate)
            .build();

    encryptService.encrypt(paste);

    verify(aesEncryptDecryptHelper, times(1)).encrypt(any(SecretKeySpec.class), eq(pasteData));
    verify(aesKeySpecBuilder, times(1)).build(eq(key));

    assertThat(paste.getPasteData()).isNotEmpty().isEqualTo(cipherText);
  }

  @Test
  void correctKey_decrypt_DecryptsSuccessfully() {
    String pasteData = RandomStringUtils.randomAlphabetic(100);
    String cipherText = RandomStringUtils.randomAlphanumeric(50);
    String secretKey = "secretKey";
    LocalDateTime creationDate = LocalDateTime.now();

    String key =
        String.format(
            "%s%s%s", systemKey, secretKey, creationDate.atZone(ZoneOffset.UTC).toEpochSecond());

    when(aesEncryptDecryptHelper.decrypt(any(SecretKeySpec.class), eq(cipherText)))
        .thenReturn(pasteData);

    Paste paste =
        new DummyPaste()
            .setPasteData(cipherText)
            .setSecretKey(secretKey)
            .setCreationDate(creationDate)
            .build();

    encryptService.decrypt(paste);

    verify(aesEncryptDecryptHelper, times(1)).decrypt(any(SecretKeySpec.class), eq(cipherText));
    verify(aesKeySpecBuilder, times(1)).build(eq(key));

    assertThat(paste.getPasteData()).isNotEmpty().isEqualTo(pasteData);
  }

  @Test
  void wrongKey_decrypt_ThrowsException() {
    when(aesEncryptDecryptHelper.decrypt(any(SecretKeySpec.class), anyString()))
        .thenThrow(PeistAccessDeniedException.class);

    assertThatThrownBy(() -> encryptService.decrypt(new DummyPaste().build()))
        .isExactlyInstanceOf(PeistAccessDeniedException.class);
  }
}
