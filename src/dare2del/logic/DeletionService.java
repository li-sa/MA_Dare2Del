package dare2del.logic;

import dare2del.gui.model.DeletionModel;
import dare2del.logic.prolog.PrologFileLoader;
import org.jpl7.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeletionService {

    private final DeletionModel deletionModel;

    public DeletionService(DeletionModel deletionModel) {
        this.deletionModel = deletionModel;
        PrologFileLoader prologFileLoader = new PrologFileLoader(deletionModel.myLogger);
    }

    public HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> getCandidatesWithReasoning_Grouped(QueryKind queryKind) {
        Term variable_term = new Variable("X");
        Term[] terms = new Term[]{variable_term};

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> tracesMap = new HashMap<>();

        String ruleName = queryKind.toString().toLowerCase() + "_files";

        try {
            Term term_setOfClause = new Compound(ruleName, new Term[]{new Variable("F"), new Variable("Set")});
            Query query_setOfClause = new Query(term_setOfClause);

            Map<String, Term> solution_setOfClause;
            List<String> value_temp = new ArrayList<>();

            while (query_setOfClause.hasMoreSolutions()) {
                solution_setOfClause = query_setOfClause.nextSolution();

                String solution_rawValue = solution_setOfClause.get("Set").toString().replaceAll("\'", "");
                String solution_rawValue_withNeg = solution_rawValue.replaceAll("\\\\\\+\\(", "NOT_");

                Matcher matcher = Pattern.compile("[\\w]+\\((([A-Za-z0-9_äÄöÖüÜß.':\\-\\\\\\s]+)+(,)*)+\\)").matcher(solution_rawValue_withNeg);
                while (matcher.find()) {
                    value_temp.add(matcher.group());
                }
            }

            query_setOfClause.close();

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
                    List<String> temp_list = new ArrayList();
                    temp_list.add(components[0]);
                    temp_hashmap.put(deletionCounterpart, temp_list);
                } else {
                    List<String> temp_list = temp_hashmap.get(deletionCounterpart);
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
                + tracesMap.size() + " " + queryKind + " candidate files with grouped reasons.");

        return tracesMap;
    }

    public boolean deleteSelectedFiles() {
        List<DetailedFile> filesToDelete = deletionModel.getFilesSelectedForDeletion();

        deletionModel.myLogger.info("[DeletionService] deleteSelectedFiles(): " + filesToDelete.size() + " files to delete.");

        for (DetailedFile detailedFile : filesToDelete) {
            File file = new File(detailedFile.getPath().toString());

            boolean successfulDeleted = file.delete();

            if (successfulDeleted) {
                deletionModel.myLogger.info("[DeletionService] deleteSelectedFiles(): " + detailedFile.getName() + " deleted successfully.");
                return true;
            } else {
                deletionModel.myLogger.info("[DeletionService] deleteSelectedFiles(): " + detailedFile.getName() + " NOT deleted.");
                return false;
            }
        }

        return false;
    }
}
