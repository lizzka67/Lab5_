package Exceptions;

import domain.ReportStatus;

public class InvalidReportStatusException extends ReportException {
    private final ReportStatus expectedStatus;
    private final ReportStatus actualStatus;

    public InvalidReportStatusException(String message, ReportStatus expected, ReportStatus actual) {
        super(message);
        this.expectedStatus = expected;
        this.actualStatus = actual;
    }

    public ReportStatus getExpectedStatus() {
        return expectedStatus;
    }

    public ReportStatus getActualStatus() {
        return actualStatus;
    }
}