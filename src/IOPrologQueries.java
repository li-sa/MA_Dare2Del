import org.jpl7.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IOPrologQueries {

    private boolean readingInput = true;

    public IOPrologQueries(List<DetailedFile> fileList) {
        System.out.println(">> PROLOG IO <<");
        init();
    }

    private void init() {
        List<String> prologFilesToLoad = new ArrayList<>();
        String CLAUSE_FILE = "src/prolog/clauses.pl";
        prologFilesToLoad.add(CLAUSE_FILE);
        String RULE_FILE = "src/prolog/irrelevanceTheory.pl";
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

        readConsoleInput();
//        readConsoleInput_firstApproach();
    }

    private void readConsoleInput() {
        while (readingInput) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n -> What do you want to do?");
            System.out.println("[0] Find all irrelevant files.");
            System.out.println("[1] Ask if specific file is irrelevant.");

            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    Term[] parameter = new Term[1];
                    parameter[0] = new Variable("X");
                    queryProlog("irrelevant", parameter, new ArrayList<>(Arrays.asList(parameter)));
                    break;
                case "1":
                    System.out.println("\n Enter file to ask for.");
                    String inputFile = scanner.nextLine();
                    Term[] parameter_inputFile = new Term[1];
                    parameter_inputFile[0] = new Variable(inputFile);
                    queryProlog("irrelevant", parameter_inputFile, new ArrayList<>());
                    break;
                default:
                    readConsoleInput();
                    break;
            }
        }
    }

    private void readConsoleInput_firstApproach() {
        while (readingInput) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n -> Enter a prolog query:");
            String input = scanner.nextLine();

            if ("exit".equals(input.toLowerCase())) {
                readingInput = false;
            } else {
                parseToPrologQuery(input);
            }
        }
    }

    private void parseToPrologQuery(String input) {
        String[] inputParameters = input.split("\\(|,|\\).|\\)");

        Term[] newTerms = new Term[inputParameters.length - 1];
        List<Term> listOfVariables = new ArrayList<>();
        for (int i = 1; i < inputParameters.length; i++) {
            if (inputParameters[i].trim().startsWith("_")) {
                newTerms[i - 1] = new Variable(inputParameters[i].split("_")[1].toUpperCase());
                listOfVariables.add(newTerms[i - 1]);
            } else {
                newTerms[i - 1] = new Atom(inputParameters[i].trim());
            }
        }

        queryProlog(inputParameters[0], newTerms, listOfVariables);

        // Just for Testing: Generating and output of trace
/*        HashMap<List<String>, List<String>> traces;
        traces = getQueryTrace(inputParameters[0] , newTerms, listOfVariables);

        for (int i = 0; i < traces.size(); i++) {
            String key_temp = traces.keySet().toArray()[i].toString();
            List<String> value_temp = traces.get(traces.keySet().toArray()[i]);
            System.out.println("["+ i + "] " + key_temp + ": " + value_temp);
        }*/
    }

    private void queryProlog(String ruleToQuery, Term[] terms, List<Term> variables) {
        Term term = new Compound(ruleToQuery, terms);

        try {
            Query query = new Query(term);
            System.out.println("*** QUERY: " + query.toString());
            System.out.println("*** Result: " + (query.hasSolution() ? "true" : "false"));

            Map<String, Term> solution;
            while (query.hasMoreSolutions() && variables.size() > 0) {
                solution = query.nextSolution();
                StringBuilder stringBuilder = new StringBuilder();
                for (Term variable : variables) {
                    stringBuilder.append(variable.toString() + " = " + solution.get(variable.toString()) + "\t");
                }
                System.out.println(stringBuilder);
            }

            askForUserDecision(ruleToQuery, terms, variables);
//            askForUserDecision_firstApproach(ruleToQuery, terms, variables);

        } catch (PrologException prolog_exception) {
            System.out.println("No valid Query! \n");
        }
    }

    private HashMap<List<String>, List<String>> getQueryTrace(String ruleToQuery, Term[] terms, List<Term> variables) {
        HashMap<List<String>, List<String>> tracesMap = new HashMap<>();
        Term term_fromUserInput = new Compound(ruleToQuery, terms);

        try {
            Term term_setOfClause = new Compound("set_of_clause", new Term[]{term_fromUserInput, new Variable("Set")});

            Query query_setOfClause = new Query(term_setOfClause);
            System.out.println("*** QUERY: " + query_setOfClause.toString());
            System.out.println("*** Result: " + (query_setOfClause.hasSolution() ? "true" : "false"));

            Map<String, Term> solution_setOfClause;
            while (query_setOfClause.hasMoreSolutions()) {
                solution_setOfClause = query_setOfClause.nextSolution();

                String solution_rawValue = solution_setOfClause.get("Set").toString().replaceAll("\'", "");
                String solution_rawValue_withNeg = solution_rawValue.replaceAll("\\\\\\+\\(", "not_");

                List<String> value_temp = new ArrayList<>();
                Matcher matcher = Pattern.compile("[\\w]+\\((([\\w\\s.':\\-\\\\]+)+(,)*)+\\)").matcher(solution_rawValue_withNeg);
                while (matcher.find()) {

                    value_temp.add(matcher.group());
                }

                // *** Sort entries in value_temp according to different keys
                HashMap<List<String>, List<String>> tempMapToSortSolution = new HashMap<>();

                for (String each : value_temp) {
                    String[] fileNames = each.split("\\(|, |\\)");
                    List<String> keyToProof = new ArrayList<>();
                    if (fileNames.length == 2) {
                        keyToProof = new ArrayList<>(Arrays.asList(fileNames[1]));
                    } else if (fileNames.length > 2) {
                        keyToProof = new ArrayList<>(Arrays.asList(fileNames[1], fileNames[2]));
                    }

                    if (tempMapToSortSolution.containsKey(keyToProof)) {
                        tempMapToSortSolution.get(keyToProof).add(each);
                    } else {
                        List<String> newValueList = new ArrayList<>(Collections.singletonList(each));
                        tempMapToSortSolution.put(keyToProof, newValueList);
                    }
                }

                for (List<String> eachKeyInMap : tempMapToSortSolution.keySet()) {
                    tracesMap.put(eachKeyInMap, tempMapToSortSolution.get(eachKeyInMap));
                }
                // ***
            }

            if (ruleToQuery.equals("irrelevant")) {
                showExplanation_simpleApproach(tracesMap);
            } else if (ruleToQuery.equals("relevant")) {
                // TODO: showExplanation of relevant examples!
                showExplanation_simpleApproach(tracesMap);
            }

            readConsoleInput();
//            askForUserDecision_firstApproach(ruleToQuery, terms, variables);

        } catch (PrologException prolog_exception) {
            System.out.println("No valid Query! \n");
        }

        return tracesMap;
    }

    private void askForUserDecision(String ruleToQuery, Term[] terms, List<Term> variables) {
        int termCounter = terms.length;

        Scanner scanner = new Scanner(System.in);
        System.out.println("[0] Back to main menu.");
        System.out.println("[1] Show explanation.");
        System.out.println("[2] Show contrary example explanation.");

        String input = scanner.nextLine();

        switch (input) {
            case "0":
                readConsoleInput();
                break;
            case "1":
                HashMap<List<String>, List<String>> traces = getQueryTrace(ruleToQuery, terms, variables);
                showExplanation_simpleApproach(traces);
                break;
            case "2":
                HashMap<List<String>, List<String>> contraryExamples_traces = getQueryTrace("relevant", terms, variables);
                showExplanation_simpleApproach(contraryExamples_traces);
                break;
            default:
                break;
        }
    }

    private void askForUserDecision_firstApproach(String ruleToQuery, Term[] terms, List<Term> variables) {
        int termCounter = terms.length;

        Scanner scanner = new Scanner(System.in);
        System.out.println("[0] New query!");
        System.out.println("[1] Show explanation.");
        if (termCounter == 2) {
            System.out.println("[2] Generalization: Show different example with first argument as variable.");
            System.out.println("[3] Generalization: Show different example with second argument as variable.");
        } else if (termCounter == 1) {
            System.out.println("[2] Show different example with argument = XXX.");
        }

        String input = scanner.nextLine();

        switch (input) {
            case "0":
                break;
            case "1":
                HashMap<List<String>, List<String>> traces = getQueryTrace(ruleToQuery, terms, variables);
                showExplanation_simpleApproach(traces);
                break;
            case "2":
                if (!variables.contains(terms[0])) {
                    terms[0] = new Variable("XXX");
                    variables.add(terms[0]);
                }
                queryProlog(ruleToQuery, terms, variables);
                break;
            case "3":
                if (termCounter == 2 && !variables.contains(terms[1])) {
                    terms[1] = new Variable("YYY");
                    variables.add(terms[1]);
                    queryProlog(ruleToQuery, terms, variables);
                }
                break;
            default:
                break;
        }
    }

    private void showExplanation_simpleApproach(HashMap<List<String>, List<String>> traces) {
        HashMap<String, StringBuilder> explanations = new HashMap<>();

        for (List<String> candidatePair : traces.keySet()) {
            String candidateToDelete = candidatePair.get(0);
            String candidateToReplace = candidatePair.size() == 2 ? candidatePair.get(1) : candidatePair.get(0);
            List<String> reasonList = traces.get(candidatePair);

            StringBuilder explanationBuilder;
            if (explanations.containsKey(candidateToDelete)) {
                explanationBuilder = explanations.get(candidateToDelete);

            } else {
                explanationBuilder = new StringBuilder();
                explanationBuilder.append("File *" + candidateToDelete + "* may be deleted because: \n");
            }

            explanationBuilder.append(">> file *" + candidateToReplace + "* is ");

            for (int i = 0; i < reasonList.size(); i++) {
                String reason = reasonList.get(i);
                String[] reasonComponents = reason.split("\\(|, |\\)");

                if (reasonList.size() == 1) {
                    explanationBuilder.append(reasonComponents[0].replaceAll("_", " "));
                } else if (i < reasonList.size() - 2) {
                    explanationBuilder.append(reasonComponents[0].replaceAll("_", " ") + ", ");
                } else if (i < reasonList.size() - 1) {
                    explanationBuilder.append(reasonComponents[0].replaceAll("_", " "));
                } else if (i == reasonList.size() - 1) {
                    explanationBuilder.append(" and " + reasonComponents[0].replaceAll("_", " "));
                }
            }

            if (!candidateToDelete.equals(candidateToReplace)) { //Only if deletion suggestion according to another file
                explanationBuilder.append(" compared to *" + candidateToDelete + "*.\n");
            } else {
                explanationBuilder.append(".\n");
            }

            explanations.put(candidateToDelete, explanationBuilder);
        }

        for (StringBuilder explanation : explanations.values()) {
            System.out.println(explanation);
        }
    }

    private String generateReasonExplanation(String comparedFile, String reason) {
        String verbalizedReason;

        String[] reasonComponents = reason.split("\\(|, |\\)");
        verbalizedReason = ">> " + comparedFile + reasonComponents[2] + " is " + reasonComponents[0].replaceAll("_", " ") + ".";

        return verbalizedReason;
    }
}