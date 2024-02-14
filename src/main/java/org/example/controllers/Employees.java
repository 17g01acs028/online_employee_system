package org.example.controllers;

import org.example.QueryBuilder.Insert;
import org.example.QueryBuilder.Select;
import org.example.QueryBuilder.Update;
import org.example.libs.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class Employees {
    public static Response addEmployee(Connection conn, Map<String, String> values){
        Response result = null;
        try {
            result = Insert.insertSingleRow(conn,"employees", values);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       return result;
    }

    public static Response updateEmployee(Connection conn, Map<String, String> values,String id){
        Response result = null;
        try {
            result = Update.updateSingleRow(conn,"employees",values,id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static Response Find(Connection conn){
        Response result = null;
        try {
            result = Select.select(conn, "employees");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Response FindById(Connection conn,int id){
        Response result = null;
        try {
            result = Select.select(conn, "employees","employee_id ="+id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Response FindByStatus(Connection conn,String status ){
        Response result = null;
        try {
            result = Select.select(conn, "employees","status ="+status);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
