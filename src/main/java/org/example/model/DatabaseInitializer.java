package org.example.model;

import java.sql.*;

public class DatabaseInitializer {
    static void createTablesIfNotExist(Connection connection,String databaseType) throws SQLException {
        boolean isMySQL = "Mysql".equalsIgnoreCase(databaseType);
        boolean isPostgreSQL = "postgresql".equalsIgnoreCase(databaseType);
        boolean isMSSQL = "mssql".equalsIgnoreCase(databaseType);


        try (Statement statement = connection.createStatement()) {


            // Create Tables
            statement.executeUpdate(getStringCompany(isMySQL, isPostgreSQL, isMSSQL));
            statement.executeUpdate(getStringDepartment(isMySQL, isPostgreSQL, isMSSQL));
            statement.executeUpdate(getStringEmployees(isMySQL, isPostgreSQL, isMSSQL));
            statement.executeUpdate(getStringEarningType(isMySQL, isPostgreSQL, isMSSQL));
            statement.executeUpdate(getStringPeriod(isMySQL, isPostgreSQL, isMSSQL));
            statement.executeUpdate(getStringDeductionType(isMySQL, isPostgreSQL, isMSSQL));
            statement.executeUpdate(getStringDeductions(isMySQL, isPostgreSQL, isMSSQL));
            statement.executeUpdate(getStringEarnings(isMySQL, isPostgreSQL, isMSSQL));

           // Generate indexes
            /*==============Company indexes start=================*/
            checkAndCreateIndex(connection, "company", "idx_company_name", "name", databaseType);
            checkAndCreateIndex(connection, "company", "idx_company_email", "email", databaseType);
            checkAndCreateIndex(connection, "company", "idx_company_phone", "phone", databaseType);
            checkAndCreateIndex(connection, "company", "idx_company_address", "address", databaseType);
            checkAndCreateIndex(connection, "company", "idx_company_postal_code", "postal_code", databaseType);
            checkAndCreateIndex(connection, "company", "idx_company_website", "website", databaseType);
            checkAndCreateIndex(connection, "company", "idx_company_city", "city", databaseType);
            checkAndCreateIndex(connection, "company", "idx_company_date_created", "date_created", databaseType);
            checkAndCreateIndex(connection, "company", "idx_company_date_modified", "date_modified", databaseType);
            /*==============Company indexes end=================*/

            /*==============Department indexes start=================*/
            checkAndCreateIndex(connection, "department", "idx_department_name", "department_name", databaseType);
            checkAndCreateIndex(connection, "department", "idx_date_created", "date_created", databaseType);
            checkAndCreateIndex(connection, "department", "idx_date_modified", "date_modified", databaseType);
            /*==============Department indexes end=================*/

            /*==============Earning_type indexes start=================*/
            checkAndCreateIndex(connection, "earning_type", "idx_earning_type_name", "name", databaseType);
            checkAndCreateIndex(connection, "earning_type", "idx_earning_type_date_created", "date_created", databaseType);
            checkAndCreateIndex(connection, "earning_type", "idx_earning_type_date_modified", "date_modified", databaseType);
            /*==============Earning_type indexes end=================*/

            /*==============Period indexes start=================*/
            checkAndCreateIndex(connection, "period", "idx_period_period", "period", databaseType);
            checkAndCreateIndex(connection, "period", "idx_period_date_created", "date_created", databaseType);
            checkAndCreateIndex(connection, "period", "idx_period_date_modified", "date_modified", databaseType);
            /*==============Period indexes end=================*/

            /*==============Earning indexes end=================*/
            checkAndCreateIndex(connection, "earnings", "idx_earnings_amount", "amount", databaseType);
            checkAndCreateIndex(connection, "earnings", "idx_earnings_date_created", "date_created", databaseType);
            checkAndCreateIndex(connection, "earnings", "idx_earnings_date_modified", "date_modified", databaseType);
            /*==============Earning indexes end=================*/

            /*==============Deductions Type indexes end=================*/
            checkAndCreateIndex(connection, "deduction_type", "idx_deduction_type_name", "name", databaseType);
            checkAndCreateIndex(connection, "deduction_type", "idx_deduction_type_date_created", "date_created", databaseType);
            checkAndCreateIndex(connection, "deduction_type", "idx_deduction_type_date_modified", "date_modified", databaseType);
            /*==============Deductions Type indexes end=================*/

            /*==============Deductions indexes end=================*/
            checkAndCreateIndex(connection, "deductions", "idx_deductions_amount", "amount", databaseType);
            checkAndCreateIndex(connection, "deductions", "idx_deductions_date_created", "date_created", databaseType);
            checkAndCreateIndex(connection, "deductions", "idx_deductions_date_modified", "date_modified", databaseType);
            /*==============Deductions indexes end=================*/

            /*==============Employee indexes start=================*/
            checkAndCreateIndex(connection, "employees", "idx_employees_username", "username", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_id_number", "id_number", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_kra_pin", "kra_pin", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_email", "email", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_firstname", "firstname", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_lastname", "lastname", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_phone", "phone", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_nssf_number", "nssf_number", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_address", "address", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_postal_code", "postal_code", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_city", "city", databaseType);
            checkAndCreateIndex(connection, "employees", "idx_employees_status", "status", databaseType);
            /*==============Employee indexes end=================*/



        }
    }
    private static String getStringEmployees(boolean isMySQL, boolean isPostgreSQL, boolean isMSSQL) {
        String baseQuery = "";
        if (isMSSQL) {
            baseQuery = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[employees]') AND type in (N'U')) BEGIN CREATE TABLE dbo.employees (";
        } else {
            baseQuery = "CREATE TABLE IF NOT EXISTS employees (";
        }

        String primaryKeyPart = "";
        String columnDefinitions = "department_id BIGINT NOT NULL, "
                + "company_id BIGINT NOT NULL, "
                + "firstname VARCHAR(50) NOT NULL, "
                + "lastname VARCHAR(50) NOT NULL, "
                + "username VARCHAR(50) NOT NULL, "
                + "id_number INT NOT NULL, "
                + "phone VARCHAR(15) NOT NULL, "
                + "nssf_number INT, "
                + "kra_pin VARCHAR(25), "
                + "email VARCHAR(50), "
                + "address VARCHAR(50), "
                + "postal_code VARCHAR(50), "
                + "city VARCHAR(50), "
                + "status VARCHAR(10) NOT NULL , "
                + "employment_date DATE NOT NULL, "
                + "employment_end_date DATE, "
                + "password VARCHAR(255) NOT NULL, ";
        String dateTimePart = "";
        String foreignKeyPart = ", FOREIGN KEY (department_id) REFERENCES department(department_id), "
                + "FOREIGN KEY (company_id) REFERENCES company(company_id)";
        String checkConstraintPart = ", CHECK (status IN ('new', 'active', 'leaving', 'terminated'))";
        String endPart = ")";
        if (isMSSQL) {
            endPart += " END;";
        } else {
            endPart += ";";
        }
        if (isMySQL) {
            primaryKeyPart = "employee_id BIGINT NOT NULL AUTO_INCREMENT, ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified DATETIME DEFAULT NULL";
        } else if (isPostgreSQL) {
            primaryKeyPart = "employee_id BIGSERIAL, ";
            dateTimePart = "date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified TIMESTAMP DEFAULT NULL";
        } else if (isMSSQL) {
            primaryKeyPart = "employee_id BIGINT IDENTITY(1,1), ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT GETDATE(), "
                    + "date_modified DATETIME NULL";
        }

        String fullQuery = baseQuery + primaryKeyPart + columnDefinitions + dateTimePart +  ", PRIMARY KEY (employee_id)" + foreignKeyPart + checkConstraintPart + endPart;
        return fullQuery;
    }



