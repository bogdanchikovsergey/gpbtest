package ru.gpb.gpbwriter;

import org.junit.jupiter.api.Test;
import ru.gpb.common.CsvRecord;
import ru.gpb.common.GpbValidateStatus;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Random;

import static ru.gpb.common.GbpConstants.CSV_DELIMETER;
import static ru.gpb.common.GbpConstants.DECIMAL_DELIMETER;

class GbpFileGeneratorTest {

    @Test
    void testValidate() {

        GbpFileGenerator gbpFileGenerator;
        GpbValidateStatus status;

        gbpFileGenerator = new GbpFileGenerator(Instant.now());
        status = gbpFileGenerator.validate();
        assert status == GpbValidateStatus.INVALID_ARGUMENTS;

        gbpFileGenerator = new GbpFileGenerator(Instant.now(), "wat");
        status = gbpFileGenerator.validate();
        assert status == GpbValidateStatus.INVALID_ARGUMENTS;

        gbpFileGenerator = new GbpFileGenerator(Instant.now(), "wat", "wot", "aaa", "bbb", "ccc");
        status = gbpFileGenerator.validate();
        assert status == GpbValidateStatus.INVALID_RECORDS_NUMBER;

        gbpFileGenerator = new GbpFileGenerator(Instant.now(), "wat", "-123", "aaa", "bbb", "ccc");
        status = gbpFileGenerator.validate();
        assert status == GpbValidateStatus.INVALID_RECORDS_NUMBER;

        gbpFileGenerator = new GbpFileGenerator(Instant.now(), "wat", "10", "aaa", "bbb", "ccc");
        status = gbpFileGenerator.validate();
        assert status == GpbValidateStatus.INVALID_INPUT_FILENAME;
    }

    @Test
    void testGenerateCsvString() {

        URL url = getClass().getResource("/test-offices.txt");
        String officeFilePath = url.getPath().replaceAll("/(.:)", "$1");    // fix for leading slash in Windows' paths e.g. /C:/blablabla
        Instant nowInstant = Instant.now();
        GbpFileGenerator gbpFileGenerator = new GbpFileGenerator(nowInstant, officeFilePath, "10", "aaa", "bbb", "ccc");
        GpbValidateStatus status = gbpFileGenerator.validate();
        assert status == GpbValidateStatus.OK;

        int sumLength = gbpFileGenerator.getSumLength();
        int recordIndex = 123;

        String generatedCsvString = gbpFileGenerator.generateCsvString(recordIndex, 365, sumLength, new Random());
        CsvRecord csvRecord = new CsvRecord(generatedCsvString);

        String date = csvRecord.date;
        assert GbpFileGenerator.DATE_TIME_FORMATTER.parse(date).get(ChronoField.YEAR) == nowInstant.atZone(ZoneId.systemDefault()).getYear() - 1;

        int csvRecordIndex = csvRecord.index;
        assert csvRecordIndex == recordIndex;

        String sumString = csvRecord.sum;
        double sum = Double.parseDouble(sumString.replace(DECIMAL_DELIMETER, '.'));
        assert sum >= GbpFileGenerator.START_SUM.doubleValue() && sum <= GbpFileGenerator.END_SUM.doubleValue();
   }
}