# datastore-tool

The datastore-tool was a short project based on the following prompt:

You will be provided with a pipe separated file in the following format:
```
STB|TITLE|PROVIDER|DATE|REV|VIEW_TIME
stb1|the matrix|warner bros|2014-04-01|4.00|1:30
stb1|unbreakable|buena vista|2014-04-03|6.00|2:05
stb2|the hobbit|warner bros|2014-04-02|8.00|2:45
stb3|the matrix|warner bros|2014-04-02|4.00|1:05
```

You must parse and import the file into a simple datastore that you create.  Assume that the datastore will become too large to fit into memory.  Records should be unique by STB, TITLE, and DATE.  Subsequent imports with the same logical record should overwrite earlier records.

Your tool must also be able to execute simple queries on this datastore.  It should accept command line args for SELECT, ORDER, and FILTER functions such as the following:
query -s TITLE,REV,DATE -o DATE,TITLE -f DATE=2014-04-01


## How to install and run

The source code can be compiled by maven.  Simply clone the repo and run "mvn clean package" in the top level directory with the pom.xml file.  This will compile the code and package it into datastore-tool-1.0.jar in the target folder.  The jar can then be run by calling "java -jar datastore-tool-1.0.jar {args}".  A datastore directory to contain any imported data will be created in the directory from which you invoke the program.

## Usage

The following command line arguments can be used:

**-i**, **--insert**:  Invokes the file importer.  Followed by the file you wish to import, as `datastore-tool-1.0.jar -i import_file`.  Cannot be used with the **-q** option.

**-q**, **--query**:  Invokes the query engine.  A select option must also be invoked with this option.  Cannot be combined with the **-i** option.

For the following options, the possible field names are STB, TITLE, PROVIDER, DATE, REV, VIEW_TIME.  Field titles are not case sensitive.  Fields should be passed in in a single string separated by commas, with no spaces in between.

**-s**, **--select**:  Select option. Followed by a comma separated, ordered list of fields to be selected.

**-o**, **--order**:  Orders the query output by the fields specified.  Takes a comma separated, ordered list of fields to order by.

**-f**, **--filter**:  Filters the query output to only include entries with a specified field value.  Currently only supports equality filtering, e.g. `TITLE="the hobbit"`.  Filter values that contain spaces should be enclosed in quotes.  Filters for multiple fields can be separated by commas.

Example query with all options:
```
datastore-tool-1.0.jar -q -s TITLE,DATE,PROVIDER -o TITLE,DATE -f TITLE="the matrix",DATE=2014-04-01
```

## Notes

The command line argument parsing is simple, and therefore brittle.  If you include an option but not its argument, the program may fail and throw stack traces in the output.  If you include non-valid fields, they will either be ignored or generate unexpected output.  If an option is invoked more than once, only the last instance will be used.

