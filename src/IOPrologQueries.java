import org.jpl7.*;

import java.util.*;

public class IOPrologQueries {

    private static String CLAUSE_FILE = "src/prolog/clauses.pl";
    private static String RULE_FILE = "src/prolog/irrelevanceTheory.pl";
    private List<DetailedFile> fileList;

    private boolean readingInput = true;

    public IOPrologQueries(List<DetailedFile> fileList) {
        System.out.println(">> PROLOG IO <<");
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

        ReadConsoleInput();
    }

    private void ReadConsoleInput() {
        while (readingInput) {
            Scanner scanner = new Scanner( System.in );
            System.out.println("Enter a prolog query:");
            String input = scanner.nextLine();

            ParseToPrologQuery(input);
        }
    }

    private void ParseToPrologQuery(String input) {
        if (input.toLowerCase().equals("stop")) {
            readingInput = false;
            return;
        }

        String[] inputParameters = input.split("\\(|,|\\)");

        Term[] newTerms = new Term[inputParameters.length - 1];
        for (int i = 1; i < inputParameters.length; i++) {
            if (Character.isUpperCase(inputParameters[i].codePointAt(0))) {
                newTerms[i-1] = new Variable(inputParameters[i].trim());
            } else {
                newTerms[i-1] = new Atom(inputParameters[i].trim());
            }
        }

        System.out.println("*** QUERY: " + inputParameters[0] + "(" + Arrays.toString(newTerms) + ")");
        Term term = new Compound(inputParameters[0] , newTerms);

        try {
            Query query = new Query(term);
            System.out.println("*** QUERY: " + query.toString());
            System.out.println("*** Result: " + (query.hasSolution() ? "succeeded" : "failed"));

            Map<String, Term> solution;
            while (query.hasMoreSolutions() ){
                solution = query.nextSolution();
                System.out.println( "solution = " + solution.values());
            }

            System.out.println("\n");
        } catch (PrologException plEx) {
            System.out.println("No valid Query! \n");
        }
    }
}
