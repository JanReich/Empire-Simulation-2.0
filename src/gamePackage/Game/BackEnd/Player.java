package gamePackage.Game.BackEnd;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.abitur.datenbanken.mysql.QueryResult;

    public class Player {

                //Attribute
            private int wood;
            private int stone;
            private int wheat;
            private int coins;
            private int worker;

            private int level;
            private int currentXP;
            private int xpToNexLevel;
            private int storageAmount;

            private int woodPerHour;
            private int stonePerHour;

            private int neededFood;
            private int wheatPerHour;
            private int wheatProduction;

                //Referenzen
            private DatabaseConnector connector;

            private final String mail;
            private final String username;

        public Player(DatabaseConnector connector, String mail) {

            this.mail = mail;
            this.connector = connector;

            connector.executeStatement("" +
                    "SELECT Level, XP, StorageAmount, Wood, Stone, Wheat, Coins, Population FROM JansEmpire_PlayerData WHERE Mail = '" + mail + "';");

            QueryResult result = connector.getCurrentQueryResult();
            level = Integer.parseInt(result.getData()[0][0]);
            currentXP = Integer.parseInt(result.getData()[0][1]);
            storageAmount = Integer.parseInt(result.getData()[0][2]);
            wood = Integer.parseInt(result.getData()[0][3]);
            stone = Integer.parseInt(result.getData()[0][4]);
            wheat = Integer.parseInt(result.getData()[0][5]);
            coins = Integer.parseInt(result.getData()[0][6]);
            worker = Integer.parseInt(result.getData()[0][7]);

            connector.executeStatement("SELECT Username FROM JansEmpire_Users WHERE Mail = '" + mail + "';");
            username = connector.getCurrentQueryResult().getData()[0][0];

            connector.executeStatement("SELECT MAX(Level) FROM JansEmpire_Level");
            int maxLevel = Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);

            if(maxLevel > level) {

                connector.executeStatement("SELECT xp FROM JansEmpire_Level WHERE Level = '" + (level + 1) + "'");
                xpToNexLevel = Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);
            } else xpToNexLevel = -1;
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
                }
            }

            if(maxLevel > level) {

                connector.executeStatement("SELECT xp FROM JansEmpire_Level WHERE Level = '" + (level + 1) + "'");
                xpToNexLevel = Integer.parseInt(connector.getCurrentQueryResult().getData()[0][0]);
            } xpToNexLevel = -1;
        }

        public void updateStorage(int storage) {

            storageAmount = storage;
            connector.executeStatement("" +
                    "UPDATE JansEmpire_PlayerData SET storage = '" + (storageAmount) + "' WHERE Mail = '" + mail +"';");
        }


            //GETTER AND SETTER
        public String getMail() {

            return mail;
        }

        public int getLevel() {

            return level;
        }

        public String getUsername() {

            return username;
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

        public void setWoodPerHour(int woodPerHour) {
            this.woodPerHour = woodPerHour;
        }

        public int getStonePerHour() {
            return stonePerHour;
        }

        public void setStonePerHour(int stonePerHour) {
            this.stonePerHour = stonePerHour;
        }

        public int getWheatPerHour() {
            return wheatPerHour;
        }

        public int getNeededFood() {
            return neededFood;
        }

        public void setNeededFood(int neededFood) {
            this.neededFood = neededFood;
        }

        public int getFoodProduction() {
            return wheatProduction;
        }

        public void setWheatProduction(int wheatProduction) {
            this.wheatProduction = wheatProduction;
        }

        public void setWheatPerHour(int wheatPerHour) {
            this.wheatPerHour = wheatPerHour;
        }
    }