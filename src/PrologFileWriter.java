import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrologFileWriter {
    FileWriter fileWriter;

    private List<DetailedFile> folderList;
    private List<DetailedFile> fileList;

    private static String prologFile = "C:\\Users\\Lisa\\IdeaProjects\\MA_FirstSample\\src\\prolog\\test.pl";

    private Map<String, List<String>> prologStatements;

    public PrologFileWriter(List<DetailedFile> fileList) {
        System.out.println(">> Prolog File Writer <<");
        System.out.println("INFO. Writing prolog file: " + prologFile);

        this.fileList = fileList;
        collectPrologStatements();
        writeStatementsToPrologFile();

        System.out.println("INFO. Finished writing. \n");
    }

    private void collectPrologStatements() {
        prologStatements = new HashMap<String, List<String>>();

        List<String> list_file = new ArrayList<>();
        List<String> list_name = new ArrayList<>();
        List<String> list_creation_time = new ArrayList<>();
        List<String> list_access_time = new ArrayList<>();
        List<String> list_change_time = new ArrayList<>();
        List<String> list_in_directory = new ArrayList<>();
        List<String> list_media_type = new ArrayList<>();
        List<String> list_size = new ArrayList<>();

        // Test File
        list_file.add("file(foo). \n");

        for (DetailedFile file : fileList) {
            /*list_file.add("file(" + file.getPath() + "). \n");
            list_name.add("name(" + file.getPath() + "," + file.getName() + "). \n");
            list_creation_time.add("creation_time(" + file.getPath() + "," + file.getCreation_time() + "). \n");
            list_access_time.add("access_time(" + file.getPath() + "," + file.getAccess_time() + "). \n");
            list_change_time.add("change_time(" + file.getPath() + "," + file.getChange_time() + "). \n");
            list_in_directory.add("in_directory(" + file.getPath() + "," + file.getIn_directory() + "). \n");
            list_media_type.add("media_type(" + file.getPath() + "," + file.getMedia_type() + "). \n");
            list_size.add("size(" + file.getPath() + "," + file.getSize() + "). \n");*/

            list_file.add("file('" + file.getName() + "'). \n");
            list_creation_time.add("creation_time('" + file.getName() + "','" + file.getCreation_time() + "'). \n");
            list_access_time.add("access_time('" + file.getName() + "','" + file.getAccess_time() + "'). \n");
            list_change_time.add("change_time('" + file.getName() + "','" + file.getChange_time() + "'). \n");
            list_in_directory.add("in_directory('" + file.getName() + "','" + file.getIn_directory().toString().replace("\\", "\\\\") + "'). \n");
            list_media_type.add("media_type('" + file.getName() + "','" + file.getMedia_type() + "'). \n");
            list_size.add("size('" + file.getName() + "','" + file.getSize() + "'). \n");
        }

        prologStatements.put("file", list_file);
        //prologStatements.put("name", list_name);
        prologStatements.put("creation_time", list_creation_time);
        prologStatements.put("access_time", list_access_time);
        prologStatements.put("change_time", list_change_time);
        prologStatements.put("in_directory", list_in_directory);
        prologStatements.put("media_type", list_media_type);
        prologStatements.put("size", list_size);
    }

    private void writeStatementsToPrologFile() {
        try {
            fileWriter = new FileWriter(prologFile);

            for (List<String> mapEntry : prologStatements.values()) {
                for (String listItem : mapEntry) {
                    fileWriter.write(listItem);
                }
                fileWriter.write("\n");
            }

            fileWriter.close();

        } catch (Exception e) {
            // TODO: Exception Handling!
        }
    }
}
