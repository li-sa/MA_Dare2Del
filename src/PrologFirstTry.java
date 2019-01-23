import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;

import java.util.List;

public class PrologFirstTry {

    private List<DetailedFile> fileList;

    public PrologFirstTry(List<DetailedFile> fileList) {
        System.out.println(">> PROLOG First Try <<");

        this.fileList = fileList;

        init();
        testQuery();
    }

    private void init() {
        Compound teacher_of = new Compound(
                "teacher_of",
                new Term[] {
                        new Atom("aristotle"),
                        new Atom("alexander")
                }
        );
    }

    private void testQuery() {
//        Term goal = new Compound( "teacher_of", new Term[]{new Atom("aristotle"),new Atom("alexander")});
//        Query q = new Query( goal );
//        System.out.println("***Query result: " + (q.hasSolution() ? "succeeded" : "failed"));

        Query q1 =
                new Query(
                        "consult",
                        new Term[] {new Atom("src/prolog/test.pl")}
                );
        System.out.println( "*** Consult " + (q1.hasSolution() ? "succeeded" : "failed"));
    }
}
