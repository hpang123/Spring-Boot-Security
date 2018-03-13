package hpang.spring.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hpang.spring.security.web.AccessDeniedHandlerImpl;

@Configuration
@EnableWebSecurity
@EnableCaching
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TodoSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private AccessDeniedHandlerImpl accessDeniedHandler;
	
	@Autowired
	public DataSource dataSource;
	/*
	@Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("board")
                .addScript("classpath:/schema.sql")
                .addScript("classpath:/data.sql")
                .build();
    }
*/
	
	/*It works with @Secure
	 * It is default configured that we do not need it
	 
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			 //This tells Spring Security that for every request 
			 // that comes in, you have to be authenticated with the system.
			.anyRequest().authenticated()
			.and().
			formLogin().and().httpBasic();
	}
	*/
	
	/* It works inMemoryAUthentication 
	 * To work with @Secured, it requires to use roles instead of authorities
	 * */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
		.withUser("user1")
		//.password("{noop}user") //{noop} disable password encoder
		.password(passwordEncoder().encode("user1"))
		.authorities("USER")
		//.roles("USER") //Only role works for @Secure
		.and()
		.withUser("user2")
		//.password("{noop}user") //{noop} disable password encoder
		.password(passwordEncoder().encode("user2"))
		.authorities("USER")
		//.roles("USER")
		.and().withUser("admin")
		//.password("{noop}admin")
		.password(passwordEncoder().encode("admin"))
		//.authorities("ADMIN", "USER");
		.authorities("ADMIN");
		//.roles("ADMIN");
	}

	
	
	/* It works
	 * generate/test encode  https://bcrypt-generator.com/ 
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
        .passwordEncoder(passwordEncoder())
        .dataSource(dataSource)
        .userCache(userCache());
	}
*/
	
	/*
	 * Remember that you must always include a wildcard at the end of a URL pattern. 
	 * Failing to do so will make the URL pattern 
	 * unable to match a URL that has request parameters. 
	 * As a result, hackers could easily skip the security check 
	 * by appending an arbitrary request parameter
	 */
	 
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		
		 // Not work well by httpMethod level
		 // Don't define same pattern with hasAnyRole, role twice. It will use the last one
		 
		//.addFilter(headAuthFilter())
		http.authorizeRequests()
				//.antMatchers("/todos*").hasAnyRole("USER", "ADMIN")
		.antMatchers("/todos*").hasAnyAuthority("USER", "ADMIN")
				.antMatchers(HttpMethod.DELETE, "/todos*").hasAuthority("ADMIN")
				.antMatchers("/todos/username**").hasAuthority("ADMIN")
				.antMatchers("/todos/encode**").permitAll()
				.and()
				.httpBasic().disable()
				//.anyRequest().authenticated()
				//.and().anonymous().principal("anonimous").authorities("ROLE_GUEST")
				.formLogin().loginPage("/login.jsp")
				.defaultSuccessUrl("/todos/")
				.failureUrl("/login.jsp?error=true")
				.permitAll()
				//.and().logout().permitAll() //It works and redirect to root path
				.and().logout().logoutSuccessUrl("/logout-success.jsp").permitAll()
				.and().exceptionHandling().accessDeniedHandler(accessDeniedHandler)
				.and().csrf().disable();
		
	}
	
	 
	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Bean
	public SpringCacheBasedUserCache userCache() throws Exception {
		Cache cache = cacheManager.getCache("userCache");
		
		//MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		//ManagementService.registerMBeans(cacheManager, mBeanServer, true, true, true, true);
		
		return new SpringCacheBasedUserCache(cache);
	}

}
