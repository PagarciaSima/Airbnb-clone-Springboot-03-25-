package com.airbnb.clone.AirbnbClone.booking.domain;

import java.time.OffsetDateTime;
import java.util.Objects;
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
	
	private static final long serialVersionUID = -2172932099435824996L;

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
	private UUID fkLandlord;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public OffsetDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(OffsetDateTime startDate) {
		this.startDate = startDate;
	}

	public OffsetDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(OffsetDateTime endDate) {
		this.endDate = endDate;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getNumberOfTravelers() {
		return numberOfTravelers;
	}

	public void setNumberOfTravelers(int numberOfTravelers) {
		this.numberOfTravelers = numberOfTravelers;
	}

	public UUID getFkTenant() {
		return fkTenant;
	}

	public void setFkTenant(UUID fkTenant) {
		this.fkTenant = fkTenant;
	}

	public UUID getFkLandlord() {
		return fkLandlord;
	}

	public void setFkLandlord(UUID fkLandlord) {
		this.fkLandlord = fkLandlord;
	}

	@Override
	public int hashCode() {
		return Objects.hash(endDate, fkLandlord, fkTenant, numberOfTravelers, startDate, totalPrice);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Booking other = (Booking) obj;
		return Objects.equals(endDate, other.endDate) && Objects.equals(fkLandlord, other.fkLandlord)
				&& Objects.equals(fkTenant, other.fkTenant) && numberOfTravelers == other.numberOfTravelers
				&& Objects.equals(startDate, other.startDate) && totalPrice == other.totalPrice;
	}

	@Override
	public String toString() {
		return "Booking [startDate=" + startDate + ", endDate=" + endDate + ", totalPrice=" + totalPrice
				+ ", numberOfTravelers=" + numberOfTravelers + ", fkTenant=" + fkTenant + ", fkLandlord=" + fkLandlord
				+ "]";
	}
	
	
	
	
}
