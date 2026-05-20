package com.bdp.application.cboard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CboardPermissionServiceTest {

    @Test
    void bitSet_matchesLegacyRolePermission() {
        assertThat(CboardPermissionService.bitSet("11", 0)).isTrue();
        assertThat(CboardPermissionService.bitSet("11", 1)).isTrue();
        assertThat(CboardPermissionService.bitSet("10", 1)).isFalse();
    }
}
