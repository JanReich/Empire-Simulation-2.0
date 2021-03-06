package engine.toolBox;

import engine.config.MySQLConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQL {

            //Referenzen
        private String host;
        private String port;
        private String database;
        private String username;
        private String password;

        private Connection con;
        private MySQLConfig config;

    public MySQL() {

        this.config = new MySQLConfig();

        host = config.getHost();
        port = config.getPort();
        database = config.getDatabase();
        username = config.getUsername();
        password = config.getPassword();
    }

    /**
     * Mit dieser Methode verbindet sich der server (das Plugin) mit der Datenbank
     */
    public void connect() {

        if(!isConnected()) {

            try {

                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
                System.out.println("[MySQL] Verbindung wurde aufgebaut!");
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * Mit dieser Methode kann die Verbindung zur Datenbank geschlossen werden
     */
    public void disconnect() {

        if(isConnected()) {

            try {

                con.close();
                System.out.print("[MySQL] Verbindung wurde geschlossen!");
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * Mit dieser Methode kann man abfragen, ob eine Verbindung zur Datenbank besteht.
     * @return connectionStatus
     */
    public boolean isConnected() {

        return (con == null ? false : true);
    }
}
