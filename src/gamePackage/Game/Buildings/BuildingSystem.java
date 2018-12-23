package gamePackage.Game.Buildings;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.abitur.datenbanken.mysql.QueryResult;
import engine.graphics.Display;
import gamePackage.Game.BackEnd.Player;
import gamePackage.Game.Enviroment.Gamefield;
import gamePackage.Game.GameManagement;

import java.util.ArrayList;

    public class BuildingSystem {

                //Attribute
            private final int offsetX;
            private final int offsetY;
            private final int amountOfFields;
            private final int fieldSquareSize;

            private int[][] fieldInformation;

                //Referenzen
            private Player player;
            private Display display;
            private Gamefield gamefield;
            private DatabaseConnector connector;

            private BuildingTemplate template;
            private GameManagement gameManager;
            private ArrayList<Building> buildings;
            private PathManagement pathManagement;

        public BuildingSystem(GameManagement gameManager, DatabaseConnector connector, Display display, Player player, Gamefield gamefield) {

            this.player = player;
            this.display = display;
            this.connector = connector;
            this.gamefield = gamefield;
            this.gameManager = gameManager;
            this.buildings = new ArrayList<>();

            offsetX = gamefield.getOffsetX();
            offsetY = gamefield.getOffsetY();
            amountOfFields = gamefield.getAmountOfFields();
            fieldSquareSize = gamefield.getFieldSquareSize();
            fieldInformation = new int[amountOfFields][amountOfFields];
            pathManagement = new PathManagement(this);
            display.getActivePanel().drawObjectOnPanel(pathManagement, 150);

            loadBuildings();
        }

        /**
         * In dieser Methode werden die Gebäude aus der Datenbank geladen.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         * SQL-Abfrage, die Type, Level, Position und Size auswählt.
         * Um diese Informationen zu bekommen, wird ein Join zwischen zwei Relationen durchgeführt
         * Gebäude erstellt und dem Display hinzugefügt
         */
        private void loadBuildings() {

            connector.executeStatement("SELECT JansEmpire_Buildings.Type, JansEmpire_Buildings.Level, JansEmpire_Buildings.Position, JansEmpire_StaticBuildings.Size " +
                    "FROM JansEmpire_Buildings INNER JOIN JansEmpire_StaticBuildings " +
                    "ON JansEmpire_Buildings.Type = JansEmpire_StaticBuildings.Type AND JansEmpire_Buildings.Level = JansEmpire_StaticBuildings.Level " +
                    "WHERE Mail = '" + player.getMail() + "';");
            QueryResult result = connector.getCurrentQueryResult();

            for (int i = 0; i < result.getRowCount(); i++) {

                if(!result.getData()[i][0].equalsIgnoreCase("Path")) {

                    String[] size = result.getData()[i][3].split("x");
                    String[] position = result.getData()[i][2].split("-");

                    Building building = new Building(this, result.getData()[i][0], Integer.parseInt(position[0]),  Integer.parseInt(position[1]), Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(result.getData()[i][1]));
                    buildings.add(building);
                    display.getActivePanel().drawObjectOnPanel(building, 150);
                    updateFieldInformation(Integer.parseInt(position[0]),  Integer.parseInt(position[1]), Integer.parseInt(size[0]), Integer.parseInt(size[1]), 1);

                    connector.executeStatement("SELECT MAX(Level) FROM JansEmpire_StaticBuildings WHERE Type = '" + result.getData()[i][0] + "';");
                    if(building.getLevel() < Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0])) {

                        building.setUpgradeable(true);
                        building.setUpgradeinformation(generateUpgradeInformation(building));
                    } else building.setUpgradeable(false);
                } else {

                    String[] position = result.getData()[i][2].split("-");
                    pathManagement.loadPath(Integer.parseInt(position[0]),  Integer.parseInt(position[1]));
                }
            }
        }

        /**
         * Diese Methode wird aufgerufen, wenn ein Gebäude beweget werden soll.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *
         */
        public void move(Building building, int x, int y) {

            int[] field = getField(x, y);
            String oldPosition = building.getPosition();
            if(field != null && isFieldEmpty(field[0], field[1], building.getFieldWidth(), building.getFieldHeight())) {

                building.setMoving(false);
                building.setFieldX(field[0]);
                building.setFieldY(field[1]);
                updateFieldInformation(field[0], field[1], building.getFieldWidth(), building.getFieldHeight(), 1);

                connector.executeStatement("" +
                        "UPDATE JansEmpire_Buildings SET Position = '" + building.getPosition() + "' WHERE Mail = '" + player.getMail() + "' AND Position = '" + oldPosition + "';");
            } else {

                building.setMoving(false);
                updateFieldInformation(building.getFieldX(), building.getFieldY(), building.getFieldWidth(), building.getFieldHeight(), 1);
            } setBuildingMode(false);
        }

        /**
         * Diese Methode wird aufgerufen, wenn ein Gebäude neu gebaut wird.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *
         */
        public void build(String type) {

            connector.executeStatement("SELECT Size FROM JansEmpire_StaticBuildings WHERE Type = '" + type + "' AND Level = '" + 1 + "';");

            String[] size = connector.getCurrentQueryResult().getData()[0][0].split("x");
            Building building = new Building(this, type, Integer.parseInt(size[0]), Integer.parseInt(size[1]));
            buildings.add(building);
            display.getActivePanel().drawObjectOnPanel(building, 150);
            building.setUpgradeinformation(generateUpgradeInformation(building));

            connector.executeStatement("SELECT MAX(Level) FROM JansEmpire_StaticBuildings WHERE Type = '" + type + "';");
            if(building.getLevel() < Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0])) {

                building.setUpgradeable(true);
                building.setUpgradeinformation(generateUpgradeInformation(building));
            } else building.setUpgradeable(false);
            gameManager.closeShop();
            gameManager.refreshShop();
        }


        /**
         * Diese Methode wird aufgerufen, wenn ein Gebäude neu gebaut wird.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *
         */
        public void buildPath() {

            pathManagement.setBuilding(true);
        }

        /**
         * Diese Methode wird aufgerufen, wenn ein gebäude zerstört wird.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *
         */
        public void destroy(Building building) {

            connector.executeStatement("DELETE FROM JansEmpire_Buildings WHERE Mail = '" + player.getMail() + "' AND Position = '" + building.getPosition() + "';");

            removeSelectedBuilding(building);
            updateFieldInformation(building.getFieldX(), building.getFieldY(), building.getFieldWidth(), building.getFieldHeight(), 0);
            gameManager.refreshShop();
            gameManager.refreshQuestbook(building.getType());
        }

        /**
         * Diese Methode wird aufgerufen, wenn ein gebäude upgegradet wird.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *
         */
        public void upgrade(Building building, int x, int y) {

            int[] position = getField(x, y);
            String oldPosition;
            UpgradeInformation information = building.getUpgradeinformation();
            if(position != null && isFieldEmpty(position[0], position[1], building.getFieldWidth(), building.getFieldHeight()) && player.checkGoods(information.getWoodCost(), information.getStoneCost(), information.getWheatCost(), information.getCoinCost(), information.getWorkerCost())) {

                building.setMoving(false);
                building.setUpgrading(false);

                oldPosition = building.getPosition();
                building.setFieldX(position[0]);
                building.setFieldY(position[1]);
                updateFieldInformation(building.getFieldX(), building.getFieldY(), building.getFieldWidth(), building.getFieldHeight(), 1);

                player.payResources(information.getWoodCost(), information.getStoneCost(), information.getWheatCost(), information.getCoinCost(), information.getWorkerCost());
                connector.executeStatement("UPDATE JansEmpire_Buildings SET Level = '" + building.getLevel() + "', Position = '" + building.getPosition() + "' WHERE Mail = '" + player.getMail() + "' AND Position = '" + oldPosition + "';");

                connector.executeStatement("SELECT MAX(Level) FROM JansEmpire_StaticBuildings WHERE Type = '" + building.getType() + "';");
                if(building.getLevel() < Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0])) {

                    building.setUpgradeable(true);
                    building.setUpgradeinformation(generateUpgradeInformation(building));
                } else building.setUpgradeable(false);
            } else {

                building.setMoving(false);
                building.setUpgrading(false);
                building.setLevel(building.getLevel() - 1);

                connector.executeStatement("SELECT Size FROM JansEmpire_StaticBuildings WHERE Type = '" + building.getType() + "' AND Level = '" + building.getLevel() + "';");
                String[] size = connector.getCurrentQueryResult().getData()[0][0].split("x");
                building.setFieldWidth(Integer.parseInt(size[0]));
                building.setFieldHeight(Integer.parseInt(size[1]));
                updateFieldInformation(building.getFieldX(), building.getFieldY(), building.getFieldWidth(), building.getFieldHeight(), 1);
            }
            setBuildingMode(false);
            gameManager.closeShop();
        }

        /**
         * Diese Methode wird aufgerufen, wenn ein gebäude gebaut wird.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *  Die Feldinformationen, auf dem das Gebäude steht werden überarbeitet
         *  Das Gebäude in die Datenbank eintragen
         *  Ressourcen werden dem Spieler abgezogen
         *
         */
        public void saveBuilding(Building building) {

            connector.executeStatement("SELECT WoodCost, StoneCost, WheatCost, CoinCost, WorkerAmount, StorageAmount, LivingRoom FROM JansEmpire_StaticBuildings WHERE Type = '" + building.getType() + "' AND Level = '" + building.getLevel() + "'");
            QueryResult result = connector.getCurrentQueryResult();

            if(player.checkGoods(Integer.parseInt(result.getData()[0][0]), Integer.parseInt(result.getData()[0][1]), Integer.parseInt(result.getData()[0][2]), Integer.parseInt(result.getData()[0][3]), Integer.parseInt(result.getData()[0][4]))) {

                setBuildingMode(false);
                player.payResources(Integer.parseInt(result.getData()[0][0]), Integer.parseInt(result.getData()[0][1]), Integer.parseInt(result.getData()[0][2]), Integer.parseInt(result.getData()[0][3]), Integer.parseInt(result.getData()[0][4]));
                updateFieldInformation(building.getFieldX(),building.getFieldY(), building.getFieldWidth(), building.getFieldHeight(), 1);
                connector.executeStatement("" +
                        "INSERT INTO JansEmpire_Buildings (" +
                        "" +
                        "Mail, Type, Level, Position) " +
                        "" +
                        "VALUES (" +
                        "" +
                        "'" + player.getMail() + "', '" + building.getType() + "', '" + building.getLevel() + "', '" + building.getFieldX() + "-" + building.getFieldY() + "');");
                gameManager.closeShop();
                gameManager.refreshShop();
                gameManager.refreshQuestbook(building.getType());
            } else removeSelectedBuilding(building);
        }

        public void buildPath(int movingX, int movingY) {

            int[] fields = getField(movingX, movingY);

            if(fields != null) {

                if (isFieldEmpty(fields[0], fields[1], 1, 1)) {

                    UpgradeInformation information = pathManagement.getUpgradeInformation();
                    if (player.checkGoods(information.getWoodCost(), information.getStoneCost(), information.getWheatCost(), information.getCoinCost(), information.getWorkerCost())) {

                        player.payResources(information.getWoodCost(), information.getStoneCost(), information.getWheatCost(), information.getCoinCost(), information.getWorkerCost());
                        pathManagement.loadPath(fields[0], fields[1]);
                        connector.executeStatement("" +
                                "INSERT INTO JansEmpire_Buildings (" +
                                "" +
                                "Mail, Type, Level, Position) " +
                                "" +
                                "VALUES (" +
                                "" +
                                "'" + player.getMail() + "', '" + "Path" + "', '" + 1 + "', '" + fields[0] + "-" + fields[1] + "');");
                        pathManagement.setBuilding(false);
                    } else pathManagement.setBuilding(false);
                } else pathManagement.setBuilding(false);
            }
        }

        /**
         * Diese Methode wird aufgerufen, wenn ein gebäude geupdatet werden muss.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *
         */
        public void updateBuilding(Building building) {


        }

        /**
         * Diese Methode wird aufgerufen, wenn die Upgradekosten geupdatet werden muss.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *
         */
        private UpgradeInformation generateUpgradeInformation(Building building) {

            connector.executeStatement("SELECT WoodCost, StoneCost, WheatCost, CoinCost, WorkerAmount, Size FROM JansEmpire_StaticBuildings WHERE Type = '" + building.getType() + "' AND Level = '" + (building.getLevel() + 1) + "';");
            QueryResult result = connector.getCurrentQueryResult();

            String[] tempSize = result.getData()[0][5].split("x");
            int[] size = new int[2];
            size[0] = Integer.parseInt(tempSize[0]);
            size[1] = Integer.parseInt(tempSize[1]);

            return new UpgradeInformation(Integer.parseInt(result.getData()[0][0]), Integer.parseInt(result.getData()[0][1]), Integer.parseInt(result.getData()[0][2]), Integer.parseInt(result.getData()[0][3]), Integer.parseInt(result.getData()[0][4]), size);
        }

        /**
         * Diese Methode wird aufgerufen, wenn die Upgradekosten geupdatet werden muss.
         * Im Zuge dessen wird folgendes ausgeführt:
         *
         *
         */
        public UpgradeInformation generateUpgradeInformation(String type) {

            connector.executeStatement("SELECT WoodCost, StoneCost, WheatCost, CoinCost, WorkerAmount, Size FROM JansEmpire_StaticBuildings WHERE Type = '" + type + "' AND Level = '" + 1 + "';");
            QueryResult result = connector.getCurrentQueryResult();

            String[] tempSize = result.getData()[0][5].split("x");
            int[] size = new int[2];
            size[0] = Integer.parseInt(tempSize[0]);
            size[1] = Integer.parseInt(tempSize[1]);

            return new UpgradeInformation(Integer.parseInt(result.getData()[0][0]), Integer.parseInt(result.getData()[0][1]), Integer.parseInt(result.getData()[0][2]), Integer.parseInt(result.getData()[0][3]), Integer.parseInt(result.getData()[0][4]), size);
        }

            //---------- Komfort-Methoden ---------- \\
        public int[] getField(int mouseX, int mouseY) {

            if(mouseX < offsetX || mouseY < offsetY || mouseX > offsetX + (amountOfFields * fieldSquareSize) || mouseY > offsetY + (amountOfFields * fieldSquareSize)) return null;

            int[] fields = new int[2];
            fields[0] = (mouseX - offsetX) / fieldSquareSize;
            fields[1] = (mouseY - offsetY) / fieldSquareSize;
            return fields;
        }

        /**
         * Mit dieser Methode wird überprüft, ob ein Gebäude an der angegebenen Stelle gebaut werden kann
         * @return true || false
         */
        public boolean isFieldEmpty(int fieldX, int fieldY, int fieldWidth, int fieldHeight) {

            for (int row = fieldX; row < fieldX + fieldWidth; row++) {
                for (int col = fieldY; col < fieldY + fieldHeight; col++) {

                    if(fieldInformation[row][col] == 1) return false;
                }
            }
            return true;
        }

        /**
         * Mit dieser Methode werden die Feldinformationen überarbeitet
         * @return true || false
         */
        public void updateFieldInformation(int fieldX, int fieldY, int fieldWidth, int fieldHeight, int value) {

            for (int row = fieldX; row < fieldX + fieldWidth; row++) {
                for (int col = fieldY; col < fieldY + fieldHeight; col++) {

                    fieldInformation[row][col] = value;
                }
            }
        }

        public void setTemplate(Building building) {

            if(template == null && building != null) {

                template = new BuildingTemplate(this, building, offsetX + (building.getFieldX() * fieldSquareSize), offsetY + (building.getFieldY() * fieldSquareSize), building.getFieldWidth() * fieldSquareSize, building.getFieldHeight() * fieldSquareSize);
                display.getActivePanel().drawObjectOnPanel(template, 151);
            } else {

                display.getActivePanel().removeObjectFromPanel(template);
                template = null;
            }
        }

        /**
         * Diese Methode removed Gebäude vom Display
         * @param building - das Gebäude, dass gelöscht werden soll
         */
        public void removeSelectedBuilding(Building building) {

            gameManager.closeShop();
            setBuildingMode(false);
            display.getActivePanel().removeObjectFromPanel(building);
            buildings.remove(building);
        }

        /**
         * In dieser Methode wird überprüft ob der Spieler genug Ressoucren hat
         * @return true = Player has enough resources || false = Player has not enough resources
         */
        public boolean checkResources(int wood, int stone, int wheat, int coins, int worker) {

            return player.checkGoods(wood, stone, wheat, coins, worker);
        }

        /**
         * In dieser Methode muss der Spieler Ressourcen bezahlen.
         * Diese werden auch direkt mit der Datenbank syncronisiert
         * @return true = Player has payed || false = Player has not enough resources
         */
        public boolean payResources(int wood, int stone, int wheat, int coins, int worker) {

            return player.payResources(wood, stone, wheat, coins, worker);
        }

            //---------- GETTER AND SETTER ---------- \\
        public int getOffsetX() {

            return offsetX;
        }

        public int getOffsetY() {

            return offsetY;
        }

        public int getAmountOfFields() {

            return amountOfFields;
        }

        public int getFieldSquareSize() {

            return fieldSquareSize;
        }

        public boolean isQuestBookOpen() {

            return gameManager.isQuestBookOpened();
        }

        public int[][] getFieldInformation() {

            return fieldInformation;
        }

        public void setBuildingMode(boolean mode) {

            gamefield.setBuildingMode(mode);
        }
    }
