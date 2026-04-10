package edu.ntnu.idi.idatt2105.backend.core.tenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ntnu.idi.idatt2105.backend.core.tenant.entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Tenant findByOrgNumber(String orgNumber);

    List<Tenant> findByName(String name);
}
