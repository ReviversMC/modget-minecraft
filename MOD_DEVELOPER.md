# Mod Developers
To opt-in a mod for ModUpdater, you must select an update strategy in ```fabric.mod.json```.

## CurseForge
This update strategy uses the CurseForge API to check for updates.

### ```fabric.mod.json```
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

- Requires Semantic Versioning
- [Requires ```build.gradle``` modification](#build-gradle-modification)

## GitHub Releases
This update strategy uses the GitHub Releases API to check for updates.

### ```fabric.mod.json```
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

- Requires Semantic Versioning
- [Requires ```build.gradle``` Modification](#build-gradle-modification)

## Maven
This update strategy uses the specified Maven repository to check for updates.

### ```fabric.mod.json```
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

- Requires Semantic Versioning
- [Requires ```build.gradle``` Modification](#build-gradle-modification)

## JSON
This update strategy uses the specified JSON file to check for updates.

### ```fabric.mod.json```
```json
{
    "custom": {
        "modupdater": {
            "strategy": "json",
            "url": "https://example.com/thing.json"
        }
    }
}
```

### JSON Format
```json
{
    "1.16.1": {
        "version": "1.0.1",
        "downloadUrl": "https://example.com/thing2.jar"
    },
    "20w20a": {
        "version": "1.0.0",
        "downloadUrl": "https://example.com/thing.jar"
    }
}
```

- Does Not Use Semantic Versioning
  - A mod is marked as out-of-date if the version in the JSON is different from the current version, so if the current version is newer than the one in the JSON, it will still be marked as out-of-date.
- ```build.gradle``` Modification Is Not Required

## ```build.gradle``` Modification
Multiple update strategies require the Minecraft version to be appended to the end of the JAR version to detect what Minecraft version a JAR supports.

Replace:
```gradle
version = project.mod_version
```
with:
```gradle
version = "${project.mod_version}+${project.minecraft_version}"
```

If you prefer hyphens you can also use:
```gradle
version = "${project.mod_version}-${project.minecraft_version}"
```

You can also just use the major version of Minecraft instead of the full version (like ```1.16``` instead of ```1.16.1``` or ```20w20a```).