package com.airbnb.clone.AirbnbClone.user.application;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbnb.clone.AirbnbClone.infrastructure.config.SecurityUtils;
import com.airbnb.clone.AirbnbClone.user.application.dto.ReadUserDto;
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
	
	/**
	 * Retrieves the currently authenticated user from the security context.
	 * <p>
	 * This method accesses the {@link SecurityContextHolder} to obtain the authenticated {@link OAuth2User},
	 * maps its attributes to a {@link User} entity using {@link SecurityUtils#mapOatuh2AttributesToUser(Map)},
	 * and then searches for the corresponding user in the database by email.
	 * Returns a {@link ReadUserDto} containing the user's information.
	 * </p>
	 *
	 * @return a {@link ReadUserDto} representing the authenticated user.
	 * @throws NoSuchElementException if no user is found with the provided email.
	 */
	@Transactional(readOnly = true)
	public ReadUserDto getAuthenticatedUserFromSecurityContext() {
		OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = SecurityUtils.mapOatuh2AttributesToUser(principal.getAttributes());
		
		return getByEmail(user.getEmail()).orElseThrow();
	}

	/**
	 * Retrieves a user by their email address and maps it to a {@link ReadUserDto}.
	 * <p>
	 * This method queries the {@link userRepository} for a {@link User} with the specified email,
	 * and if found, maps the entity to a {@link ReadUserDto} using {@link userMapper}.
	 * </p>
	 *
	 * @param email the email address of the user to retrieve.
	 * @return an {@link Optional} containing the {@link ReadUserDto} if found, or an empty {@link Optional} if not.
	 */
	@Transactional(readOnly = true)
	private Optional<ReadUserDto> getByEmail(String email) {
		Optional<User> oneByEmail = userRepository.findOneByEmail(email);
		return oneByEmail.map(userMapper::readUserDTOToUser);
	}
	
	/**
	 * Synchronizes the user data with the Identity Provider (IdP).
	 * 
	 * This method checks if the user already exists in the local database. If the user exists, it compares the
	 * "updated_at" timestamp from the IdP with the last modified date of the user in the local database. If the IdP
	 * timestamp is more recent or if forced resynchronization is requested, the user's data is updated.
	 * If the user does not exist in the local database, a new user is created.
	 * 
	 * @param oAuth2User The OAuth2User object containing the user attributes from the IdP.
	 * @param forceResync A flag to force resynchronization even if the IdP data is not more recent than the local data.
	 */
	public void syncWithIdp(OAuth2User oAuth2User, boolean forceResync) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		User user = SecurityUtils.mapOatuh2AttributesToUser(attributes);
		Optional<User> existingUser = userRepository.findOneByEmail(user.getEmail());
		
		if(existingUser.isPresent()) {
			if(attributes.get(UPDATED_AT_KEY) != null) {
				Instant lastModifiedDate = existingUser.orElseThrow().getLastModifiedDate();
				Instant idpModifiedDate;
				if(attributes.get(UPDATED_AT_KEY) instanceof Instant instant) {
					idpModifiedDate = instant;
				} else {
					idpModifiedDate = Instant.ofEpochSecond((Integer) attributes.get(UPDATED_AT_KEY));
				}
				if(idpModifiedDate.isAfter(lastModifiedDate) || forceResync ) {
					updateUser(user);
				}
			}
		} else {
			userRepository.saveAndFlush(user);
		}
	}

	/**
	 * Updates the details of an existing user in the repository.
	 * This method looks up a user by their email address and updates their attributes if the user exists.
	 * The attributes include the user's email, first name, last name, authorities (roles), and image URL.
	 * 
	 * @param user The user object containing the new information to update. The user must have a valid email 
	 *             as it is used to locate the existing user in the repository.
	 * 
	 * @throws NoSuchElementException If the user cannot be found in the repository by email, an exception will 
	 *                                be thrown, though this is not explicitly handled in this method.
	 */
	private void updateUser(User user) {
		Optional<User> userToUpdateOpt = userRepository.findOneByEmail(user.getEmail());
		if (userToUpdateOpt.isPresent()) {
			User userToUpdate = userToUpdateOpt.get();
			userToUpdate.setEmail(user.getEmail());
			userToUpdate.setFirstName(user.getFirstName());
			userToUpdate.setLastName(user.getLastName());
			userToUpdate.setAuthorities(user.getAuthorities());
			userToUpdate.setImageUrl(user.getImageUrl());
			userRepository.saveAndFlush(userToUpdate);
		}
	}
	
	/**
	 * Retrieves a user by their public ID and maps it to a {@link ReadUserDto}.
	 * 
	 * This method first attempts to find the user in the repository using the provided public ID. 
	 * If a user is found, the user data is then mapped to a {@link ReadUserDto} using the userMapper.
	 * If the user is not found, an empty {@link Optional} is returned.
	 * 
	 * @param publicId The public ID of the user to retrieve.
	 *                 It is expected that the public ID is unique within the system.
	 * @return An {@link Optional} containing the {@link ReadUserDto} if a user with the given public ID is found,
	 *         or an empty {@link Optional} if no such user exists.
	 */
	public Optional<ReadUserDto> getByPublicId(UUID publicId) {
		Optional<User> oneByPublicId = userRepository.findOneByPublicId(publicId);
		return oneByPublicId.map(userMapper::readUserDTOToUser);
	}
	
}