    private static String getStringCompany(boolean isMySQL, boolean isPostgreSQL, boolean isMSSQL) {
        String baseQuery = "";
        if (isMSSQL) {
            baseQuery = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[company]') AND type in (N'U')) BEGIN CREATE TABLE dbo.company (";
        } else {
            baseQuery = "CREATE TABLE IF NOT EXISTS company (";
        }

        String primaryKeyPart = "";
        String dateTimePart = "";
        String endPart = ")";
        if (isMSSQL) {
            endPart += " END;";
        } else {
            endPart += ";";
        }
        if (isMySQL) {
            primaryKeyPart = "company_id BIGINT NOT NULL AUTO_INCREMENT, ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified DATETIME DEFAULT NULL, ";
        } else if (isPostgreSQL) {
            primaryKeyPart = "company_id BIGSERIAL, ";
            dateTimePart = "date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified TIMESTAMP DEFAULT NULL, ";
        } else if (isMSSQL) {
            primaryKeyPart = "company_id BIGINT IDENTITY(1,1), ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT GETDATE(), "
                    + "date_modified DATETIME NULL, ";
        }

        String fieldPart = "name VARCHAR(50) NOT NULL UNIQUE, "
                + "mission TEXT, "
                + "vision TEXT, "
                + "motto TEXT, "
                + "email VARCHAR(50) UNIQUE, "
                + "phone VARCHAR(15), "
                + "address VARCHAR(50) NOT NULL, "
                + "postal_code INT NOT NULL, "
                + "website VARCHAR(50), "
                + "city VARCHAR(50) NOT NULL, "; // Ensure NOT NULL for city if that's a requirement

        String fullQuery = baseQuery + primaryKeyPart + fieldPart + dateTimePart + "PRIMARY KEY (company_id)" + endPart;
        return fullQuery;
    }

