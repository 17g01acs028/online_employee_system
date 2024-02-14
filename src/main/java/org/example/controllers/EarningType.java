package org.example.controllers;

import org.example.QueryBuilder.Insert;
import org.example.QueryBuilder.Select;
import org.example.QueryBuilder.Update;
import org.example.libs.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class EarningType {
    public static Response addEarningType(Connection conn, Map<String, String> values){
        Response result = null;
        try {
            result = Insert.insertSingleRow(conn,"earning_type", values);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
       return result;
    }

    public static Response updateEarningType(Connection conn, Map<String, String> values,String id){
        Response result = null;
        try {
            result = Update.updateSingleRow(conn,"earning_type",values,id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static Response Find(Connection conn){
        Response result = null;
        try {
            result = Select.select(conn, "earning_type");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Response Find(Connection conn, String where){
        Response result = null;
        try {
            result = Select.select(conn, "earning_type",where);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Response FindById(Connection conn,int id){
        Response result = null;
        try {
            result = Select.select(conn, "earning_type","earning_type_id ="+id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
