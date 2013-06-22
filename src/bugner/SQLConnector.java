/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author c9cf4fd2NTUT
 */
public class SQLConnector {

    private static SQLConnector instance = new SQLConnector();
    private String host;
    private String dbn;
    private String user;
    private String pass;
    private Connection con;

    private SQLConnector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized static SQLConnector getInstance() {
        return instance;
    }

    public synchronized void setConnectionParameter(String hostAddr, String dbName, String userAcc, String password) {
        host = hostAddr;
        dbn = dbName;
        user = userAcc;
        pass = password;
    }

    public synchronized void incert(String table, List<String> cols, List<String> vals) throws SQLException, ClassNotFoundException {
        con = getCon();
        String sqlStr = "insert into " + table + "(" + cols.get(0);
        for (int i = 1; i < cols.size(); i++) {
            sqlStr += "," + cols.get(i);
        }
        sqlStr += ") values (" + vals.get(0);
        for (int i = 1; i < vals.size(); i++) {
            sqlStr += "," + vals.get(i);
        }
        sqlStr += ")";
        System.out.println(sqlStr);
        Statement st = con.createStatement();
        st.execute(sqlStr);
        st.close();
        con.close();
    }

    private synchronized Connection getCon() throws SQLException, ClassNotFoundException {
        return DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbn + "?", user, pass);
    }
}
