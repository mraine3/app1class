Angular App1 Lesson 26f
-----------------------


<pre>
To Get started From Project Setup
 1. <a href="https://github.com/traderres/webClass/blob/master/learnAngular/lessons/howToInitializePostgresDatabase.txt">Install Postgres, setup the app1_db database, and create the app1_user account</a>

 2. Clone the project
    unix> git clone https://github.com/traderres/angularApp1Lessons.git angularApp1
    unix> cd angularApp1
    unix> git checkout lesson26f/server-side-grid/validate-query

 3. Verify that the webapp works
    a. Compile the project (into an executable JAR)
       unix> mvn clean package -Pprod

    b. Run the executable jar
       unix> java -jar ./backend/target/backend-1.0-SNAPSHOT-exec.jar

    c. Connect to the webapp at
       http://localhost:8080/app1
 
 4. Setup Debugging in IntelliJ Ultimate
    a. Open the project in IntelliJ
    b. <a href="https://github.com/traderres/webClass/blob/master/learnAngular/lessons/lesson01_debug_existing_webapp.txt">Setup debugging (so you can debug TypeScript and Java code)</a>

</pre>
