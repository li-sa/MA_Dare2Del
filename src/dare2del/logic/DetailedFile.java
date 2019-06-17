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

    private String name_escaped;
    private String path_escaped;
    private String inDirectory_escaped;

    private String name;
    private Path path;
    private long creation_time;
    private long access_time;
    private long change_time;
    private File in_directory;
    private String media_type;
    private long size;

    public DetailedFile(File file) {
        setMetadata(file);
    }

    private void setMetadata(File file) {
        BasicFileAttributes fileAttributes = null;

        try {
            fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (Exception e) {
            // TODO: Exception Handling!
        }

        this.name = file.getName();
        this.path = file.toPath();
        this.creation_time = getSecondsSinceEpoch(Objects.requireNonNull(fileAttributes).creationTime());
        this.access_time = getSecondsSinceEpoch(fileAttributes.lastAccessTime());
        this.change_time = getSecondsSinceEpoch(fileAttributes.lastModifiedTime());
        this.in_directory = file.getParentFile();
        this.media_type = FilenameUtils.getExtension(file.getName());
        this.size = fileAttributes.size();

        this.name_escaped = StringEscapeUtils.escapeJava(this.name);
        this.path_escaped = StringEscapeUtils.escapeJava(this.path.toString().replace("\\", "\\\\"));
        this.inDirectory_escaped = StringEscapeUtils.escapeJava(this.in_directory.toString().replace("\\", "\\\\"));
    }

    private long getSecondsSinceEpoch(FileTime fileTime){
        Instant instant = fileTime.toInstant();
        return instant.getEpochSecond();
    }

    public long getCreation_time() {
        return creation_time;
    }

    public long getAccess_time() {
        return access_time;
    }

    public long getChange_time() {
        return change_time;
    }

    public File getIn_directory() {
        return in_directory;
    }

    public String getMedia_type() {
        return media_type;
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
        return name_escaped;
    }

    public String getPathEscaped() {
        return path_escaped;
    }

    public String getInDirectoryEscaped() {
        return inDirectory_escaped;
    }
}
