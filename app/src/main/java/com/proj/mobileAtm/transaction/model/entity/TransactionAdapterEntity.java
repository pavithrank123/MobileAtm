package com.proj.mobileAtm.transaction.model.entity;

import lombok.Data;

@Data
public class TransactionAdapterEntity {

    private String amount;
    private String transactionType;
    private String debitedAt;

}
