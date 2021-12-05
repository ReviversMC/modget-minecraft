package com.github.reviversmc.modget.minecraft.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import net.minecraft.text.Text
import java.util.*

class Util {
    companion object {
        @JvmField
        var shownUpdateNotification: Boolean = false

        @JvmStatic
        fun showToast(line1: Text, line2: Text) {
            Objects.requireNonNull(MinecraftClient.getInstance()).toastManager.add(
                SystemToast(
                    SystemToast.Type.TUTORIAL_HINT,
                    line1,
                    line2
                )
            )
        }
    }
}
