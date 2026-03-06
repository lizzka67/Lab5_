package validation;

import domain.Report;
import domain.ReportLine;

public class Validator {

    public static void validateReport(Report report) {
        if (report.getName() == null || report.getName().trim().isEmpty()) {
            throw new validation.ValidationException("Ошибка: Название отчета не может быть пустым.");
        }
        if (report.getName().length() > 128) {
            throw new validation.ValidationException("Ошибка: Название отчета слишком длинное (макс. 128 символов).");
        }
    }

    public static void validateReportLine(ReportLine line) {
        if (line.getUnit() == null || line.getUnit().trim().isEmpty()) {
            throw new validation.ValidationException("Ошибка: Единицы измерения не могут быть пустыми.");
        }
        if (line.getUnit().length() > 16) {
            throw new validation.ValidationException("Ошибка: Название единиц измерения слишком длинное (макс. 16 символов).");
        }
    }
}