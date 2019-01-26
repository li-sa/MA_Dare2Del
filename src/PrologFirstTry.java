import org.jpl7.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrologFirstTry {

    private static String CLAUSE_FILE = "src/prolog/clauses.pl";
    private static String RULE_FILE = "src/prolog/irrelevanceTheory.pl";
    private List<DetailedFile> fileList;

    public PrologFirstTry(List<DetailedFile> fileList) {
        System.out.println(">> PROLOG First Try <<");

        this.fileList = fileList;

        init();

    }

    private void init() {
        List<String> plToLoad = new ArrayList<String>();
        plToLoad.add(CLAUSE_FILE);
        plToLoad.add(RULE_FILE);

        JPL.init();

        for (String eachPl : plToLoad) {
            Query consultQuery = new Query("consult", new Term[]{
                    new Atom(eachPl)});
            if (!consultQuery.hasSolution()) {
                // TODO: Exception Handling
                //throw new Exception("File not found: " + eachPl);
                System.out.println("!!! EXCEPTION: No file " + eachPl + " found !!!");
            }
            consultQuery.close();
        }

        testQuery();
    }

    private void testQuery() {
        System.out.println("***QUERY: file(abc)");
        Term goal = new Compound("file", new Term[]{new Atom("abc")});
        Query q = new Query(goal);
        System.out.println("***Query result: " + (q.hasSolution() ? "succeeded" : "failed"));

        System.out.println("***QUERY: in_same_directory(Textdokument_1-1.txt, Textdokument_1-2.txt)");
        Term sameDir = new Compound("in_same_directory", new Term[]{new Atom("Textdokument_1-1.txt"), new Atom("Textdokument_1-2.txt")});
        Query q_sameDir = new Query(sameDir);
        System.out.println("***Query result: " + (q_sameDir.hasSolution() ? "succeeded" : "failed"));

        /*System.out.println("***QUERY: newer(Textdokument_1-1.txt, Textdokument_1-2.txt)");
        Term term_newer = new Compound("newer", new Term[]{new Atom("Textdokument_1-1.txt"), new Atom("Textdokument_1-2.txt")});
        Query q_newer = new Query(term_newer);
        System.out.println("***Query result: " + (q_newer.hasSolution() ? "succeeded" : "failed"));*/

        System.out.println("***QUERY: creation_time(Textdokument_1-1.txt)");
        Term term_creationTime = new Compound("creation_time", new Term[]{new Atom("Textdokument_1-1.txt"), new Variable("X")});
        Query q_creationTime = new Query(term_creationTime);
        System.out.println("***Query result: " + (q_creationTime.hasSolution() ? "succeeded" : "failed"));
        Map<String, Term> solution;
        while (q_creationTime.hasMoreSolutions() ){
            solution = q_creationTime.nextSolution();
            System.out.println( "X = " + solution.get("X"));
        }

//        Query q1 =
//                new Query(
//                        "consult",
//                        new Term[] {new Atom("src/prolog/clauses.pl")}
//                );
//        System.out.println( "*** Consult " + (q1.hasSolution() ? "succeeded" : "failed"));
    }
}
