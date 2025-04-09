package com.airbnb.clone.AirbnbClone.listing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airbnb.clone.AirbnbClone.listing.domain.Listing;

public interface ListingRepository extends JpaRepository<Listing, Long>{

}
