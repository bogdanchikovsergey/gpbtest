package ru.gpb.gpbgrouper;

import org.junit.jupiter.api.Test;
import ru.gpb.common.GpbValidateStatus;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

class GbpFileGrouperTest {

    @Test
    void testValidate() {

        GbpFileGrouper grouper;
        GpbValidateStatus status;

        grouper = new GbpFileGrouper("wat");
        status = grouper.validate();
        assert status == GpbValidateStatus.INVALID_ARGUMENTS;

        grouper = new GbpFileGrouper("wat", "wot", "aaa", "bbb", "ccc");
        status = grouper.validate();
        assert status == GpbValidateStatus.INVALID_INPUT_FILENAME;
    }

    @Test
    void testGenerateStats() {

        GpbValidateStatus status;
        URL url;

        url = getClass().getResource("/test-data1.txt");
        String dataFile1 = url.getPath().replaceAll("/(.:)", "$1");    // fix for leading slash in Windows' paths e.g. /C:/blablabla
        url = getClass().getResource("/test-data2.txt");
        String dataFile2 = url.getPath().replaceAll("/(.:)", "$1");
        url = getClass().getResource("/test-data3.txt");
        String dataFile3 = url.getPath().replaceAll("/(.:)", "$1");

        GbpFileGrouper grouper = new GbpFileGrouper("aaa", "bbb", dataFile1, dataFile2, dataFile3);
        status = grouper.validate();
        assert status == GpbValidateStatus.OK;

        status = grouper.generateStats();
        assert status == GpbValidateStatus.OK;

        Map<Long, BigDecimal> dateStatMap = grouper.getDateStatsMap();

        assert dateStatMap.get(17555L).doubleValue() == 0;
        assert dateStatMap.get(17557L).doubleValue() == 1.0D;
        assert dateStatMap.get(17660L).doubleValue() == 233.48;
        assert dateStatMap.get(17663L).doubleValue() == 2.12;
        assert dateStatMap.get(17715L).doubleValue() == 123;
        assert dateStatMap.get(17773L).doubleValue() == 801;
        assert dateStatMap.get(17792L).doubleValue() == 10.5;
        assert dateStatMap.get(17867L).doubleValue() == 2.12;

        assert dateStatMap instanceof TreeMap;

        Long firstKey = ((TreeMap<Long, BigDecimal>) dateStatMap).navigableKeySet().pollFirst();
        Long lastKey = ((TreeMap<Long, BigDecimal>) dateStatMap).navigableKeySet().pollLast();

        assert firstKey != null && firstKey == 17555L;
        assert lastKey != null && lastKey == 17867L;

        Map<String, BigDecimal> officeToSumStatsMap = grouper.getOfficeToSumStatsMap();

        assert officeToSumStatsMap.get("ENO").doubleValue() == 801;
        assert officeToSumStatsMap.get("BOWIE").doubleValue() == 243.98;
        assert officeToSumStatsMap.get("FRIPP").doubleValue() == 124;
        assert officeToSumStatsMap.get("IGGY").doubleValue() == 2.12;
        assert officeToSumStatsMap.get("CURTIS").doubleValue() == 2.12;

        assert officeToSumStatsMap instanceof LinkedHashMap;

        List<String> officeList = new ArrayList<>();
        List<Double> sumList = new ArrayList<>();

        officeToSumStatsMap.forEach((k, v) -> {
            officeList.add(k);
            sumList.add(v.doubleValue());
        });

        assert officeList.size() == 5;
        assert officeList.get(0).equals("ENO");
        assert officeList.get(1).equals("BOWIE");
        assert officeList.get(2).equals("FRIPP");
        assert officeList.get(3).equals("IGGY");
        assert officeList.get(4).equals("CURTIS");

        assert sumList.size() == 5;
        assert sumList.get(0) == 801;
        assert sumList.get(1) == 243.98;
        assert sumList.get(2) == 124;
        assert sumList.get(3) == 2.12;
        assert sumList.get(4) == 2.12;
    }
}