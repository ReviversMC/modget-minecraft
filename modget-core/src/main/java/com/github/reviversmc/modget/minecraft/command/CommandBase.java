package com.github.reviversmc.modget.minecraft.command;

import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;

public abstract class CommandBase {
    volatile static boolean manifestApiOutdated = false;
    volatile static boolean isRunning = false;
    volatile static EnvType ENVIRONMENT;


    public static void setManifestApiOutdated(boolean outdated) {
        manifestApiOutdated = outdated;
    }


	public void register(EnvType env) {
        if ((ENVIRONMENT = env) == EnvType.SERVER) {
            registerServer();
        } else {
            registerClient();
        }
    }

	abstract void registerServer();

	abstract void registerClient();


    public abstract class StartThread extends Thread {
        public PlayerEntity player;

        public StartThread(PlayerEntity player) {
            this.player = player;
        }

        public void run() {
            if (isRunning) {
                VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                        String.format("error.%s.command_already_processing", Modget.NAMESPACE)));
            }
        }
    }

}
