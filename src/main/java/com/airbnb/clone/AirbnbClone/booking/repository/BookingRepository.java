package com.airbnb.clone.AirbnbClone.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airbnb.clone.AirbnbClone.booking.domain.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long>{

}
