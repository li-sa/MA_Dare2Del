package dare2del.logic;

import dare2del.gui.model.DeletionModel;
import dare2del.logic.prolog.PrologFileLoader;
import org.jpl7.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeletionService {

    private final DeletionModel deletionModel;

    public DeletionService(DeletionModel deletionModel) {
        this.deletionModel = deletionModel;
        PrologFileLoader prologFileLoader = new PrologFileLoader(deletionModel.myLogger);
    }

    public List<DetailedFile> getCandidates(QueryKind queryKind) {
        List<DetailedFile> candidateFilesToDelete = new ArrayList<>();

        Term[] parameter = new Term[]{new Variable("X")};
        List<String> resultPaths = queryProlog(queryKind.toString().toLowerCase(), parameter, new ArrayList<>(Arrays.asList(parameter)));

        List<DetailedFile> fileList = deletionModel.getFileList();

        for (String eachResult : resultPaths) {
            Path eachResultPath = Paths.get(eachResult);
            DetailedFile detailedFileAccordingToPath = fileList.stream()
                    .filter(file -> eachResultPath.equals(file.getPath())).findAny().orElse(null);
            if (detailedFileAccordingToPath != null && !candidateFilesToDelete.contains(detailedFileAccordingToPath)) {
                candidateFilesToDelete.add(detailedFileAccordingToPath);
            }
        }

        deletionModel.myLogger.info("[DeletionService] getCandidates() found " + candidateFilesToDelete.size() + " candidate files to delete.");

        return candidateFilesToDelete;
    }

    public HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> getCandidatesWithReasoning_Grouped(QueryKind queryKind) {
        Term variable_term = new Variable("X");
        Term[] terms = new Term[]{variable_term};

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> tracesMap = new HashMap<>();

        try {
            Term term_ruleToQuery = new Compound(queryKind.toString().toLowerCase(), terms);
            Term term_setOfClause = new Compound("set_of_clause", new Term[]{term_ruleToQuery, new Variable("Set")});
            Query query_setOfClause = new Query(term_setOfClause);

            Map<String, Term> solution_setOfClause;
            List<String> value_temp = new ArrayList<>();

            while (query_setOfClause.hasMoreSolutions()) {
                solution_setOfClause = query_setOfClause.nextSolution();

                String solution_rawValue = solution_setOfClause.get("Set").toString().replaceAll("\'", "");
                String solution_rawValue_withNeg = solution_rawValue.replaceAll("\\\\\\+\\(", "NOT_");

                Matcher matcher = Pattern.compile("[\\w]+\\((([\\w\\s.':\\-\\\\]+)+(,)*)+\\)").matcher(solution_rawValue_withNeg);
                while (matcher.find()) {
                    value_temp.add(matcher.group());
                }
            }

            for (String each : value_temp) {
                HashMap<DetailedFile, List<String>> temp_hashmap = new HashMap<>();

                String[] components = each.split("\\(|, |\\)");
                int componentsCounter = components.length;

                DetailedFile deletionCandidate = deletionModel.getFileList().stream().filter(x -> x.getPath()
                        .equals(Paths.get(components[1]))).findAny().orElse(null);
                DetailedFile deletionCounterpart = deletionCandidate;
                if (componentsCounter > 2) {
                    deletionCounterpart = deletionModel.getFileList().stream().filter(x -> x.getPath()
                            .equals(Paths.get(components[2]))).findAny().orElse(deletionCandidate);
                }

                //Is deletion candidate already in traceMap? Then take the inner hashmap.
                if (tracesMap.containsKey(deletionCandidate)) {
                    temp_hashmap = tracesMap.get(deletionCandidate);
                }

                //Add reason to specific inner hashmap
                if (!temp_hashmap.containsKey(deletionCounterpart)) {
                    List temp_list = new ArrayList();
                    temp_list.add(components[0]);
                    temp_hashmap.put(deletionCounterpart, temp_list);
                } else {
                    List temp_list = temp_hashmap.get(deletionCounterpart);
                    temp_list.add(components[0]);
                    temp_hashmap.put(deletionCounterpart, temp_list);
                }

                tracesMap.put(deletionCandidate, temp_hashmap);
            }


        } catch (PrologException prolog_exception) {
            deletionModel.myLogger.warning("[DeletionService] Exception in getCandidatesWithReasoning_Grouped(): "
                    + prolog_exception.getMessage());
        }

        deletionModel.myLogger.info("[DeletionService] getCandidatesWithReasoning_Grouped() found "
                + tracesMap.size() + " candidate files with grouped reasons.");

        return tracesMap;
    }

    public HashMap<List<String>, List<String>> getCandidatesWithReasoning(QueryKind queryKind) {
        Term variable_term = new Variable("X");
        Term[] terms = new Term[]{variable_term};

        HashMap<List<String>, List<String>> tracesMap = new HashMap<>();

        try {
            Term term_ruleToQuery = new Compound(queryKind.toString().toLowerCase(), terms);
            Term term_setOfClause = new Compound("set_of_clause", new Term[]{term_ruleToQuery, new Variable("Set")});
            Query query_setOfClause = new Query(term_setOfClause);

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
                        keyToProof = new ArrayList<>(Collections.singletonList(fileNames[1]));
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

        } catch (PrologException prolog_exception) {
            deletionModel.myLogger.warning("[DeletionService] Exception in getCandidatesWithReasoning(): "
                    + prolog_exception.getMessage());
        }

        deletionModel.myLogger.info("[DeletionService] getCandidatesWithReasoning() found "
                + tracesMap.size() + " candidate files with reasons.");

        return tracesMap;
    }

    private List<String> queryProlog(String ruleToQuery, Term[] terms, List<Term> variables) {
        List<String> results = new ArrayList<>();

        Term term = new Compound(ruleToQuery, terms);

        try {
            Query query = new Query(term);

            Map<String, Term> solution;
            while (query.hasMoreSolutions() && variables.size() > 0) {
                solution = query.nextSolution();
                for (Term variable : variables) {
                    Term solutionTerm = solution.get(variable.toString());

                    String filePathName = String.valueOf(solutionTerm).substring(1, solutionTerm.toString().length() - 1);
                    results.add(filePathName);
                }
            }

        } catch (PrologException prolog_exception) {
            deletionModel.myLogger.warning("[DeletionService] Exception in queryProlog(): "
                    + prolog_exception.getMessage());
            System.out.println("No valid Query! \n");
        }

        return results;
    }
}
