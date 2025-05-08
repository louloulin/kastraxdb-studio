package ai.magicdb.server.web.api.controller.dashboard.converter;

import java.util.List;

import ai.magicdb.server.domain.api.model.Chart;
import ai.magicdb.server.domain.api.chart.ChartCreateParam;
import ai.magicdb.server.domain.api.chart.ChartUpdateParam;
import ai.magicdb.server.web.api.controller.dashboard.request.ChartCreateRequest;
import ai.magicdb.server.web.api.controller.dashboard.request.ChartUpdateRequest;
import ai.magicdb.server.web.api.controller.dashboard.vo.ChartVO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author moji
 * @version ChartWebConverter.java, v 0.1 June 9, 2023 15:46 moji Exp $
 * @date 2023/06/09
 */
@Mapper(componentModel = "spring")
public abstract class ChartWebConverter {

    /**
     * Model conversion
     *
     * @param chart
     * @return
     */
    @Mappings({
        @Mapping(target = "connectable", expression = "java(chart.getDataSourceName() != null)"),
    })
    public abstract ChartVO model2vo(Chart chart);

    /**
     * Model conversion
     *
     * @param charts
     * @return
     */
    public abstract List<ChartVO> model2vo(List<Chart> charts);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract ChartCreateParam req2param(ChartCreateRequest request);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract ChartUpdateParam req2updateParam(ChartUpdateRequest request);
}
