package ru.gpb.gpbgrouper;

import org.junit.jupiter.api.Test;
import ru.gpb.common.GpbValidateStatus;

class GbpFileGrouperTest {

    @Test
    void testValidate() {

        GbpFileGrouper gbpFileGrouper;
        GpbValidateStatus status;

        gbpFileGrouper = new GbpFileGrouper("wat");
        status = gbpFileGrouper.validate();
        assert status == GpbValidateStatus.INVALID_ARGUMENTS;

        gbpFileGrouper = new GbpFileGrouper("wat", "wot", "aaa", "bbb", "ccc");
        status = gbpFileGrouper.validate();
        assert status == GpbValidateStatus.INVALID_INPUT_FILENAME;
    }
}