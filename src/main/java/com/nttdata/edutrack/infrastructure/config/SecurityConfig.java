package com.nttdata.edutrack.infrastructure.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/actuator/health", "/api/publico/**", "/error").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/notas").hasRole("ESTUDIANTE")
						.requestMatchers(HttpMethod.POST, "/api/notas").hasRole("DOCENTE")
						.requestMatchers("/api/reportes/**").hasRole("REPORTES")
						.anyRequest().authenticated())
				.oauth2ResourceServer(o -> o
						.jwt(jwt -> jwt.jwtAuthenticationConverter(rolesDeKeycloak())));
		return http.build();
	}

	@Bean
	JwtAuthenticationConverter rolesDeKeycloak() {
		var converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(this::rolesDelRealm);
		return converter;
	}

	private Collection<GrantedAuthority> rolesDelRealm(Jwt jwt) {
		Map<String, Object> realmAccess = jwt.getClaim("realm_access");
		if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> roles)) {
			return List.of();
		}

		return roles.stream()
				.filter(String.class::isInstance)
				.map(String.class::cast)
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toUnmodifiableList());
	}
}
