package gamePackage.Game.BackEnd;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.abitur.datenbanken.mysql.QueryResult;

import java.text.SimpleDateFormat;
import java.util.Date;

    public class Player {

                //Attribute
            private int wood;
            private int stone;
            private int wheat;
            private int coins;
            private int worker;

            private int level;
            private int maxLevel;
            private int currentXP;
            private int xpToNexLevel;
            private int storageAmount;

            private int woodPerHour;
            private int stonePerHour;

            private int population;
            private int neededFood;
            private int wheatPerHour;
            private int wheatProduction;

            private int neededGold;
            private int collectedGold;
            private int goldProduction;

                //Referenzen
            private String mail;
            private String username;

            private DatabaseConnector connector;

        public Player(DatabaseConnector connector, String mail) {

            this.mail = mail;
            this.connector = connector;

            loadPlayerData();
        }

        private void loadPlayerData() {

                //Laden der PlayerData
            QueryResult playerData = getPlayerData();

            level = Integer.parseInt(playerData.getData()[0][0]);
            currentXP = Integer.parseInt(playerData.getData()[0][1]);
            storageAmount = Integer.parseInt(playerData.getData()[0][2]);
            wood = Integer.parseInt(playerData.getData()[0][3]);
            stone = Integer.parseInt(playerData.getData()[0][4]);
            wheat = Integer.parseInt(playerData.getData()[0][5]);
            coins = Integer.parseInt(playerData.getData()[0][6]);
            worker = Integer.parseInt(playerData.getData()[0][7]);
            username = playerData.getData()[0][8];

                //Laden der Level relevanten Daten
            connector.executeStatement("SELECT MAX(Level) FROM JansEmpire_Level");
            maxLevel = Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);

            if(maxLevel > level) {

                xpToNexLevel = getXPtoNextLevel();
            } else xpToNexLevel = -1;

                //Laden der Ressourcen die Pro Stunde berechnet werden
            QueryResult buildingData = getBuildings();

            for (int i = 0; i < buildingData.getRowCount(); i++) {

                population += Integer.parseInt(buildingData.getData()[i][0]);

                woodPerHour += Integer.parseInt(buildingData.getData()[i][1]);
                stonePerHour += Integer.parseInt(buildingData.getData()[i][2]);
                wheatProduction += Integer.parseInt(buildingData.getData()[i][3]);

                neededGold += Integer.parseInt(buildingData.getData()[i][5]);
                collectedGold += Integer.parseInt(buildingData.getData()[i][4]);
            }

            neededFood = population * 3;
            goldProduction = collectedGold - neededGold;
            wheatPerHour = wheatProduction - neededFood;
        }

        /**
         * In dieser Methode bekommt ein Spieler Ressourcen gutgeschrieben.
         * Dieser werden auch direkt mit der Datenbank syncronisiert
         */
        public void deposit(int wood, int stone, int wheat, int coins, int worker) {

            this.wood += wood;
            if(this.wood > storageAmount) this.wood = storageAmount;

            this.stone += stone;
            if(this.stone > storageAmount) this.stone = storageAmount;

            this.wheat += wheat;
            if(this.wheat > storageAmount) this.wheat = storageAmount;

            this.coins += coins;
            this.worker += worker;

            connector.executeStatement("" +
                    "UPDATE JansEmpire_PlayerData SET " +
                    "Wood = '" + this.wood +  "'," +
                    "Stone = '" + this.stone + "'," +
                    "Wheat = '" + this.wheat + "'," +
                    "Coins = '" + this.coins + "'," +
                    "Population = '" + this.worker + "'" +
                    "WHERE Mail = '" + mail + "';");
        }

        /**
         * In dieser Methode muss der Spieler Ressourcen bezahlen.
         * Diese werden auch direkt mit der Datenbank syncronisiert
         * @return true = Player has payed || false = Player has not enough resources
         */
        public boolean payResources(int wood, int stone, int wheat, int coins, int worker) {

            if(checkGoods(wood, stone, wheat, coins, worker)) {

                this.wood -= wood;
                this.stone -= stone;
                this.wheat -= wheat;
                this.coins -= coins;
                this.worker -= worker;

                //Update in mySQL
                connector.executeStatement("" +
                        "UPDATE JansEmpire_PlayerData SET " +
                        "Wood = '" + this.wood +  "'," +
                        "Stone = '" + this.stone + "'," +
                        "Wheat = '" + this.wheat + "'," +
                        "Coins = '" + this.coins + "'," +
                        "Population = '" + this.worker + "'" +
                        "WHERE Mail = '" + mail + "';");

                return true;
            } else return false;
        }

        /**
         * In dieser Methode wird überprüft ob der Spieler genug Ressoucren hat
         * @return true = Player has enough resources || false = Player has not enough resources
         */
        public boolean checkGoods(int wood, int stone, int wheat, int coins, int worker) {

            if(wood <= this.wood && stone <= this.stone && wheat <= this.wheat && coins <= this.coins && worker <= this.worker) return true;
            return false;
        }

        public void addXP(int xp) {

            this.currentXP += xp;

            connector.executeStatement("" +
                            "UPDATE JansEmpire_PlayerData SET XP = '" + this.currentXP + "' WHERE Mail = '" + mail +"';");

            connector.executeStatement("SELECT MAX(Level) FROM JansEmpire_Level");
            int maxLevel = Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);


            if(level != maxLevel) {

                if(this.currentXP >= xpToNexLevel) {

                    level += 1;
                    connector.executeStatement("" +
                            "UPDATE JansEmpire_PlayerData SET Level = '" + level + "' WHERE Mail = '" + mail +"';");
                    connector.executeStatement("SELECT xp FROM JansEmpire_Level WHERE Level = '" + level + "'");
                    xpToNexLevel = Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);
                }
            }

            if(maxLevel > level) {

                connector.executeStatement("SELECT xp FROM JansEmpire_Level WHERE Level = '" + (level + 1) + "'");
                xpToNexLevel = Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);
            } else xpToNexLevel = -1;
        }

                //---------- Komfort-Methoden ---------- \\
            /**
             * Diese Methode gibt alle Gebäude die sich im Besitz des Players
             * befinden wieder. Anhand dieser können und Berechnungen durchgeführt werden.
             */
            public QueryResult getBuildings() {

                connector.executeStatement("SELECT JansEmpire_StaticBuildings.LivingRoom, JansEmpire_StaticBuildings.WoodProduction, JansEmpire_StaticBuildings.StoneProduction, JansEmpire_StaticBuildings.WheatProduction, JansEmpire_StaticBuildings.CoinProduction, JansEmpire_StaticBuildings.NeededGold FROM JansEmpire_Buildings " +
                        "INNER JOIN JansEmpire_StaticBuildings ON JansEmpire_Buildings.Type = JansEmpire_StaticBuildings.Type AND JansEmpire_Buildings.Level = JansEmpire_StaticBuildings.Level " +
                        "WHERE Mail = '" + mail + "';");
                return connector.getCurrentQueryResult();
            }

            /**
             * Dieser Methode return alle relevanten PlayerDaten die in Bezug auf die aktualle
             * Session wichtig sind
             */
            public QueryResult getPlayerData() {

                connector.executeStatement("SELECT JansEmpire_PlayerData.Level, JansEmpire_PlayerData.XP, JansEmpire_PlayerData.StorageAmount, JansEmpire_PlayerData.Wood, JansEmpire_PlayerData.Stone, JansEmpire_PlayerData.Wheat, JansEmpire_PlayerData.Coins, JansEmpire_PlayerData.Population, JansEmpire_Users.Username FROM JansEmpire_PlayerData " +
                        "INNER JOIN JansEmpire_Users ON JansEmpire_PlayerData.Mail = JansEmpire_Users.Mail " +
                        "WHERE JansEmpire_PlayerData.Mail = '" + mail + "';");
                return connector.getCurrentQueryResult();
            }

            /**
             * In dieser Methode werden die benötigten Ressourcen bis zum nächsten Level berechnet.
             */
            public int getXPtoNextLevel() {

                connector.executeStatement("SELECT xp FROM JansEmpire_Level WHERE Level = '" + (level + 1) + "';");
                return Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);
            }

            /**
             * Returnt das den Zeitpunkt der letzten aktualisierung
             */
            public Date getLastRefresh() {

                connector.executeStatement("SELECT LastRefresh FROM JansEmpire_PlayerData WHERE Mail = '" + mail + "';");
                String lastRefreshData = connector.getCurrentQueryResult().getData()[0][0];

                try {

                    SimpleDateFormat sdfToDate = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                    return sdfToDate.parse(lastRefreshData);
                } catch (Exception e) {

                    System.out.println("Datetime - Error beim Formatieren");
                }
                return null;
            }

        //GETTER AND SETTER
        public String getMail() {

            return mail;
        }

        public int getLevel() {

            return level;
        }

        public int getCoins() {

            return coins;
        }

        public int getWood() {

            return wood;
        }

        public int getStone() {

            return stone;
        }

        public int getWheat() {

            return wheat;
        }

        public String getUsername() {

            return username;
        }

        public int getPopulation() {

            return worker;
        }

        public int getStorageAmount() {

            return storageAmount;
        }

        public int getXpToNexLevel() {

            return xpToNexLevel;
        }

        public int getCurrentXP() {

            return currentXP;
        }

        public int getWoodPerHour() {

            return woodPerHour;
        }

        public int getStonePerHour() {

            return stonePerHour;
        }

        public int getWheatPerHour() {

            return wheatPerHour;
        }

        public int getNeededFood() {

            return neededFood;
        }

        public int getFoodProduction() {

            return wheatProduction;
        }

        public int getGoldProduction() {

            return goldProduction;
        }
    }