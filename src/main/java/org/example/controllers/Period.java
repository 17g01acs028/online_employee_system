package org.example.controllers;

import org.example.QueryBuilder.Insert;
import org.example.QueryBuilder.Select;
import org.example.QueryBuilder.Update;
import org.example.libs.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class Period {
    public static Response addPeriod(Connection conn, Map<String, String> values){
        Response result = null;
        try {
            result = Insert.insertSingleRow(conn,"period", values);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       return result;
    }

    public static Response updatePeriod(Connection conn, Map<String, String> values,String id){
        Response result = null;
        try {
            result = Update.updateSingleRow(conn,"period",values,id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static Response Find(Connection conn){
        Response result = null;
        try {
            result = Select.select(conn, "period");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Response Find(Connection conn,String where){
        Response result = null;
        try {
            result = Select.select(conn, "period",where);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static Response Find(Connection conn,String[] column,String where){
        Response result = null;
        try {
            result = Select.select(conn,"period",column,where);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Response FindById(Connection conn,int id){
        Response result = null;
        try {
            result = Select.select(conn, "period","period_id ="+id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Response FindPeriodId(Connection conn, String period){
        Response result = null;
        try {
            String[] column = {"period_id"};
            result = Period.Find(conn,column,"period = '"+period+"'");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
