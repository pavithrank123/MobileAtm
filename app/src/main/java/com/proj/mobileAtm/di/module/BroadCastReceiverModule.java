package com.proj.mobileAtm.di.module;

import com.proj.mobileAtm.base.service.SessionTimerService;
import com.proj.mobileAtm.geofence.GeoFencingBroadcastReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BroadCastReceiverModule {

    @ContributesAndroidInjector
    abstract GeoFencingBroadcastReceiver contributesReceiver();

    @ContributesAndroidInjector
    abstract SessionTimerService contributesService();

}
