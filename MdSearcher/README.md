This is a demo project of the MDSearcher-Project
---

This application collects different .md or .markdown files from different maven dependency projects and puts them together to one big markdown file.

There are several steps to make in the MDSearcher-Project, which only include the "pom.xml" file and the "OneClassOfProject.txt" in the resources folder:

1. Open the pom.xml file and go to modules section and add the relative path of the project.

    Example:

        <modules>
            <module>../../ExampleFolder/MdProjekt1</module>
            <module>../../MdProjekt2</module>
            <module>../../MdProjekt3</module>
        </modules>'''

2. Add the dependencies int the dependencies section to the project like this:

        <dependency>
            <groupId>org.mdproject</groupId>
            <artifactId>MdProjekt1</artifactId>
            <version>1.0</version>
            <scope>compile</scope>
        </dependency>
    IMPORTANT! Do not remove the dependency "commons-io" as for it is used for collecting the md-files.


3. Sync the Project. Relatively there should be a sync-button on the Top-Right corner of the screen. If not then go to the Build-Tab on the bottom, go to Sync and click on the Sync-Button.


4. Finally go to the OneClassOfProject.txt file in \src\main\resources\ and write down one classname (e.g. MdProject1Class) which these dependency projects have. In this order, the big md File will paste the contents in this order seperated by 3 horizontal lines. If there are more than on md files in one project, then these will be ordered alphabetically seperated by two horizontal lines.

    IMPORTANT: Don't write classnames, which multiple projects have e.g. Main.

    TIP: Make an empty class, which has the same name as the project and put the name here. So you also know, which project is meant.


5. Run the application. A file named MasterMd.md will be created.

NOTE: This concept of writing a classname of the project is not optimal. It would be better to just write the projectname, but I haven't found a solution yet. It might be improved, if I find a better solution, but this should suffice and is still easy to do.