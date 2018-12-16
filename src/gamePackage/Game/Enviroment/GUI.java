package gamePackage.Game.Enviroment;

import engine.graphics.interfaces.GraphicalObject;
import engine.graphics.interfaces.LiteInteractableObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;
import gamePackage.Game.BackEnd.Player;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

    public class GUI implements LiteInteractableObject {

                //Attribute
            private boolean hoverWood;
            private boolean hoverStone;
            private boolean hoverWheat;
            private boolean hoverMilitary;

            private BufferedImage woodOverlay;
            private BufferedImage stoneOverlay;
            private BufferedImage wheatOverlay;
            private BufferedImage militaryOverlay;

                //Referenzen
            private Player player;
            private BufferedImage gui;
            private BufferedImage taxGUI;
            private BufferedImage menuGUI;
            private BufferedImage resourceBar;

        public GUI(Player player) {

            this.player = player;
            this.gui = ImageHelper.getImage("res/images/Gui/GUI.png");
            this.taxGUI = ImageHelper.getImage("res/images/Gui/TaxGUI.png");
            this.menuGUI = ImageHelper.getImage("res/images/Gui/MenuGUI.png");
            this.resourceBar = ImageHelper.getImage("res/images/Gui/ResourceBar.png");

            this.woodOverlay = ImageHelper.getImage("res/images/Gui/wood.png");
            this.stoneOverlay = ImageHelper.getImage("res/images/Gui/stone.png");
            this.wheatOverlay = ImageHelper.getImage("res/images/Gui/wheat.png");
            this.militaryOverlay = ImageHelper.getImage("res/images/Gui/military.png");
        }

        @Override
        public void update(double delta) {

        }

        @Override
        public void draw(DrawHelper draw) {

            draw.drawImage(menuGUI, 842, 875, 176.8, 120.9);
            draw.drawImage(taxGUI, 810, 850, 121 * 1.3, 41 * 1.3);
            draw.drawImage(resourceBar, 240, 0, 418 * 1.3, 36 * 1.3);

            double scale1 = (double) player.getWood() / (double) player.getStorageAmount();
            double scale2 = (double) player.getStone() / (double) player.getStorageAmount();
            double scale3 = (double) player.getWheat() / (double) player.getStorageAmount();

            if(scale1 > 1) scale1 = 1;
            if(scale2 > 1) scale2 = 1;
            if(scale3 > 1) scale3 = 1;

            draw.setColour(Color.RED);
            draw.fillRec(328, 18, (int) (45 * scale1), 5);
            draw.fillRec(418, 18, (int) (45 * scale2), 5);
            draw.fillRec(508, 18, (int) (45 * scale3), 5);

            draw.setColour(Color.BLACK);
            draw.setFont(new Font("Roboto", Font.PLAIN, 12));
            final DecimalFormat separator = new java.text.DecimalFormat("##,###");

            draw.drawString(separator.format(player.getWood()), 328, 35, 45);
            draw.drawString(separator.format(player.getStone()), 417, 35, 45);
            draw.drawString(separator.format(player.getWheat()), 507, 35, 45);

            draw.drawString(separator.format(player.getPopulation()), 595, 35, 45);
            draw.drawString("0", 683, 35, 45);

            draw.drawImage(gui, 0, 5, 134 * 1.3, 135 * 1.3);
            draw.setFont(new Font("Roboto", Font.BOLD, 20));
            if(player.getLevel() >= 10) draw.drawString(player.getLevel() + "", 112, 68);
            else draw.drawString(player.getLevel() + "", 118, 68);

            draw.setFont(new Font("Roboto", Font.BOLD, 14));
            draw.drawString(player.getUsername(), 4 + (145 - draw.getFontWidth(player.getUsername())) / 2, 115);
            draw.setFont(new Font("Roboto", Font.PLAIN, 14));
            draw.drawString(separator.format(player.getCoins()), 37, 158, 70);

            draw.setColour(new Color(36, 45, 51));
            draw.setFont(new Font("Roboto", Font.BOLD, 14));

            int currentProgress = player.getCurrentXP();
            double xpToNexLevel = player.getXpToNexLevel();

            double progress;
            if(xpToNexLevel != -1) {

                progress = ((double) currentProgress / xpToNexLevel);

            } else progress = 1;
            draw.fillRec(10, 80, (int) (128 * progress), 17);
            draw.setColour(Color.WHITE);
            draw.drawString(separator.format(player.getCurrentXP()), 47, 93);

            if(hoverWood) {

                draw.setColour(Color.BLACK);
                draw.setFont(new Font("Roboto", Font.PLAIN, 12));
                draw.drawImage(woodOverlay, 290, 46, 150 , 80);
                draw.drawString(separator.format(player.getWoodPerHour()), 298, 95, 35);
                draw.drawString(separator.format(player.getStorageAmount()), 401, 114);
            }

            else if(hoverStone) {

                draw.setColour(Color.BLACK);
                draw.setFont(new Font("Roboto", Font.PLAIN, 12));
                draw.drawImage(stoneOverlay, 335, 46, 150 , 80);
                draw.drawString(separator.format(player.getStonePerHour()), 348, 95, 35);
                draw.drawString(separator.format(player.getStorageAmount()), 446, 114);
            }

            else if(hoverWheat) {

                draw.setColour(Color.BLACK);
                draw.setFont(new Font("Roboto", Font.PLAIN, 12));
                draw.drawImage(wheatOverlay, 425, 46, 150 , 150);

                draw.drawString(separator.format(player.getFoodProduction()), 431, 108, 35);
                draw.drawString(separator.format(player.getNeededFood()), 431, 128, 35);

                if(player.getWheatPerHour() >= 0) draw.setColour(Color.BLACK);
                else draw.setColour(Color.RED);

                draw.drawString(separator.format(player.getWheatPerHour()), 431, 158, 35);
                draw.setColour(Color.BLACK);
                draw.drawString(separator.format(player.getStorageAmount()), 515, 178);
            }

            else if(hoverMilitary) {

                draw.setColour(Color.BLACK);
                draw.setFont(new Font("Roboto", Font.PLAIN, 12));
                draw.drawImage(militaryOverlay, 535, 46, 200 , 220);
            }

            draw.setColour(Color.WHITE);
            draw.setFont(new Font("Roboto", Font.BOLD, 14));
            draw.drawString("\'R\' - Refresh", 840, 30);
            draw.drawString("\'C\' - Cheat Ressources", 840, 50);
        }

        @Override
        public void mouseReleased(MouseEvent event) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

            if(isInside(e, 290, 12, 88,30)) hoverWood = true; else  hoverWood = false;
            if(isInside(e, 380, 12, 88, 30)) hoverStone = true; else hoverStone = false;
            if(isInside(e, 479, 12, 88, 30)) hoverWheat = true; else hoverWheat = false;
            if(isInside(e, 650, 12, 88, 30)) hoverMilitary = true; else hoverMilitary = false;
        }

        private boolean isInside(MouseEvent e, int x, int y, int width, int height) {

            if(e.getX() > x && e.getX() < x + width && e.getY() > y && e.getY() < y + height) return true; return false;
        }
    }
