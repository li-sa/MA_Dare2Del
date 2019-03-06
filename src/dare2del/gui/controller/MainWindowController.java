package dare2del.gui.controller;

import dare2del.gui.model.DeletionModel;
import dare2del.logic.DetailedFile;
import dare2del.logic.FileCrawler;
import dare2del.logic.PrologFileWriter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainWindowController {

    public DeletionModel deletionModel;

    private Path rootPath;
    private List<DetailedFile> fileList;

    public MainWindowController(String pathName) {
        validatePath(pathName);

        // Preparation: Crawl files within rootPath and write metadata to prolog file clauses.pl
        FileCrawler fileCrawler = new FileCrawler(rootPath);
        fileList = fileCrawler.getFileList();
        PrologFileWriter prologFileWriter = new PrologFileWriter(fileList);

        getDeletionCandidates();
        this.deletionModel = new DeletionModel(fileList);
    }

    private void getDeletionCandidates() {

    }

    public boolean validatePath(String pathName) {
        try {
            rootPath = Paths.get(pathName);
        } catch (Exception e) {
            System.out.println("INFO. Root folder ist no valid path (" + pathName + ").");
            throw new IllegalArgumentException();
        }

        if (rootPath.toFile().isDirectory()) {
            return true;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
