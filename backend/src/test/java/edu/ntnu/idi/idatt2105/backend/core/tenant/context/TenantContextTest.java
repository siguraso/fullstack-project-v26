package edu.ntnu.idi.idatt2105.backend.core.tenant.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ntnu.idi.idatt2105.backend.common.exception.UnauthorizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TenantContextTest {

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  void getCurrentOrgThrowsWhenNotSet() {
    UnauthorizedException exception = assertThrows(UnauthorizedException.class, TenantContext::getCurrentOrg);
    assertEquals("Organization context is missing", exception.getMessage());
  }

  @Test
  void setCurrentOrgAndClearBehaveAsExpected() {
    TenantContext.setCurrentOrg(99L);
    assertEquals(99L, TenantContext.getCurrentOrg());

    TenantContext.clear();

    assertThrows(UnauthorizedException.class, TenantContext::getCurrentOrg);
  }
}

