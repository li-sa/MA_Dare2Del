# Dare2Del

Dare2Del is an assistence software to generate deletion candidate files in a file system with descriptive explanations. 
This application was developed by Lisa Schatt at the University of Bamberg in her Master thesis submitted in summer term 2019.


## Contents

- Folder './Dare2Del_IntelliJ-Export': Project export from IntelliJ of the whole project.
- Folder './Dare2Del_Application': Compiled project to JAR file and Windows executable for Dare2Del.


## Usage

The Dare2Del_JavaFX.jar can be executed via 'java -jar Dare2Del_JavaFX.jar' in the command prompt.

Folder structure in './Dare2Del_Application':
- '/DirectoryExample': Example folder with some files to be loaded at the start of the application. 
- '/htmlFiles': HTML templates which are integrated into the JavaFX application to show the deletion reasoning.
- '/prologFiles': Underlying Prolog files. The Prolog rule set 'irrelevanceTheory.pl' as well as the dynamically generated facts of crawled files in 'clauses.pl'.
- '/logs': Log files generated while using the application. 


## System Requirements

- 64-bit Java Runtime Environment, Version 1.8
- SWI-Prolog (https://www.swi-prolog.org/Download.html)