    private static String getStringDepartment(boolean isMySQL, boolean isPostgreSQL, boolean isMSSQL) {
        String baseQuery = "";
        if (isMSSQL) {
            baseQuery = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[department]') AND type in (N'U')) BEGIN CREATE TABLE dbo.department (";
        } else {
            baseQuery = "CREATE TABLE IF NOT EXISTS department (";
        }

        String primaryKeyPart = "";
        String dateTimePart = "";
        String foreignKeyPart = ", FOREIGN KEY (company_id) REFERENCES company(company_id)";
        String endPart = ")";
        if (isMSSQL) {
            endPart += " END;";
        } else {
            endPart += ";";
        }
        if (isMySQL) {
            primaryKeyPart = "department_id BIGINT NOT NULL AUTO_INCREMENT, ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified DATETIME DEFAULT NULL, ";
        } else if (isPostgreSQL) {
            primaryKeyPart = "department_id BIGSERIAL, ";
            dateTimePart = "date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified TIMESTAMP DEFAULT NULL, ";
        } else if (isMSSQL) {
            primaryKeyPart = "department_id BIGINT IDENTITY(1,1), ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT GETDATE(), "
                    + "date_modified DATETIME NULL, ";
        }

        String fieldPart = "department_name VARCHAR(50) NOT NULL, "
                + "company_id BIGINT NOT NULL, ";

        String fullQuery = baseQuery + primaryKeyPart + fieldPart + dateTimePart + "PRIMARY KEY (department_id)" + foreignKeyPart + endPart;
        return fullQuery;
    }
    private static String getStringEarningType(boolean isMySQL, boolean isPostgreSQL, boolean isMSSQL) {
        String baseQuery = "";
        if (isMSSQL) {
            baseQuery = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[earning_type]') AND type in (N'U')) BEGIN CREATE TABLE dbo.earning_type (";
        } else {
            baseQuery = "CREATE TABLE IF NOT EXISTS earning_type (";
        }

        String primaryKeyPart = "";
        String dateTimePart = "";
        String foreignKeyPart = ", FOREIGN KEY (company_id) REFERENCES company(company_id)";
        String endPart = ")";
        if (isMSSQL) {
            endPart += " END;";
        } else {
            endPart += ";";
        }
        if (isMySQL) {
            primaryKeyPart = "earning_type_id BIGINT NOT NULL AUTO_INCREMENT, ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified DATETIME DEFAULT NULL, ";
        } else if (isPostgreSQL) {
            primaryKeyPart = "earning_type_id BIGSERIAL, ";
            dateTimePart = "date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified TIMESTAMP DEFAULT NULL, ";
        } else if (isMSSQL) {
            primaryKeyPart = "earning_type_id BIGINT IDENTITY(1,1), ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT GETDATE(), "
                    + "date_modified DATETIME NULL, ";
        }

        String fieldPart = "name VARCHAR(50) NOT NULL UNIQUE, "
                + "company_id BIGINT NOT NULL," +
                " rate INT NULL, ";

        String fullQuery = baseQuery + primaryKeyPart + fieldPart + dateTimePart + "PRIMARY KEY (earning_type_id)" + foreignKeyPart + endPart;
        return fullQuery;
    }
    private static String getStringPeriod(boolean isMySQL, boolean isPostgreSQL, boolean isMSSQL) {
        String baseQuery = "";
        if (isMSSQL) {
            baseQuery = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[period]') AND type in (N'U')) BEGIN CREATE TABLE dbo.period (";
        } else {
            baseQuery = "CREATE TABLE IF NOT EXISTS period (";
        }

        String primaryKeyPart = "";
        String dateTimePart = "";
        String endPart = ")";
        if (isMSSQL) {
            endPart += " END;";
        } else {
            endPart += ";";
        }
        if (isMySQL) {
            primaryKeyPart = "period_id BIGINT NOT NULL AUTO_INCREMENT, ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified DATETIME DEFAULT NULL, ";
        } else if (isPostgreSQL) {
            primaryKeyPart = "period_id BIGSERIAL, ";
            dateTimePart = "date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified TIMESTAMP DEFAULT NULL, ";
        } else if (isMSSQL) {
            primaryKeyPart = "period_id BIGINT IDENTITY(1,1), ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT GETDATE(), "
                    + "date_modified DATETIME NULL, ";
        }

        String fieldPart = "period VARCHAR(50) NOT NULL UNIQUE," +
                " status VARCHAR(6) NOT NULL, ";

        String fullQuery = baseQuery + primaryKeyPart + fieldPart + dateTimePart + "PRIMARY KEY (period_id)" + endPart;
        return fullQuery;
    }

