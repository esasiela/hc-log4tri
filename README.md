# hc-log4tri
Webapp for planning and logging triathlon training

# Data Source

The DAO layer expects a jndi context.  If you are running the whole thing in a J2EE container, you can supply it via *META-INF/context.xml*.  If running outside a container (a main() method, test scripts, etc.) you can supply a system property *log4tri.context* which contains the name of a context file (which must be in the CLASSPATH).

Example META-INF/context.xml snippet for MYSQL database.  Make sure to put the driver jar in the classpath of the container, not packaged in the .war file:
```
 <Resource name="jdbc/log4tri" auth="Container" type="javax.sql.DataSource"
               maxTotal="100" maxIdle="30" maxWaitMillis="10000"
               username="lftdev" password="fakefake" driverClassName="com.mysql.jdbc.Driver"
               url="jdbc:mysql://maiden.hc/lftdevdb?useSSL=false"/>
```

