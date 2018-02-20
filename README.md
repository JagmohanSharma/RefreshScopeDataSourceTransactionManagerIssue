# RefreshScopeDataSourceTransactionManagerIssue
This is used to replicate issue reported with https://github.com/spring-cloud/spring-cloud-commons/issues/327

The `PropertyBasedDynamicBeanDefinitionRegistrar` class is used for dynamically initializing Property beans(DataSOurceProperties) and dependent consumer(DataSource), if any.

`JdbcBeanDefinitionRegistrar` class is used to register `jdbcTemplate` and `DataSourceTransactionManager` beans dynamically using previously created data sources beans using PropertyBasedDynamicBeanDefinitionRegistrar.


To replicate this issue:

please up the application using mvn spring-boot:run

once up , please hit endpoint `/rest/v1/save` which basically save one entity object to H2 data base using JpaRepository.

once we hit, we get below error:


There was an unexpected error (type=Internal Server Error, status=500).
`No value for key [org.apache.tomcat.jdbc.pool.DataSource@1b718e8e{ConnectionPool[defaultAutoCommit=null; defaultReadOnly=null; defaultTransactionIsolation=-1; defaultCatalog=null; driverClassName=org.h2.Driver; maxActive=100; maxIdle=100; minIdle=10; initialSize=10; maxWait=30000; testOnBorrow=false; testOnReturn=false; timeBetweenEvictionRunsMillis=5000; numTestsPerEvictionRun=0; minEvictableIdleTimeMillis=60000; testWhileIdle=false; testOnConnect=false; password=********; url=jdbc:h2:file:~/h2/testdb; username=sa; validationQuery=null; validationQueryTimeout=-1; validatorClassName=null; validationInterval=3000; accessToUnderlyingConnectionAllowed=true; removeAbandoned=false; removeAbandonedTimeout=60; logAbandoned=false; connectionProperties=null; initSQL=null; jdbcInterceptors=null; jmxEnabled=true; fairQueue=true; useEquals=true; abandonWhenPercentageFull=0; maxAge=0; useLock=false; dataSource=null; dataSourceJNDI=null; suspectTimeout=0; alternateUsernameAllowed=false; commitOnReturn=false; rollbackOnReturn=false; useDisposableConnectionFacade=true; logValidationErrors=false; propagateInterruptState=false; ignoreExceptionOnPreLoad=false; useStatementFacade=true; }] bound to thread [http-nio-8080-exec-9]`
