package dare2del.logic.prolog;

import org.jpl7.Atom;
import org.jpl7.JPL;
import org.jpl7.Query;
import org.jpl7.Term;

import java.util.ArrayList;
import java.util.List;

public class PrologFileLoader {

    private final String CLAUSE_FILE = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../clauses.pl";
    private final String RULE_FILE = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../irrelevanceTheory.pl";

    public PrologFileLoader() {
        List<String> prologFilesToLoad = new ArrayList<>();
        prologFilesToLoad.add(CLAUSE_FILE);
        prologFilesToLoad.add(RULE_FILE);

        JPL.init();

        for (String eachPrologFile : prologFilesToLoad) {
            Query consultQuery = new Query("consult", new Term[]{
                    new Atom(eachPrologFile)});
            if (!consultQuery.hasSolution()) {
                // TODO: Exception Handling
                //throw new Exception("File not found: " + eachPrologFile);
                System.out.println("!!! EXCEPTION: No file " + eachPrologFile + " found !!!");
            }
            consultQuery.close();
        }
    }
}
