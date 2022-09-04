# godot-in-app-update

In App Update Android Module for Godot Game.

## Usage & Docs

### Getting started

### Build library

1. Clone this repository.

2. Download the AAR Godot Library from [Godot website](https://godotengine.org/download), select Standard or Mono Version.

3. Put the `Godot AAR Library` on the libs folder `InAppUpdate(project folder)->app->libs`

4. Check the `build.gradle(Module)` file, and in the dependicies block make sure that the first line `compileOnly fileTree('dir': 'libs', includes: ['godot-lib.3.5.stable.release.aar'])` have the correct name of the file (godot arr library), in this case I used 3.5 version.

5. Press Build Button on Android Studio.

6. You can found the compiled library in the path `InAppUpdate->app->build->outputs->aar`

### Preparing for use

1. Make a file `InAppUpdate.gdap` and put the following content:

```Python
[config]

name="InAppUpdate"
binary_type="local"
binary="InAppUpdate.aar" # put here the name of library

[dependencies]

local=[]
remote=["com.google.android.play:app-update:2.0.0"]
custom_maven_repos=[]
```

2. Rename the resulting Android Studio build file (app-debug.aar) to `InAppUpdate.aar`, take this file together with `InAppUpdate.gdap` file and put them in the plugins folder of you Godot Project `android->plugins`

3. Open You project in Godot Engine

4. Set up [Android Custom Builds](https://docs.godotengine.org/en/stable/tutorials/export/android_custom_build.html#doc-android-custom-build)

5. Enable the InAppUpdate plugin

   ![Enabling the plugin](/docImage/InAppUpdatePlugin.png)

### Example Code for using

You can put it in a custom node and call in the first screen of you game.

This code connect all signal and check for updates.

```Python
func _ready():
	InAppUpdateManager = Engine.get_singleton("InAppUpdate")

	if InAppUpdateManager != null :
        InAppUpdateManager.connect("installation_accepted",self, "on_installation_accepted")
		InAppUpdateManager.connect("installation_cancelled",self, "on_installation_cancelled")
		InAppUpdateManager.connect("installation_error",self, "on_installation_error")
		InAppUpdateManager.connect("update_downloaded",self, "on_update_downloaded")

		InAppUpdateManager.checkForUpdate()
	else:
		print("Android InAppUpdate support is not enabled. Make sure you have enabled 'Custom Build' and the InAppUpdate plugin in your Android export settings! InAppUpdate will not work.")

    func on_installation_accepted():
	    # Write code here
	    pass


    func on_installation_cancelled():
	    # Write code here
	    pass


    func on_installation_error(error):
	    # Write code here
	    pass


    func on_update_downloaded():
	    # Write code here
	    pass
```

This plugin use 2 method for install updates `IMMEDIATE` and `FLEXIBLE` the plugin apply `IMMEDIATE` automatically, but for `FLEXIBLE` you need call a method for apply the update.

The next code is a example for use (using a button for call method):

```Python
func on_update_downloaded():
	installationResultLabel.text = "Download Completed"
	installButton.disabled = false
	pass


func _on_Button_pressed():
	InAppUpdateManager.installUpdate();
```

## Limitations

This plugin only work for Android API 21+, for more info check the [android library docs](https://developer.android.com/guide/playcore/in-app-updates/kotlin-java?hl=en-419#setup)
