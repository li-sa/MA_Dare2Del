package dare2del.logic.IO;

import dare2del.logic.FileCrawler;
import dare2del.logic.PrologFileWriter;

import java.nio.file.Paths;
import java.util.logging.Logger;

class IOMain {

    public static void main(String[] args) {
        // CRAWLING
//        String rootDir = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir";
        String rootDir = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir_2";
        FileCrawler fileCrawler = new FileCrawler(Paths.get(rootDir), Logger.getLogger(Logger.GLOBAL_LOGGER_NAME));

        // WRITE TO PL
        PrologFileWriter prologFW = new PrologFileWriter(fileCrawler.getFileList(), Logger.getLogger(Logger.GLOBAL_LOGGER_NAME));

        // PROLOG
        IOPrologQueries ioProlog = new IOPrologQueries(fileCrawler.getFileList());
    }
}
