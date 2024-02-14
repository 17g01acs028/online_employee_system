package org.example.libs;

import org.example.controllers.Employees;
import org.example.controllers.Period;
import org.example.controllers.Transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Checker {

    public static Response period(Connection conn){

        return null;
    }

    public static void EmployeeStatus(Connection conn, String employee_id){

      Response user = Employees.FindById(conn, Integer.parseInt(employee_id));
//        System.out.println("Employment Date: "+);
//        System.out.println("Employment Period: "+);

        System.out.println(calculateMonthGap(Checker.reformatPeriod(user.getFieldValue("employment_date")),Checker.reformatPeriod(Period.Find(conn).getFieldValue("period"))));
//        Map<String, String> values = new HashMap<String, String>();
//        values.put("status","active");
//        Response r =  Employees.updateEmployee(conn,values,employee_id);
//        System.out.println(r);

        Transactions.updateStatus(conn,Integer.parseInt(employee_id));
    }

    public static Map<String,Double> salaryChecker(Connection connection, int employee_id) {
      double salary = 0;
        Map<String, Double> salaries = new TreeMap<>((o1, o2) -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
            try {
                Date date1 = dateFormat.parse(o1);
                Date date2 = dateFormat.parse(o2);
                return date1.compareTo(date2);
            } catch (Exception e) {
                // This should not happen if all dates are in the correct format
                throw new IllegalArgumentException("Invalid date format", e);
            }
        });

        try {
            // First, find the earning_type_id for "salary"
            String getEarningTypeIdSql = "SELECT earning_type_id FROM earning_type WHERE name = ?";
            try (PreparedStatement getEarningTypeIdStmt = connection.prepareStatement(getEarningTypeIdSql)) {
                getEarningTypeIdStmt.setString(1, "salary");
                try (ResultSet earningTypeRs = getEarningTypeIdStmt.executeQuery()) {

                    if (earningTypeRs.next()) {
                        int salaryEarningTypeId = earningTypeRs.getInt("earning_type_id");
                      // Now, check if there's an entry in the earnings table for the employee with this earning_type_id
                        String checkEarningsSql = "SELECT * FROM earnings WHERE employee_id = ? AND earning_type_id = ?";
                        try (PreparedStatement checkEarningsStmt = connection.prepareStatement(checkEarningsSql)) {
                            checkEarningsStmt.setInt(1, employee_id);
                            checkEarningsStmt.setInt(2, salaryEarningTypeId);

                            try (ResultSet earningsRs = checkEarningsStmt.executeQuery()) {
                                while (earningsRs.next()) {
                                    // If an entry exists, the employee has a salary.
                                    String period = Period.FindById(connection,earningsRs.getInt("period_id")).getFieldValue("period");
                                    Double salary_amount = earningsRs.getDouble("amount");
                                    salaries.put(period,salary_amount);
                                }
                            }
                        }


                    }
                }
            }
            return salaries;

        } catch (SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            // Handle exception (e.g., logging)
        }

        return  null;
    }

    public static ArrayList<String> lastEarningChecker(Connection connection, int employee_id, int period_id) {
        ArrayList<String> stringList = new ArrayList<>();
        try {
            // First, find the earning_type_id for "salary"
            String getEarningTypeIdSql = "SELECT earning_type_id FROM earning_type WHERE name = ?";
            try (PreparedStatement getEarningTypeIdStmt = connection.prepareStatement(getEarningTypeIdSql)) {
                getEarningTypeIdStmt.setString(1, "salary");
                try (ResultSet earningTypeRs = getEarningTypeIdStmt.executeQuery()) {

                    if (earningTypeRs.next()) {
                        int salaryEarningTypeId = earningTypeRs.getInt("earning_type_id");
                        // Now, check if there's an entry in the earnings table for the employee with this earning_type_id
                        String checkEarningsSql = "SELECT * FROM earnings WHERE employee_id = ? AND earning_type_id != ? AND period_id = ?";
                        try (PreparedStatement checkEarningsStmt = connection.prepareStatement(checkEarningsSql)) {
                            checkEarningsStmt.setInt(1, employee_id);
                            checkEarningsStmt.setInt(2, salaryEarningTypeId);
                            checkEarningsStmt.setInt(3, period_id);
                            try (ResultSet earningsRs = checkEarningsStmt.executeQuery()) {

                                while (earningsRs.next() && earningsRs.getInt(1) > 0) {
                                    stringList.add(""+earningsRs.getDouble("earning_type_id"));
                                }
                            }
                        }


                    }
                }
            }
            System.out.println("Done with other earning checks");
            // Convert ArrayList to String array:
            return stringList;

        } catch (SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            // Handle exception (e.g., logging)
        }

        return  null;
    }

    public static ArrayList<String> lastDeductionChecker(Connection connection, int employee_id, int period_id) {
        ArrayList<String> stringList = new ArrayList<>();
        try {
            // First, find the earning_type_id for "salary"
            String getEarningTypeIdSql = "SELECT deduction_type_id FROM deduction_type WHERE name = ?";
            try (PreparedStatement getEarningTypeIdStmt = connection.prepareStatement(getEarningTypeIdSql)) {
                getEarningTypeIdStmt.setString(1, "PAYE");
                try (ResultSet earningTypeRs = getEarningTypeIdStmt.executeQuery()) {

                    if (earningTypeRs.next()) {
                        int deductionTypeId = earningTypeRs.getInt("deduction_type_id");
                        // Now, check if there's an entry in the earnings table for the employee with this earning_type_id
                        String checkEarningsSql = "SELECT * FROM deductions WHERE employee_id = ? AND deduction_type_id != ? AND period_id = ?";
                        try (PreparedStatement checkEarningsStmt = connection.prepareStatement(checkEarningsSql)) {
                            checkEarningsStmt.setInt(1, employee_id);
                            checkEarningsStmt.setInt(2, deductionTypeId);
                            checkEarningsStmt.setInt(3, period_id);
                            try (ResultSet earningsRs = checkEarningsStmt.executeQuery()) {

                                while (earningsRs.next() && earningsRs.getInt(1) > 0) {
                                    stringList.add(""+earningsRs.getDouble("deduction_type_id"));
                                }
                            }
                        }


                    }
                }
            }

            // Convert ArrayList to String array:
            return stringList;

        } catch (SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            // Handle exception (e.g., logging)
        }

        return  null;
    }
    public static Map.Entry<String, Double> getLastPaymentAndPeriod(Map<String, Double> salaries) {
        if (salaries.isEmpty()) {
            return null;
        }
        Map.Entry<String, Double> lastEntry = null;
        for (Map.Entry<String, Double> entry : salaries.entrySet()) {
            lastEntry = entry;
        }
        return lastEntry;
    }

    public static Map.Entry<String, Double> getSecondLastPaymentAndPeriod(Map<String, Double> salaries) {
        if (salaries.size() < 2) {
            return null;
        }

        Map.Entry<String, Double> secondLastEntry = null;
        Map.Entry<String, Double> lastEntry = null;

        for (Map.Entry<String, Double> entry : salaries.entrySet()) {
            if (lastEntry != null) {
                secondLastEntry = lastEntry;
            }
            lastEntry = entry;
        }
        return secondLastEntry;
    }

    public static long calculateMonthGap(String startPeriod, String endPeriod) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth start = YearMonth.parse(startPeriod, formatter);
        YearMonth end = YearMonth.parse(endPeriod, formatter);
        return ChronoUnit.MONTHS.between(start, end);
    }
    public static String reformatPeriod(String periodStr) {
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MM/yyyy");

        // Enhanced input formats to include various date and datetime patterns
        DateTimeFormatter[] inputFormats = new DateTimeFormatter[]{
                // Date formats without time
                DateTimeFormatter.ofPattern("M/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/M"),
                DateTimeFormatter.ofPattern("MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/M/d"),
                DateTimeFormatter.ofPattern("yyyy/d/M"),
                // "-" separator patterns without time
                DateTimeFormatter.ofPattern("M-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-M"),
                DateTimeFormatter.ofPattern("MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM"),
                DateTimeFormatter.ofPattern("d-M-yyyy"),
                DateTimeFormatter.ofPattern("M-d-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-M-d"),
                DateTimeFormatter.ofPattern("yyyy-d-M"),
                // Date-time formats
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("M/d/yyyy H:m:s"),
                DateTimeFormatter.ofPattern("yyyy/M/d H:m:s"),
                DateTimeFormatter.ofPattern("M-d-yyyy H:m:s"),
                DateTimeFormatter.ofPattern("yyyy-M-d H:m:s"),
                // ISO 8601 Date and Time with timezone
                DateTimeFormatter.ISO_OFFSET_DATE_TIME,
                DateTimeFormatter.ISO_ZONED_DATE_TIME,
                // Including milliseconds and 'T' separator patterns
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
                // RFC 1123 Date Time
                DateTimeFormatter.RFC_1123_DATE_TIME
        };

        for (DateTimeFormatter inputFormat : inputFormats) {
            try {
                // Attempt to parse the input as a LocalDate or LocalDateTime
                try {
                    LocalDate date = LocalDate.parse(periodStr, inputFormat);
                    YearMonth period = YearMonth.from(date);
                    return period.format(outputFormat);
                } catch (DateTimeParseException e) {
                    // If LocalDate parsing fails, attempt LocalDateTime parsing
                    LocalDateTime dateTime = LocalDateTime.parse(periodStr, inputFormat);
                    YearMonth period = YearMonth.from(dateTime);
                    return period.format(outputFormat);
                }
            } catch (DateTimeParseException ignored) {
                // If parsing fails, continue to the next format
            }
        }

        DateTimeFormatter[] yearMonthFormats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy/M", Locale.US),
                DateTimeFormatter.ofPattern("M/yyyy", Locale.US),
                DateTimeFormatter.ofPattern("yyyy-MM", Locale.US),
                DateTimeFormatter.ofPattern("MM-yyyy", Locale.US),
        };

        for (DateTimeFormatter format : yearMonthFormats) {
            try {
                YearMonth period = YearMonth.parse(periodStr, format);
                return outputFormat.format(period);
            } catch (DateTimeParseException ignored) {
                // Ignore and continue to the next format
            }
        }

        // If none of the formats match, return null
        return null;
    }


    public static String getPreviousPeriod(String periodStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        try {
            YearMonth currentPeriod = YearMonth.parse(periodStr, formatter);
            YearMonth previousPeriod = currentPeriod.minusMonths(1);

            return previousPeriod.format(formatter);
        } catch (Exception e) {
            return null;
        }
    }
}
