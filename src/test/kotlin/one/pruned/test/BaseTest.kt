package one.pruned.test

import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ScriptUtils
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource

@ActiveProfiles("test")
open class BaseTest {

    @Autowired
    private lateinit var dataSource: DataSource

    @Before
    fun setUp() {
        dataSource.connection.use {
            ScriptUtils.executeSqlScript(it, ClassPathResource("/db/init-test-data.sql"))
        }
    }
}
