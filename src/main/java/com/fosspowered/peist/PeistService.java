package com.fosspowered.peist;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SpringBootApplication
public class PeistService {
  public static void main(String[] args) {
    SpringApplication.run(PeistService.class, args);
  }
}
