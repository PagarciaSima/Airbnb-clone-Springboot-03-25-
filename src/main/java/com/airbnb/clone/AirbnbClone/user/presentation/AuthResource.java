package com.airbnb.clone.AirbnbClone.user.presentation;

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

}
