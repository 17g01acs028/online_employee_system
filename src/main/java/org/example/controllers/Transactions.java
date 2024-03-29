package org.example.controllers;

import org.example.libs.Checker;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;


import static org.example.libs.Checker.calculateMonthGap;

public class Transactions {


    public static  void RollOver(Connection conn,String newPeriod){
        try {
            //variables
            double salary;
            String earningTypeId = EarningType.Find(conn,"name = 'salary'").getFieldValue("earning_type_id");
            //Active Period
            String activePeriod= Period.Find(conn,"status = 'live'").getFieldValue("period");;
            String activePeriodId = Period.Find(conn,"status = 'live'").getFieldValue("period_id");

            if(activePeriod.equalsIgnoreCase(newPeriod) || Checker.calculateMonthGap(activePeriod,newPeriod) < 0){
                System.out.println("Please note that we cannot roll to previous Period or current Period");
            }else {
            conn.setAutoCommit(false);
            System.out.println("Please note that we are rolling over to a new Period");
            //create and deactivate current active period
            Map<String, String> values = new HashMap<String, String>();
            values.put("period", newPeriod);
            values.put("status", "live");
            Period.addPeriod(conn, values);

            //Close current period
            Map<String, String> values1 = new HashMap<String, String>();
            values1.put("status", "closed");
            Period.updatePeriod(conn, values1, activePeriodId);

            //Fetch all workers to check if they have being paid in the current month
            JSONArray jsonArray = new JSONArray(Employees.Find(conn).getResponse());

            // Iterate through each object in the array
            for (int i = 0; i < jsonArray.length(); i++) {
                // Get the JSONObject at the current index
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract data from the JSONObject
                String id = jsonObject.getString("employee_id");

                //Before Start update status
                Transactions.updateStatus(conn, Integer.parseInt(id));

                //Select the data again and validate
                String status = Employees.FindById(conn, Integer.parseInt(id)).getFieldValue("status");

                System.out.println(status);

                //check if the employee has ever been paid
                Map<String, Double> salaryList = Checker.salaryChecker(conn, Integer.parseInt(id));

                System.out.println(salaryList);
                if ("active".equalsIgnoreCase(status) || "leaving".equalsIgnoreCase(status)) {
                    if (salaryList == null || salaryList.isEmpty()) {
                        //Define salary for the first time
                        System.out.println("Employee with id "+id+" and Name "+ Employees.FindById(conn,Integer.parseInt(id)).getFieldValue("firstname")+"  " + Employees.FindById(conn,Integer.parseInt(id)).getFieldValue("lastname")+" has never being paid please enter their Initial salary. Please not this employee status is now set to *Active*");
                    } else {
                        //If they are active or leaving
                        Map.Entry<String, Double> salaryAndPeriod = Checker.getLastPaymentAndPeriod(salaryList);
                        assert salaryAndPeriod != null;
                        String period_check = salaryAndPeriod.getKey();
                        Double amount = salaryAndPeriod.getValue();

                        if (period_check.equalsIgnoreCase(newPeriod)) {
                            salary = amount;

                        } else {
                            salary = calculateCurrentMonthSalary(amount);
                            Map<String, String> inserts = new HashMap<String, String>();
                            System.out.println(earningTypeId);
                            inserts.put("earning_type_id", earningTypeId);
                            inserts.put("employee_id", id);
                            inserts.put("amount", "" + salary);
                            inserts.put("period_id", Period.Find(conn, "status = 'live'").getValueByKey("period_id"));
                            System.out.println(Earning.addEarning(conn, inserts));
                        }

                    }
                }


            }
            conn.commit();
        }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }
    public static void EmployeePayment(Connection connection, int employee_id) {
        //Variables
        Double salary;
        String period;
        String period_id;

        try {
            connection.setAutoCommit(false);
        //Update Employee status before starting transaction
        Transactions.updateStatus(connection,employee_id);
            System.out.println("Done updating user status");
        //Fetch And Store id for active period
        period_id = Period.Find(connection,"status = 'live'").getFieldValue("period_id");
        period = Period.Find(connection,"status = 'live'").getFieldValue("period");

        //check if the employee has ever been paid
        Map<String,Double> salaryList = Checker.salaryChecker(connection,employee_id);

        if(salaryList == null){
            //Define salary for the first time
            System.out.println("Employee with id "+employee_id+" and Name "+ Employees.FindById(connection,employee_id).getFieldValue("firstname")+"  " + Employees.FindById(connection,employee_id).getFieldValue("lastname")+" has never being paid please enter their Initial salary to Continue With payment Process.");
        }else{

            System.out.println("User has being paid before Start here");
            //Now get the latest salary from the list and period
            Map.Entry<String, Double> salaryAndPeriod =  Checker.getLastPaymentAndPeriod(salaryList);
            assert salaryAndPeriod != null;
            String period_check = salaryAndPeriod.getKey();
            Double amount = salaryAndPeriod.getValue();

            if(period_check.equalsIgnoreCase(period)){
               salary = amount;

            }else{
               salary = calculateCurrentMonthSalary(amount);
            }

            if(salaryList.size() > 1 && period_check.equalsIgnoreCase(period) ){
                period_check = Objects.requireNonNull(Checker.getSecondLastPaymentAndPeriod(salaryList)).getKey();
            }

            //Check for previous allowances and add more or reduce
            ArrayList<String> rates = null;
            ArrayList<String> rates_deductions = null;
           if(period_check.equalsIgnoreCase(period) && salaryList.size() <= 1 ){

           }else{

               //First check if you have User last other earnings
               String p_id = Period.Find(connection,"period = '"+period_check+"'").getFieldValue("period_id");
               rates = Checker.lastEarningChecker(connection, employee_id, Integer.parseInt(p_id));
               rates_deductions = Checker.lastDeductionChecker(connection, employee_id, Integer.parseInt(p_id));
           }

           if(rates_deductions == null){
               rates_deductions =  new ArrayList<>();
           }

            if(rates == null){
                rates =  new ArrayList<>();
            }

           //This section allows you to add or remove allowance
            rates.add("3");
            rates_deductions.add("1");

           // This method iterates through all allowances id and gets rates then inserts the allowances to earning
            ArrayList<Integer> allowancesRates = new ArrayList<>();
            for (String rate : rates) {

                int rateValue = Integer.parseInt(EarningType.FindById(connection,(int) Double.parseDouble(rate)).getFieldValue("rate"));

                //check if allowance exists. If yes just update.
                String[] columns = {"count(*)"};
                int recordExist = Integer.parseInt(Earning.Find(connection,columns,"employee_id = '"+ employee_id+ "' and " + " period_id = '" + period_id +"' and earning_type_id = '"+rate+"'").getFieldValue("count(*)"));

                //Insert new allowance to earnings table
                Map<String,String> values = new HashMap<>();
                values.put("earning_type_id", rate);
                values.put("employee_id",""+employee_id);

                //calculate allowance amount
                double amount_total = salary * ((double) rateValue /100);
                values.put("amount",""+amount_total);
                values.put("period_id",period_id);
                if(recordExist > 0){
                    String id = Earning.Find(connection,"employee_id = '"+ employee_id+ "' and " + " period_id = '" + period_id +"' and earning_type_id = '"+rate+"'").getFieldValue("earning_id") ;
                    Earning.updateEarnings(connection,values,id);
                }else{
                    Earning.addEarning(connection,values);
                }

                //add the rate here to calculate all allowances later
                allowancesRates.add(rateValue);
            }

            //Calculate all allowances
           Double allowances =  calculateAllOtherEarnings(connection,salary,allowancesRates,employee_id);
            System.out.println("User Allowances " + allowances);

            //Calculate to total pay
           double totalPay =  salary + allowances;
            System.out.println("User total pay " + totalPay );
          // This method iterates through all allowances id and gets rates then inserts the allowances to earning
            ArrayList<Integer> deductionsRates = new ArrayList<>();
            for (String rate : rates_deductions) {

                int rateValue = Integer.parseInt(DeductionType.FindById(connection,(int) Double.parseDouble(rate)).getFieldValue("rate"));

                //check if deductions exists. If yes just update.
                String[] columns = {"count(*)"};

                int recordExist = Integer.parseInt(Deduction.Find(connection,columns,"employee_id = '"+ employee_id+ "' and " + " period_id = '" + period_id +"' and deduction_type_id = '"+rate+"'").getFieldValue("count(*)"));

                //Insert to deductions table
                Map<String,String> values = new HashMap<>();
                values.put("deduction_type_id", rate);
                values.put("employee_id",""+employee_id);

                //calculate allowance amount
                double amount_total = salary * ((double) rateValue /100);
                values.put("amount",""+amount_total);
                values.put("period_id",period_id);

                if(recordExist > 0){
                    String id = Deduction.Find(connection,"employee_id = '"+ employee_id+ "' and " + " period_id = '" + period_id +"' and deduction_type_id = '"+rate+"'").getFieldValue("deduction_id") ;
                    Deduction.updateDeductions(connection,values,id);
                }else{
                    Deduction.addDeduction(connection,values);
                }

                //add the rate here to calculate all allowances later
                deductionsRates.add(rateValue);
            }

            double deductions = calculationsWithRate(salary,deductionsRates);
            System.out.println("User deductions " + deductions);
            //Calculate taxable income
            double taxableIncome =totalPay - deductions;
            System.out.println("User Taxable income " + taxableIncome);
            if(taxableIncome > 25000){


                int rateValue = Integer.parseInt(DeductionType.Find(connection,"name = 'PAYE'").getFieldValue("rate"));
                String d_id = DeductionType.Find(connection,"name = 'PAYE'").getFieldValue("deduction_type_id");

                //check if deductions exists. If yes just update.
                String[] columns = {"count(*)"};
                int recordExist = Integer.parseInt(Deduction.Find(connection,columns,"employee_id = '"+ employee_id+ "' and " + " period_id = '" + period_id +"' and deduction_type_id = '"+d_id+"'").getFieldValue("count(*)"));

                Map<String,String> values = new HashMap<>();
                values.put("deduction_type_id", d_id);
                values.put("employee_id",""+employee_id);

                //calculate PAYE amount
                double amount_total = taxableIncome * ((double) rateValue /100);
                values.put("amount",""+amount_total);
                values.put("period_id",period_id);

                if(recordExist > 0){
                    String id = Deduction.Find(connection,"employee_id = '"+ employee_id+ "' and " + " period_id = '" + period_id +"' and deduction_type_id = '"+d_id+"'").getFieldValue("deduction_id") ;
                    Deduction.updateDeductions(connection,values,id);
                }else{
                    Deduction.addDeduction(connection,values);
                }

                System.out.println("User PAYE " + amount_total);
            }

        }

        connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }

    private static double calculateCurrentMonthSalary(double salary) {
        // Logic to get previous month's salary for 2% increase
        return salary * 1.02;
    }

    public static void updateStatus(Connection conn, int employee_id){
    String status = "";
    String period = Period.Find(conn,"status = 'live'").getFieldValue("period");
    String employmentDate= Employees.FindById(conn, employee_id).getFieldValue("employment_date");
    String employmentEndDate = Employees.FindById(conn, employee_id).getFieldValue("employment_end_date");

        if (calculateMonthGap(org.example.libs.Checker.reformatPeriod(employmentDate), org.example.libs.Checker.reformatPeriod(period)) > 0) {
           status = "active";
        }else if(calculateMonthGap(org.example.libs.Checker.reformatPeriod(employmentDate), org.example.libs.Checker.reformatPeriod(period)) <= 0){
            status = "new";
        }
       if(employmentEndDate != null) {
//           System.out.println(calculateMonthGap(org.example.libs.Checker.reformatPeriod(Employees.FindById(conn, employee_id).getFieldValue("employment_end_date")), org.example.libs.Checker.reformatPeriod(Period.Find(conn).getFieldValue("period"))));
           if (calculateMonthGap(org.example.libs.Checker.reformatPeriod(employmentEndDate), org.example.libs.Checker.reformatPeriod(period)) == 0) {
               status = "leaving";
           } else if (calculateMonthGap(org.example.libs.Checker.reformatPeriod(employmentEndDate), org.example.libs.Checker.reformatPeriod(period)) > 0) {
               status = "terminated";
           }
       }

        Map<String, String> values = new HashMap<String, String>();
        values.put("status",status);
        Employees.updateEmployee(conn,values,""+employee_id);

    }

    // Calculate monthly allowances
    public static double calculateAllOtherEarnings(Connection conn,double salary , ArrayList<Integer> rates, int employee_id) {
        if (calculateMonthGap(org.example.libs.Checker.reformatPeriod(Employees.FindById(conn, employee_id).getFieldValue("employment_date")), org.example.libs.Checker.reformatPeriod(Period.Find(conn).getFieldValue("period"))) <= 3) {
            return 0;
        }
       return  calculationsWithRate(salary,rates);
    }

    public static double calculationsWithRate(double salary, ArrayList<Integer> rates) {
        double rate = 0;
        for (int i = 0; i < rates.size(); i++) {
            rate += rates.get(i);
        }
        rate = rate / 100;
        return salary * rate;
    }

    public static void initialiseEmployeeSalary(Connection conn, int employee_id, double salary){

        //Check if the employee has ever been paid
        String salaryId = EarningType.Find(conn,"name = 'salary'").getFieldValue("earning_type_id");
        String period_id = Period.Find(conn,"status = 'live'").getFieldValue("period_id");

        String[] columns = {"count(*)"};
        int recordExist = Integer.parseInt(Earning.Find(conn,columns,"employee_id = '"+ employee_id+ "' and earning_type_id = '"+salaryId+"'").getFieldValue("count(*)"));

        if(recordExist > 0){
            System.out.println("This user's salary is already initialised please continue with payments.");
        }else{
            Map<String,String> values = new HashMap<>();
            values.put("earning_type_id", salaryId);
            values.put("employee_id",""+employee_id);

            //calculate allowance amount
            values.put("amount",""+ salary);
            values.put("period_id",period_id);
            Earning.addEarning(conn,values);
        }

    }

}
