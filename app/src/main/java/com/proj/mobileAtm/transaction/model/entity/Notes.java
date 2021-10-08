package com.proj.mobileAtm.transaction.model.entity;

import lombok.Data;

@Data
public class Notes {

    protected int available;
    protected int value;

    public Notes(int available, int value) {
        this.available = available;
        this.value = value;
    }

}
