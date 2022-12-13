# TODOs:

-   Fix Export
    -   Exported Java-Runtime was missing `d3d, ws`, seemingly because not all necessary DLLs were copied from the JavaFX Sdk to the runtime
-   Package Export to a single Launcher
    -   Find some way to package the distributable into a single launcher executable
    -   Best way is probably by letting the launcher create a temporary directory with all the files and calling them then (wouldn't fuck up paths)
-   Fix Umlaut-changes
    -   Only the last vowel/diphtong in a word should have its umlaut changed
-   Check Term-Sorting
    -   Make sure the terms are sorted correctly after calling `query`
-   createTerm fails for certain inputs
    -   in first test, it especially failed for male-accusative-plural
    -   go through with Debugger, to fix it
-   Add Templates
-   Add proper Error Handling
