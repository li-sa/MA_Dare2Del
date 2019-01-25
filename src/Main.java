public class Main {

    public static void main(String[] args) {
        // CRAWLING
        String rootDir = "C:\\Users\\Lisa\\Documents\\Studium_AI-M\\MA_2\\TestDir";
        FileCrawler fileCrawler = new FileCrawler(rootDir);

        // WRITE TO PL
        PrologFileWriter prologFW = new PrologFileWriter(fileCrawler.getFileList());

        // PROLOG
        PrologFirstTry prologFirst = new PrologFirstTry(fileCrawler.getFileList());
    }
}
