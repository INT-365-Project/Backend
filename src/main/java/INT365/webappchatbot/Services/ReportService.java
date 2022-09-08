package INT365.webappchatbot.Services;

import INT365.webappchatbot.Mappers.ReportMapper;
import INT365.webappchatbot.Models.req.ReportRequest;
import INT365.webappchatbot.Models.resp.ReportResponse;
import INT365.webappchatbot.Repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public List<ReportResponse> getReports() {
        return ReportMapper.INSTANCE.createReportListResponse(this.reportRepository.findAll());
    }

    public ReportResponse getReportById(Long reportId) {
        return ReportMapper.INSTANCE.createReportResponse(this.reportRepository.findById(reportId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no id - " + reportId)));
    }

    @Transactional
    public Map<String, Long> createReport(ReportRequest request) {
        if (request.getReportId() > 0)
            this.reportRepository.findById(request.getReportId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no id - " + request.getReportId()));
        Long reportId = this.reportRepository.saveAndFlush(ReportMapper.INSTANCE.createReport(request, new Date())).getReportId();
        Map<String, Long> map = new HashMap<>();
        map.put("reportId", reportId);
        return map;
    }
}
