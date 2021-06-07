**Note:** Currently, none of the following features are implemented. Wait for the first release to get that functionality!

# Modget
The Minecraft Mod Package Manager!

Modget is based on TheBrokenRail's [ModUpdater](https://gitea.thebrokenrail.com/TheBrokenRail/ModUpdater) mod and is inspired heavily by Microsoft's [Winget](https://github.com/microsoft/winget-cli).

Currenty, Modget supports the following commands:
```
/modget upgrade			Lists all available mod updates
/modget upgrade <modname>	Updates the according mod
/modget upgrade --all		Updates all mods
```

In contrast to the original ModUpdater, Modget doesn't require specific opt-ins; it uses its own manifest repository to automatically scan and detect all mods based on their modid.

## Mod Users
In the future, it is planned to add a proper GUI. In the meantime, you can use the above mentioned server commands.

## Mod Developers
If you want to add first-party support for your mods, please regularly sumbmit new versions over at the [manifest repository](https://github.com/ReviversMC/modget-manifests)

## Changelog
[View Changelog](CHANGELOG.md)
