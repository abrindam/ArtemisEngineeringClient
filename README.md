Artemis Engineering Client
=========================

This is a Java client for Artemis Space Bridge Simulator (http://artemis.eochu.com/). It attempts to provide an better experience 
for those playing the Engineering station by providing various improvements and advanced functionality.

More information on specific functionality will be available as development progresses.

Contributing
-----------

These instruction assume you are using Eclipse. They are tested on Eclipse Mars (4.5.1).

First, check out this repository into your Eclipse workspace.

Next, install dependencies. You need to download the Artemis Client Library v2.6.0 from https://github.com/rjwut/ArtClientLib/releases. 
You may also want to install the source code as well so you can look at it while developing. You should place all dependencies in the
`lib/` folder inside the repository. You may need to create this folder.

If you are on Mac or Linux, the follow commands should do the trick:

    cd <repository location>
    mkdir lib
    cd lib
    curl -OL https://github.com/rjwut/ArtClientLib/releases/download/v2.6.0/artclientlib-2.6.0.jar
    curl -L https://github.com/rjwut/ArtClientLib/archive/v2.6.0.tar.gz | tar -xzvf -
    
Next, you need to configure Eclipse. Open Eclipse, and choose File->New Project. Enter the name of this repository exactly as the name
of the project. If done correctly, Eclipse will realize this folder already exists and will configure automatically.

Then, right click on the `lib/artclientlib-2.6.0.jar` file and choose Build Path -> Add to Build Path. At this point, the project should
build correctly with no errors.

Finally, to attach the source, once again right-click the jar file and choose Build Path -> Configure Build Path. Navigate to the Library tab.
Locate `artclientlib-2.6.0.jar` in the list and click the triangle to see more details. Click the Source Attachment entry and then click the
Edit button on the right. Now point to the folder containing the source files. You should now be able to click on library classes such as 
`ShipSystem` and see their source.

The entry point to the program is located in the `ClientMain.java` file. 