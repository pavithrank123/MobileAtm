package com.proj.mobileAtm.di.component;

import com.proj.mobileAtm.transaction.adapters.TransactionListAdapter;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

@Module
public class AdapterModule {

    @Provides
    public TransactionListAdapter getTransactionsadapter() {
        return new TransactionListAdapter(new ArrayList<>());
    }
}
