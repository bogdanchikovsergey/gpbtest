package ru.gpbex;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.gpbex.GpbValidateStatus.*;

class GbpFileGenerator {

    private static final int ARGS_NUMBER = 5;
    private static final int SECONDS_IN_DAY = 86400;
    private static final int DAYS_IN_YEAR = 365;
    private static final int DAYS_IN_LEAP_YEAR = 366;
    private static final String DATE_FORMAT_STRING = "dd.MM.yyyy HH:mm:ss";
    private static final String NUMBER_FORMAT_STRING = "##0.00";
    private static final char DELIMETER = ';';
    private static final char DECIMAL_DELIMETER = ',';
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);
    static final BigDecimal START_SUM = new BigDecimal(10000.12).setScale(2, RoundingMode.CEILING);
    static final BigDecimal END_SUM = new BigDecimal(100000.50).setScale(2, RoundingMode.CEILING);

    private final DecimalFormat decimalFormat;
    private String[] args;
    private Instant runInstant;
    private int recordsNumber;
    private String outFileName1;
    private String outFileName2;
    private String outFileName3;
    private ZonedDateTime initialZDT;

    private List<String> officesList;

    GbpFileGenerator(Instant runInstant, String... args) {
        this.runInstant = runInstant;
        this.args = args;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalFormatSymbols.setDecimalSeparator(DECIMAL_DELIMETER);
        decimalFormat = new DecimalFormat(NUMBER_FORMAT_STRING, decimalFormatSymbols);
    }

    GpbValidateStatus validate() {

        if (args == null || args.length != ARGS_NUMBER) {
            return INVALID_ARGUMENTS;
        }

        String officesFileName = args[0];

        try {
            recordsNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return INVALID_RECORDS_NUMBER;
        }
        if (recordsNumber <= 0) {
            return INVALID_RECORDS_NUMBER;
        }

        outFileName1 = args[2];
        outFileName2 = args[3];
        outFileName3 = args[4];

        try {
            officesList = Files.readAllLines(Paths.get(officesFileName), UTF_8);
        } catch (IOException e) {
            return INVALID_OFFICES_FILENAME;
        }

        initialZDT = ZonedDateTime.ofInstant(runInstant, ZoneId.systemDefault())
                .truncatedTo(ChronoUnit.DAYS)
                .withDayOfYear(1)
                .minus(1, ChronoUnit.YEARS);

        return OK;
    }

    GpbValidateStatus generate() {

        Random randomNumber = new Random();
        int size1 = randomNumber.nextInt(recordsNumber);
        int size2 = randomNumber.nextInt(recordsNumber - size1);
        int size3 = recordsNumber - size1 - size2;

        try {
            generateToFile(outFileName1, size1);
            generateToFile(outFileName2, size2);
            generateToFile(outFileName3, size3);
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_WRITING_FILE;
        }

        return OK;
    }

    private void generateToFile(String filename, int recordsCount) throws IOException {

        String csvString;
        Random randomGenerator = new Random();

        boolean isLeapYear = Year.of(initialZDT.getYear()).isLeap();
        int daysInYear = isLeapYear ? DAYS_IN_LEAP_YEAR : DAYS_IN_YEAR;
        int sumLength = getSumLength();

        try (PrintWriter out = new PrintWriter(filename)) {

            for (int recordIndex = 1; recordIndex < recordsCount + 1; recordIndex++) {
                csvString = generateCsvString(recordIndex, daysInYear, sumLength, randomGenerator);
                out.println(csvString);
            }
        }
    }

    int getSumLength() {
        return END_SUM.subtract(START_SUM).multiply(BigDecimal.valueOf(100)).intValue();
    }

    String generateCsvString(int recordIndex, int daysInYear, int sumLength, Random randomGenerator) {

        ZonedDateTime zdt;
        String officeName;
        String date;
        String sum;
        String csvString;

        zdt = initialZDT.plus(randomGenerator.nextInt(SECONDS_IN_DAY * daysInYear), ChronoUnit.SECONDS);
        officeName = officesList.get(randomGenerator.nextInt(officesList.size()));
        date = DATE_TIME_FORMATTER.format(zdt);
        sum = decimalFormat.format(START_SUM.add(new BigDecimal((double) randomGenerator.nextInt(sumLength) / 100).setScale(2, RoundingMode.CEILING)));
        csvString = date + DELIMETER + officeName + DELIMETER + recordIndex + DELIMETER + sum;
        return csvString;
    }
}
