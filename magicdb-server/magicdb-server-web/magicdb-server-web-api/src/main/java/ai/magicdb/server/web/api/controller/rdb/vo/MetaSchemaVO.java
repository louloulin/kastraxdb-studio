package ai.magicdb.server.web.api.controller.rdb.vo;

import ai.magicdb.spi.model.Database;
import ai.magicdb.spi.model.Schema;
import lombok.Data;

import java.util.List;
@Data
public class MetaSchemaVO {
    /**
     * database list
     */
    private List<Database> databases;

    /**
     * schema list
     */
    private List<Schema> schemas;
}
