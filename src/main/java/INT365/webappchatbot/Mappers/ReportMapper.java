package INT365.webappchatbot.Mappers;

import INT365.webappchatbot.Entities.Report;
import INT365.webappchatbot.Models.req.ReportRequest;
import INT365.webappchatbot.Models.resp.ReportResponse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;

@Mapper
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    @IterableMapping(elementTargetType = ReportResponse.class)
    List<ReportResponse> createReportListResponse(List<Report> reportList);

    ReportResponse createReportResponse(Report report);

    @Mappings({
            @Mapping(source = "request.reportId", target = "reportId"),
            @Mapping(source = "request.topic", target = "topic"),
            @Mapping(source = "request.description", target = "description"),
            @Mapping(source = "date", target = "createDate"),
    })
    Report createReport(ReportRequest request, Date date);
}
