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

            String[] parts = input.split("\\s+");
            String command = parts[0];

            try {
                switch (command) {
                    case "exit":
                        System.out.println("Выход из программы...");
                        return;

                    case "help":
                        System.out.println("Команды: rep_create_sample, rep_addline, rep_list, rep_show, rep_lines, rep_updateline, rep_delline, rep_finalize, rep_sign, rep_export");
                        break;

                    // 1) rep_create_sample <sample_id>
                    case "rep_create_sample":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите sample_id");
                        long sampleId = Long.parseLong(parts[1]);

                        System.out.print("Название отчёта: ");
                        String name = scanner.nextLine().trim();

                        Report report = service.createSampleReport(sampleId, name);
                        System.out.println("OK report_id=" + report.getId());
                        break;

                    // 2) rep_addline <report_id>
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

                    // 3) rep_list [--status DRAFT|FINAL|SIGNED]
                    case "rep_list":
                        Map<String, String> listArgs = parseArgs(parts, 1);
                        String statusFilter = listArgs.get("status");

                        System.out.println("ID  Name              Status");
                        for (Report r : service.getAllReports()) {
                            if (statusFilter != null && !r.getStatus().name().equalsIgnoreCase(statusFilter)) {
                                continue;
                            }
                            System.out.printf("%-3d %-17s %s\n", r.getId(), r.getName(), r.getStatus());
                        }
                        break;

                    // 4) rep_show <report_id>
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

                    // 5) rep_lines <report_id>
                    case "rep_lines":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdLines = Long.parseLong(parts[1]);
                        Set<ReportLine> lines = service.getLinesByReportId(reportIdLines);

                        System.out.println("ID  Param          Value  Unit");
                        for (ReportLine l : lines) {
                            System.out.printf("%-3d %-14s %-6.2f %s\n", l.getId(), l.getParam(), l.getValue(), l.getUnit());
                        }
                        break;

                    // 6) rep_updateline <line_id> field=value ...
                    case "rep_updateline":
                        if (parts.length < 3) throw new ValidationException("Ошибка: укажите line_id и поля (например, value=7.10)");
                        long lineIdUpdate = Long.parseLong(parts[1]);
                        Map<String, String> updateArgs = parseArgs(parts, 2);

                        MeasurementParam newParam = updateArgs.containsKey("param") ? MeasurementParam.valueOf(updateArgs.get("param").toUpperCase()) : null;
                        Double newValue = updateArgs.containsKey("value") ? Double.parseDouble(updateArgs.get("value")) : null;
                        String newUnit = updateArgs.get("unit");

                        service.updateReportLine(lineIdUpdate, newParam, newValue, newUnit);
                        System.out.println("OK");
                        break;

                    // 7) rep_delline <line_id>
                    case "rep_delline":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите line_id");
                        long lineIdDel = Long.parseLong(parts[1]);
                        service.deleteReportLine(lineIdDel);
                        System.out.println("OK deleted");
                        break;

                    // 8) rep_finalize <report_id>
                    case "rep_finalize":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdFinal = Long.parseLong(parts[1]);
                        service.finalizeReport(reportIdFinal);
                        System.out.println("OK report " + reportIdFinal + " FINAL");
                        break;

                    // 9) rep_sign <report_id>
                    case "rep_sign":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdSign = Long.parseLong(parts[1]);
                        String username = "yarus"; // имитируем текущего пользователя
                        service.signReport(reportIdSign, username);
                        System.out.println("OK report " + reportIdSign + " SIGNED by " + username);
                        break;

                    // 10) rep_export <report_id>
                    case "rep_export":
                        if (parts.length < 2) throw new ValidationException("Ошибка: укажите report_id");
                        long reportIdExport = Long.parseLong(parts[1]);
                        service.getReportById(reportIdExport); // Проверка существования
                        System.out.println("Report exported (text)");
                        break;

                    default:
                        System.out.println("Ошибка: неизвестная команда.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID и значения должны быть числами!");
            } catch (IllegalArgumentException e) {
                // Перехват ошибки из Enum.valueOf() если ввели плохой MeasurementParam
                System.out.println("Ошибка: неверное значение параметра. Используйте PH, CONDUCTIVITY, TURBIDITY или NITRATE.");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Критическая ошибка: " + e.getMessage());
            }
        }
    }

    // Вспомогательный метод для парсинга аргументов вида field=value и флагов --status DRAFT
    private static Map<String, String> parseArgs(String[] parts, int startIndex) {
        Map<String, String> args = new HashMap<>();
        for (int i = startIndex; i < parts.length; i++) {
            String part = parts[i];
            if (part.contains("=")) {
                String[] split = part.split("=", 2);
                if (split.length == 2) {
                    args.put(split[0], split[1].replace("\"", ""));
                }
            } else if (part.startsWith("--") && i + 1 < parts.length) {
                args.put(part.substring(2), parts[i + 1]);
                i++;
            }
        }
        return args;
    }
}
