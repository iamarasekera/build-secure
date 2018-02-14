# build-secure
A software program to enforce secure mobile application development.

Minimum System Requirements
* Android Studio 3.0
* Android SDK
* JDK 1.7

Deployment

Steps to import “buildsec” library into “Android Studio” and build mobile application.

1. Open your android project in Android Studio.
2. Download the buildSec.jar and buildSecProcessor.jar, from the github location using git or as a zip archive and unzip it.
3. Copy the jar files in to the lib folder in the ‘app’ module of your android project.
4. Go to File -> Import Module and import the library as a module.
5. Right click your app in project view and select "Open Module Settings".
6. Click the "Dependencies" tab and then the '+' button.
7. Select "Module Dependency".
8. Select "buildSec.jar Library".
9. Select “buildSecProcessor.jar”.
10. Edit your project's “build.gradle” file to add the following lines in the "defaultConfig" section
``javaCompileOptions{ annotationProcessorOptions{ includeCompileClasspath true } }``

