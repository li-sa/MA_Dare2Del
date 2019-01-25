import org.jpl7.*;

import java.util.List;

public class PrologFirstTry {

    private List<DetailedFile> fileList;
    private static String CLAUSE_FILE = "src/prolog/test.pl";
    private static String RULE_FILE = "src/prolog/irrelevanceTheory.pl";

    public PrologFirstTry(List<DetailedFile> fileList) {
        System.out.println(">> PROLOG First Try <<");

        this.fileList = fileList;

        init();

    }

    private void init() {
        JPL.init();

        Query consultQuery_clauses = new Query("consult", new Term[] {
                new Atom(CLAUSE_FILE)});
        if (! consultQuery_clauses.hasSolution()) {
            // TODO: Exception Handling
            //throw new Exception("File not found: " + CLAUSE_FILE);
            System.out.println("!!! EXCEPTION: No file " + CLAUSE_FILE + " found !!!");
        }
        consultQuery_clauses.close();

       Query consultQuery_rules = new Query("consult", new Term[] {
                new Atom(RULE_FILE)});
        if (! consultQuery_rules.hasSolution()) {
            // TODO: Exception Handling
            //throw new Exception("File not found: " + RULE_FILE);
            System.out.println("!!! EXCEPTION: No file " + RULE_FILE + " found !!!");
        }
        consultQuery_rules.close();

        testQuery();
    }

    private void testQuery() {
        System.out.println("***QUERY: file(abc)");
        Term goal = new Compound( "file", new Term[]{new Atom("abc")});
        Query q = new Query( goal );
        System.out.println("***Query result: " + (q.hasSolution() ? "succeeded" : "failed"));

        System.out.println("***QUERY: in_same_directory(Textdokument_1-1.txt, Textdokument_1-2.txt)");
        Term sameDir = new Compound( "in_same_directory", new Term[]{new Atom("Textdokument_1-1.txt"), new Atom("Textdokument_1-2.txt")});
        Query q_sameDir = new Query( sameDir );
        System.out.println("***Query result: " + (q_sameDir.hasSolution() ? "succeeded" : "failed"));

//        Query q1 =
//                new Query(
//                        "consult",
//                        new Term[] {new Atom("src/prolog/test.pl")}
//                );
//        System.out.println( "*** Consult " + (q1.hasSolution() ? "succeeded" : "failed"));
    }
}
