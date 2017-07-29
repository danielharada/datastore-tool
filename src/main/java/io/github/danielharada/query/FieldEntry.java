package io.github.danielharada.query;

import io.github.danielharada.ArgumentFlags;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Object to represent entries in our datastore.  Implements Comparable to allow us to define which fields
 * we order by.
 */
public class FieldEntry implements Comparable<FieldEntry>{

    private String stb;
    private String title;
    private String provider;
    private LocalDate date;
    private float rev;
    private LocalTime viewTime;
    //private static String orderArgs;
    private static ArgumentFlags argFlags;

    public FieldEntry(String stb, String title, String provider, LocalDate date, float rev, LocalTime viewTime) {
        this.stb = stb;
        this.title = title;
        this.provider = provider;
        this.date = date;
        this.rev = rev;
        this.viewTime = viewTime;
        //this.orderArgs = orderArgs;
        this.argFlags = new ArgumentFlags();
    }

    public String getStb() {
        return stb;
    }

    public void setStb(String stb) {
        this.stb = stb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getRev() {
        return rev;
    }

    public void setRev(float rev) {
        this.rev = rev;
    }

    public LocalTime getViewTime() {
        return viewTime;
    }

    public void setViewTime(LocalTime viewTime) {
        this.viewTime = viewTime;
    }

    public int compareTo(FieldEntry compareEntry){
        int compareResult = 0;
        // Iterate over the fields that we want to order by, and run the comparison on those fields.
        // The first field that has a non 0 (i.e. non-equality) result, we return that result.  Otherwise
        // the two FieldEntries are considered equal.
        for(String field : parseOrderArgs()){
            compareResult = compareCases(compareEntry, field);
            if(compareResult != 0){
                return compareResult;
            }
        }
        return compareResult;
    }

    public int compareCases(FieldEntry compareEntry, String compareField){
        int compareResult = 0;
        switch(compareField.toLowerCase()){
            case "stb":
                compareResult = stb.compareTo(compareEntry.getStb());
                break;
            case "title":
                compareResult = title.compareTo(compareEntry.getTitle());
                break;
            case "provider":
                compareResult = provider.compareTo(compareEntry.getProvider());
                break;
            case "date":
                compareResult = date.compareTo(compareEntry.getDate());
                break;
            case "rev":
                compareResult = Float.compare(rev, compareEntry.getRev());
                break;
            case "view_time":
                compareResult = viewTime.compareTo(compareEntry.getViewTime());
                break;
        }

        return compareResult;
    }

    public List<String> parseOrderArgs(){
         return Arrays.asList(argFlags.getOrderArgs().split(","));
    }
}
