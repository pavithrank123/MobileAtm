package com.proj.mobileAtm.di.component;

import android.app.Application;

import com.proj.mobileAtm.base.view.MainApplication;
import com.proj.mobileAtm.di.module.ActivityBindingModule;
import com.proj.mobileAtm.di.module.BroadCastReceiverModule;
import com.proj.mobileAtm.di.module.ContextModule;
import com.proj.mobileAtm.di.module.SharedPreferenceModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {ContextModule.class, AndroidSupportInjectionModule.class, ActivityBindingModule.class, SharedPreferenceModule.class, BroadCastReceiverModule.class, AdapterModule.class})
public interface ApplicationComponent extends AndroidInjector<MainApplication> {

    void inject(MainApplication application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        Builder sharedpreference(SharedPreferenceModule module);
        ApplicationComponent build();
    }
}
