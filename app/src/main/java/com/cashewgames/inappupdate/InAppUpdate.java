package com.cashewgames.inappupdate;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InAppUpdate extends GodotPlugin {
    private Activity activity;
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 100;

    public InAppUpdate(Godot godot) {
        super(godot);
        activity = godot.getActivity();
        mAppUpdateManager = AppUpdateManagerFactory.create(activity);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "InAppUpdate";
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new HashSet<>();
        signals.add(new SignalInfo("testSignal", String.class));
        return signals;
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        List<String> pluginsMethods = new ArrayList<String>();
        pluginsMethods.add("checkForUpdate");
        return pluginsMethods;
    }

    public void checkForUpdate() {
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, activity, RC_APP_UPDATE);

                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mAppUpdateManager.registerListener(installStateUpdatedListener);
    }

    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                showCompletedUpdate();
            }
        }
    };

    private void showCompletedUpdate() {
        emitSignal("update_downloaded");
    }

    public void InstallFlexibleUpdate() {
        mAppUpdateManager.completeUpdate();
    }


    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_APP_UPDATE && resultCode != RESULT_OK) {
            emitSignal("installation_canceled");
        } else {
            emitSignal("installation_accepted");
        }
        super.onMainActivityResult(requestCode, resultCode, data);
    }
}
