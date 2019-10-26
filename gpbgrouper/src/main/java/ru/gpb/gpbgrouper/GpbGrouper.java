package ru.gpb.gpbgrouper;

import ru.gpb.common.GpbValidateStatus;

public class GpbGrouper {

    public static void main(String... args) {

        final GbpFileGrouper fileGrouper;
        GpbValidateStatus validateStatus;

        fileGrouper = new GbpFileGrouper(args);
        validateStatus = fileGrouper.validate();

        if (validateStatus != GpbValidateStatus.OK) {
            System.out.println("Invalid arguments: " + validateStatus);
            usage();
            System.exit(validateStatus.getCode());
        }

        validateStatus = fileGrouper.generate();
        System.exit(validateStatus.getCode());
    }

    private static void usage() {

        System.out.println("Usage: dbpgrouper.jar stats-dates.txt stats-offices.txt ops1.txt ops2.txt ops3.txt");
    }
}
