package io.github.danielharada;

/**
 * Parses command line arguments
 */
public class ArgumentFlags {

    private static boolean dataImportFlag;
    private static String dataImportFile;
    private static boolean queryFlag;
    private static boolean selectFlag;
    private static String selectArgs;
    private static boolean orderFlag;
    private static String orderArgs;
    private static boolean filterFlag;
    private static String filterArgs;

    public ArgumentFlags(){}

    public ArgumentFlags(String[] args){
        this.dataImportFlag = false;
        this.queryFlag = false;
        this.selectFlag = false;
        this.orderFlag = false;
        this.filterFlag = false;

        for(int  i = 0; i < args.length; i++){
            if(args[i].equals("-i") || args[i].equals("--import")){
                this.dataImportFlag = true;
                this.dataImportFile = args[i+1];

            }

            else if(args[i].equals("-q") || args[i].equals("--query")){
                this.queryFlag = true;
            }

            else if(args[i].equals("-s") || args[i].equals("--select")){
                this.selectFlag = true;
                this.selectArgs = args[i+1];
            }

            else if(args[i].equals("-o") || args[i].equals("--order")){
                this.orderFlag = true;
                this.orderArgs = args[i+1];
            }

            else if(args[i].equals("-f") || args[i].equals("--filter")){
                this.filterFlag = true;
                this.filterArgs = args[i+1];
            }
        }
    }

    public boolean getDataImportFlag() {
        return dataImportFlag;
    }

    public String getDataImportFile() {
        return dataImportFile;
    }

    public boolean getQueryFlag() {
        return queryFlag;
    }

    public boolean getSelectFlag() {
        return selectFlag;
    }

    public String getSelectArgs() {
        return selectArgs;
    }

    public boolean getOrderFlag() {
        return orderFlag;
    }

    public String getOrderArgs() {
        return orderArgs;
    }

    public boolean getFilterFlag() {
        return filterFlag;
    }

    public String getFilterArgs() {
        return filterArgs;
    }
}
