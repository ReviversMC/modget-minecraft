package com.github.nebelnidas.modget.modget_minecraft.command;

import com.github.nebelnidas.modget.modget_minecraft.Modget;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class CommandBase {
    volatile static boolean manifestApiOutdated = false;
    volatile static boolean isRunning = false;
    volatile static String ENVIRONMENT;


    public static void setManifestApiOutdated(boolean outdated) {
        manifestApiOutdated = outdated;
    }


	public void register(String env) {
        ENVIRONMENT = env;
        if (env.equals("CLIENT")) {
            registerClient();
        } else {
            registerServer();
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
            if (isRunning == true) {
                player.sendMessage(new TranslatableText("error." + Modget.NAMESPACE + ".command_already_processing")
                    .formatted(Formatting.RED), false
                );
            }
        }
    }

}
