package com.proj.mobileAtm.di.module;

import static androidx.security.crypto.MasterKeys.AES256_GCM_SPEC;

import android.app.Application;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import com.proj.mobileAtm.base.service.LocalCacheService;
import com.proj.mobileAtm.base.service.LocalCacheServiceImpl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferenceModule {

    SharedPreferences sharedPreferences;
    private final String filename = "local_cache";

    public SharedPreferenceModule(Application mApplication) {
        try {
            MasterKey masterKey = new MasterKey.Builder(mApplication, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    mApplication,
                    filename,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreference(){
        return this.sharedPreferences;
    }

    @Singleton
    @Provides
    LocalCacheService provideLocalCacheService(SharedPreferences sharedPreferences){
        return new LocalCacheServiceImpl(sharedPreferences);
    }

}
