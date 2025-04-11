package com.airbnb.clone.AirbnbClone.infrastructure.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.airbnb.clone.AirbnbClone.user.domain.Authority;
import com.airbnb.clone.AirbnbClone.user.domain.User;
import com.nimbusds.oauth2.sdk.auth.JWTAuthentication;

public class SecurityUtils {

	private static final String ROLE_LANDLORD = "ROLE_LANDLORD";
	private static final String ROLE_TENANT = "ROLE_TENANT";
	private static final String CLAIMS_NAMESPACE = "https://www.pgs.es/roles";

	public static User mapOatuh2AttributesToUser(Map<String, Object> attributes) {
		User user = new User();
		String sub = String.valueOf(attributes.get("sub"));

		String username = null;
		if (null != attributes.get("preferred_username")) {
			username = ((String) attributes.get("preferred_username")).toLowerCase();
		}

		if (null != attributes.get("given_name")) {
			user.setFirstName((String) attributes.get("given_name"));
		} else if (null != attributes.get("nickname")) {
			user.setFirstName((String) attributes.get("nickname"));
		}

		if (null != attributes.get("family_name")) {
			user.setLastName((String) attributes.get("family_name"));
		}

		if (null != attributes.get("email")) {
			user.setEmail((String) attributes.get("email"));
		} else if (sub.contains("|") && (null != username && username.contains("@"))) {
			user.setEmail(username);
		} else {
			user.setEmail(sub);
		}

		if (null != attributes.get("picture")) {
			user.setLastName((String) attributes.get("picture"));
		}

		if (null != attributes.get(CLAIMS_NAMESPACE)) {
			List<String> authoritiesRaw = (List<String>) attributes.get(CLAIMS_NAMESPACE);
			Set<Authority> authorities = authoritiesRaw.stream().map(authority -> {
				Authority auth = new Authority();
				auth.setName(authority);
				return auth;
			}).collect(Collectors.toSet());
			user.setAuthorities(authorities);
		}
		return user;

	}

	public static List<SimpleGrantedAuthority> extractAuthorityFromCLaims(Map<String, Object> claims) {
		return mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
	}

	private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
		return (List<String>) claims.get(CLAIMS_NAMESPACE);
	}

	private static List<SimpleGrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
		return roles.stream().filter(role -> role.startsWith("ROLE_")).map(SimpleGrantedAuthority::new).toList();
	}


    public static boolean hasCurrentUserAnyOfAuthorities(String ...authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && getAuthorities(authentication)
                .anyMatch(authority -> Arrays.asList(authorities).contains(authority)));
    }

	private static Stream<String> getAuthorities(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication instanceof JwtAuthenticationToken jwtAuthenticationToken
				? extractAuthorityFromCLaims(jwtAuthenticationToken.getToken().getClaims())
				: authentication.getAuthorities();
		return authorities.stream().map(GrantedAuthority::getAuthority);
	}
}
