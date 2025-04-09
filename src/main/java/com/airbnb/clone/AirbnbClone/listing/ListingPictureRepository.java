package com.airbnb.clone.AirbnbClone.listing;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airbnb.clone.AirbnbClone.listing.domain.ListingPicture;

public interface ListingPictureRepository extends JpaRepository<ListingPicture, Long>{

}
