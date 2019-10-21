package dare2del.logic;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileCrawler {

    private final Logger myLogger;

    private Path rootPath;
    private final List<DetailedFile> folderList;
    private final List<DetailedFile> fileList;

    public FileCrawler(Path rootPath, Logger myLogger) {
        this.myLogger = myLogger;
        this.rootPath = rootPath;

        myLogger.info("[FileCrawler] Start crawling on " + rootPath + ".");

        folderList = new ArrayList<>();
        fileList = new ArrayList<>();

        File rootFile = rootPath.toFile();
        crawl(rootFile);

        printResults();

        myLogger.info("[FileCrawler] Finished crawling. Found " + folderList.size() + " folders and " + fileList.size() + " files.");
    }

    private void crawl(File file) {
        DetailedFile detailedFile = new DetailedFile(file);

        if (file.isDirectory()) {
            folderList.add(detailedFile);
            File[] subFiles = file.listFiles();

            for (File subFile : subFiles) {
                crawl(subFile);
            }
        } else {
            fileList.add(detailedFile);
        }
    }

    private File init(String pathName) {
        try {
            rootPath = Paths.get(pathName);
        } catch (Exception e) {
            myLogger.warning("[FileCrawler] Exception in init(): " + e.getMessage());
            throw new IllegalArgumentException();
        }

        if (rootPath.toFile().isDirectory()) {
            return rootPath.toFile();
        } else {
            myLogger.warning("[FileCrawler] Exception in init(): " + pathName + " is no valid directory.");
            throw new IllegalArgumentException();
        }
    }

    private void printResults() {
        System.out.println("----------");

        System.out.println("FOLDERS:");
        for (int i = 0; i < folderList.size(); i++) {
            DetailedFile cF = this.folderList.get(i);
            System.out.println(i + ") " + cF.getName() + " [created: " + cF.getCreationTime() + "; last accessed: " +
                    cF.getAccessTime() + "; last modified: " + cF.getChangeTime() + "; parent file: " +
                    cF.getInDirectory() + "; file extension: " + cF.getMediaType() + "; size: " + cF.getSize() +
                    "; path: " + cF.getPath() + "]");
        }

        System.out.println("----------");

        System.out.println("FILES:");
        for (int i = 0; i < fileList.size(); i++) {
            DetailedFile cF = this.fileList.get(i);
            System.out.println(i + ") " + cF.getName() + " [created: " + cF.getCreationTime() + "; last accessed: " +
                    cF.getAccessTime() + "; last modified: " + cF.getChangeTime() + "; parent file: " +
                    cF.getInDirectory() + "; file extension: " + cF.getMediaType() + "; size: " + cF.getSize() +
                    "; path: " + cF.getPath() + "]");
        }

        System.out.println("----------");
    }

    public List<DetailedFile> getFileList() {
        return fileList;
    }

    public List<DetailedFile> getFolderList() {
        return folderList;
    }
}
