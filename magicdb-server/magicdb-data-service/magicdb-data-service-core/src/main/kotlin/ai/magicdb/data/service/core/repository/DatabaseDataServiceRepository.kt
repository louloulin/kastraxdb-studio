package ai.magicdb.data.service.core.repository

import ai.magicdb.data.service.api.DataServiceRepository
import ai.magicdb.data.service.api.model.DataService
import ai.magicdb.data.service.api.model.ServiceGroup
import ai.magicdb.data.service.api.model.ServiceParameter
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.UUID

/**
 * Database implementation of DataServiceRepository
 */
@Repository
class DatabaseDataServiceRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper
) : DataServiceRepository {
    
    private val serviceRowMapper = RowMapper<DataService> { rs, _ ->
        mapDataService(rs)
    }
    
    private val groupRowMapper = RowMapper<ServiceGroup> { rs, _ ->
        mapServiceGroup(rs)
    }
    
    private val parameterRowMapper = RowMapper<ServiceParameter> { rs, _ ->
        mapServiceParameter(rs)
    }
    
    @Transactional
    override fun saveService(dataService: DataService): DataService {
        val exists = getService(dataService.id) != null
        
        if (exists) {
            // Update existing service
            jdbcTemplate.update(
                """
                UPDATE DS_DATA_SERVICE SET 
                    NAME = ?, 
                    DESCRIPTION = ?, 
                    TYPE = ?, 
                    DATA_SOURCE_ID = ?, 
                    DATABASE_NAME = ?, 
                    SCHEMA_NAME = ?, 
                    TABLE_NAME = ?, 
                    SCRIPT = ?, 
                    LANGUAGE = ?, 
                    GROUP_ID = ?, 
                    GMT_MODIFIED = ?, 
                    ENABLED = ?, 
                    TIMEOUT = ?, 
                    CACHE_TIME = ?, 
                    MODIFIED_USER_ID = ?, 
                    TAGS = ?, 
                    METADATA = ? 
                WHERE ID = ?
                """,
                dataService.name,
                dataService.description,
                dataService.type,
                dataService.dataSourceId,
                dataService.databaseName,
                dataService.schemaName,
                dataService.tableName,
                dataService.script,
                dataService.language,
                dataService.groupId,
                dataService.gmtModified,
                dataService.enabled,
                dataService.timeout,
                dataService.cacheTime,
                dataService.modifiedUserId,
                objectMapper.writeValueAsString(dataService.tags),
                objectMapper.writeValueAsString(dataService.metadata),
                dataService.id
            )
        } else {
            // Insert new service
            jdbcTemplate.update(
                """
                INSERT INTO DS_DATA_SERVICE (
                    ID, NAME, DESCRIPTION, TYPE, DATA_SOURCE_ID, DATABASE_NAME, 
                    SCHEMA_NAME, TABLE_NAME, SCRIPT, LANGUAGE, GROUP_ID, 
                    GMT_CREATE, GMT_MODIFIED, ENABLED, TIMEOUT, CACHE_TIME, 
                    CREATE_USER_ID, MODIFIED_USER_ID, TAGS, METADATA
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                dataService.id,
                dataService.name,
                dataService.description,
                dataService.type,
                dataService.dataSourceId,
                dataService.databaseName,
                dataService.schemaName,
                dataService.tableName,
                dataService.script,
                dataService.language,
                dataService.groupId,
                dataService.gmtCreate,
                dataService.gmtModified,
                dataService.enabled,
                dataService.timeout,
                dataService.cacheTime,
                dataService.createUserId,
                dataService.modifiedUserId,
                objectMapper.writeValueAsString(dataService.tags),
                objectMapper.writeValueAsString(dataService.metadata)
            )
        }
        
        // Delete existing parameters
        jdbcTemplate.update("DELETE FROM DS_SERVICE_PARAMETER WHERE SERVICE_ID = ?", dataService.id)
        
        // Insert new parameters
        dataService.parameters.forEach { parameter ->
            saveParameter(parameter)
        }
        
        return getService(dataService.id)!!
    }
    
    @Transactional
    override fun deleteService(id: String): Boolean {
        // Parameters will be deleted automatically due to foreign key constraint with CASCADE
        val rowsAffected = jdbcTemplate.update("DELETE FROM DS_DATA_SERVICE WHERE ID = ?", id)
        return rowsAffected > 0
    }
    
    override fun getService(id: String): DataService? {
        try {
            val service = jdbcTemplate.queryForObject(
                "SELECT * FROM DS_DATA_SERVICE WHERE ID = ?",
                serviceRowMapper,
                id
            )
            
            if (service != null) {
                val parameters = getParametersByService(id)
                return service.copy(parameters = parameters)
            }
            
            return null
        } catch (e: Exception) {
            // Service not found
            return null
        }
    }
    
    override fun getAllServices(): List<DataService> {
        val services = jdbcTemplate.query("SELECT * FROM DS_DATA_SERVICE", serviceRowMapper)
        
        return services.map { service ->
            val parameters = getParametersByService(service.id)
            service.copy(parameters = parameters)
        }
    }
    
    override fun getServicesByGroup(groupId: String): List<DataService> {
        val services = jdbcTemplate.query(
            "SELECT * FROM DS_DATA_SERVICE WHERE GROUP_ID = ?",
            serviceRowMapper,
            groupId
        )
        
        return services.map { service ->
            val parameters = getParametersByService(service.id)
            service.copy(parameters = parameters)
        }
    }
    
    @Transactional
    override fun saveParameter(parameter: ServiceParameter): ServiceParameter {
        if (parameter.id != null) {
            // Update existing parameter
            jdbcTemplate.update(
                """
                UPDATE DS_SERVICE_PARAMETER SET 
                    NAME = ?, 
                    TYPE = ?, 
                    DESCRIPTION = ?, 
                    DEFAULT_VALUE = ?, 
                    REQUIRED = ?, 
                    ORDER_NUM = ?, 
                    GMT_MODIFIED = ? 
                WHERE ID = ?
                """,
                parameter.name,
                parameter.type,
                parameter.description,
                parameter.defaultValue,
                parameter.required,
                parameter.orderNum,
                LocalDateTime.now(),
                parameter.id
            )
            
            return parameter
        } else {
            // Insert new parameter
            jdbcTemplate.update(
                """
                INSERT INTO DS_SERVICE_PARAMETER (
                    SERVICE_ID, NAME, TYPE, DESCRIPTION, DEFAULT_VALUE, 
                    REQUIRED, ORDER_NUM, GMT_CREATE, GMT_MODIFIED
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                parameter.serviceId,
                parameter.name,
                parameter.type,
                parameter.description,
                parameter.defaultValue,
                parameter.required,
                parameter.orderNum,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
            
            // Get the generated ID
            val id = jdbcTemplate.queryForObject(
                """
                SELECT ID FROM DS_SERVICE_PARAMETER 
                WHERE SERVICE_ID = ? AND NAME = ? 
                ORDER BY ID DESC LIMIT 1
                """,
                Long::class.java,
                parameter.serviceId,
                parameter.name
            )
            
            return parameter.copy(id = id)
        }
    }
    
    override fun deleteParameter(id: Long): Boolean {
        val rowsAffected = jdbcTemplate.update("DELETE FROM DS_SERVICE_PARAMETER WHERE ID = ?", id)
        return rowsAffected > 0
    }
    
    override fun getParametersByService(serviceId: String): List<ServiceParameter> {
        return jdbcTemplate.query(
            "SELECT * FROM DS_SERVICE_PARAMETER WHERE SERVICE_ID = ? ORDER BY ORDER_NUM",
            parameterRowMapper,
            serviceId
        )
    }
    
    @Transactional
    override fun saveGroup(serviceGroup: ServiceGroup): ServiceGroup {
        val exists = getGroup(serviceGroup.id) != null
        
        if (exists) {
            // Update existing group
            jdbcTemplate.update(
                """
                UPDATE DS_SERVICE_GROUP SET 
                    NAME = ?, 
                    DESCRIPTION = ?, 
                    PARENT_ID = ?, 
                    ORDER_NUM = ?, 
                    GMT_MODIFIED = ?, 
                    MODIFIED_USER_ID = ?, 
                    METADATA = ? 
                WHERE ID = ?
                """,
                serviceGroup.name,
                serviceGroup.description,
                serviceGroup.parentId,
                serviceGroup.orderNum,
                serviceGroup.gmtModified,
                serviceGroup.modifiedUserId,
                objectMapper.writeValueAsString(serviceGroup.metadata),
                serviceGroup.id
            )
        } else {
            // Insert new group
            jdbcTemplate.update(
                """
                INSERT INTO DS_SERVICE_GROUP (
                    ID, NAME, DESCRIPTION, PARENT_ID, ORDER_NUM, 
                    GMT_CREATE, GMT_MODIFIED, CREATE_USER_ID, MODIFIED_USER_ID, METADATA
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                serviceGroup.id,
                serviceGroup.name,
                serviceGroup.description,
                serviceGroup.parentId,
                serviceGroup.orderNum,
                serviceGroup.gmtCreate,
                serviceGroup.gmtModified,
                serviceGroup.createUserId,
                serviceGroup.modifiedUserId,
                objectMapper.writeValueAsString(serviceGroup.metadata)
            )
        }
        
        return getGroup(serviceGroup.id)!!
    }
    
    @Transactional
    override fun deleteGroup(id: String): Boolean {
        val rowsAffected = jdbcTemplate.update("DELETE FROM DS_SERVICE_GROUP WHERE ID = ?", id)
        return rowsAffected > 0
    }
    
    override fun getGroup(id: String): ServiceGroup? {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM DS_SERVICE_GROUP WHERE ID = ?",
                groupRowMapper,
                id
            )
        } catch (e: Exception) {
            // Group not found
            return null
        }
    }
    
    override fun getAllGroups(): List<ServiceGroup> {
        return jdbcTemplate.query("SELECT * FROM DS_SERVICE_GROUP", groupRowMapper)
    }
    
    override fun getGroupsByParent(parentId: String?): List<ServiceGroup> {
        return if (parentId == null) {
            jdbcTemplate.query(
                "SELECT * FROM DS_SERVICE_GROUP WHERE PARENT_ID IS NULL ORDER BY ORDER_NUM",
                groupRowMapper
            )
        } else {
            jdbcTemplate.query(
                "SELECT * FROM DS_SERVICE_GROUP WHERE PARENT_ID = ? ORDER BY ORDER_NUM",
                groupRowMapper,
                parentId
            )
        }
    }
    
    private fun mapDataService(rs: ResultSet): DataService {
        return DataService(
            id = rs.getString("ID"),
            name = rs.getString("NAME"),
            description = rs.getString("DESCRIPTION"),
            type = rs.getString("TYPE"),
            dataSourceId = rs.getLong("DATA_SOURCE_ID").takeIf { !rs.wasNull() },
            databaseName = rs.getString("DATABASE_NAME"),
            schemaName = rs.getString("SCHEMA_NAME"),
            tableName = rs.getString("TABLE_NAME"),
            script = rs.getString("SCRIPT"),
            language = rs.getString("LANGUAGE"),
            groupId = rs.getString("GROUP_ID"),
            gmtCreate = rs.getTimestamp("GMT_CREATE").toLocalDateTime(),
            gmtModified = rs.getTimestamp("GMT_MODIFIED").toLocalDateTime(),
            enabled = rs.getBoolean("ENABLED"),
            timeout = rs.getLong("TIMEOUT"),
            cacheTime = rs.getLong("CACHE_TIME"),
            createUserId = rs.getLong("CREATE_USER_ID").takeIf { !rs.wasNull() },
            modifiedUserId = rs.getLong("MODIFIED_USER_ID").takeIf { !rs.wasNull() },
            tags = try {
                objectMapper.readValue(
                    rs.getString("TAGS"),
                    objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
                )
            } catch (e: Exception) {
                emptyList()
            },
            metadata = try {
                objectMapper.readValue(
                    rs.getString("METADATA"),
                    objectMapper.typeFactory.constructMapType(Map::class.java, String::class.java, Any::class.java)
                )
            } catch (e: Exception) {
                emptyMap()
            }
        )
    }
    
    private fun mapServiceGroup(rs: ResultSet): ServiceGroup {
        return ServiceGroup(
            id = rs.getString("ID"),
            name = rs.getString("NAME"),
            description = rs.getString("DESCRIPTION"),
            parentId = rs.getString("PARENT_ID"),
            orderNum = rs.getInt("ORDER_NUM"),
            gmtCreate = rs.getTimestamp("GMT_CREATE").toLocalDateTime(),
            gmtModified = rs.getTimestamp("GMT_MODIFIED").toLocalDateTime(),
            createUserId = rs.getLong("CREATE_USER_ID").takeIf { !rs.wasNull() },
            modifiedUserId = rs.getLong("MODIFIED_USER_ID").takeIf { !rs.wasNull() },
            metadata = try {
                objectMapper.readValue(
                    rs.getString("METADATA"),
                    objectMapper.typeFactory.constructMapType(Map::class.java, String::class.java, Any::class.java)
                )
            } catch (e: Exception) {
                emptyMap()
            }
        )
    }
    
    private fun mapServiceParameter(rs: ResultSet): ServiceParameter {
        return ServiceParameter(
            id = rs.getLong("ID"),
            serviceId = rs.getString("SERVICE_ID"),
            name = rs.getString("NAME"),
            type = rs.getString("TYPE"),
            description = rs.getString("DESCRIPTION"),
            defaultValue = rs.getString("DEFAULT_VALUE"),
            required = rs.getBoolean("REQUIRED"),
            orderNum = rs.getInt("ORDER_NUM"),
            gmtCreate = rs.getTimestamp("GMT_CREATE")?.toLocalDateTime(),
            gmtModified = rs.getTimestamp("GMT_MODIFIED")?.toLocalDateTime()
        )
    }
}
