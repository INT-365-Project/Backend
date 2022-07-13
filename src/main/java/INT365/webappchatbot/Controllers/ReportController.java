package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Constants.ModelConstant;
import INT365.webappchatbot.Models.ResponseModel;
import INT365.webappchatbot.Models.req.ReportRequest;
import INT365.webappchatbot.Services.JwtUserDetailService;
import INT365.webappchatbot.Services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// @CrossOrigin(value = "http://20.92.229.38", allowedHeaders = "*")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    JwtUserDetailService userDetailService;
    @Autowired
    private ReportService reportService;

    @GetMapping("")
    public ResponseModel<Object> getReports() {
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.reportService.getReports())
                .build();
    }

    @GetMapping("/")
    public ResponseModel<Object> getReportById(@RequestParam("reportId") Long reportId) {
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.reportService.getReportById(reportId))
                .build();
    }

    @PostMapping("/createReport")
    public ResponseModel<Object> createReport(@RequestBody ReportRequest request) {
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.reportService.createReport(request))
                .build();
    }
}
