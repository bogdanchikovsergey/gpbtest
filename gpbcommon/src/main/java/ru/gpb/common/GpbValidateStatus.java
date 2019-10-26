package ru.gpb.common;

public enum GpbValidateStatus {

    OK(0),
    INVALID_ARGUMENTS(-101),
    INVALID_INPUT_FILENAME(-103),
    INVALID_RECORDS_NUMBER(-104),
    ERROR_WRITING_FILE(-105);

    private final int code;

    GpbValidateStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
