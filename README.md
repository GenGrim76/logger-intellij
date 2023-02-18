# logger-intellij: A complete logger for intellij IDEA by [Fulvio Marcelli](https://gitlab.com/fulvio1993/logger-intellij)

# Install and Usage instructions

This is an Intellij plugin client for the [CAS Logger](https://github.com/elPeroN/logger-backend)
 and [CAS Dashboard](https://github.com/elPeroN/CAS-dashboard).

## How to: compile

 - Clone this repository and open it as a project with an Intellij IDE.

 - Change the project SDK:
   - open the project settings (File -> Project Structure)
   - go to the project tab
   - change the projejct SDK to `Intellij Platform Plugin SDK` (you may need to add a new sdk, e.g. 17)
   - apply your changes and wait until the needed downloads and reindex complete

 - Check that the `intellij.version` field in the [build.gradle](build.gradle) file matches your
   current IDE version. If not, update it (this will keep working only as long as their API
   remains fairly stable).

 - Create a Gradle run job for buildPlugin:
   - Run -> Edit configurations...
   - Modify the default job target, replace `runIde` with `buildPlugin`
   - Launch the job
   - The compiled plugin can now be found in `build/libs/logger-1.0-SNAPSHOT.jar`

## How to: install and use
 
 - Install the plugin:
   - File -> Settings -> Plugins
   - Click on the cog -> Install Plugin from Disk...
   - Restart the IDE if required

 - Log in:
   - Logger Intellij -> Login
   - Fill in your credentials (e.g. `https://server_address/logger` )
   - A popup will inform you of a successfull login
   - This operation must be done every time you reopen the IDE as the plugin does not store the 
     credentials 
   
 - Start coding!
