package com.thebrokenrail.modupdater.strategy;

import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.util.ConfigObject;
import com.thebrokenrail.modupdater.util.ModUpdate;
import com.thebrokenrail.modupdater.util.ModUpdateStrategy;
import com.thebrokenrail.modupdater.util.Util;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class MavenStrategy implements ModUpdateStrategy {
    @Override
    public ModUpdate checkForUpdate(ConfigObject obj, String oldVersion, String name) {
        String repository;
        String group;
        String artifact;
        try {
            repository = obj.getString("repository");
            group = obj.getString("group");
            artifact = obj.getString("artifact");
        } catch (ConfigObject.MissingValueException e) {
            ModUpdater.invalidModUpdaterConfig(name);
            return null;
        }

        String mavenRoot = String.format("%s/%s/%s", repository, group.replaceAll("\\.", "/"), artifact);

        String data;
        try {
            data = Util.urlToString(mavenRoot + "/maven-metadata.xml");
        } catch (IOException e) {
            ModUpdater.getLogger().warn("Unable To Access Maven Repository: " + name);
            return null;
        }

        Document doc;
        try {
            SAXReader reader = new SAXReader();
            doc = reader.read(new ByteArrayInputStream(data.getBytes()));
        } catch (DocumentException e) {
            ModUpdater.getLogger().warn("Maven Repository Sent Invalid Data: " + name);
            return null;
        }

        List<Node> versions = doc.selectNodes("/metadata/versioning/versions/*");

        String newestVersion = null;
        for (Node node : versions) {
            String version = node.getText();
            if (Util.isVersionCompatible(version)) {
                if (newestVersion != null) {
                    try {
                        if (SemanticVersion.parse(version).compareTo(SemanticVersion.parse(newestVersion)) > 0) {
                            newestVersion = version;
                        }
                    } catch (VersionParsingException ignored) {
                    }
                } else {
                    newestVersion = version;
                }
            }
        }

        if (newestVersion != null) {
            try {
                if (SemanticVersion.parse(newestVersion).compareTo(SemanticVersion.parse(oldVersion)) > 0) {
                    return new ModUpdate(oldVersion, newestVersion, mavenRoot + '/' + newestVersion + '/' + artifact + '-' + newestVersion + Util.JAR_EXTENSION, name);
                } else {
                    return null;
                }
            } catch (VersionParsingException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
