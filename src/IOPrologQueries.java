import org.jpl7.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

        getQueryTrace(inputParameters[0] , newTerms, listOfVariables);
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

    private void getQueryTrace(String ruleToQuery, Term[] terms, List<Term> variables) {
        Term term = new Compound(ruleToQuery, terms);

        try {
            Term term_setof = new Compound("set_of_clause", new Term[]{term, new Variable("Set")});

            Query query = new Query(term_setof);
            System.out.println("*** QUERY: " + query.toString());
            System.out.println("*** Result: " + (query.hasSolution() ? "true" : "false"));

            Map<String, Term> solution;
            while (query.hasMoreSolutions() && variables.size() > 0){
                solution = query.nextSolution();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(solution.get("Set"));
                System.out.println(stringBuilder);
            }

        } catch (PrologException prolog_exception) {
            System.out.println("No valid Query! \n");
        }
    }

    private void getQueryTrace_old(String ruleToQuery, Term[] terms, List<Term> variables) {
        Term term = new Compound(ruleToQuery, terms);

        // setof(Body,(clause(irrelevant_compared_to_other_file(A,B),Body),call(Body)),List).
        Variable variable_body = new Variable("Body");
        Variable variable_resultList = new Variable("ResultList");
        Term term_clause = new Compound("clause", new Term[]{term, variable_body});
        Term term_call = new Compound("call", new Term[]{variable_body});
        Term term_setof = new Compound("setof", new Term[]{variable_body, new Atom("("+ term_clause + "," + term_call +")"), variable_resultList});

        try {
/*            // Clause
            Query query_clause = new Query(term_clause);
            System.out.println("*** QUERY: " + query_clause.toString());
            System.out.println("*** Result: " + (query_clause.hasSolution() ? "true" : "false"));

            Map<String, Term> solution1;
            Term body_fromClause = null;
            while (query_clause.hasMoreSolutions()) {
                solution1 = query_clause.nextSolution();
                body_fromClause = solution1.get("Body");
            }

            // Call
            Term term_call = new Compound("call", new Term[]{body_fromClause});
            Query query_call = new Query(term_call);
            System.out.println("*** QUERY: " + query_call.toString());
            System.out.println("*** Result: " + (query_call.hasSolution() ? "true" : "false"));

            Map<String, Term> solution2;
            Term goal_fromCall = null;
            while (query_call.hasMoreSolutions()) {
                solution2 = query_call.nextSolution();
                goal_fromCall = solution2.get("Body");
            }*/

            // Setof
//            Term term_setof = new Compound("setof", new Term[]{variable_body, goal_fromCall, variable_resultList});

            Query query = new Query(term_setof);
            System.out.println("*** QUERY: " + query.toString());
            System.out.println("*** Result: " + (query.hasSolution() ? "true" : "false"));

            Map<String, Term> solution;
            while (query.hasMoreSolutions() && variables.size() > 0){
                solution = query.nextSolution();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(solution.get("ResultList"));
                /*for (Term variable : variables) {
                    stringBuilder.append(variable.toString() + " = " + solution.get(variable.toString()) + "\t");
                }*/
                System.out.println(stringBuilder);
            }

//            askForUserDecision(ruleToQuery, terms, variables);

        } catch (PrologException prolog_exception) {
            System.out.println("No valid Query! \n");
        }
    }

    private void askForUserDecision(String ruleToQuery, Term[] terms, List<Term> variables) {
        int termCounter = terms.length;

        Scanner scanner = new Scanner( System.in );
        System.out.println("[0] Yeeah, another query!");
        System.out.println("[1] Show explanation.");
        if(termCounter == 2) {
            System.out.println("[2] Show different example with first argument = XXX.");
            System.out.println("[3] Show different example with second argument = YYY.");
        } else if (termCounter == 1) {
            System.out.println("[2] Show different example with argument = XXX.");
        }

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
                if (termCounter == 2) {
                    terms[1] = new Variable("YYY");
                    variables.add(terms[1]);
                    QueryProlog(ruleToQuery, terms, variables);
                }
                break;
            default:
                break;
        }
    }

    private void showExplanation(){
        System.out.println("Here might be an explanation!");
    }
}