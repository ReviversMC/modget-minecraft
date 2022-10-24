package com.github.reviversmc.modget.minecraft;


import com.github.reviversmc.modget.minecraft.command.ListCommand;
import com.github.reviversmc.modget.minecraft.command.MigrationCheckCommand;
import com.github.reviversmc.modget.minecraft.command.RefreshCommand;
import com.github.reviversmc.modget.minecraft.command.ReposAddCommand;
import com.github.reviversmc.modget.minecraft.command.ReposDisableCommand;
import com.github.reviversmc.modget.minecraft.command.ReposEnableCommand;
import com.github.reviversmc.modget.minecraft.command.ReposListCommand;
import com.github.reviversmc.modget.minecraft.command.ReposRemoveCommand;
import com.github.reviversmc.modget.minecraft.command.SearchCommand;
import com.github.reviversmc.modget.minecraft.command.UpgradeCommand;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ModgetEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        new Thread(() -> Modget.INSTANCE.init()).start();

        // Register Commands
        EnvType env = FabricLoader.getInstance().getEnvironmentType();
        new ListCommand().register(env);
        new SearchCommand().register(env);
        new RefreshCommand().register(env);
        new UpgradeCommand().register(env);
        new MigrationCheckCommand().register(env);
        new ReposListCommand().register(env);
        new ReposAddCommand().register(env);
        new ReposEnableCommand().register(env);
        new ReposDisableCommand().register(env);
        new ReposRemoveCommand().register(env);
    }
}
