package com.airbnb.clone.AirbnbClone.booking.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.airbnb.clone.AirbnbClone.sharedkernel.domain.AbstractAuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking extends AbstractAuditingEntity<Long>{
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookingSequenceGenerator")
    @SequenceGenerator(name = "bookingSequenceGenerator", sequenceName = "booking_generator", allocationSize = 1)
    @Column(name = "id")
    private Long id;
	
	@UuidGenerator
	@Column(name = "public_id", nullable = false)
	private UUID publicId;
	
	@Column(name = "start_date", nullable = false)
	private OffsetDateTime startDate;
	
	@Column(name = "end_date", nullable = false)
	private OffsetDateTime endDate;
	
	@Column( name = "total_price", nullable = false)
	private int totalPrice;
	
	@Column( name = "nb_of_travelers", nullable = false)
	private int numberOfTravelers;
	
	@Column(name = "fk_tenant", nullable = false)
	private UUID fkTenant;
	
	@Column(name = "fk_listing", nullable = false)
	private UUID fkListing;
	
	
}
