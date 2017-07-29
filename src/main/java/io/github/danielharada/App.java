package io.github.danielharada;

import io.github.danielharada.dataimport.FileParser;
import io.github.danielharada.query.FieldEntry;
import io.github.danielharada.query.FieldEntryManager;
import io.github.danielharada.query.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class App {

    private FileParser fileParser;
    private ArgumentFlags argFlags;
    private Query query;
    private FieldEntryManager fieldEntryManager;

    public App(String[] args){
        this.fileParser = new FileParser();
        this.argFlags =  new ArgumentFlags(args);
        this.query = new Query(argFlags);
        this.fieldEntryManager = new FieldEntryManager();
    }

    public static void main( String[] args ){
        App app = new App(args);
        app.initializeDatastore();
        app.processArgs();
    }

    /**
     * Creates the datastore directory if it does not already exist.
     */
    public void initializeDatastore(){
        try{
            Files.createDirectories(Paths.get(Constants.dataStoreDirectory));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Selects whether to run the query or file import engine based on the command line arguments
     */
    public void processArgs(){
        // Do not allow us to try to both import and query at the same time
        if(argFlags.getDataImportFlag() && argFlags.getQueryFlag()){
            System.out.println("Cannot invoke both the query (-q) and import (-i) options at once, please choose one only");
        }

        // Run the importer if the import flag is true
        if(argFlags.getDataImportFlag()){
            importFile(argFlags.getDataImportFile());
        }

        // If we run a query, we need to have a select argument
        else if(argFlags.getQueryFlag() && !argFlags.getSelectFlag()){
            System.out.println("A select option (-s) is required when querying");
        }

        // Run the query engine if query flag is true
        else if(argFlags.getQueryFlag()){
            query();
        }

        //  If we get here we've not chosen to query or to import
        else {
            System.out.println("Please choose to either run a query (-q) or import new data (-i)");
        }
    }

    /**
     * Runs the import engine to read in a data file and store it in our datastore
     * @param sourceFile file to read in
     */
    public void importFile(String sourceFile){
        Map<String, List<String>> entriesByDate = fileParser.parseInputFile(sourceFile);
        fileParser.mergeByDate(entriesByDate);
        fileParser.writeByDate(entriesByDate);
    }

    /**
     * Runs the query engine, printing the results out to the screen
     */
    public void query(){
        List<String> rawEntryList = query.readDataStore(Constants.dataStoreDirectory);
        List<FieldEntry> fieldEntryList = fieldEntryManager.convertList(rawEntryList);
        // Only try to sort our output if the order flag is true
        if(argFlags.getOrderFlag()){
            Collections.sort(fieldEntryList);
        }
        List<String> reducedEntryList = fieldEntryManager.selectFields(fieldEntryList, argFlags.getSelectArgs());
        // Print results to screen
        reducedEntryList.forEach(entry -> System.out.println(entry.toString()));
    }
}
