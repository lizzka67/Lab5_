package service;

import domain.MeasurementParam;
import domain.Report;
import domain.ReportLine;
import domain.ReportStatus;
import validation.ValidationException;

import validation.Validator;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class ReportService {

    private final Set<Report> reports = new HashSet<>();
    private final Set<ReportLine> reportLines = new HashSet<>();

    private long reportIdCounter = 1;
    private long lineIdCounter = 1;
    private final String currentUser = "SYSTEM"; 
    public Report createSampleReport(long sampleId, String name) {
        Report report = new Report();
        report.setId(reportIdCounter++);
        report.setName(name);
        report.setSampleId(sampleId);
        report.setStatus(ReportStatus.DRAFT);
        report.setOwnerUsername(currentUser);
        report.setCreatedAt(Instant.now());
        report.setUpdatedAt(Instant.now());

        Validator.validateReport(report);
        reports.add(report);
        return report;
    }

    public Set<Report> getAllReports() {
        return reports;
    }

    public Report getReportById(long reportId) {
        for (Report r : reports) {
            if (r.getId() == reportId) {
                return r;
            }
        }
        throw new validation.ValidationException("Ошибка: отчет с id=" + reportId + " не найден");
    }

    public ReportLine addReportLine(long reportId, MeasurementParam param, double value, String unit) {
        Report report = getReportById(reportId);

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new ValidationException("Ошибка: можно добавлять строки только в отчет со статусом DRAFT.");
        }

        ReportLine line = new ReportLine();
        line.setId(lineIdCounter++);
        line.setReportId(reportId);
        line.setParam(param);
        line.setValue(value);
        line.setUnit(unit);
        line.setCreatedAt(Instant.now());
        line.setUpdatedAt(Instant.now());

        Validator.validateReportLine(line);
        reportLines.add(line);

        report.setUpdatedAt(Instant.now());
        return line;
    }

    public Set<ReportLine> getLinesByReportId(long reportId) {
        getReportById(reportId);

        Set<ReportLine> result = new HashSet<>();
        for (ReportLine line : reportLines) {
            if (line.getReportId() == reportId) {
                result.add(line);
            }
        }
        return result;
    }

    public ReportLine getLineById(long lineId) {
        for (ReportLine line : reportLines) {
            if (line.getId() == lineId) {
                return line;
            }
        }
        throw new ValidationException("Ошибка: строка с id=" + lineId + " не найдена");
    }

    public void updateReportLine(long lineId, MeasurementParam newParam, Double newValue, String newUnit) {
        ReportLine line = getLineById(lineId);
        Report report = getReportById(line.getReportId());

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new validation.ValidationException("Ошибка: можно изменять строки только в отчете со статусом DRAFT.");
        }

        if (newParam != null) line.setParam(newParam);
        if (newValue != null) line.setValue(newValue);
        if (newUnit != null) line.setUnit(newUnit);

        line.setUpdatedAt(Instant.now());
        Validator.validateReportLine(line);
        report.setUpdatedAt(Instant.now());
    }

    public void deleteReportLine(long lineId) {
        ReportLine line = getLineById(lineId);
        Report report = getReportById(line.getReportId());

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new validation.ValidationException("Ошибка: можно удалять строки только в отчете со статусом DRAFT.");
        }

        reportLines.remove(line);
        report.setUpdatedAt(Instant.now());
    }

    public void finalizeReport(long reportId) {
        Report report = getReportById(reportId);
        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new validation.ValidationException("Ошибка: можно финализировать только DRAFT отчет.");
        }
        report.setStatus(ReportStatus.FINAL);
        report.setUpdatedAt(Instant.now());
    }

    public void signReport(long reportId, String username) {
        Report report = getReportById(reportId);
        if (report.getStatus() == ReportStatus.DRAFT) {
            throw new ValidationException("Ошибка: сначала сделайте finalize (отчет должен быть FINAL).");
        }
        if (report.getStatus() == ReportStatus.SIGNED) {
            throw new ValidationException("Ошибка: отчет уже подписан.");
        }
        report.setStatus(ReportStatus.SIGNED);
        report.setSignedBy(username);
        report.setUpdatedAt(Instant.now());
    }
}
