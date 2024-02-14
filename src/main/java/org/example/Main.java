package org.example;

import org.example.controllers.*;
import org.example.libs.Checker;
import org.example.libs.Response;
import org.example.model.DatabaseConnection;
import static org.example.libs.ConfigFileChecker.configFileChecker;

import java.sql.Connection;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class Main {
    public static  String fileName;
    public static void main(String[] args) throws Exception {
        Response checker = configFileChecker("config",fileName);
        if(checker.getStatus()){
            DatabaseConnection connection = new DatabaseConnection("config/"+checker.getMessage());
            Connection conn = connection.getConnection();

            //System.out.println(Checker.reformatPeriod("2023/01/10"));
          // );
           // Map<String, String> values = new HashMap<String, String>();

//            values.put("period",Checker.reformatPeriod("2023/08"));
//            values.put("status","live");
 //           String[] column = {"period_id"};
 //           System.out.println(Period.FindPeriodId(conn,"08/2023"));

//            Response r =Period.Find(conn,"status = 'live'");
//            System.out.println( r);
//            System.out.println( r.getFieldValue("period"));
//

           // Response r = Earning.Find(conn,"employee_id = 1 and earning_id = 1");
            //System.out.println( r);

          //  int[] rates = {1,2,3};
          //  System.out.println(Transactions.calculateAllowances(5000,rates,1));

    // Checker.EmployeeStatus(conn,"1");
//          Map<String, String> values = new HashMap<String,String>();
//             Response r =  Employees.updateEmployee(conn,values,"3");
//
//            System.out.println(r);

           // Checker.initialSalaryChecker(conn,1,1);
          //  System.out.println(Arrays.toString(Checker.lastEarningChecker(conn, 1, 1)));
          //  System.out.println(Checker.getLastPaymentAndPeriod(Objects.requireNonNull(Checker.salaryChecker(conn, 1))));
        // Transactions.EmployeePayment(conn,1);
        //   Transactions.RollOver(conn,"11/2023");
        }else{
            System.out.println(checker.getMessage());
        }
    }



}