package gamePackage.Game.Buildings;

    public class UpgradeInformation {

            //Attribute
        private int woodCost;
        private int coinCost;
        private int stoneCost;
        private int wheatCost;
        private int workerCost;
        private int[] upgradeSize;

            //Referenzen

        public UpgradeInformation(int woodCost, int coinCost, int stoneCost, int wheatCost, int workerCost, int[] upgradeSize) {

            this.woodCost = woodCost;
            this.coinCost = coinCost;
            this.stoneCost = stoneCost;
            this.wheatCost = wheatCost;
            this.workerCost = workerCost;
            this.upgradeSize = upgradeSize;
        }

        public int getWoodCost() {

            return woodCost;
        }

        public int getCoinCost() {

            return coinCost;
        }

        public int getStoneCost() {

            return stoneCost;
        }

        public int getWheatCost() {

            return wheatCost;
        }

        public int getWorkerCost() {

            return workerCost;
        }

        public int[] getUpgradeSize() {

            return upgradeSize;
        }
    }