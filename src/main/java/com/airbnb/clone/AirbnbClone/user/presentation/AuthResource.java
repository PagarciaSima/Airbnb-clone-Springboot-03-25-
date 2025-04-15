package com.airbnb.clone.AirbnbClone.user.presentation;

import java.text.MessageFormat;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.airbnb.clone.AirbnbClone.user.application.UserService;
import com.airbnb.clone.AirbnbClone.user.application.dto.ReadUserDto;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

	private final UserService userService;
	private final ClientRegistration registration;

	public AuthResource(UserService userService, ClientRegistrationRepository clientRegistrationRepository) {
		super();
		this.userService = userService;
		this.registration = clientRegistrationRepository.findByRegistrationId("okta");
	}

	/**
	 * Retrieves the currently authenticated user.
	 *
	 * <p>If {@code forceResync} is true, the method returns a 500 Internal Server Error.
	 * Otherwise, it synchronizes the user data with the identity provider (IdP) and returns
	 * the user information as a {@link ReadUserDto}.</p>
	 *
	 * @param user the authenticated {@link OAuth2User} provided by Spring Security
	 * @param forceResync if true, forces an error response instead of syncing the user
	 * @return a {@link ResponseEntity} containing the {@link ReadUserDto} of the authenticated user,
	 *         or 500 Internal Server Error if {@code forceResync} is true
	 */
	@GetMapping("/get-authenticated-user")
	public ResponseEntity<ReadUserDto> getAuthenticatedUser(
			@AuthenticationPrincipal OAuth2User user,
			@RequestParam boolean forceResync
	) {
		if (forceResync) {
			return new ResponseEntity<> (HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			this.userService.syncWithIdp(user, forceResync);
			ReadUserDto readUserDto = userService.getAuthenticatedUserFromSecurityContext();
			return new ResponseEntity<ReadUserDto>(readUserDto, HttpStatus.OK);
		}
	}
	
	/**
	 * Logs out the current user by invalidating the local session and generating the logout URL 
	 * for the external identity provider (e.g., Auth0, Keycloak).
	 *
	 * @param request the HTTP servlet request containing headers and session information
	 * @return a ResponseEntity containing a JSON map with the key "logoutUrl" and the value 
	 *         being the URL to redirect the user to complete the logout process with the identity provider
	 */
	public ResponseEntity<Map<String, String>> logout(
			HttpServletRequest request
	) {
		// Url proveedor autenticación
		String issuerUri = registration.getProviderDetails().getIssuerUri();
		// Origen del front (ej localhost:4200)
		String originUrl = request.getHeader(HttpHeaders.ORIGIN);
		Object[] params = {issuerUri, registration.getClientId(), originUrl};
		String logoutUrl = MessageFormat.format("{0}v2/logout?client_id={1}&returnTo={2}", params);
		request.getSession().invalidate();
		return ResponseEntity.ok().body(Map.of("logoutUrl", logoutUrl));
	}

}
