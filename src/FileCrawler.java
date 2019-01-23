import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileCrawler {

    private Path rootPath;
    private List<DetailedFile> folderList;
    private List<DetailedFile> fileList;

    public FileCrawler(String pathName) {
        System.out.println(">> File Crawler <<");
        System.out.println("INFO. Crawling startet on: " + pathName);

        folderList = new ArrayList<>();
        fileList = new ArrayList<>();

        File rootFile = init(pathName);

        crawl(rootFile);

        printResults();

        System.out.println("INFO. Finished crawling. Found " + folderList.size() + " folders and " + fileList.size() + " files. \n");
    }

    private void crawl(File file) {
        DetailedFile detailedFile = new DetailedFile(file);

        if (file.isDirectory()) {
            folderList.add(detailedFile);
            File[] subFiles = file.listFiles();

            for (int i = 0; i < subFiles.length; i++) {
                crawl(subFiles[i]);
            }
        } else {
            fileList.add(detailedFile);
        }
    }

    private File init(String pathName) {
        try {
            rootPath = Paths.get(pathName);
        } catch (Exception e) {
            System.out.println("INFO. Root folder ist no valid path (" + pathName + ").");
        }

        if (rootPath.toFile().isDirectory()) {
            File rootFile = rootPath.toFile();
            return rootFile;
        } else {
            throw new IllegalArgumentException();
        }
    }


    private void printResults() {
        System.out.println("----------");

        System.out.println("FOLDERS:");
        for (int i = 0; i < folderList.size(); i++) {
            DetailedFile cF = this.folderList.get(i);
            System.out.println(i + ") " + cF.getName() + " [created: " + cF.getCreation_time() + "; last accessed: " +
                    cF.getAccess_time() + "; last modified: " + cF.getChange_time() + "; parent file: " +
                    cF.getIn_directory() + "; file extension: " + cF.getMedia_type() + "; size: " + cF.getSize() +
                    "; path: " + cF.getPath() + "]");
        }

        System.out.println("----------");

        System.out.println("FILES:");
        for (int i = 0; i < fileList.size(); i++) {
            DetailedFile cF = this.fileList.get(i);
            System.out.println(i + ") " + cF.getName() + " [created: " + cF.getCreation_time() + "; last accessed: " +
                    cF.getAccess_time() + "; last modified: " + cF.getChange_time() + "; parent file: " +
                    cF.getIn_directory() + "; file extension: " + cF.getMedia_type() + "; size: " + cF.getSize() +
                    "; path: " + cF.getPath() + "]");
        }

        System.out.println("----------");
    }

    public List<DetailedFile> getFolderList() {
        return folderList;
    }

    public List<DetailedFile> getFileList() {
        return fileList;
    }
}
