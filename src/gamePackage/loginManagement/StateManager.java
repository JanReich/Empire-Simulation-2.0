package gamePackage.loginManagement;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.config.MySQLConfig;
import engine.graphics.Display;
import gamePackage.Game.GameManagement;

public class StateManager {

                //Attribute

                //Referenzen
            private String mail;
            private Display display;
            private BackEnd backEnd;
            private DatabaseConnector connector;

        public StateManager(Display display) {

            this.display = display;

            mySQLSetup();
            displayLoginScreen();
        }

        private void mySQLSetup() {

            MySQLConfig config = new MySQLConfig();
            connector = new DatabaseConnector(config.getHost(), Integer.parseInt(config.getPort()), config.getDatabase(), config.getUsername(), config.getPassword());
        }

        /**
         * In dieser Methode wird das Backend erstellt und das Login / Register-form gezeichnet
         */
        public void displayLoginScreen() {

            backEnd = new BackEnd(connector);
            FormLayout layout = new FormLayout(display, backEnd, this);
            display.getActivePanel().drawObjectOnPanel(layout);
        }

        /**
         * Removt alle Elemente vom Display
         */
        public void clearDisplay() {

            mail = backEnd.getMail();
            display.getActivePanel().removeAllObjectsFromPanel();
        }


        public void loadGame() {

            LoadingScreen loadingScreen = new LoadingScreen();
            display.getActivePanel().drawObjectOnPanel(loadingScreen, 3);


            GameManagement management = new GameManagement(connector, display);
            management.load(mail);

            display.getActivePanel().removeObjectFromPanel(loadingScreen);
        }
    }
