package com.proj.mobileAtm.di.module;

import com.proj.mobileAtm.authentication.view.UserAuthActivity;
import com.proj.mobileAtm.dashboard.view.DashboardActivity;
import com.proj.mobileAtm.dashboard.view.LauncherPageActivity;
import com.proj.mobileAtm.transaction.view.BalanceActivity;
import com.proj.mobileAtm.transaction.view.TransactionActivity;
import com.proj.mobileAtm.transaction.view.TransactionsListActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract UserAuthActivity bindBaseActivity();

    @ContributesAndroidInjector
    abstract DashboardActivity bindDashboardActivity();

    @ContributesAndroidInjector
    abstract TransactionActivity bindTransactionActivity();

    @ContributesAndroidInjector
    abstract LauncherPageActivity bindLauncherPageActivity();

    @ContributesAndroidInjector
    abstract TransactionsListActivity bindTransactionsListActivity();

    @ContributesAndroidInjector
    abstract BalanceActivity bindBalanceActivity();

}
