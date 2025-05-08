package ai.magicdb.server.web.api.controller.task;

import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.web.api.aspect.ConnectionInfoAspect;
import ai.magicdb.server.web.api.controller.rdb.request.DataExportRequest;
import ai.magicdb.server.web.api.controller.task.biz.TaskBizService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@ConnectionInfoAspect
@RequestMapping("/api/export")
@Controller
@Slf4j
public class ExportController {

    @Autowired
    private TaskBizService taskBizService;


    /**
     * export data
     *
     * @param request
     * @return
     */
    @PostMapping("/export_data")
    public DataResult<Long> export(@Valid @RequestBody DataExportRequest request) {
        return taskBizService.exportResultData(request);
    }

    @PostMapping("/export_doc")
    public DataResult<Long> exportDoc(@Valid @RequestBody DataExportRequest request) {
        return taskBizService.exportSchemaDoc(request);
    }


}
