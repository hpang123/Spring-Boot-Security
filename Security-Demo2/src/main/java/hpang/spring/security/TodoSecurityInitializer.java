package hpang.spring.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

//register the filters used by Spring Security
public class TodoSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

    public TodoSecurityInitializer() {
    	/*
    	 * pass the security configuration classes that
    	 * are used to bootstrap the security
    	 */
        super(TodoSecurityConfig.class);
    }
}
