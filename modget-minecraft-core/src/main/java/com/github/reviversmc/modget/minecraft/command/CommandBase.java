package com.github.reviversmc.modget.minecraft.command;

import com.github.reviversmc.modget.minecraft.Modget;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

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
                player.sendMessage(new TranslatableText("error." + Modget.NAMESPACE + ".command_already_processing")
                    .formatted(Formatting.RED), false
                );
            }
        }
    }

}
