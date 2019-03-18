package dare2del.logic;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrologFileWriter {
    private FileWriter fileWriter;

    //    private List<DetailedFile> folderList;
    private final List<DetailedFile> fileList;

    private final String clauseFile_pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../clauses.pl";

    private Map<String, List<String>> prologStatements;

    public PrologFileWriter(List<DetailedFile> fileList) {
        System.out.println(">> Prolog File Writer <<");
        System.out.println(getClass().getProtectionDomain().getCodeSource().getLocation());
        System.out.println("INFO. Writing prolog file: " + clauseFile_pathString);

        this.fileList = fileList;
        collectPrologStatements();
        writeClausesToPrologFile();

        System.out.println("INFO. Finished writing. \n");
    }

    private void collectPrologStatements() {
        prologStatements = new HashMap<>();

        List<String> list_file = new ArrayList<>();
        List<String> list_creation_time = new ArrayList<>();
        List<String> list_access_time = new ArrayList<>();
        List<String> list_change_time = new ArrayList<>();
        List<String> list_in_directory = new ArrayList<>();
        List<String> list_path = new ArrayList<>();

        for (DetailedFile file : fileList) {
            String filePath = file.getPath().toString().replace("\\", "\\\\");

            list_file.add("file('" + filePath + "'). \n");
            list_creation_time.add("creation_time('" + filePath + "'," + file.getCreation_time() + "). \n");
            list_access_time.add("access_time('" + filePath + "'," + file.getAccess_time() + "). \n");
            list_change_time.add("change_time('" + filePath + "'," + file.getChange_time() + "). \n");
            list_in_directory.add("in_directory('" + filePath + "','" + file.getIn_directory().toString().replace("\\", "\\\\") + "'). \n");
            list_path.add("path('" + file.getName() + "','" + filePath + "'). \n");
        }

        prologStatements.put("file", list_file);
        prologStatements.put("creation_time", list_creation_time);
        prologStatements.put("access_time", list_access_time);
        prologStatements.put("change_time", list_change_time);
        prologStatements.put("in_directory", list_in_directory);
        prologStatements.put("path", list_path);
    }

    private void writeClausesToPrologFile() {
        try {
            fileWriter = new FileWriter(clauseFile_pathString);

            for (List<String> mapEntry : prologStatements.values()) {
                for (String listItem : mapEntry) {
                    fileWriter.write(listItem);
                }
                fileWriter.write("\n");
            }

            fileWriter.close();

        } catch (Exception e) {
            throw new IllegalArgumentException("EXCEPTION in writeClausesToPrologFile(): " + e.getMessage());
        }
    }
}
