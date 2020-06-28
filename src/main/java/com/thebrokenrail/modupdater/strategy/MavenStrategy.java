package com.thebrokenrail.modupdater.strategy;

import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.api.ConfigObject;
import com.thebrokenrail.modupdater.api.UpdateStrategy;
import com.thebrokenrail.modupdater.data.ModUpdate;
import com.thebrokenrail.modupdater.util.Util;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MavenStrategy implements UpdateStrategy {
    private final SAXReader reader = new SAXReader();

    @Override
    @Nullable
    public ModUpdate run(ConfigObject obj, String oldVersion, String name) {
        String repository;
        String group;
        String artifact;
        try {
            repository = obj.getString("repository");
            group = obj.getString("group");
            artifact = obj.getString("artifact");
        } catch (ConfigObject.MissingValueException e) {
            ModUpdater.logWarn(name, e.getMessage());
            return null;
        }

        String mavenRoot = String.format("%s/%s/%s", repository, group.replaceAll("\\.", "/"), artifact);

        String data;
        try {
            data = Util.urlToString(mavenRoot + "/maven-metadata.xml");
        } catch (IOException e) {
            ModUpdater.logWarn(name, e.toString());
            return null;
        }

        Document doc;
        try (InputStream source = new ByteArrayInputStream(data.getBytes())) {
            doc = reader.read(source);
        } catch (DocumentException | IOException e) {
            ModUpdater.logWarn(name, e.toString());
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
                    return new ModUpdate(oldVersion, newestVersion, String.format("%s/%s/%s-%s%s", mavenRoot, newestVersion, artifact, newestVersion, Util.JAR_EXTENSION), name);
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
