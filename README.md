# ModUpdater
A simple Minecraft mod updater.

Created For [ModFest 1.16](https://modfest.net/1.16)

**NOTE:** This is only able to scan mods that have opted-in!

## Mod Users
Go to the Mod Menu and click the configure icon for ModUpdater to view available updates.

## Mod Developers
Both ```fabric.mod.json``` and ```build.gradle``` must be modified to opt-in to ModUpdater.

### ```fabric.mod.json```
**Maven**
```json
{
    "custom": {
        "modupdater": {
            "strategy": "maven",
            "repository": "https://maven.fabricmc.net",
            "group": "net.fabricmc.fabric-api",
            "artifact": "fabric-api"
        }
    }
}
```

**CurseForge**
```json
{
    "custom": {
        "modupdater": {
            "strategy": "curseforge",
            "projectID": 306612
        }
    }
}
```

**GitHub Releases**
```json
{
    "custom": {
        "modupdater": {
            "strategy": "github",
            "owner": "Repository Owner",
            "repository": "Repository Name"
        }
    }
}
```

### ```build.gradle```
To properly detect the version of a file, the Minecraft version must be appended to the file name.

Replace:
```gradle
version = project.mod_version
```
with your preferred format's code from the table below:

| Format | Example | Code |
| --- | --- | --- |
| ```<VERSION>+<MC-VERSION>``` (recommended) | ```thing-1.0.0+1.16.1.jar``` | ```version = "${project.mod_version}+${project.minecraft_version}"``` |
| ```<VERSION>-<MC-VERSION>``` | ```thing-1.0.0-1.16.1.jar``` | ```version = "${project.mod_version}-${project.minecraft_version}"``` |
| ```<VERSION>+<MC-MAJOR>``` | ```thing-1.0.0-1.16.jar``` | ```version = "${project.mod_version}+${project.minecraft_major_version}"``` |
| ```<VERSION>-<MC-MAJOR>``` | ```thing-1.0.0-1.16.jar``` | ```version = "${project.mod_version}-${project.minecraft_major_version}"``` |

When using a format using the Minecraft major version (specified as ```<MC-MAJOR>```), ```minecraft_mjaor_version``` must be specified in ```gradle.properties```, for instance ```minecraft_major_version = 1.16```.

## Changelog
[View Changelog](CHANGELOG.md)

## Credits
- The icon was created by ``ProspectorDev``
- The GitHub Releases strategy was written by ``AppleTheGolden``