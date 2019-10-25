package ru.gpbex;

public enum GpbValidateStatus {

    OK(0),
    NOT_INITIALIZED(-101),
    WRONG_ARGUMENTS(-102),
    INVALID_OFFICES_FILENAME(-103),
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
