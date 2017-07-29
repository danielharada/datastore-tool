package io.github.danielharada.dataimport;

import io.github.danielharada.Constants;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: need to setup log4j

/**
 *  Reads in data from files and writes to our datastore
 */
public class FileParser {

    /**
     *  Reads in a file, validate that each row entry matches our expected formats
     *  and creates a map between the date field for each row and its full entry.
     *  Any rows that fail the validation will be dropped.
     */
    public Map<String, List<String>> parseInputFile(String dataImportFile){
        Map<String, List<String>> entriesByDate = new HashMap<String, List<String>>();

        try(Stream<String> fileStream = Files.lines(Paths.get(dataImportFile))){
            entriesByDate = fileStream.skip(1)  // skip the header row
                    .filter(rowEntry -> validateEntryFormat(rowEntry))  // filter out entries that don't meet our expected format
                    .collect(Collectors.groupingBy(rowEntry -> parseDate(rowEntry)));  // Group by entry's date field
        } catch(NoSuchFileException e){
            System.out.printf("The file %s does not exist\n", dataImportFile);
        } catch (IOException e){
            // TODO:  apply appropriate logging
            e.printStackTrace();
        }

        return entriesByDate;
    }

    /**
     * Validates that the data we're reading in matches our expected data formats.
     * @param rowEntry Input file line to validate
     * @return  Boolean indicating whether data is valid or not
     */
    public boolean validateEntryFormat(String rowEntry){
        boolean validRow = false;
        String[] fields = rowEntry.split(Constants.escapedInputDelimiter);
        // Make sure we have 6 fields before trying to access them all
        if(fields.length == 6) {
            validRow = fields[0].length() <= Constants.stbMaxLength
                    && fields[1].length() <= Constants.titleMaxLength
                    && fields[2].length() <= Constants.providerMaxLength
                    && fields[3].length() <= Constants.dateFormat.length()
                    // Ensure Date field has dashes in correct spots
                    && fields[3].indexOf("-") == 4
                    && fields[3].lastIndexOf("-") == 7
                    // Ensure Currency has 2 places after decimal
                    && fields[4].indexOf(".") == fields[4].length() - 3
                    // Ensure view_time has a two digit minute, and has at most a two digit hour
                    && fields[5].indexOf(":") == fields[5].length() - 3
                    && fields[5].length() <= 5;
        }
        if (!validRow){
            // TODO:  implement logging so we don't drop data silently
        }

        return validRow;
    }

    /**
     *  Pulls out the date field from the input row
     * @param rowEntry
     * @return
     */
    public String parseDate(String rowEntry){
        return rowEntry.split(Constants.escapedInputDelimiter)[3];
    }

    /**
     *  Iterates through the data we have imported by date, for each date we attempt to pull in data from
     *  the datastore and merge with the imported data.  Any existing data with duplicate keys to the
     *  imported data will be dropped.
     * @param entriesByDate a map between dates and a list of entries for that date
     */
    public void mergeByDate(Map<String, List<String>> entriesByDate){
        // For each date, pull any data that exists for that date already from /var/datastore/{date}
        entriesByDate.forEach((date,entryList) -> {
            String dataStoreFile = Constants.dataStoreDirectory.concat("/").concat(date);
            // If our data store file already exists, we need to merge its content with our newly imported data
            if(Files.exists(Paths.get(dataStoreFile))){
                mergeEntries(entryList, dataStoreFile);
            }
            //persistToDisk(entryList, dataStoreFile);
        });
    }

    /**
     * For a single date value, read in that date's datastore file and merge it with our imported data.
     * Any existing data with duplicate keys to the imported data will be dropped.
     * @param entryList  imported data
     * @param dataStoreFile  string path to the datastore file for our date
     */
    public void mergeEntries(List<String> entryList, String dataStoreFile){
        // Create a list of keys from our data import
        List<String> importKeyList = createKeyList(entryList);
        try(Stream<String> dataStoreStream = Files.lines(Paths.get(dataStoreFile))){
            // If the file exists, we need to bring in any entries we aren't updating
            dataStoreStream.filter(storeEntry -> !importKeyList.contains(parseKey(storeEntry)))
                    .forEach(storeEntry -> entryList.add(storeEntry));
        } catch(IOException e){
            //TODO:  expand logging
            e.printStackTrace();
        }
    }

    /**
     * Writes our merged data into files by date
     * @param entriesByDate map between dates and list of entries to write
     */
    public void writeByDate(Map<String, List<String>> entriesByDate){
        entriesByDate.forEach((date, entryList) -> {
            String dataStoreFile = Constants.dataStoreDirectory.concat("/").concat(date);
            try {
                Files.write(Paths.get(dataStoreFile), entryList, StandardCharsets.UTF_8);
            } catch(Exception e){
                //TODO:  expand logging
                e.printStackTrace();
            }
        });
    }

    /**
     * Create a list of keys from our list of entries
     * @param entryList
     * @return
     */
    public List<String> createKeyList(List<String> entryList){
        List<String> keyList = new ArrayList<String>();
        entryList.forEach(entry -> {
            keyList.add(parseKey(entry));
        });

        return keyList;
    }

    /**
     *  Generates the key string to check for each entry.
     *  Entries are to be unique on the key (STB, Title, Date)
     */

    public String parseKey(String rowEntry){
        String[] fields = rowEntry.split(Constants.escapedInputDelimiter);
        return fields[0]  // STB field
                .concat(Constants.inputDelimiter)
                .concat(fields[1])  // Title field
                .concat(Constants.inputDelimiter)
                .concat(fields[3]);  //  Date field
    }
}

