package octopus;

import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import orientdbimporter.CSVBatchImporter;

import java.io.File;

public class OctopusMain {

    public static void main(String[] args) throws java.lang.Exception
    {
        System.setProperty("ORIENTDB_HOME","orientdb");
        System.setProperty("orientdb.www.path","orientdb/www");
        System.setProperty("orientdb.config.file","conf/orientdb-server-config.xml");
        
        OServer server = OServerMain.create();
        server.startup();
        server.activate();

//        CSVBatchImporter importer = new CSVBatchImporter();
//
//        importer.setDbName("abc");
//        try {
//            importer.importCSVFiles("nodes.csv", "edges.csv");
//        }
//        catch (OSchemaException exception) {
//            exception.printStackTrace();
//            throw exception;
//        }
    }
}
