import java.io.FileWriter;
import java.util.List;

public class PrologFileWriter {
    FileWriter fileWriter;

    private List<DetailedFile> folderList;
    private List<DetailedFile> fileList;

    private static String prologFile = "C:\\Users\\Lisa\\IdeaProjects\\MA_FirstSample\\src\\prolog\\test.pl";

    public PrologFileWriter(List<DetailedFile> fileList) {
        System.out.println(">> Prolog File Writer <<");
        System.out.println("INFO. Writing prolog file: " + prologFile);

        this.fileList = fileList;
        writeStatementsToPrologFile();

        System.out.println("INFO. Finished writing. \n");
    }

    private void writeStatementsToPrologFile() {

        try {
            fileWriter = new FileWriter(prologFile);

            for (DetailedFile file : fileList) {
                fileWriter.write("file(" + file.getPath() + "). \n");
                fileWriter.write("name(" + file.getPath() + ", " + file.getName() + "). \n");
                fileWriter.write("access_time(" + file.getPath() + ", " + file.getAccess_time() + "). \n");
                fileWriter.write("change_time(" + file.getPath() + ", " + file.getChange_time() + "). \n");
                fileWriter.write("in_directory(" + file.getPath() + ", " + file.getIn_directory() + "). \n");
                fileWriter.write("media_type(" + file.getPath() + ", " + file.getMedia_type() + "). \n");
                fileWriter.write("size(" + file.getPath() + ", " + file.getSize() + "). \n");
                fileWriter.write("\n");
            }

            fileWriter.close();

        } catch (Exception e) {
            // TODO: Exception Handling!
        }

    }
}
