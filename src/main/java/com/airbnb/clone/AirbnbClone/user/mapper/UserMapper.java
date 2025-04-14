package com.airbnb.clone.AirbnbClone.user.mapper;

import org.mapstruct.Mapper;

import com.airbnb.clone.AirbnbClone.user.application.dto.ReadUserDto;
import com.airbnb.clone.AirbnbClone.user.domain.Authority;
import com.airbnb.clone.AirbnbClone.user.domain.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	ReadUserDto readUserDTOToUser(User user);
	
	default String mapAuthoritiesToString(Authority authority) {
		return authority.getName();
	}

}
