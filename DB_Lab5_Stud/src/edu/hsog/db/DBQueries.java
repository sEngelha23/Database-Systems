package edu.hsog.db;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBQueries {

    static public <ResultSet, Connection> int count() {

        int count = 0;

        ResultSet rs = null;

        Statement st = null;

        Connection con = Globals.getPoolConnection();

        try {
            st = con.createStatement();
            String q = "SELECT COUNT(*) FROM gadgets";
            System.out.printf(q);
            rs = st.executeQuery(q);
            rs.next();
            count = rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs!=null) rs.close();
                if (st!=null) st.close();
                if (con!=null)con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return count;
    }

    static public boolean login(String username, String password) {

        int count = 0;

        ResultSet rs = null;

        Statement st = null;

        Connection con = Globals.getPoolConnection();

        try {
            st = con.createStatement();
            String q = "SELECT COUNT(*)\n" +
                    "FROM users\n" +
                    "where email='" + username + "' and password='" + password + "'";
            System.out.printf(q);
            rs = st.executeQuery(q);
            rs.next();
            int c = rs.getInt(1);
            return (c==1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
