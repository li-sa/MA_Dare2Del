package dare2del.logic.prolog;

import org.jpl7.Atom;
import org.jpl7.JPL;
import org.jpl7.Query;
import org.jpl7.Term;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PrologFileLoader {

    private final Logger myLogger;

    private final String PROLOGFILES_DIRECTORY = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "prologFiles";

    public PrologFileLoader(Logger myLogger) {
        this.myLogger = myLogger;

        List<String> prologFilesToLoad = new ArrayList<>();
        prologFilesToLoad = addAllPrologFiles(prologFilesToLoad);

        JPL.init();

        for (String eachPrologFile : prologFilesToLoad) {
            myLogger.info("[PrologFileLoader] Load file " + eachPrologFile + ".");
            Query consultQuery = new Query("consult", new Term[]{
                    new Atom(eachPrologFile)});
            if (!consultQuery.hasSolution()) {
                myLogger.warning("[PrologFileLoader] Warning in Constructor: No file " + eachPrologFile + " found.");
            }
            consultQuery.close();
        }
    }

    private List<String> addAllPrologFiles(List<String> prologFilesToLoad) {
        Path prologDirPath = Paths.get(PROLOGFILES_DIRECTORY.replaceFirst("/", ""));
        File prologDir = prologDirPath.toFile();

        for (File eachFile : prologDir.listFiles()) {
            if (eachFile.isFile() && eachFile.getName().endsWith(".pl")) {
                prologFilesToLoad.add(eachFile.getPath());
            }
        }

        myLogger.info("[PrologFileLoader] addAllPrologFiles() found " + prologFilesToLoad.size() + " prologFiles files to load.");

        return prologFilesToLoad;
    }
}
