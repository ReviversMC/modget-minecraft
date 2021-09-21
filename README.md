# Modget
The Minecraft Mod Package Manager!

Modget is based on TheBrokenRail's [ModUpdater](https://gitea.thebrokenrail.com/TheBrokenRail/ModUpdater) mod and is inspired heavily by Microsoft's [Winget](https://github.com/microsoft/winget-cli).

Currenty, Modget supports the following commands:
```
/modget list                    Lists all installed mods recognized by modget
/modget repos list			    Lists all installed manifest repositories
/modget upgrade                 Lists all available mod updates
/modget refresh                 Refreshes the local manifest cache
```

These additional commands will be added over the next few releases:
```
/modget search <mod name>       Searches the repository for the according mod
/modget install <mod name>      Downloads the according mod
/modget uninstall <mod name>    Deletes the according mod
/modget upgrade <mod name>      Updates the according mod
/modget upgrade --all           Updates all mods
/modget repos add <repo link>   Adds a custom repository
/modget repos enable <repo id>  Enables the selected repository
/modget repos disable <repo id> Disables the selected repository
/modget repos remove <repo id>  Removes a custom repository
```

In contrast to the original ModUpdater, Modget doesn't require specific opt-ins. It detects mods based on their modid and gets the update files using its manifest repository.

## Mod Users
In the future, it is planned to add a proper GUI. In the meantime, you can use the above mentioned server commands.

## Mod Developers
If you want to add first-party support for your mods, please regularly submit new versions over at the [manifest repository](https://github.com/ReviversMC/modget-manifests).

## Community
[![Discord chat](https://img.shields.io/badge/chat%20on-discord-7289DA?logo=discord&logoColor=white)](https://discord.gg/nVDXfCRyMk)

We have an [official Discord community](https://discord.gg/nVDXfCRyMk) for all of our projects. By joining, you can:
- Get installation help and technical support with all of our mods 
- Be notified of the latest developments as they happen
- Get involved and collaborate with the rest of our team
- ... and just hang out with the rest of our community.

## Changelog
[View Changelog](CHANGELOG.md)
