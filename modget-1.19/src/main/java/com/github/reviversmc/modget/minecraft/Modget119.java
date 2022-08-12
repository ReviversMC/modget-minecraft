package com.github.reviversmc.modget.minecraft;

import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage119;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText119;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager119;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager119;

import net.fabricmc.api.ModInitializer;

public class Modget119 implements ModInitializer {

    @Override
    public void onInitialize() {
        VersionAgnosticText.set(new VersionAgnosticText119());
        VersionAgnosticMessage.set(new VersionAgnosticMessage119());
        VersionAgnosticServerCommandManager.set(new VersionAgnosticServerCommandManager119());
        VersionAgnosticClientCommandManager.set(new VersionAgnosticClientCommandManager119());
    }

}
