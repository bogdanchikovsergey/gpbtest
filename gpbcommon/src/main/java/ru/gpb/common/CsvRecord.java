package ru.gpb.common;

import static ru.gpb.common.GbpConstants.CSV_DELIMETER;

public class CsvRecord {

    public String date;
    public String office;
    public int index;
    public String sum;

    public CsvRecord(String date, String office, int index, String sum) {
        this.date = date;
        this.office = office;
        this.index = index;
        this.sum = sum;
    }

    public CsvRecord(String csvString) {

        String[] fields = csvString.split(CSV_DELIMETER);
        this.date = fields[0];
        this.office = fields[1];
        this.index = Integer.parseInt(fields[2]);
        this.sum = fields[3];
    }

    public String format() {
        return this.date +
                CSV_DELIMETER +
                this.office +
                CSV_DELIMETER +
                this.index +
                CSV_DELIMETER +
                this.sum;
    }
}
