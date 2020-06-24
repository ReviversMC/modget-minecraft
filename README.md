# ModUpdater
A simple Minecraft mod updater.

## Mod Users
Go to the Mod Menu and click the configure icon for ModUpdater.

## Mod Developers
Place this in your ``fabric.mod.json``:

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

Also replace this in ````build.gradle````:
```gradle
version = project.mod_version
```
with:
```gradle
version = "${project.mod_version}+${project.minecraft_version}"
```

## Changelog
[View Changelog](CHANGELOG.md)

## Credits
The icon is a recolored version of ModMenu's icon.