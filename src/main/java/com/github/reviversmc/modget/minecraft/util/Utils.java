package com.github.reviversmc.modget.minecraft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class Utils {
    public static final String JAR_EXTENSION = ".jar";
    public static Boolean shownUpdateNotification = false;

	public static Utils create() {
		return new Utils();
	}


    public String urlToString(String urlStr) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        URL url = new URL(urlStr);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        }

        return stringBuilder.toString();
    }


    public String getVersionFromFileName(String fileName) {
        while (!Character.isDigit(fileName.charAt(0))) {
            int index = fileName.indexOf("-");
            fileName = fileName.substring(index != -1 ? index + 1 : 0);
        }
        if (fileName.endsWith(JAR_EXTENSION)) {
            fileName = fileName.substring(0, fileName.length() - JAR_EXTENSION.length());
        }
        return fileName;
    }


    public String getMinecraftVersion() {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("minecraft");

        return mod.get().getMetadata().getVersion().getFriendlyString();
    }


    public boolean isFileCompatible(String fileName) {
        return !fileName.endsWith("-dev" + JAR_EXTENSION) && !fileName.endsWith("-sources" + JAR_EXTENSION) && !fileName.endsWith("-sources-dev" + JAR_EXTENSION) && fileName.endsWith(JAR_EXTENSION);
    }

    public static void showToast(Text line1, Text line2) {
        Objects.requireNonNull(MinecraftClient.getInstance()).getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT,
                line1,
                line2
        ));
    }
}
