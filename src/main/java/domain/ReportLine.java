package domain;

import java.time.Instant;
import java.util.Objects;

public class ReportLine {
    private long id;
    private long reportId;
    private MeasurementParam param;
    private double value;
    private String unit;
    private Instant createdAt;
    private Instant updatedAt;

    public ReportLine() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getReportId() { return reportId; }
    public void setReportId(long reportId) { this.reportId = reportId; }

    public MeasurementParam getParam() { return param; }
    public void setParam(MeasurementParam param) { this.param = param; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportLine that = (ReportLine) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}