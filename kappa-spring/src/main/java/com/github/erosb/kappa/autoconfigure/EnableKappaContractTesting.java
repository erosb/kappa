package com.github.erosb.kappa.autoconfigure;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(KappaContractTestingConfiguration.class)
public @interface EnableKappaContractTesting {

  boolean validateRequests() default true;
}
