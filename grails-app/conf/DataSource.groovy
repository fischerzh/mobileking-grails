//dataSource {
//    pooled = true
//    driverClassName = "org.h2.Driver"
//    username = "sa"
//    password = ""
//}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url ="jdbc:mysql://176.28.9.173:3306/product_king_test?useUnicode=yes&characterEncoding=UTF-8"
			dialect = org.hibernate.dialect.MySQL5InnoDBDialect
			username = "mf"
			password = "master2013"
			properties {
			   maxActive = -1
			   minEvictableIdleTimeMillis=1800000
			   timeBetweenEvictionRunsMillis=1800000
			   numTestsPerEvictionRun=3
			   testOnBorrow=true
			   testWhileIdle=true
			   testOnReturn=true
			   validationQuery="SELECT 1"
			}        }
    }
	
	
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    production {
        dataSource {
			dbCreate = "update"
//			url = "jdbc:h2:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
			dialect = org.hibernate.dialect.MySQL5InnoDBDialect
			driverClassName = "com.mysql.jdbc.Driver"
			url ="jdbc:mysql://176.28.9.173:3306/product_king?useUnicode=yes&characterEncoding=UTF-8"
			pooled = true
			username = "mf"
			password = "master2013"
			properties {
			   maxActive = -1
			   minEvictableIdleTimeMillis=1800000
			   timeBetweenEvictionRunsMillis=1800000
			   numTestsPerEvictionRun=3
			   testOnBorrow=true
			   testWhileIdle=true
			   testOnReturn=true
			   validationQuery="SELECT 1"
			}
		}
//			pooled = true
//			driverClassName = "com.mysql.jdbc.Driver"
//			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
//			url ="jdbc:mysql://176.28.9.173:3306/product_king?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true"
//			dialect = org.hibernate.dialect.MySQL5InnoDBDialect
//			username = "mf"
//			password = "master2013"
////            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
//		    properties {
//               maxActive = -1
//               minEvictableIdleTimeMillis=1800000
//               timeBetweenEvictionRunsMillis=1800000
//               numTestsPerEvictionRun=3
//               testOnBorrow=true
//               testWhileIdle=true
//               testOnReturn=true
//               validationQuery="SELECT 1"
//	            }
    }
}
