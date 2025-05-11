package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.ServiceTestResultDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.*

/**
 * 服务测试结果Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface ServiceTestResultMapper : BaseMapper<ServiceTestResultDO> {
    
    /**
     * 根据测试ID查询测试结果
     *
     * @param testId 测试ID
     * @return 测试结果
     */
    @Select("SELECT * FROM data_service_test_result WHERE test_id = #{testId}")
    fun selectByTestId(@Param("testId") testId: String): ServiceTestResultDO?
    
    /**
     * 根据服务ID查询测试结果列表
     *
     * @param serviceId 服务ID
     * @return 测试结果列表
     */
    @Select("SELECT * FROM data_service_test_result WHERE service_id = #{serviceId} ORDER BY execution_timestamp DESC")
    fun selectByServiceId(@Param("serviceId") serviceId: String): List<ServiceTestResultDO>
    
    /**
     * 根据测试ID更新测试结果
     *
     * @param result 测试结果
     * @return 影响行数
     */
    @Update("UPDATE data_service_test_result SET " +
            "test_name = #{testName}, " +
            "service_id = #{serviceId}, " +
            "parameters = #{parameters}, " +
            "expected_result = #{expectedResult}, " +
            "actual_result = #{actualResult}, " +
            "passed = #{passed}, " +
            "error_message = #{errorMessage}, " +
            "execution_time = #{executionTime}, " +
            "execution_timestamp = #{executionTimestamp}, " +
            "execution_user_id = #{executionUserId} " +
            "WHERE test_id = #{testId}")
    fun updateByTestId(result: ServiceTestResultDO): Int
    
    /**
     * 根据测试ID删除测试结果
     *
     * @param testId 测试ID
     * @return 影响行数
     */
    @Delete("DELETE FROM data_service_test_result WHERE test_id = #{testId}")
    fun deleteByTestId(@Param("testId") testId: String): Int
}
