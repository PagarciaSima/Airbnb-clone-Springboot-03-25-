package com.airbnb.clone.AirbnbClone.infrastructure.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		// Evita guardar el token csrf en atributos de sesión
		requestHandler.setCsrfRequestAttributeName(null);
		
		httpSecurity.authorizeHttpRequests(authorize -> authorize
				.anyRequest()
				.authenticated())
		// Configuración CSRF (protección contra Cross-Site Request Forgery)
		.csrf(csrf -> csrf
		    // Guarda el token CSRF en una cookie en el cliente (para acceder desde js en cliente)
		    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) 
		    
		    // Evita que Spring meta el token CSRF como atributo en la request automáticamente (será manual en los headers)
		    .csrfTokenRequestHandler(requestHandler)
		)

		// Habilita el login con OAuth2 (por ejemplo, a través de Google, GitHub, etc.)
		.oauth2Login(Customizer.withDefaults())

		// Configura el backend como un "resource server", es decir, que acepta y valida tokens JWT
		.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))

		// Habilita el cliente OAuth2, por ejemplo, si desde el backend llamas a otros servicios protegidos por OAuth2
		.oauth2Client(Customizer.withDefaults());

		return httpSecurity.build();
	}
	
	@Bean
	public GrantedAuthoritiesMapper userAuthoritiesMapper() {
	    // Creamos una nueva instancia de GrantedAuthoritiesMapper
	    return authorities -> {
	        // Inicializamos un conjunto para almacenar las autoridades (roles/permisos)
	        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

	        // Iteramos sobre las autoridades que nos llegan
	        authorities.forEach(grantedAuthority -> {
	            // Comprobamos si la autoridad es de tipo OidcUserAuthority
	            if (grantedAuthority instanceof OidcUserAuthority oidcUserAuthority) {
	                // Si es OidcUserAuthority, extraemos los claims del usuario
	                // y los transformamos en roles/authorities utilizando un método personalizado.
	                grantedAuthorities.addAll(
	                    SecurityUtils.extractAuthorityFromCLaims(
	                        oidcUserAuthority.getUserInfo().getClaims()
	                    )
	                );
	            }
	        });

	        // Devolvemos el conjunto de authorities transformados
	        return grantedAuthorities;
	    };
	}

}
