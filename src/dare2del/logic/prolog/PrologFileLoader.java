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

public class PrologFileLoader {

//    private final String CLAUSE_FILE = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../PrologFiles/clauses.pl";
//    private final String RULE_FILE = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../PrologFiles/irrelevanceTheory.pl";

    private final String PROLOGFILES_DIRECTORY = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../PrologFiles";

    public PrologFileLoader() {
        List<String> prologFilesToLoad = new ArrayList<>();
        prologFilesToLoad = addAllPrologFiles(prologFilesToLoad);

//        prologFilesToLoad.add(CLAUSE_FILE);
//        prologFilesToLoad.add(RULE_FILE);

        JPL.init();

        for (String eachPrologFile : prologFilesToLoad) {
            Query consultQuery = new Query("consult", new Term[]{
                    new Atom(eachPrologFile)});
            if (!consultQuery.hasSolution()) {
                // TODO: Exception Handling
                System.out.println("!!! EXCEPTION: No file " + eachPrologFile + " found !!!");
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

        return prologFilesToLoad;
    }
}
