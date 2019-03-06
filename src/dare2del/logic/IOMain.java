package dare2del.logic;

class IOMain {

    public static void main(String[] args) {
        // CRAWLING
//        String rootDir = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir";
        String rootDir = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir_2";
        FileCrawler fileCrawler = new FileCrawler(rootDir);

        // WRITE TO PL
        PrologFileWriter prologFW = new PrologFileWriter(fileCrawler.getFileList());

        // PROLOG
        IOPrologQueries ioProlog = new IOPrologQueries(fileCrawler.getFileList());
    }
}
