package com.proj.mobileAtm.transaction.model.entity;

import lombok.Data;

@Data
public class KeyValueEntity {

    private int key ;
    private int value;

    public KeyValueEntity(int key, int value) {
        this.key = key;
        this.value = value;
    }
}
