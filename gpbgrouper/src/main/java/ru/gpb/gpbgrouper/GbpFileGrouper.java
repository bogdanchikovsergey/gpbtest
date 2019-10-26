package ru.gpb.gpbgrouper;

import ru.gpb.common.CsvRecord;
import ru.gpb.common.GbpConstants;
import ru.gpb.common.GpbValidateStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;
import static ru.gpb.common.GbpConstants.*;
import static ru.gpb.common.GpbValidateStatus.*;

class GbpFileGrouper {

    private static final int ARGS_NUMBER = 5;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STRING);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);

    private String inFileName1;
    private String inFileName2;
    private String inFileName3;
    private String datesFileName;
    private String officesFileName;

    private Map<Long, BigDecimal> dateStatsMap;
    private Map<String, BigDecimal> officeToSumStatsMap;

    private final DecimalFormat decimalFormat;

    private String[] args;

    public Map<Long, BigDecimal> getDateStatsMap() {
        return dateStatsMap;
    }

    public Map<String, BigDecimal> getOfficeToSumStatsMap() {
        return officeToSumStatsMap;
    }

    GbpFileGrouper(String... args) {
        this.args = args;
        dateStatsMap = new TreeMap<>();
        officeToSumStatsMap = new LinkedHashMap<>();
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalFormatSymbols.setDecimalSeparator(DECIMAL_DELIMETER);
        decimalFormat = new DecimalFormat(NUMBER_FORMAT_STRING, decimalFormatSymbols);
    }

    GpbValidateStatus validate() {

        if (args == null || args.length != ARGS_NUMBER) {
            return INVALID_ARGUMENTS;
        }

        datesFileName = args[0];
        officesFileName = args[1];
        inFileName1 = args[2];
        inFileName2 = args[3];
        inFileName3 = args[4];

        if (!Files.exists(Paths.get(inFileName1))
                || !Files.exists(Paths.get(inFileName2))
                || !Files.exists(Paths.get(inFileName3))) {
            return INVALID_INPUT_FILENAME;
        }

        return OK;
    }

    GpbValidateStatus generate() {

        GpbValidateStatus status = generateStats();
        if(status != OK) {
            return status;
        }

        try (PrintWriter out = new PrintWriter(datesFileName)) {
            dateStatsMap.forEach((k, v) -> out.println(DATE_FORMATTER.format(LocalDate.ofEpochDay(k)) + CSV_DELIMETER + decimalFormat.format(v)));
        } catch (FileNotFoundException e) {
            return ERROR_WRITING_FILE;
        }

        try (PrintWriter out = new PrintWriter(officesFileName)) {
            officeToSumStatsMap.forEach((k, v) -> out.println(k + CSV_DELIMETER + decimalFormat.format(v)));
        } catch (FileNotFoundException e) {
            return ERROR_WRITING_FILE;
        }

        return OK;
    }

    GpbValidateStatus generateStats() {

        try {
            generateStatsForFile(inFileName1);
            generateStatsForFile(inFileName2);
            generateStatsForFile(inFileName3);
        } catch (IOException e) {
            return INVALID_INPUT_FILENAME;
        }

        officeToSumStatsMap = officeToSumStatsMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue, reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return OK;
    }

    private void generateStatsForFile(String filename) throws IOException {

        Files.lines(Paths.get(filename)).forEach(this::updateStats);
    }

    private void updateStats(String csvRecordString) {

        CsvRecord csvRecord = new CsvRecord(csvRecordString);
        String office = csvRecord.office;
        long epochDay = LocalDate.from(DATE_TIME_FORMATTER.parse(csvRecord.date)).toEpochDay();

        BigDecimal sum = new BigDecimal(csvRecord.sum.replace(GbpConstants.DECIMAL_DELIMETER, '.')).setScale(2, RoundingMode.CEILING);
        BigDecimal totalDaySum = dateStatsMap.getOrDefault(epochDay, new BigDecimal(0).setScale(2, RoundingMode.CEILING));
        dateStatsMap.put(epochDay, totalDaySum.add(sum));

        BigDecimal totalOfficeSum = officeToSumStatsMap.getOrDefault(office, new BigDecimal(0).setScale(2, RoundingMode.CEILING));
        officeToSumStatsMap.put(office, totalOfficeSum.add(sum));
    }
}
