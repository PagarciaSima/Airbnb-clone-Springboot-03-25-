package com.airbnb.clone.AirbnbClone.user.application.dto;

import java.util.UUID;

public record ReadUserDto(UUID publicId, String firstName, String lastName) {

}
