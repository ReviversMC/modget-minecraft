package com.github.nebelnidas.modget.command;

import com.github.nebelnidas.modget.Modget;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class CommandBase {
    static boolean isRunning = false;

	public void register(String env) {
        if (env.equals("CLIENT")) {
            registerClient();
        } else {
            registerServer();
        }
    }

	abstract void registerServer();

	abstract void registerClient();

    protected boolean checkAlreadyRunning(PlayerEntity player) {
        if (isRunning == true) {
            player.sendMessage(new TranslatableText("error." + Modget.NAMESPACE + ".command_already_processing")
                .formatted(Formatting.RED), false
            );
            return true;
        }
        return false;
    }

}
