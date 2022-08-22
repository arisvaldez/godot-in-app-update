package com.cashewgames.inappupdate;

import android.app.Activity;
import android.content.Intent;
import android.util.ArraySet;

import androidx.annotation.NonNull;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InAppUpdate extends GodotPlugin {
    private Activity activity;

    public InAppUpdate(Godot godot) {
        super(godot);
        activity = godot.getActivity();
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
        pluginsMethods.add("getHello");
        pluginsMethods.add("getHelloSignal");
        pluginsMethods.add("shareText");
        return pluginsMethods;
    }

    public String getHello(){
        return "HelloWorld";
    }

    public void getHelloSignal(String s){
        emitSignal("testSignal",s);
    }

    public void shareText(String text){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        activity.startActivity(Intent.createChooser(intent, "test"));
    }
}
