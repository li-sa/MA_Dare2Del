package dare2del.logic;

import dare2del.gui.model.DeletionModel;
import dare2del.logic.prolog.PrologFileLoader;
import org.jpl7.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DeletionService {

    private DeletionModel deletionModel;

    public DeletionService(DeletionModel deletionModel) {
        this.deletionModel = deletionModel;
        PrologFileLoader prologFileLoader = new PrologFileLoader();
    }

    public List<DetailedFile> getDeletionCandidates() {
        List<DetailedFile> candidateFilesToDelete = new ArrayList<>();

        Term[] parameter = new Term[1];
        parameter[0] = new Variable("X");
        List<String> resultPaths = queryProlog("irrelevant", parameter, new ArrayList<>(Arrays.asList(parameter)));

        List<DetailedFile> fileList = deletionModel.getFileList();

        for (String eachResult : resultPaths) {
            Path eachPath = Paths.get(eachResult);
            DetailedFile detailedFiletoPath = fileList.stream().filter(file -> eachPath.equals(file.getPath())).findAny().orElse(null);
            if (detailedFiletoPath != null && !candidateFilesToDelete.contains(detailedFiletoPath)) {
                candidateFilesToDelete.add(detailedFiletoPath);
            }
        }

        return candidateFilesToDelete;
    }

    private List<String> queryProlog(String ruleToQuery, Term[] terms, List<Term> variables) {
        List<String> results = new ArrayList<>();

        Term term = new Compound(ruleToQuery, terms);

        try {
            Query query = new Query(term);
            System.out.println("*** QUERY: " + query.toString());
            System.out.println("*** Result: " + (query.hasSolution() ? "true" : "false"));

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
            System.out.println("No valid Query! \n");
        }

        return results;
    }
}