    private static String getStringEarnings(boolean isMySQL, boolean isPostgreSQL, boolean isMSSQL) {
        String baseQuery = "";
        if (isMSSQL) {
            baseQuery = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[earnings]') AND type in (N'U')) BEGIN CREATE TABLE dbo.earnings (";
        } else {
            baseQuery = "CREATE TABLE IF NOT EXISTS earnings (";
        }

        String primaryKeyPart = "";
        String dateTimePart = "";
        String foreignKeyPart = ", FOREIGN KEY (earning_type_id) REFERENCES earning_type(earning_type_id), "
                + "FOREIGN KEY (employee_id) REFERENCES employees(employee_id), "
                + "FOREIGN KEY (period_id) REFERENCES period(period_id)";
        String endPart = ")";
        if (isMSSQL) {
            endPart += " END;";
        } else {
            endPart += ";";
        }
        if (isMySQL) {
            primaryKeyPart = "earning_id BIGINT NOT NULL AUTO_INCREMENT, ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified DATETIME DEFAULT NULL, ";
        } else if (isPostgreSQL) {
            primaryKeyPart = "earning_id BIGSERIAL, ";
            dateTimePart = "date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified TIMESTAMP DEFAULT NULL, ";
        } else if (isMSSQL) {
            primaryKeyPart = "earning_id BIGINT IDENTITY(1,1), ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT GETDATE(), "
                    + "date_modified DATETIME NULL, ";
        }

        String fieldPart = "earning_type_id BIGINT NOT NULL, "
                + "employee_id BIGINT NOT NULL, "
                + "amount DECIMAL(10,2) NOT NULL, "
                + "period_id BIGINT NOT NULL, ";

        String fullQuery = baseQuery + primaryKeyPart + fieldPart + dateTimePart + "PRIMARY KEY (earning_id)" + foreignKeyPart + endPart;
        return fullQuery;
    }


    private static String getStringDeductionType(boolean isMySQL, boolean isPostgreSQL, boolean isMSSQL) {
        String baseQuery = "";
        if (isMSSQL) {
            baseQuery = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[deduction_type]') AND type in (N'U')) BEGIN CREATE TABLE dbo.deduction_type (";
        } else {
            baseQuery = "CREATE TABLE IF NOT EXISTS deduction_type (";
        }

        String primaryKeyPart = "";
        String dateTimePart = "";
        String foreignKeyPart = ", FOREIGN KEY (company_id) REFERENCES company(company_id)";
        String endPart = ")";
        if (isMSSQL) {
            endPart += " END;";
        } else {
            endPart += ";";
        }
        if (isMySQL) {
            primaryKeyPart = "deduction_type_id BIGINT NOT NULL AUTO_INCREMENT, ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified DATETIME DEFAULT NULL, ";
        } else if (isPostgreSQL) {
            primaryKeyPart = "deduction_type_id BIGSERIAL, ";
            dateTimePart = "date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified TIMESTAMP DEFAULT NULL, ";
        } else if (isMSSQL) {
            primaryKeyPart = "deduction_type_id BIGINT IDENTITY(1,1), ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT GETDATE(), "
                    + "date_modified DATETIME NULL, ";
        }

        String fieldPart = "name VARCHAR(50) NOT NULL UNIQUE, "
                + "company_id BIGINT NOT NULL, " +
                " rate INT NULL, ";

        String fullQuery = baseQuery + primaryKeyPart + fieldPart + dateTimePart + "PRIMARY KEY (deduction_type_id)" + foreignKeyPart + endPart;
        return fullQuery;
    }

