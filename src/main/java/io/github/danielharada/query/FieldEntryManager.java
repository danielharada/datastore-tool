package io.github.danielharada.query;

import io.github.danielharada.Constants;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides utility functions for operating on FieldEntry objects
 * and List<FieldEntry> objects
 */
public class FieldEntryManager {

    /**
     * Takes a List of FieldEntry objects and a comma separated String of fields to select,
     * and returns a list of those fields in a single string, separated by our output delimiter
     */
    public List<String> selectFields(List<FieldEntry> fieldEntryList, String selectArgs){
        List<String> reducedList = new ArrayList<>();
        for(FieldEntry fieldEntry : fieldEntryList){
            reducedList.add(reduceToString(fieldEntry, selectArgs));
        }
        return reducedList;
    }

    /**
     * Takes a fieldEntry object in and returns a single delimited string containing
     * only the fields specified in our select argument
     * @param fieldEntry object to reduce
     * @param selectArgs fields to reduce to
     * @return single delimited string
     */
    public String reduceToString(FieldEntry fieldEntry, String selectArgs){
        List<String> fields = Arrays.asList(selectArgs.split(","));
        String reducedString = "";
        for(String field : fields){
            reducedString = reducedString.concat(selectCase(fieldEntry, field)).concat(Constants.outputDelimiter);
        }
        // We add an extra delimiter character at the very end, so we substring to knock it off
        return reducedString.substring(0,reducedString.length()-1);
    }

    /**
     * Translates the a select parameter to a fieldEntry field
     * @param fieldEntry the FieldEntry object we're pulling a field from
     * @param selectField the field that we want to pull
     * @return a String representation of the field we selected from the FieldEntry object
     */
    public String selectCase(FieldEntry fieldEntry, String selectField){
        String value = "none";
        switch(selectField.toLowerCase()) {
            case "stb":
                value = fieldEntry.getStb();
                break;
            case "title":
                value = fieldEntry.getTitle();
                break;
            case "provider":
                value = fieldEntry.getProvider();
                break;
            case "date":
                value = fieldEntry.getDate().toString();
                break;
            case "rev":
                //value = Float.toString(fieldEntry.getRev());
                value = String.format("%.2f", fieldEntry.getRev());
                break;
            case "view_time":
                value = fieldEntry.getViewTime().toString();
                break;
        }
        return value;
    }

    /**
     * Converts a list of string lines from the datastore into a list of FieldEntry objects
     * @param entryList List of string lines from the datastore
     * @return List of FieldEntry objects parsed from the datastore lines
     */
    public List<FieldEntry> convertList(List<String> entryList){
        List<FieldEntry> fieldEntryList = new ArrayList<>();
        entryList.forEach(entry -> fieldEntryList.add(convertRow(entry)));

        return fieldEntryList;
    }

    /**
     * Takes in a single line from the datastore and converts it into a FieldEntry object
     * @param entryRow
     * @return
     */
    public FieldEntry convertRow(String entryRow){
        String[] fields = entryRow.split(Constants.escapedInputDelimiter);

        // Need to specify the format on view_time parameter, otherwise we get an error when trying to store
        // it as a LocalTime object
        DateTimeFormatter viewTimeFormatter = DateTimeFormatter.ofPattern(Constants.viewtimeFormat);
        return new FieldEntry(fields[0],  // stb
                fields[1],  // title
                fields[2],  // provider
                LocalDate.parse(fields[3]),  // date
                Float.parseFloat(fields[4]),  // rev
                LocalTime.parse(fields[5], viewTimeFormatter));  // view_time
    }

}
