package org.benchmarker.bmcontroller.template.service.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ParentTXCheckTest {

    @Test
    @DisplayName("부모 트랜젝션 활성화 여부 확인")
    void testIsParentTransactionActive() {
        assertThrows(IllegalStateException.class, ParentTXCheck::IsParentTransactionActive);
    }

    @Test
    @DisplayName("부모 트랜젝션 비활성화 여부 확인")
    @Transactional
    void testIsParentTransactionNotActive() {
        ParentTXCheck.IsParentTransactionActive();
    }

}