    private static String getStringDeductions(boolean isMySQL, boolean isPostgreSQL, boolean isMSSQL) {
        String baseQuery = "";
        if (isMSSQL) {
            baseQuery = "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[deductions]') AND type in (N'U')) BEGIN CREATE TABLE dbo.deductions (";
        } else {
            baseQuery = "CREATE TABLE IF NOT EXISTS deductions (";
        }

        String primaryKeyPart = "";
        String dateTimePart = "";
        String foreignKeyPart = ", FOREIGN KEY (employee_id) REFERENCES employees(employee_id), "
                + "FOREIGN KEY (deduction_type_id) REFERENCES deduction_type(deduction_type_id), "
                + "FOREIGN KEY (period_id) REFERENCES period(period_id)";
        String endPart = ")";
        if (isMSSQL) {
            endPart += " END;";
        } else {
            endPart += ";";
        }
        if (isMySQL) {
            primaryKeyPart = "deduction_id BIGINT NOT NULL AUTO_INCREMENT, ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified DATETIME DEFAULT NULL, ";
        } else if (isPostgreSQL) {
            primaryKeyPart = "deduction_id BIGSERIAL, ";
            dateTimePart = "date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "date_modified TIMESTAMP DEFAULT NULL, ";
        } else if (isMSSQL) {
            primaryKeyPart = "deduction_id BIGINT IDENTITY(1,1), ";
            dateTimePart = "date_created DATETIME NOT NULL DEFAULT GETDATE(), "
                    + "date_modified DATETIME NULL, ";
        }

        String fieldPart = "employee_id BIGINT NOT NULL, "
                + "deduction_type_id BIGINT NOT NULL, "
                + "amount DECIMAL(10,2) NOT NULL, " // Use DECIMAL for consistency across databases
                + "period_id BIGINT NOT NULL, ";

        String fullQuery = baseQuery + primaryKeyPart + fieldPart + dateTimePart + "PRIMARY KEY (deduction_id)" + foreignKeyPart + endPart;
        return fullQuery;
    }


    private static void checkAndCreateIndex(Connection connection, String tableName, String indexName, String columnName, String datasetype) throws SQLException {

          if("mysql".equalsIgnoreCase(datasetype)) {
              // Check if the index exists
              ResultSet resultSet = connection.getMetaData().getIndexInfo(null, null, tableName, false, false);
              boolean indexExists = false;
              while (resultSet.next()) {
                  String existingIndexName = resultSet.getString("INDEX_NAME");
                  if (indexName.equalsIgnoreCase(existingIndexName)) {
                      indexExists = true;
                      break;
                  }
              }
              resultSet.close();

              // If the index doesn't exist, create it
              if (!indexExists) {
                  try (Statement statement = connection.createStatement()) {
                      statement.executeUpdate("CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")");
                      System.out.println("Index '" + indexName + "' created for table '" + tableName + "'.");
                  }
              }
          }else if("postgresql".equalsIgnoreCase(datasetype)){
              String checkQuery = "SELECT * FROM pg_index JOIN pg_class ON pg_class.oid=pg_index.indexrelid "
                      + "JOIN pg_namespace ON pg_namespace.oid=pg_class.relnamespace "
                      + "WHERE pg_class.relname = ? AND pg_namespace.nspname = 'public';";

              boolean indexExists = false;
              try (PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
                  pstmt.setString(1, indexName);
                  ResultSet resultSet = pstmt.executeQuery();
                  indexExists = resultSet.next();
                  resultSet.close();
              }

              // If the index doesn't exist, create it
              if (!indexExists) {
                  try (Statement statement = connection.createStatement()) {
                      statement.executeUpdate("CREATE INDEX " + indexName + " ON public." + tableName + "(" + columnName + ")");
                      System.out.println("Index '" + indexName + "' created for table '" + tableName + "'.");
                  }
              }
          } else if("mssql".equalsIgnoreCase(datasetype)){
              String checkQuery = "SELECT * FROM sys.indexes WHERE name = ? AND object_id = OBJECT_ID(?);";

              boolean indexExists = false;
              try (PreparedStatement pstmt = connection.prepareStatement(checkQuery)) {
                  pstmt.setString(1, indexName);
                  pstmt.setString(2, tableName);
                  ResultSet resultSet = pstmt.executeQuery();
                  indexExists = resultSet.next();
                  resultSet.close();
              }

              // If the index doesn't exist, create it
              if (!indexExists) {
                  try (Statement statement = connection.createStatement()) {
                      statement.executeUpdate("CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")");
                      System.out.println("Index '" + indexName + "' created for table '" + tableName + "'.");
                  }
              }
        }

        }


}
