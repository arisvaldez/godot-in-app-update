/*************************************************************************/
/*  MIT License                                                          */
/*  InAppUpdate.java                                                     */
/*************************************************************************/
/* Copyright (c) 2022 Arismendy Polanco                                  */
/*                                                                       */
/* Permission is hereby granted, free of charge, to any person obtaining */
/* a copy of this software and associated documentation files (the       */
/* "Software"), to deal in the Software without restriction, including   */
/* without limitation the rights to use, copy, modify, merge, publish,   */
/* distribute, sublicense, and/or sell copies of the Software, and to    */
/* permit persons to whom the Software is furnished to do so, subject to */
/* the following conditions:                                             */
/*                                                                       */
/* The above copyright notice and this permission notice shall be        */
/* included in all copies or substantial portions of the Software.       */
/*                                                                       */
/* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,       */
/* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF    */
/* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.*/
/* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY  */
/* CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,  */
/* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE     */
/* SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                */
/*************************************************************************/


package com.cashewgames.inappupdate;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
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
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.HashSet;
import java.util.Set;

public class InAppUpdate extends GodotPlugin {
    private Activity activity;
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 100;
    private String updateType = "flexible";


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
        signals.add(new SignalInfo("installation_accepted"));
        signals.add(new SignalInfo("installation_cancelled"));
        signals.add(new SignalInfo("installation_error", String.class));
        signals.add(new SignalInfo("update_downloaded"));
        return signals;
    }

    @UsedByGodot
    public void checkForUpdate() {
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        updateType = "flexible";
                        mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, activity, RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        emitSignal("installation_error", e.getMessage());
                    }
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        updateType = "immediate";
                        mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, activity, RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        emitSignal("installation_error", e.getMessage());
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
                emitSignal("update_downloaded");
            }
        }
    };

    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_APP_UPDATE && resultCode != RESULT_OK) {
            emitSignal("installation_cancelled");
        } else {
            emitSignal("installation_accepted");
        }
        super.onMainActivityResult(requestCode, resultCode, data);
    }

    @UsedByGodot
    public void installUpdate() {
        if(updateType.equals("flexible")) {
            mAppUpdateManager.completeUpdate();
        }
    }

    @UsedByGodot
    public String getUpdateType() {
        return updateType;
    }
}
