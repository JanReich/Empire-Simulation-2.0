package gamePackage.Game.Buildings;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.abitur.datenbanken.mysql.QueryResult;
import engine.graphics.Display;
import gamePackage.Game.Enviroment.Gamefield;

import java.awt.event.MouseEvent;

    public class Building extends PlaceableObject {

                //Attribute
            private int level;
            private int upSize[];
            private int woodCost;
            private int stoneCost;
            private int wheatCost;
            private int coinCost;
            private int workerCost;

                //Referenzen
            private String typeOfBuilding;

            private Display display;
            private Gamefield gamefield;
            private BuildingOverlay overlay;
            private DatabaseConnector connector;

        /**
         * Dieser Konstruktor wird aufgerufen, wenn ein Gebäude neu gebaut wird.
         */
        public Building(DatabaseConnector connector, Display display, Gamefield gamefield, String type, int width, int height) {

            super(gamefield, type, width, height);

            this.level = 1;
            this.display = display;
            this.typeOfBuilding = type;
            this.gamefield = gamefield;
            this.connector = connector;
            isUpgradeable = isUpgradeble(connector, 1);


        }

        /**
         * Dieser Konstruktor wird aufgerufen, wenn ein gebäude aus der Datenbank geladen wird.
         */
        public Building(DatabaseConnector connector, Display display, Gamefield gamefield, String type, int level, int x, int y, int width, int height) {

            super(gamefield, type, level, x, y, width, height);

            this.level = level;
            this.display = display;
            this.typeOfBuilding = type;
            this.gamefield = gamefield;
            this.connector = connector;
            isUpgradeable = isUpgradeble(connector, level);
            gamefield.simulateBuild(x, y, width, height);


        }


        // ---------- UPGRADE METHODS ---------- \\

        public void generateUpgradeCost(int level) {

            connector.executeStatement("SELECT WoodCost, StoneCost, WheatCost, CoinCost, WorkerAmount, Size FROM JansEmpire_StaticBuildings WHERE Level = '" + (level + 1) + "'  AND Type = '" + typeOfBuilding + "';");

            QueryResult result = connector.getCurrentQueryResult();
            coinCost = Integer.parseInt(result.getData()[0][3]);
            woodCost = Integer.parseInt(result.getData()[0][0]);
            stoneCost = Integer.parseInt(result.getData()[0][1]);
            wheatCost = Integer.parseInt(result.getData()[0][2]);
            workerCost = Integer.parseInt(result.getData()[0][4]);

            String[] tempSize = result.getData()[0][5].split("x");
            upSize = new int[2];
            upSize[0] = Integer.parseInt(tempSize[0]);
            upSize[1] = Integer.parseInt(tempSize[1]);
        }

        public boolean isUpgradeble(DatabaseConnector connector, int level) {

            if(typeOfBuilding.equalsIgnoreCase("Path")) return false;

            connector.executeStatement("SELECT MAX(Level) FROM JansEmpire_StaticBuildings WHERE Type = '" + typeOfBuilding + "'");
            if(level >= Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0])) return false;
            else {

                generateUpgradeCost(level);
                return true;
            }
        }

        public void upgrade() {

            if(isUpgradeable) {

                if(gamefield.getPlayer().checkGoods(woodCost, stoneCost, wheatCost, coinCost, workerCost)) {

                        //Größe bleibt gleich
                    level += 1;
                    loadImage(typeOfBuilding, level);
                    System.out.println(getSize() + " " + upSize[0] + ":" + upSize[1]);
                    String temp = upSize[0] + ":" + upSize[1];
                    if(getSize().equals(temp)) {

                        doUpgrade();
                    } else {

                    }
                }
            }
        }

        private void doUpgrade() {

            gamefield.getPlayer().payResources(woodCost, stoneCost, wheatCost, coinCost, workerCost);
            connector.executeStatement("UPDATE JansEmpire_Buildings SET Level = '" + level + "' WHERE Mail = '" + gamefield.getPlayer().getMail() + "' AND Position = '" + width + "-" + height + "';");
            generateUpgradeCost(level);
        }

        // ---------- UPGRADE METHOD END ---------- \\

        @Override
        public void mouseReleased(MouseEvent e) {

            if(e.getX() > x && e.getX() < x + width && e.getY() > y && e.getY() < y + height && overlay == null) {

                if(!gamefield.isMenuActive()) {

                    overlay = new BuildingOverlay(this, x, y, width, height);
                    display.getActivePanel().drawObjectOnPanel(overlay, 151);
                }
            } else {

                display.getActivePanel().removeObjectFromPanel(overlay);
                overlay = null;
            }
        }

        public BuildingOverlay getBuildingOverlay() {

            return overlay;
        }

        public int getWoodCost() {

            return woodCost;
        }

        public int getStoneCost() {

            return stoneCost;
        }

        public int getWheatCost() {

            return wheatCost;
        }

        public int getCoinCost() {

            return coinCost;
        }

        public int getWorkerCost() {

            return workerCost;
        }
    }
