import com.orientechnologies.orient.core.exception.OSchemaException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.gremlin.groovy.Gremlin;
import com.tinkerpop.pipes.Pipe;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import org.json.JSONObject;
import bjoern.plugins.instructionlinker.InstructionLinkerPlugin;

import orientdbimporter.CSVBatchImporter;
import octopus.OctopusMain;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegrationTestClass {
    OctopusMain main;

    private void deleteOrientdbTestDatabases() throws java.io.IOException
    {
        Pattern testDatabasePattern =
                Pattern.compile(".*__TEST__.*");
        File databases =
                new File(System.getProperty("ORIENTDB_HOME") + "/databases/");
        for (File database: databases.listFiles())
        {
            Matcher testDatabaseMatcher = testDatabasePattern.matcher(database.getPath());
            if (database.isDirectory() &&
                    testDatabaseMatcher.matches())
            {
                FileUtils.deleteDirectory(database);
            }
        }
    }

    @BeforeTest
    public void initOrientdb() throws java.lang.Exception
    {
        System.setProperty("OCTOPUS_HOME","../projects/octopus/octopus-server");
        main = new OctopusMain();
        main.startOrientdb();
    }
    @AfterTest
    public void finitOrientdb() throws java.io.IOException
    {
        main.stopOrientdb();

        deleteOrientdbTestDatabases();
    }
    @Test
    public void csvBatchImporterTest() throws java.lang.Exception
    {

        CSVBatchImporter importer = new CSVBatchImporter();

        importer.setDbName("__TEST__1");
        try {
            importer.importCSVFiles(
                    "test/src/resources/nodes.csv",
                    "test/src/resources/edges.csv");
        }
        catch (OSchemaException exception) {
            exception.printStackTrace();
            throw exception;
        }

        OrientGraph graph = new OrientGraph(
                "plocal:" + System.getProperty("ORIENTDB_HOME") + "/databases/" + "__TEST__1");

        Pipe pipe = Gremlin.compile("_().map");
        pipe.setStarts(graph.getVertices());
    }

    @Test
    public void instructionLinkerPluginTest() throws java.lang.Exception
    {
        InstructionLinkerPlugin instructionLinker = new InstructionLinkerPlugin();
        JSONObject config = new JSONObject();
        config.put("database","__TEST__1");

        instructionLinker.configure(config);
        instructionLinker.beforeExecution();
        instructionLinker.execute();
        instructionLinker.afterExecution();
    }
}
