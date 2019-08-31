package dare2del.logic;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PrologFileWriter {

    private final Logger myLogger;
    private final String clauseFile_pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../prologFiles/clauses.pl";

    public PrologFileWriter(Logger myLogger) {
        this.myLogger = myLogger;
    }

    public void write(List<DetailedFile> fileList) {
        myLogger.info("[PrologFileWriter] Start writing prolog file " + clauseFile_pathString + ".");

        Map<String, List<String>> prologStatements = collectPrologStatements(fileList);
        writeClausesToPrologFile(prologStatements);

        myLogger.info("[PrologFileWriter] Finished writing prolog file.");
    }

    private Map<String, List<String>> collectPrologStatements(List<DetailedFile> fileList) {
        Map<String, List<String>> prologStatements = new HashMap<>();

        List<String> list_file = new ArrayList<>();
        List<String> list_creation_time = new ArrayList<>();
        List<String> list_access_time = new ArrayList<>();
        List<String> list_change_time = new ArrayList<>();
        List<String> list_in_directory = new ArrayList<>();
        List<String> list_path = new ArrayList<>();

        for (DetailedFile file : fileList) {
            list_file.add("file('" + file.getPathEscaped() + "'). \n");
            list_creation_time.add("creation_time('" + file.getPathEscaped() + "'," + file.getCreation_time() + "). \n");
            list_access_time.add("access_time('" + file.getPathEscaped() + "'," + file.getAccess_time() + "). \n");
            list_change_time.add("change_time('" + file.getPathEscaped() + "'," + file.getChange_time() + "). \n");
            list_in_directory.add("in_directory('" + file.getPathEscaped() + "','" + file.getInDirectoryEscaped() + "'). \n");
            list_path.add("path('" + StringEscapeUtils.escapeJava(file.getNameEscaped()) + "','" + file.getPathEscaped() + "'). \n");
        }

        prologStatements.put("file", list_file);
        prologStatements.put("creation_time", list_creation_time);
        prologStatements.put("access_time", list_access_time);
        prologStatements.put("change_time", list_change_time);
        prologStatements.put("in_directory", list_in_directory);
        prologStatements.put("path", list_path);

        return prologStatements;
    }

    private void writeClausesToPrologFile(Map<String, List<String>> prologStatements) {
        try {
            FileWriter fileWriter = new FileWriter(clauseFile_pathString, false);

            for (List<String> mapEntry : prologStatements.values()) {
                for (String listItem : mapEntry) {
                    fileWriter.write(listItem);
                }
                fileWriter.write("\n");
            }

            fileWriter.close();

        } catch (Exception e) {
            myLogger.warning("[PrologFileWriter] Exception in writeClausesToPrologFile(): " + e.getMessage());
            throw new IllegalArgumentException();
        }
    }
}
