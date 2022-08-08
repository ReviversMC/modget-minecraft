package com.github.reviversmc.modget.minecraft;

import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticCommandManager;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticCommandManager119;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage119;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText119;

import net.fabricmc.api.ModInitializer;

public class Modget119 implements ModInitializer {

    @Override
    public void onInitialize() {
        VersionAgnosticText.set(new VersionAgnosticText119());
        VersionAgnosticMessage.set(new VersionAgnosticMessage119());
        VersionAgnosticCommandManager.set(new VersionAgnosticCommandManager119());
    }

}
