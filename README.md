# hc-log4tri
Webapp for planning and logging triathlon training

# Data Source

The DAO layer expects a jndi context.  Since the definition of the data source includes the database credentials, best to keep it out of source repository, meaning we'll avoid *META-INF/context.xml*.

For Eclipse WTP development environment, put the resource definition in *server.xml* in the *Servers* project in your workspace.  This must be done after you add the log4tri project to the server in Eclipse, and gets destroyed if you remove the project from the server, so keep a copy somewhere (somewhere safe since it has your credentials).

For Tomcat production environment, put a *<context>* tag with the resource definition in *$CATALINA_HOME/conf/Catalina/localhost/APP_NAME.xml*.  Alternatively, if you want the datasource available to ALL webapps on the server, put the resource definition in *$CATALINA_HOME/conf/context.xml*.  

If running outside a J2EE container (ex: a *main()* method, test scripts, etc.) you can supply a system property *log4tri.context* which contains the name of a context file (which must be in the CLASSPATH).  The context file should match the Tomcat format.

Example resource definition snippet for MYSQL database.  Likely you need to put the driver jar in the classpath of the container, not packaged in the .war file:
```
 <Resource name="jdbc/log4tri" auth="Container" type="javax.sql.DataSource"
               maxTotal="100" maxIdle="30" maxWaitMillis="10000"
               username="DB_USER" password="DB_PASS" driverClassName="com.mysql.jdbc.Driver"
               url="jdbc:mysql://DB_HOST/DB_NAME?useSSL=false"/>
```

