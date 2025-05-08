package ai.magicdb.server.web.api.controller.rdb;

import java.util.List;

import ai.magicdb.server.domain.api.param.SchemaOperationParam;
import ai.magicdb.server.domain.api.param.SchemaQueryParam;
import ai.magicdb.server.domain.api.service.DatabaseService;
import ai.magicdb.server.domain.api.service.DlTemplateService;
import ai.magicdb.server.domain.api.service.TableService;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import ai.magicdb.server.web.api.aspect.ConnectionInfoAspect;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.magicdb.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.magicdb.server.web.api.controller.rdb.request.SchemaCreateRequest;
import ai.magicdb.server.web.api.controller.rdb.request.UpdateSchemaRequest;
import ai.magicdb.server.web.api.controller.rdb.vo.SchemaVO;
import ai.magicdb.spi.model.Schema;
import ai.magicdb.spi.model.Sql;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * shema controller
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/schema")
@RestController
public class SchemaController {

    @Autowired
    private TableService tableService;

    @Autowired
    private DlTemplateService dlTemplateService;

    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DatabaseService databaseService;

    /**
     * Query the schema_list contained in the database
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public ListResult<SchemaVO> list(@Valid DataSourceBaseRequest request) {
        SchemaQueryParam queryParam = SchemaQueryParam.builder().dataSourceId(request.getDataSourceId()).dataBaseName(
                request.getDatabaseName()).refresh(request.isRefresh()).build();
        ListResult<Schema> tableColumns = databaseService.querySchema(queryParam);
        List<SchemaVO> tableVOS = rdbWebConverter.schemaDto2vo(tableColumns.getData());
        return ListResult.of(tableVOS);
    }

    /**
     * Delete schema
     *
     * @param request
     * @return
     */
    @PostMapping("/delete_schema")
    public ActionResult deleteSchema(@Valid @RequestBody DataSourceBaseRequest request) {
        SchemaOperationParam param = SchemaOperationParam.builder().databaseName(request.getDatabaseName())
                .schemaName(request.getSchemaName()).build();
        return databaseService.deleteSchema(param);
    }

    /**
     * Create schema
     *
     * @param request
     * @return
     */
    @PostMapping("/create_schema_sql")
    public DataResult<Sql> createSchema(@Valid @RequestBody SchemaCreateRequest request) {
        Schema schema = Schema.builder().databaseName(request.getDatabaseName())
                .name(request.getSchemaName())
                .owner(request.getOwner())
                .comment(request.getComment())
                .build();
        return databaseService.createSchema(schema);
    }

    /**
     * Modify schema
     *
     * @param request
     * @return
     */
    @PostMapping("/modify_schema")
    public ActionResult modifySchema(@Valid @RequestBody UpdateSchemaRequest request) {
        SchemaOperationParam param = SchemaOperationParam.builder().databaseName(request.getDatabaseName())
                .schemaName(request.getSchemaName()).newSchemaName(request.getNewSchemaName()).build();
        return databaseService.modifySchema(param);
    }
}
