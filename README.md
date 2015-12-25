# Artemis Engineering Client

This is a Java client for [Artemis Space Bridge Simulator](http://artemis.eochu.com/). It attempts to provide an better experience for those playing the Engineering station by providing various usability improvements and advanced functionality.

More information on specific functionality will be available as development progresses.

## Contributing

### General
The entry point to the program is located in the `ClientMain.java` file.

### IDE Configuration

#### Dependencies
You need to download the [Artemis Client Library v2.6.0](https://github.com/rjwut/ArtClientLib/releases). You may also want to install the source code as well so you can look at it while developing. You should place all dependencies in the `lib/` folder inside the repository. You may need to create this folder.

Inside the root of the project, run the following commands (Mac/Linux; on Windows, manually download and extract the Artemic Client Library):

    cd <repository location, e.g. ~/workspace/ArtemisEngineeringClient>
    mkdir lib
    cd lib
    curl -OL https://github.com/rjwut/ArtClientLib/releases/download/v2.6.0/artclientlib-2.6.0.jar
    curl -L https://github.com/rjwut/ArtClientLib/archive/v2.6.0.tar.gz | tar -xzvf -

#### Eclipse

These instruction assume you are using Eclipse Mars (4.5.1).
    
Open Eclipse and choose `File -> New Project`. Enter the name of this repository exactly as the name of the project. If done correctly, Eclipse will realize this folder already exists and will configure automatically.

Then, right click on the `lib/artclientlib-2.6.0.jar` file and choose `Build Path -> Add to Build Path`. At this point, the project should build correctly with no errors.

Finally, to attach the source, once again right-click the jar file and choose `Build Path -> Configure Build Path`. Navigate to the `Library` tab. Locate `artclientlib-2.6.0.jar` in the list and click the triangle to see more details. Click the `Source Attachment` entry and then click the `Edit` button on the right. Now point to the folder containing the source files. You should now be able to click on library classes such as `ShipSystem` and see their source.


#### IntelliJ IDEA
These instructions assume you are using IntelliJ IDEA 14.1.4.

1. Select `Import Project`.
1. Select the root directory of your clone.
1. Select Create project from existing sources.
1. Continue selecting defaults until the project loads.
1. In the project window's tree, locate the lib directory under `ArtemisEngineeringClient`.
1. Right click on artclientlib-2.6.0.jar, select `Add as source...`, and select `Build -> Rebuild Project`.
	- You should now have no compilation errors.
1. Right click on `ArtemisEngineeringClient`, select `Open module settings...`, and navigate to `Project Settings -> Libraries`
1. Select the `+` at the top of the UI (to add a new library), select `Java`, and select `ArtemisEngineeringClient/lib/artclientlib-2.6.0.jar`.
	- This will allow the ArtClientLib test suite to compile.
1. Select `artclientlib-2.6.0`.  Select the `+` in the bottom of the UI (not the one at the top to add a new library) and select the `lib/ArtClientLib-2.6.0` directory.
	- You should now be able to navigate directly to the ArtClientLib source code from within your source files and see no compilation errors in the `ArtClientLib` directory.

	