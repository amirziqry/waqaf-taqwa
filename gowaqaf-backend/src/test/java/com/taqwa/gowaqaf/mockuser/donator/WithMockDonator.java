package com.taqwa.gowaqaf.mockuser.donator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockDonatorSecurityContextFactory.class, setupBefore = TestExecutionEvent.TEST_EXECUTION)
public @interface WithMockDonator {

	String username() default "super_donator";

}
