package org.benchmarker.bmcontroller.template.service.utils;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * check parent transaction
 */
public class ParentTXCheck {

    /**
     * check parent transaction
     *
     * @throws IllegalStateException if parent transaction is not active or not exist
     */
    public static void IsParentTransactionActive() {
        boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        if (!actualTransactionActive) {
            throw new IllegalStateException("Parent transaction is not active");
        }
    }
}
