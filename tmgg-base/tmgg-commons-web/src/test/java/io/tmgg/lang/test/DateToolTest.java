package io.tmgg.lang.test;

import io.tmgg.lang.DateTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class DateToolTest {


    @Test
    public void testBetween(){
        Assertions.assertEquals(true, DateTool.isBetween("2024-11-01", "2024-10-01", "2024-12-01"));
        Assertions.assertEquals(true, DateTool.isBetween("2024-11-01", "2024-11-01", "2024-12-01"));
        Assertions.assertEquals(true, DateTool.isBetween("2024-12-01", "2024-11-01", "2024-12-01"));
        Assertions.assertEquals(false, DateTool.isBetween("2025-12-01", "2024-11-01", "2024-12-01"));
        Assertions.assertEquals(false, DateTool.isBetween("2023-12-01", "2024-11-01", "2024-12-01"));
        Assertions.assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                DateTool.isBetween("2023-12-01 00:00:00", "2024-11-01", "2024-12-01");
            }
        });

        Assertions.assertThrows(Exception.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                DateTool.isBetween(null, "2024-11-01", "2024-12-01");
            }
        });
    }
}
