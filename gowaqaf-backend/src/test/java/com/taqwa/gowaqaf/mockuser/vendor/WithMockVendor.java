package com.taqwa.gowaqaf.mockuser.vendor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockVendorSecurityContextFactory.class, setupBefore = TestExecutionEvent.TEST_EXECUTION)
public @interface WithMockVendor {

	String username() default "super_vendor";

}
