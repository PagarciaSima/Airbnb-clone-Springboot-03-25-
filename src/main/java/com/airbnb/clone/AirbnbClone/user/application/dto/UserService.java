package com.airbnb.clone.AirbnbClone.user.application.dto;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.airbnb.clone.AirbnbClone.infrastructure.config.SecurityUtils;
import com.airbnb.clone.AirbnbClone.user.domain.User;
import com.airbnb.clone.AirbnbClone.user.mapper.UserMapper;
import com.airbnb.clone.AirbnbClone.user.repository.UserRepository;

@Service
public class UserService {

	private static final String UPDATED_AT_KEY = "updated_at";
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	
	public UserService(UserRepository userRepository, UserMapper userMapper) {
		super();
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}
	
	public ReadUserDto getAuthenticatedUserFromSecurityContext() {
		OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = SecurityUtils.mapOatuh2AttributesToUser(principal.getAttributes());
		
		return getByEmail(user.getEmail()).orElseThrow();
	}

	private Optional<ReadUserDto> getByEmail(String email) {
		Optional<User> oneByEmail = userRepository.findOneByEmail(email);
		return oneByEmail.map(userMapper::readUserDTOToUser);
	}
	
}
