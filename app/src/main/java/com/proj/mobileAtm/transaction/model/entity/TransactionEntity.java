package com.proj.mobileAtm.transaction.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionEntity {
    private int amount;
    private String transactionType;
    private long debitedAt;
}
