package gamePackage.Game;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.graphics.Display;
import gamePackage.Game.BackEnd.Player;
import gamePackage.Game.BackEnd.Refresh;
import gamePackage.Game.Enviroment.GUI;
import gamePackage.Game.Enviroment.Gamefield;
import gamePackage.Game.Enviroment.Shop;
import gamePackage.Game.Quest.QuestBook;

    public class GameManagement {

                //Attribute

                //Referenzen
            private Shop shop;
            private Gamefield gamefield;

            private Display display;
            private QuestBook questBook;
            private DatabaseConnector connector;

        public GameManagement(DatabaseConnector connector, Display display) {

            this.display = display;
            this.connector = connector;
        }

        public void load(String mail) {

            Player player = new Player(connector, mail);

            gamefield = new Gamefield(this, connector, display, player);
            display.getActivePanel().drawObjectOnPanel(gamefield, 0);

            GUI gui = new GUI(player);
            display.getActivePanel().drawObjectOnPanel(gui, 999);

            shop = new Shop(gamefield.getBuildingSystem(), player, display, connector);
            display.getActivePanel().drawObjectOnPanel(shop, 100);

            questBook = new QuestBook(connector, display,this, player);
            display.getActivePanel().drawObjectOnPanel(questBook, 200);

            Refresh refresh = new Refresh(player);
        }

        public void closeShop() {

            shop.close();
        }

        public void refreshShop() {

            shop.refreshShop();
        }

        public boolean isQuestBookOpened() {

            return questBook.isOpened();
        }

        public void setBuildingMode(boolean mode) {

            gamefield.setBuildingMode(mode);
        }

        public void refreshQuestbook(String type) {

            questBook.refresh(type);
        }
    }