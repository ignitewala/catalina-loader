# Custom tomcat class loader to use java specific library - such as Java 8 and Java 9. For specfic versions of Java some jars are not compatible such as MSSQL jdbcdriver 4 is used for java 7 but 4.2 is used in java 8. If we put 4.2 jars in Java 7 , the JVM doesnt start up. similarly 3rd party framwroks such as FICO Blaze - 7.25 runs in Java 7 but 7.4 runs in Java 8.

'''To cater that created''' a custom class loader, here user has to put the jar under the catalina/tomcat lib folder.
Define the following snippet in tomcat context file. Here the 
# versionedClasspath is the java specific folder where we can put jars. 
<Loader className="com.util.loader.CatalinaWebappLoader" virtualClasspath="${Generic_location}\specific.jar;${network_path}\PROPERTIES"  versionedClasspath="${specific location for version specific jars}"/>

