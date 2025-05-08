package ai.magicdb.server.domain.repository;

import ai.magicdb.server.domain.repository.mapper.TaskMapper;

public class MapperUtils {

    public static TaskMapper getTaskMapper() {
        return Dbutils.getMapper(TaskMapper.class);
    }
}
