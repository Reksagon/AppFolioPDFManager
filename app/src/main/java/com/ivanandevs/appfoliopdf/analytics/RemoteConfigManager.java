package com.ivanandevs.appfoliopdf.analytics;


import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class RemoteConfigManager {

    private FirebaseRemoteConfig firebaseRemoteConfig;

    public RemoteConfigManager() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
    }

    public Single<String> getString(String key) {
        return Single.create(emitter -> {
            firebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String value = firebaseRemoteConfig.getString(key);
                            emitter.onSuccess(value);
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }

    public Single<List<String>> getStrings(List<String> keys) {
        List<Single<String>> singles = new ArrayList<>();

        for (String key : keys) {
            singles.add(getString(key));
        }

        return Single.zip(singles, objects -> {
            List<String> values = new ArrayList<>();

            for (Object object : objects) {
                values.add((String) object);
            }

            return values;
        });
    }
}
