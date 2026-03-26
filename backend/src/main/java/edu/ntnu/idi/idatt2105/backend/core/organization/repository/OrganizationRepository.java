package edu.ntnu.idi.idatt2105.backend.core.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.organization.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
}
