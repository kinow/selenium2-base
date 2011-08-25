selenium2-base -- A base project for Selenium test projects
====================================

## DESCRIPTION

selenium2-base project can be used as basis for Selenium web automation 
projects. It uses TestNG to execute Selenium2/WebDriver tests. You can 
build plain tests or use Page Objects to help you automating your solution. 

Another feature in this project is Data Driven Testing. Through the use of 
Excel in conjunction with TestNG Data Provider you are able to put all your 
test data in Excel sheets that will be consumed by your tests.

Last, but not least, this solutions generates TAP, what means you can extend 
your test results. One result of it, is that you can take screen shots of your 
tests (except if you are using HTML Driver) and TAP will save them for you. 
If you integrate these tests in Jenkins with Jenkins TestLink Plug-in, then 
the plug-in will know how to upload your screen shots as attachments for the 
tests in TestLink.

## Building selenium2-base

It is a Java Maven project. In order to build it you will need download the 
source code and execute mvn -e -X clean install. If everything goes fine you 
will see a BUILD SUCCESS message near the end of the console log. 

## Final notes

Big thanks here to Cesar Almeida Fernandes, who helped me to do write this code. 
Please, consult the Javadoc for instructions on how to use the objects.
