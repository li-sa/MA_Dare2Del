package dare2del.logic;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Objects;

public class DetailedFile {

    private String name;
    private Path path;
    private long creationTime;
    private long accessTime;
    private long changeTime;
    private File inDirectory;
    private String mediaType;
    private long size;

    private String nameEscaped;
    private String pathEscaped;
    private String inDirectoryEscaped;

    public DetailedFile(File file) {
        setMetadata(file);
    }

    private void setMetadata(File file) {
        BasicFileAttributes fileAttributes = null;

        try {
            fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.name = file.getName();
        this.path = file.toPath();
        this.creationTime = getSecondsSinceEpoch(Objects.requireNonNull(fileAttributes).creationTime());
        this.accessTime = getSecondsSinceEpoch(fileAttributes.lastAccessTime());
        this.changeTime = getSecondsSinceEpoch(fileAttributes.lastModifiedTime());
        this.inDirectory = file.getParentFile();
        this.mediaType = FilenameUtils.getExtension(file.getName());
        this.size = fileAttributes.size();

        this.nameEscaped = StringEscapeUtils.escapeJava(this.name);
        this.pathEscaped = StringEscapeUtils.escapeJava(this.path.toString().replace("\\", "\\\\"));
        this.inDirectoryEscaped = StringEscapeUtils.escapeJava(this.inDirectory.toString().replace("\\", "\\\\"));
    }

    private long getSecondsSinceEpoch(FileTime fileTime) {
        Instant instant = fileTime.toInstant();
        return instant.getEpochSecond();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getAccessTime() {
        return accessTime;
    }

    public long getChangeTime() {
        return changeTime;
    }

    public File getInDirectory() {
        return inDirectory;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public Path getPath() {
        return path;
    }

    public String getNameEscaped() {
        return nameEscaped;
    }

    public String getPathEscaped() {
        return pathEscaped;
    }

    public String getInDirectoryEscaped() {
        return inDirectoryEscaped;
    }
}
