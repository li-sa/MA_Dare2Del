import org.jpl7.*;

import java.util.List;

public class PrologFirstTry {

    private List<DetailedFile> fileList;
    private static String RULES_FILE = "src/prolog/test.pl";

    public PrologFirstTry(List<DetailedFile> fileList) {
        System.out.println(">> PROLOG First Try <<");

        this.fileList = fileList;

        init();

    }

    private void init() {
        JPL.init();

        Query consultQuery = new Query("consult", new Term[] {
                new Atom(RULES_FILE)});
        if (! consultQuery.hasSolution()) {
            // TODO: Exception Handling
            //throw new Exception("File not found: " + RULES_FILE);
            System.out.println("!!! EXCEPTION: No file " + RULES_FILE + " found !!!");
        }
        consultQuery.close();

        // TODO: Load .pl's
        testQuery();
    }

    private void testQuery() {
        Term goal = new Compound( "file", new Term[]{new Atom("abc")});
        Query q = new Query( goal );
        System.out.println("***Query result: " + (q.hasSolution() ? "succeeded" : "failed"));

//        Query q1 =
//                new Query(
//                        "consult",
//                        new Term[] {new Atom("src/prolog/test.pl")}
//                );
//        System.out.println( "*** Consult " + (q1.hasSolution() ? "succeeded" : "failed"));
    }
}
