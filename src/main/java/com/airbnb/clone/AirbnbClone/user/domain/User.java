package com.airbnb.clone.AirbnbClone.user.domain;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "airbnb_user")
public class User extends AbstractAuditingEntity<Long> implements Serializable{

}
