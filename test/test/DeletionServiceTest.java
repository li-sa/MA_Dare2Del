package test;

import dare2del.gui.model.DeletionModel;
import dare2del.logic.DetailedFile;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DeletionServiceTest {

    private final Path testFiles_temp;

    public DeletionServiceTest() {
        String pathString = getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/", "");
        testFiles_temp =  Paths.get(pathString + "Testdata/temp");
    }

    //region TESTS FOR DELETION CANDIDATES
    @Test
    public void singleFile_empty() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        try {
            newfile1.getParentFile().mkdirs();

            newfile1.createNewFile();

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();

        DetailedFile deletionCandidate = (DetailedFile) deletionPairs.keySet().toArray()[0];

        Assert.assertEquals(deletionPairs.size(), 1);
        Assert.assertEquals(deletionCandidate.getPath(), newfile1.toPath());
        Assert.assertEquals(deletionPairs.get(deletionCandidate).get(deletionCandidate).get(0), "empty");
    }

    @Test
    public void singleFile_temp() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile_temp.txt");
        try {
            newfile1.getParentFile().mkdirs();

            newfile1.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();

        DetailedFile deletionCandidate = (DetailedFile) deletionPairs.keySet().toArray()[0];

        Assert.assertEquals(deletionPairs.size(), 1);
        Assert.assertEquals(deletionCandidate.getPath(), newfile1.toPath());
        Assert.assertEquals(deletionPairs.get(deletionCandidate).get(deletionCandidate).get(0), "named_with_obsolete_identifier");
    }

    @Test
    public void singleFile_old() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile_old.txt");
        try {
            newfile1.getParentFile().mkdirs();

            newfile1.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();

        DetailedFile deletionCandidate = (DetailedFile) deletionPairs.keySet().toArray()[0];

        Assert.assertEquals(deletionPairs.size(), 1);
        Assert.assertEquals(deletionCandidate.getPath(), newfile1.toPath());
        Assert.assertEquals(deletionPairs.get(deletionCandidate).get(deletionCandidate).get(0), "named_with_obsolete_identifier");
    }

    @Test
    public void singleFile_olderThanOneYear() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        try {
            newfile1.getParentFile().mkdirs();

            newfile1.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2016, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();

        DetailedFile deletionCandidate = (DetailedFile) deletionPairs.keySet().toArray()[0];

        Assert.assertEquals(deletionPairs.size(), 1);
        Assert.assertEquals(deletionCandidate.getPath(), newfile1.toPath());
        Assert.assertEquals(deletionPairs.get(deletionCandidate).get(deletionCandidate).get(0), "older_than_one_year");
    }

    @Test
    public void twoFiles_earlierCreated() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        File newfile2 = new File(testFiles_temp.toString() + "/testfile1.txt");

        try {
            newfile1.getParentFile().mkdirs();
            newfile2.getParentFile().mkdirs();

            newfile1.createNewFile();
            newfile2.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(newfile2, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

            Calendar c2 = Calendar.getInstance();
            c2.set(2019, Calendar.JANUARY, 02);
            Files.setAttribute(newfile2.toPath(), "creationTime", FileTime.fromMillis(c2.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastAccessTime", FileTime.fromMillis(c2.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastModifiedTime", FileTime.fromMillis(c2.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nmPairs = deletionModel.getNearMissPairs_grouped();

        Assert.assertEquals(deletionPairs.size(), 1);
        Assert.assertEquals(nmPairs.size(), 0);
        Assert.assertEquals(((DetailedFile) deletionPairs.keySet().toArray()[0]).getPath(), newfile1.toPath());
    }

    @Test
    public void twoFiles_minimalDifferentName() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        File newfile2 = new File(testFiles_temp.toString() + "/textfile.txt");

        try {
            newfile1.getParentFile().mkdirs();
            newfile2.getParentFile().mkdirs();

            newfile1.createNewFile();
            newfile2.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(newfile2, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

            Files.setAttribute(newfile2.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nmPairs = deletionModel.getNearMissPairs_grouped();

        Assert.assertEquals(deletionPairs.size(), 2);
        Assert.assertEquals(nmPairs.size(), 0);
    }

    @Test
    public void twoFiles_noSimilarName() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        File newfile2 = new File(testFiles_temp.toString() + "/abc.txt");

        try {
            newfile1.getParentFile().mkdirs();
            newfile2.getParentFile().mkdirs();

            newfile1.createNewFile();
            newfile2.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(newfile2, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

            Files.setAttribute(newfile2.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nmPairs = deletionModel.getNearMissPairs_grouped();

        Assert.assertEquals(deletionPairs.size(), 0);
        Assert.assertEquals(nmPairs.size(), 2);
    }

    @Test
    public void twoFiles_noSimilarContent() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        File newfile2 = new File(testFiles_temp.toString() + "/testfile1.txt");

        try {
            newfile1.getParentFile().mkdirs();
            newfile2.getParentFile().mkdirs();

            newfile1.createNewFile();
            newfile2.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(newfile2, "abc", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

            Files.setAttribute(newfile2.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nmPairs = deletionModel.getNearMissPairs_grouped();

        Assert.assertEquals(deletionPairs.size(), 0);
        Assert.assertEquals(nmPairs.size(), 1);
    }

    @Test
    public void twoFiles_notInSameDirectory() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        File newfile2 = new File(testFiles_temp.toString() + "/subdir/testfile1.txt");

        try {
            newfile1.getParentFile().mkdirs();
            newfile2.getParentFile().mkdirs();

            newfile1.createNewFile();
            newfile2.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(newfile2, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

            Files.setAttribute(newfile2.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nmPairs = deletionModel.getNearMissPairs_grouped();

        Assert.assertEquals(deletionPairs.size(), 0);
        Assert.assertEquals(nmPairs.size(), 2);
    }

    @Test
    public void twoFiles_differentTimestamp_notInSameDirectory() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        File newfile2 = new File(testFiles_temp.toString() + "/subdir/testfile1.txt");

        try {
            newfile1.getParentFile().mkdirs();
            newfile2.getParentFile().mkdirs();

            newfile1.createNewFile();
            newfile2.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(newfile2, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

            Calendar c2 = Calendar.getInstance();
            c2.set(2019, Calendar.JANUARY, 02);
            Files.setAttribute(newfile2.toPath(), "creationTime", FileTime.fromMillis(c2.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastAccessTime", FileTime.fromMillis(c2.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastModifiedTime", FileTime.fromMillis(c2.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nmPairs = deletionModel.getNearMissPairs_grouped();

        Assert.assertEquals(deletionPairs.size(), 0);
        Assert.assertEquals(nmPairs.size(), 1);
    }

    @Test
    public void twoFiles_differentMediatype() {
        cleanTemp();

        File newfile1 = new File(testFiles_temp.toString() + "/testfile.txt");
        File newfile2 = new File(testFiles_temp.toString() + "/testfile.png");

        try {
            newfile1.getParentFile().mkdirs();
            newfile2.getParentFile().mkdirs();

            newfile1.createNewFile();
            newfile2.createNewFile();

            FileUtils.writeStringToFile(newfile1, "Hier steht Text!", StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(newfile2, "Hier steht Text!", StandardCharsets.UTF_8, true);

            Calendar c1 = Calendar.getInstance();
            c1.set(2019, Calendar.JANUARY, 01);
            Files.setAttribute(newfile1.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile1.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

            Files.setAttribute(newfile2.toPath(), "creationTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastAccessTime", FileTime.fromMillis(c1.getTimeInMillis()));
            Files.setAttribute(newfile2.toPath(), "lastModifiedTime", FileTime.fromMillis(c1.getTimeInMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        DeletionModel deletionModel = setupDeletionModel();

        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> deletionPairs = deletionModel.getDeletionPairs_grouped();
        HashMap<DetailedFile, HashMap<DetailedFile, List<String>>> nmPairs = deletionModel.getNearMissPairs_grouped();

        Assert.assertEquals(deletionPairs.size(), 0);
        Assert.assertEquals(nmPairs.size(), 2);
    }
    //endregion

    //region COMMON
    private DeletionModel setupDeletionModel() {
        DeletionModel deletionModel = new DeletionModel();
        deletionModel.setRootPath(testFiles_temp);
        deletionModel.initProlog(testFiles_temp);
        deletionModel.initDeletionModel();
        return deletionModel;
    }

    private void cleanTemp() {
        try {
            FileUtils.cleanDirectory(testFiles_temp.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion
}