import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class DetailedFile {

    private File file;

    private String name;
    private FileTime creation_time;
    private FileTime access_time;
    private FileTime change_time;
    private File in_directory;
    private String media_type;
    private long size;
    private Path path;

    public DetailedFile(File file) {
        this.file = file;
        setMetadata();
    }

    private void setMetadata() {
        BasicFileAttributes fileAttributes = null;

        try {
            fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (Exception e) {
            // TODO: Exception Handling!
        }

        this.name = file.getName();
        this.creation_time = fileAttributes.creationTime();
        this.access_time = fileAttributes.lastAccessTime();
        this.change_time = fileAttributes.lastModifiedTime();
        this.in_directory = file.getParentFile();
        this.media_type = getFileExtension();
        this.size = fileAttributes.size();
        this.path = file.toPath();
    }

    private String getFileExtension() {
        String fileExtension = null;

        String extensionSeperator = ".";
        String fileName = this.file.getName();

        if (fileName.lastIndexOf(extensionSeperator) != -1 && fileName.lastIndexOf(extensionSeperator) != 0) {
            fileExtension = fileName.substring(fileName.lastIndexOf(extensionSeperator) + 1);
        }

        return fileExtension;
    }

    public File getFile() {
        return file;
    }

    public FileTime getCreation_time() {
        return creation_time;
    }

    public FileTime getAccess_time() {
        return access_time;
    }

    public FileTime getChange_time() {
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
}
