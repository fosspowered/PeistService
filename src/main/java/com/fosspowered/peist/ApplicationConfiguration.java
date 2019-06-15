package com.fosspowered.peist;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Configuration
@EnableAspectJAutoProxy
public class ApplicationConfiguration {}
