package edu.ntnu.idi.idatt2105.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
}
