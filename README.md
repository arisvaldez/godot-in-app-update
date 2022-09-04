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
		print("Android IAP support is not enabled. Make sure you have enabled 'Custom Build' and the InAppUpdate plugin in your Android export settings! IAP will not work.")

```
