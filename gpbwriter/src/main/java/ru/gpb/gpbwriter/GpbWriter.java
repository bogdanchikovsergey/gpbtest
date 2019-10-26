package ru.gpb.gpbwriter;

import ru.gpb.common.GpbValidateStatus;

import java.time.Instant;

public class GpbWriter {

    public static void main(String... args) {

        final GbpFileGenerator fileGenerator;
        GpbValidateStatus validateStatus;
        final Instant runInstant = Instant.now();

        fileGenerator = new GbpFileGenerator(runInstant, args);
        validateStatus = fileGenerator.validate();

        if (validateStatus != GpbValidateStatus.OK) {
            System.out.println("Invalid arguments: " + validateStatus);
            usage();
            System.exit(validateStatus.getCode());
        }

        validateStatus = fileGenerator.generate();
        System.exit(validateStatus.getCode());
    }

    private static void usage() {

        System.out.println("Usage: gpbex.jar offices_filename records_number outfile1 outfile2 outfile3");
    }
}
