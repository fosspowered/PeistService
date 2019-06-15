package com.fosspowered.peist.util.encryption;

import static org.assertj.core.api.Assertions.assertThat;

import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AesKeySpecBuilderTest {
  private static AesKeySpecBuilder aesKeySpecBuilder;

  @BeforeAll
  static void init() {
    aesKeySpecBuilder = new AesKeySpecBuilder();
  }

  @Test
  void build() {
    SecretKeySpec testKey = aesKeySpecBuilder.build("testKey");
    assertThat(testKey).isNotNull();
    assertThat(testKey.getEncoded().length).isEqualTo(32);
  }
}
