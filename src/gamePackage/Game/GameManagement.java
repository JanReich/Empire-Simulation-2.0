package gamePackage.Game;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.graphics.Display;
import gamePackage.Game.BackEnd.Player;
import gamePackage.Game.Enviroment.GUI;
import gamePackage.Game.Enviroment.Gamefield;
import gamePackage.Game.Enviroment.Shop;

    public class GameManagement {

                //Attribute

                //Referenzen
            private Display display;
            private DatabaseConnector connector;

        public GameManagement(DatabaseConnector connector, Display display) {

            this.display = display;
            this.connector = connector;
        }

        public void load(String mail) {

            Player player = new Player(connector, mail);

            Gamefield gamefield = new Gamefield(connector, display, player);
            display.getActivePanel().drawObjectOnPanel(gamefield, 0);

            GUI gui = new GUI(player);
            display.getActivePanel().drawObjectOnPanel(gui, 999);

            Shop shop = new Shop(display, player, connector, gamefield);
            display.getActivePanel().drawObjectOnPanel(shop, 100);
        }
    }
