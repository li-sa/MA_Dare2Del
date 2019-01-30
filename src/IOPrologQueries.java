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
            System.out.println("\n -> Enter a prolog query:");
            String input = scanner.nextLine();

            switch (input.toLowerCase()) {
                case "exit":
                    readingInput = false;
                    break;
                default:
                    ParseToPrologQuery(input);
                    break;
            }
        }
    }

    private void ParseToPrologQuery(String input) {
        String[] inputParameters = input.split("\\(|,|\\).|\\)");

        Term[] newTerms = new Term[inputParameters.length - 1];
        List<Term> listOfVariables = new ArrayList<Term>();
        for (int i = 1; i < inputParameters.length; i++) {
            if (inputParameters[i].trim().startsWith("_")) {
                newTerms[i-1] = new Variable(inputParameters[i].split("_")[1].toUpperCase());
                listOfVariables.add(newTerms[i-1]);
            } else {
                newTerms[i-1] = new Atom(inputParameters[i].trim());
            }
        }

        QueryProlog(inputParameters[0] , newTerms, listOfVariables);
    }

    private void QueryProlog(String ruleToQuery, Term[] terms, List<Term> variables) {
        Term term = new Compound(ruleToQuery, terms);

        try {
            Query query = new Query(term);
            System.out.println("*** QUERY: " + query.toString());
            System.out.println("*** Result: " + (query.hasSolution() ? "true" : "false"));

            Map<String, Term> solution;
            while (query.hasMoreSolutions() && variables.size() > 0){
                solution = query.nextSolution();
                StringBuilder stringBuilder = new StringBuilder();
                for (Term variable : variables) {
                    stringBuilder.append(variable.toString() + " = " + solution.get(variable.toString()) + "\t");
                }
                System.out.println(stringBuilder);
            }

//            askForUserDecision(ruleToQuery, terms, variables);

        } catch (PrologException prolog_exception) {
            System.out.println("No valid Query! \n");
        }
    }

    private void askForUserDecision(String ruleToQuery, Term[] terms, List<Term> variables) {
        Scanner scanner = new Scanner( System.in );
        System.out.println("[0] Yeeah, another query!");
        System.out.println("[1] Show explanation.");
        System.out.println("[2] Show different example with first argument = XXX.");
        System.out.println("[3] Show different example with second argument = YYY.");
        String input = scanner.nextLine();

        switch (input) {
            case "0":
                break;
            case "1":
                showExplanation();
                break;
            case "2":
                terms[0] = new Variable("XXX");
                variables.add(terms[0]);
                QueryProlog(ruleToQuery, terms, variables);
                break;
            case "3":
                terms[1] = new Variable("YYY");
                variables.add(terms[1]);
                QueryProlog(ruleToQuery, terms, variables);
                break;
            default:
                break;
        }
    }

    private void showExplanation(){
        System.out.println("Here might be an explanation!");
    }
}