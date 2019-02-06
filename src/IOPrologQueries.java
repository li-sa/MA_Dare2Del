import org.jpl7.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        HashMap<List<String>, List<String>> traces;
        traces = getQueryTrace(inputParameters[0] , newTerms, listOfVariables);

        for (int i = 0; i < traces.size(); i++) {
            String key_temp = traces.keySet().toArray()[i].toString();
            List<String> value_temp = traces.get(traces.keySet().toArray()[i]);
            System.out.println("["+ i + "] " + key_temp + ": " + value_temp);
        }
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

    private HashMap<List<String>, List<String>> getQueryTrace(String ruleToQuery, Term[] terms, List<Term> variables) {
        HashMap<List<String>, List<String>> traces = new HashMap<>();
        Term term = new Compound(ruleToQuery, terms);

        try {
            Term term_setof = new Compound("set_of_clause", new Term[]{term, new Variable("Set")});

            Query query = new Query(term_setof);
            System.out.println("*** QUERY: " + query.toString());
            System.out.println("*** Result: " + (query.hasSolution() ? "true" : "false"));

            Map<String, Term> solution;
            while (query.hasMoreSolutions()){
                solution = query.nextSolution();

                String term0;
                String term1;

                if (variables.contains(terms[0])) {
                    term0 = solution.get(terms[0].toString()).toString().replaceAll("\'", "");
                } else {
                    term0 = terms[0].toString();
                }

                if (variables.contains(terms[1])) {
                    term1 = solution.get(terms[1].toString()).toString().replaceAll("\'", "");
                } else {
                    term1 = terms[1].toString();
                }

                List<String> key_temp = Arrays.asList(term0, term1);
                String value_raw_temp = solution.get("Set").toString().replaceAll("\'", "");

                List<String> value_temp = new ArrayList<>();
                Matcher matcher  = Pattern.compile("[\\w]+\\((([\\w\\s.'-]+)+(,)*)+\\)").matcher(value_raw_temp);
                while (matcher.find()) {
                    value_temp.add(matcher.group());
                }

                traces.put(key_temp, value_temp);
//                askForUserDecision(ruleToQuery, terms, variables, traces);
            }
            showExplanation_simpleApproach(traces);

        } catch (PrologException prolog_exception) {
            System.out.println("No valid Query! \n");
        }

        return traces;
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

    private void askForUserDecision(String ruleToQuery, Term[] terms, List<Term> variables, HashMap<List<String>, List<String>> traces) {
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
                showExplanation_simpleApproach(traces);
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

    private void showExplanation_simpleApproach(HashMap<List<String>, List<String>> traces){
        HashMap<String, StringBuilder> explanations = new HashMap<>();

        for (List<String> candidatePair : traces.keySet()) {
            String candidateToDelete = candidatePair.get(0);
            List<String> reasonList = traces.get(candidatePair);

            StringBuilder explanationBuilder;
            if (explanations.containsKey(candidateToDelete)) {
                explanationBuilder = explanations.get(candidateToDelete);

            } else {
                explanationBuilder = new StringBuilder();
                explanationBuilder.append("File *" + candidateToDelete + "* may be deleted because: \n");
            }

            explanationBuilder.append(">> file *" + candidatePair.get(1) + "* is ");


            for (int i = 0; i < reasonList.size(); i++) {
                String reason = reasonList.get(i);
                String[] reasonComponents = reason.split("\\(|, |\\)");

                if (i < reasonList.size() - 2) {
                    explanationBuilder.append(reasonComponents[0].replaceAll("_", " ") + ", ");
                } else if (i < reasonList.size() - 1) {
                    explanationBuilder.append(reasonComponents[0].replaceAll("_", " "));
                } else if (i == reasonList.size() - 1) {
                    explanationBuilder.append(" and " + reasonComponents[0].replaceAll("_", " "));
                }
            }

            explanationBuilder.append(" compared to *" + candidateToDelete + "*.\n");

            explanations.put(candidateToDelete, explanationBuilder);
        }

        for (StringBuilder explanation : explanations.values()) {
            System.out.println(explanation);
        }


/*        StringBuilder explanationBuilder = new StringBuilder();

        for (List<String> deletionCandidate : traces.keySet()) {
            explanationBuilder.append("File *" + deletionCandidate.get(0) + "* may be deleted because: \n");

            for (String reason : traces.get(deletionCandidate)) {
                explanationBuilder.append(generateReasonExplanation(deletionCandidate.get(1), reason)).append("\n");
            }
        }

        System.out.println(explanationBuilder);*/

    }

    private String generateReasonExplanation(String comparedFile, String reason) {
        String verbalizedReason = "";

        String[] reasonComponents = reason.split("\\(|, |\\)");
        verbalizedReason = ">> " + comparedFile + reasonComponents[2] + " is " + reasonComponents[0].replaceAll("_", " ") + ".";

        return verbalizedReason;
    }
}