package com.github.reviversmc.modget.minecraft;

import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage116;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText116;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager116;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager116;

import net.fabricmc.api.ModInitializer;

public class ModgetEntrypoint116 implements ModInitializer {

    @Override
    public void onInitialize() {
        VersionAgnosticText.set(new VersionAgnosticText116());
        VersionAgnosticMessage.set(new VersionAgnosticMessage116());
        VersionAgnosticServerCommandManager.set(new VersionAgnosticServerCommandManager116());
        VersionAgnosticClientCommandManager.set(new VersionAgnosticClientCommandManager116());
    }

}
