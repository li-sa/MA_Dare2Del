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

    public HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> getCandidatesWithReasoning(QueryKind queryKind) {
        Term variableTerm = new Variable("X");
        Term[] terms = new Term[]{variableTerm};

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> tracesMap = new HashMap<>();

        String ruleName = queryKind.toString().toLowerCase() + "_files";

        try {
            Term termSetOfClause = new Compound(ruleName, new Term[]{new Variable("F"), new Variable("Set")});
            Query querySetOfClause = new Query(termSetOfClause);

            Map<String, Term> solutionSetOfClause;
            List<String> valueTemp = new ArrayList<>();

            while (querySetOfClause.hasMoreSolutions()) {
                solutionSetOfClause = querySetOfClause.nextSolution();

                String solutionRawValue = solutionSetOfClause.get("Set").toString().replaceAll("\'", "");
                String solutionRawValueWithNeg = solutionRawValue.replaceAll("\\\\\\+\\(", "NOT_");

                Matcher matcher = Pattern.compile("[\\w]+\\((([A-Za-z0-9_äÄöÖüÜß.':\\-\\\\\\s]+)+(,)*)+\\)").matcher(solutionRawValueWithNeg);
                while (matcher.find()) {
                    valueTemp.add(matcher.group());
                }
            }

            querySetOfClause.close();

            for (String each : valueTemp) {
                HashMap<DetailedFile, List<String>> hashmapTemp = new HashMap<>();

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
                    hashmapTemp = tracesMap.get(deletionCandidate);
                }

                //Add reason to specific inner hashmap
                if (!hashmapTemp.containsKey(deletionCounterpart)) {
                    List<String> temp_list = new ArrayList();
                    temp_list.add(components[0]);
                    hashmapTemp.put(deletionCounterpart, temp_list);
                } else {
                    List<String> temp_list = hashmapTemp.get(deletionCounterpart);
                    temp_list.add(components[0]);
                    hashmapTemp.put(deletionCounterpart, temp_list);
                }

                tracesMap.put(deletionCandidate, hashmapTemp);
            }
        } catch (PrologException prologException) {
            deletionModel.myLogger.warning("[DeletionService] Exception in getCandidatesWithReasoning(): "
                    + prologException.getMessage());
        }

        deletionModel.myLogger.info("[DeletionService] getCandidatesWithReasoning() found "
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
