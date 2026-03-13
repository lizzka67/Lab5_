package src;

import domain.MeasurementParam;
import domain.Report;
import domain.ReportLine;
import service.ReportService;
import validation.ValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final ReportService service = new ReportService();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Система управления отчетами (Domain 6). Введите help для списка команд.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split(" ");
            String command = parts[0];

            try {
                switch (command) {
                    case "exit":
                        System.out.println("Выход из программы...");
                        return;

                    case "help":
                        System.out.println("Команды: rep_create_sample, rep_addline, rep_list, rep_show, rep_lines, rep_updateline, rep_delline, rep_finalize, rep_sign, rep_export");
                        break;

                    case "rep_create_sample":
                        if (parts.length < 3) throw new ValidationException("Ошибка: укажите sample_id и username");
                        long sampleId = Long.parseLong(parts[1]);
                        String username = parts[2];
                        System.out.print("Название отчёта: ");
                        String name = scanner.nextLine().trim();

                        Report report = service.createSampleReport(sampleId, name, username);
                        System.out.println("OK report_id=" + report.getId());
                        break;

                    case "rep_addline":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdAdd = Long.parseLong(parts[1]);

                        System.out.print("Параметр (PH/CONDUCTIVITY/TURBIDITY/NITRATE): ");
                        String paramStr = scanner.nextLine().trim().toUpperCase();
                        MeasurementParam param = MeasurementParam.valueOf(paramStr);

                        System.out.print("Значение: ");
                        double value = Double.parseDouble(scanner.nextLine().trim());

                        System.out.print("Единицы: ");
                        String unit = scanner.nextLine().trim();

                        ReportLine line = service.addReportLine(reportIdAdd, param, value, unit);
                        System.out.println("OK line_id=" + line.getId());
                        break;

                    case "rep_list":
                        System.out.println("ID Name Status");
                        for (Report r : service.getAllReports()) {
                            System.out.println(r.getId() +" "+ r.getName() +" "+ r.getStatus());
                        }
                        break;

                    case "rep_show":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdShow = Long.parseLong(parts[1]);
                        Report rShow = service.getReportById(reportIdShow);
                        int linesCount = service.getLinesByReportId(reportIdShow).size();

                        System.out.println("Report #" + rShow.getId());
                        System.out.println("name: " + rShow.getName());
                        System.out.println("status: " + rShow.getStatus());
                        System.out.println("lines: " + linesCount);
                        if (rShow.getSignedBy() != null) System.out.println("signed by: " + rShow.getSignedBy());
                        break;

                    case "rep_lines":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdLines = Long.parseLong(parts[1]);
                        Set<ReportLine> lines = service.getLinesByReportId(reportIdLines);

                        System.out.println("ID Param Value Unit");
                        for (ReportLine l : lines) {
                            System.out.println(l.getId() +" "+ l.getParam() +" "+ l.getValue() +" "+ l.getUnit());
                        }
                        break;

                    case "rep_updateline":
                        if (parts.length < 3) throw new ValidationException("Ошибка: укажите line_id и 1 поле (например, value=7.10)");
                        long lineIdUpdate = Long.parseLong(parts[1]);
                        String[] part = parts[2].split("=");
                        String field = part[0];
                        MeasurementParam newParam = null;
                        Double newValue = null;
                        String newUnit = null;

                        if (field.equals("param")) {
                            newParam = MeasurementParam.valueOf(parts[1].toUpperCase());
                        } else if (field.equals("value")) {
                            newValue = Double.parseDouble(parts[1]);
                        } else if (field.equals("unit")) {
                            newUnit = parts[1];
                        }

                        service.updateReportLine(lineIdUpdate, newParam, newValue, newUnit);
                        System.out.println("OK");
                        break;

                    case "rep_delline":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите line_id");
                        long lineIdDel = Long.parseLong(parts[1]);
                        service.deleteReportLine(lineIdDel);
                        System.out.println("OK deleted");
                        break;
                    case "rep_finalize":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdFinal = Long.parseLong(parts[1]);
                        service.finalizeReport(reportIdFinal);
                        System.out.println("OK report " + reportIdFinal + " FINAL");
                        break;

                    case "rep_sign":
                        if (parts.length < 3) throw new ValidationException("Ошибка: укажите report_id и username");
                        long reportIdSign = Long.parseLong(parts[1]);
                        String signer = parts[2];
                        service.signReport(reportIdSign, signer);
                        System.out.println("OK report " + reportIdSign + " SIGNED by " + signer);
                        break;

                    case "rep_export":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdExport = Long.parseLong(parts[1]);

                        Report reportExport = service.getReportById(reportIdExport);

                        System.out.println("ID: " + reportExport.getId());
                        System.out.println("Название: " + reportExport.getName());
                        System.out.println("ID пробы (sampleId): " + reportExport.getSampleId());
                        System.out.println("ID эксперимента (experimentId): " + reportExport.getExperimentId());
                        System.out.println("Статус: " + reportExport.getStatus());
                        System.out.println("Владелец: " + reportExport.getOwnerUsername());
                        System.out.println("Подписант: " + (reportExport.getSignedBy() != null ? reportExport.getSignedBy() : "не подписан"));
                        System.out.println("Создан: " + reportExport.getCreatedAt());
                        System.out.println("Обновлен: " + reportExport.getUpdatedAt());

                        Set<ReportLine> linesExport = service.getLinesByReportId(reportIdExport);
                        System.out.println("Строки отчета");
                        if (linesExport.isEmpty()) {
                            System.out.println("Строк отчета нет");
                        } else {
                            System.out.println("ID Параметр Значение Единицы");
                            for (ReportLine lineExport : linesExport) {
                                System.out.println(lineExport.getId() +" "+ lineExport.getParam() +" "+ lineExport.getValue() +" "+ lineExport.getUnit());
                            }
                        }
                        break;

                    default:
                        System.out.println("Ошибка: неизвестная команда.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID и значения должны быть числами!");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверное значение параметра. Используйте PH, CONDUCTIVITY, TURBIDITY или NITRATE.");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Критическая ошибка: " + e.getMessage());
            }
        }
    }

}
