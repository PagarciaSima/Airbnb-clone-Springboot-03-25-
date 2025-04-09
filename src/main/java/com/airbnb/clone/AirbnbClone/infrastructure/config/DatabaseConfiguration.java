package com.airbnb.clone.AirbnbClone.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories({"com.airbnb.clone.AirbnbClone.user.repository"})
@EnableTransactionManagement
@EnableJpaAuditing
public class DatabaseConfiguration {

}
