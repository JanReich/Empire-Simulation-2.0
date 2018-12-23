package gamePackage.Game.BackEnd;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

    public class Refresh {

                //Attribute

                //Referenzen
            private Timer timer;
            private Player player;

        public Refresh(Player player) {

            this.player = player;

            timer();
        }

        /**
         * In dieser Methode wird mittels einer TimerTask
         *
         * 1.000 = 1s
         * 10.000 = 10s
         * 60.000 = 60s = 1 Min
         */
        private void timer() {

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    calculateResources();
                }
            }, 0, 10000 * 60);
        }

        /**
         * In dieser Methode wird berechnet, wie viele ressourcen der Spieler bekommt.
         * Dazu werden die Zeit der letzten aktualisierung benötigt, sowie die Produktion pro Stunde
         */
        private void calculateResources() {

            Date lastRefresh = player.getLastRefresh();

            long timeDifferenceMilliseconds = new Date().getTime() - lastRefresh.getTime();
            long diffSeconds = timeDifferenceMilliseconds / 1000;
            long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);

            long coinAmount;
            long woodAmount;
            long stoneAmount;
            long wheatAmount;

                //Max Aktualisierung: Letzte aktualisierung muss mindestens 1 Minute her sein!
            if(diffSeconds >= 60) {

                if(diffDays >= 3) {

                    woodAmount = (long) (player.getWoodPerHour() * 3 * 24);
                    stoneAmount = (long) (player.getStonePerHour() * 3 * 24);
                    wheatAmount = (long) (player.getWheatPerHour() * 3 * 24);
                    coinAmount = (long) (player.getGoldProduction() * 3 * 24);
                } else {

                    double scale = (double) diffSeconds / (double) 60;

                    if(diffSeconds > 3600) System.out.println(Math.round(scale / 60) + " Stunde/n sind seit der letzten aktualisierung vergangen!");
                    else System.out.println(Math.round(scale) + " Minute/n sind seit der letzten aktualisierung vergangen!");

                    scale = scale / (double) 60;
                    woodAmount = (long) ((double) player.getWoodPerHour() * (scale));
                    stoneAmount = (long) ((double) player.getStonePerHour() * (scale));
                    wheatAmount = (long) ((double) player.getWheatPerHour() * (scale));
                    coinAmount = (long) ((double) player.getGoldProduction() * (scale));
                }

                player.deposit((int) woodAmount, (int) stoneAmount, (int) wheatAmount, (int) coinAmount, 0);
            }
        }
    }
