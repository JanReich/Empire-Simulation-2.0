package gamePackage.loginManagement;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.abitur.datenbanken.mysql.QueryResult;
import engine.config.MySQLConfig;
import engine.cryptography.MD5;

public class BackEnd {

                //Attribute

                //Referenzen
            private String mail;

            private MySQLConfig config;
            private DatabaseConnector connector;

        public BackEnd(DatabaseConnector connector) {

            config = new MySQLConfig();
            this.connector = connector;
        }

        /**
         * In dieser Methode wird überprüft, ob es einen benutzer mit den angegebenen Login-Daten gibt
         * @param loginName - Eingegebener LoginName
         * @param password - Eingegebeses Passwort
         * @return successful = true || failed = false
         */
        public boolean login(String loginName, String password) {

                //Login with Mail
            if(loginName.contains("@"))
                connector.executeStatement("" +
                        "SELECT Password, Mail FROM JansEmpire_Users WHERE Mail = '" + loginName + "'");
             else
                connector.executeStatement("" +
                        "SELECT Password, Mail FROM JansEmpire_Users WHERE Username = '" + loginName + "'");

            if(connector.getCurrentQueryResult().getRowCount() >= 1) {

                if(connector.getCurrentQueryResult().getData()[0][0] != null) {

                    String md5Password = MD5.ENCODE(password);
                    if(md5Password.equals(connector.getCurrentQueryResult().getData()[0][0])) {

                        mail = connector.getCurrentQueryResult().getData()[0][1];
                        if(config.isDebugging()) System.out.println("[MySQL-Info] Login successful...");
                        return true;
                    } else return false;
                }
            }
            return false;
        }

        /**
         *
         * @param username - Eingegebener Username
         * @param password - Eingegebenes Passwort
         * @param mail - Eingegebene E-Mail-Adresse
         * @return successful = true || failed = false
         */
        public boolean register(String username, String password, String mail) {

            if(username != null && password != null && mail != null && mail.contains("@") && username != mail && mail.length() >= 5 && username.length() >= 3) {

                createTableIfNotExist();

                    //Überprüfen, ob es zu dem Username || zu der Mail einen Eintrag gibt
                connector.executeStatement("" +
                        "SELECT * FROM JansEmpire_Users WHERE Mail = '" + mail + "' OR Username = '" + username + "'");

                    //Falls es keinen Eintrag gibt:
                if(connector.getCurrentQueryResult().getRowCount() == 0) {

                        //MD5-Verschlüsselung
                    String md5Password = MD5.ENCODE(password);

                    connector.executeStatement("" +
                            "INSERT INTO JansEmpire_Users " +
                            "(Mail, Username, Password) " +
                            "" +
                            "VALUES " +
                            "" +
                            "('" + mail + "', '" + username + "', '" + md5Password + "')");

                        //Select basic Storage-Amount
                    connector.executeStatement("" +
                            "SELECT StorageAmount From JansEmpire_StaticBuildings WHERE Level = '1' AND Type = 'Castle';");
                    int startStorage = Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);

                    connector.executeStatement("" +
                            "INSERT INTO JansEmpire_PlayerData " +
                            "(Mail, Level, XP, StorageAmount, Wood, Stone, Wheat, Coins, Population) " +
                            "" +
                            "VALUES " +
                            "" +
                            "('" + mail + "', '1', '50', '" + startStorage + "', '" + (startStorage / 2 )+ "', '" + (startStorage / 2) + "', '" + startStorage + "', '" + startStorage + "', '10');");

                        //Setzte das Startgebäude, dass der Spieler am start besitzt
                    connector.executeStatement("" +
                            "INSERT INTO JansEmpire_Buildings " +
                            "(Mail, Type, Level, Position) " +
                            "" +
                            "VALUES " +
                            "" +
                            "('" + mail + "', 'Castle', '1', '0-0');");

                    connector.executeStatement("" +
                            "SELECT QuestID FROM JansEmpire_QuestList");
                    QueryResult result = connector.getCurrentQueryResult();

                    for (int i = 0; i < result.getRowCount(); i++)
                        connector.executeStatement("" +
                                "INSERT INTO JansEmpire_UserQuestList " +
                                "(Mail, QuestID, Done) " +
                                "" +
                                "VALUES " +
                                "" +
                                "('" + mail + "', '" + result.getData()[i][0] + "', 'false');");
                    if(config.isDebugging()) System.out.println("[MySQL-Info] Die Registierung wurde erfolgreich abgeschlossen...");
                    return true;
                } else return false;
            } else return false;
        }

        /**
         * In dieser Methode werden die einzelnen Tabellen erstellt, falls Sie noch nicht existieren, oder gelöscht wurden
         */
        private void createTableIfNotExist() {

            connector.executeStatement("" +
                    "CREATE TABLE IF NOT EXISTS JansEmpire_Users ( " +
                    "Mail VARCHAR(50) NOT NULL PRIMARY KEY , " +
                    "Username VARCHAR(20) NOT NULL ," +
                    "Password VARCHAR(50) NOT NULL);");

            connector.executeStatement("" +
                    "CREATE TABLE JansEmpire_PlayerData IF NOT EXISTS( " +
                    "Mail VARCHAR(50) NOT NULL PRIMARY KEY, " +
                    "Level INT(2) NOT NULL , " +
                    "XP INT(7) NOT NULL , " +
                    "Wood INT(5) NOT NULL , " +
                    "Stone INT(5) NOT NULL , " +
                    "Wheat INT(5) NOT NULL , " +
                    "Gold INT(7) NOT NULL , " +
                    "Population INT(4) NOT NULL);");
        }

        public String getMail() {

            return mail;
        }
    }
