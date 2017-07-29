package io.github.danielharada.query;

import io.github.danielharada.ArgumentFlags;
import io.github.danielharada.Constants;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by daniel.harada on 17-07-28.
 */
public class Query {

    private ArgumentFlags argFlags;

    public Query(ArgumentFlags argFlags){
        this.argFlags = argFlags;

    }

    /**
     * Pulls lines from the datastore into a list of strings.  Any filter conditions are applied while reading in
     * the data.
     * @param datastoreDirectory string path to the directory where our datastore resides
     * @return List of unparsed string lines from the datastore
     */
    public List<String> readDataStore(String datastoreDirectory){
        String dateFilter = "*";
        // If we filter on date, pull in that date value
        if(argFlags.getFilterFlag()){
            dateFilter = dateFilter();
        }

        List<String> rowEntries = new ArrayList<String>();

        // Stream over every file in our path that matches our dateFilter glob pattern.  For each file we find,
        // append its entries onto our rowEntries list
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(datastoreDirectory), dateFilter)){
            directoryStream.forEach(path -> readFileAndAppendLinesToList(path, rowEntries));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return rowEntries;

    }

    /**
     * Streams in a single file, applying any non-date parameter filters if any exist.  Each line we read in
     * is appended to a list.
     * @param path  path to the file we're reading
     * @param entryList  list of string lines we're appending to
     */
    public void readFileAndAppendLinesToList(Path path, List<String> entryList){
        // Check if we apply any filters.  If so, check if it's more than just date filtering
        if(hasNonDateFilter()){
            try(Stream<String> fileStream = Files.lines(path)){
                fileStream.filter(rowEntry -> queryFilter(rowEntry)).forEach(rowEntry -> entryList.add(rowEntry));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        // If we don't have any filters other than on date, then don't apply the filter function
        else {
            try (Stream<String> fileStream = Files.lines(path)) {
                fileStream.forEach(rowEntry -> entryList.add(rowEntry));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if there are any filters on a field other than the date field.  If so, returns true.
     * @return boolean indicating whether there are non-date filter parameters
     */
    public boolean hasNonDateFilter(){
        boolean nonDateFilter = false;
        List<String> nonDateParams = Arrays.asList("stb", "title", "provider", "rev", "view_time");
        if(argFlags.getFilterFlag()){
            String[] filters = argFlags.getFilterArgs().split(",");
            for(int i = 0; i < filters.length; i++) {
                String[] filterParams = filters[i].split("=");
                if(nonDateParams.contains(filterParams[0].toLowerCase())){
                    nonDateFilter = true;
                    break;
                }
            }
        }
        return nonDateFilter;
    }

    /**
     * Returns a glob pattern matching any date filter present.  If no date filter, * is returned to search on all dates
     * @return string containing glob pattern for date filter
     */
    public String dateFilter(){
        // Defaults to * to match all dates, if no date filter is present
        String date = "*";
        // Iterate through all of the filter parameters.  If date is in there, pull the date value
        String[] filters = argFlags.getFilterArgs().split(",");
        for(int i = 0; i < filters.length; i++){
            String[] filterParams = filters[i].split("=");
            if (filterParams[0].equalsIgnoreCase("date")) {
                date = filterParams[1];
            }
        }
            return date;
    }


    /**
     *  Used as input for the file stream filter.  If this method returns false, the stream will filter that entry.
     * @param rowEntry
     * @return false if the row fails to match our filter, true if the row matches our filter
     */
    public boolean queryFilter(String rowEntry){
        // Default the booleans to true, if we don't apply a filter on that field then that field remains true
        boolean matchFilterSTB = true;
        boolean matchFilterTitle = true;
        boolean matchFilterProvider = true;
        boolean matchFilterRev = true;
        boolean matchFilterViewTime = true;

        String[] fields = rowEntry.split(Constants.escapedInputDelimiter);

        if(argFlags.getFilterFlag()){
            // Our filters are comma separated, and we will then need to split on the = sign to determine
            // what field it is we're filtering and the filter value
            String[] filters = argFlags.getFilterArgs().split(",");

            for(int i = 0; i < filters.length; i++){
                String[] filterParams = filters[i].split("=");

                // There is no date case because we already filtered at file read in
                switch(filterParams[0].toLowerCase()){
                    case "stb":
                        matchFilterSTB = filterParams[1].equalsIgnoreCase(fields[0]);
                        break;
                    case "title":
                        matchFilterTitle = filterParams[1].equalsIgnoreCase(fields[1]);
                        break;
                    case "provider":
                        matchFilterProvider = filterParams[1].equalsIgnoreCase(fields[2]);
                        break;
                    case "rev":
                        matchFilterRev = filterParams[1].equalsIgnoreCase(fields[4]);
                        break;
                    case "view_time":
                        matchFilterViewTime = filterParams[1].equalsIgnoreCase(fields[5]);
                        break;
                }
            }
        }

        // If any field did not match the filter, we return a false result to drop the whole entry
        return matchFilterSTB && matchFilterTitle && matchFilterProvider && matchFilterRev && matchFilterViewTime;
    }
}
