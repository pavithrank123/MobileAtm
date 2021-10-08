package com.proj.mobileAtm.transaction.model.entity;

import java.util.List;

import lombok.Data;

@Data
public class CurrencyDispatched {

    private List<KeyValueEntity> currencyList;

    public CurrencyDispatched(List<KeyValueEntity> currencyList) {
        this.currencyList = currencyList;
    }
}
