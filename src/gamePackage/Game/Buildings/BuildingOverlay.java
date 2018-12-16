package gamePackage.Game.Buildings;

import engine.graphics.interfaces.LiteInteractableObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

    public class BuildingOverlay implements LiteInteractableObject {

                //Attribute
            private int x;
            private int y;
            private int width;
            private int height;

            private int posX;
            private int posY;

            private boolean destroyed;
            private boolean drawPrice;

            private boolean path;
            private PathManagement pathManagement;

                //Referenzen
            private Building building;
            private BufferedImage move;
            private BufferedImage destroy;
            private BufferedImage priceList;
            private BufferedImage upgradeButton;

        public BuildingOverlay(PathManagement pathManagement, int x, int y, int width, int height, int posX, int posY) {

            this.posX = posX;
            this.posY = posY;

            path = true;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.pathManagement = pathManagement;
            this.move = ImageHelper.getImage("res/images/Gui/Shop/move.png");
            this.destroy = ImageHelper.getImage("res/images/Gui/Shop/Destroy.png");
            this.priceList = ImageHelper.getImage("res/images/Gui/Shop/PriceItem.png");
            this.upgradeButton = ImageHelper.getImage("res/images/Gui/Shop/UpgradeButton.png");
        }

        public BuildingOverlay(Building building, int x, int y, int width, int height) {

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.building = building;
            this.move = ImageHelper.getImage("res/images/Gui/Shop/move.png");
            this.destroy = ImageHelper.getImage("res/images/Gui/Shop/Destroy.png");
            this.priceList = ImageHelper.getImage("res/images/Gui/Shop/PriceItem.png");
            this.upgradeButton = ImageHelper.getImage("res/images/Gui/Shop/UpgradeButton.png");
        }

        @Override
        public void draw(DrawHelper draw) {

            if(!destroyed) {

                draw.setColour(new Color(175, 175, 175, 175));
                draw.fillHollowCircle(x + (width / 2), y + (height / 2), 50, 10);

                if(!path) {

                    draw.drawImage(move, x + (width / 2) - 65, y + (height / 2) - 10, 35, 35);

                    if (building.isDestroyable()) {

                        draw.drawImage(destroy, x + (width / 2) - 17, y + (height / 2) + 28, 35, 35);
                    }

                    if (building.isUpgradeable()) {

                        draw.drawImage(upgradeButton, x + (width / 2) - 17, y + (height / 2) - 60, 35, 35);

                        if (drawPrice) {

                            draw.drawImage(priceList, x + (width / 2) - 47, y + (height / 2));

                            draw.setColour(Color.BLACK);
                            final DecimalFormat separator = new DecimalFormat("##,###");
                            draw.drawString(separator.format(building.getCoinCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 18);
                            draw.drawString(separator.format(building.getStoneCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 33);
                            draw.drawString(separator.format(building.getWoodCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 48);
                            draw.drawString(separator.format(building.getWorkerCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 63);
                            draw.drawString(separator.format(building.getWheatCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 78);
                        }
                    }
                } else {

                    draw.drawImage(destroy, x + (width / 2) - 17, y + (height / 2) + 28, 35, 35);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            if(!path) {

                    //Destroy
                if (e.getX() > x + (width / 2) - 17 && e.getX() < x + (width / 2) + 18 && e.getY() > y + (height / 2) + 28 && e.getY() < y + (height / 2) + 63) {

                    building.destroy();
                }

                    //Upgrade
                else if (e.getX() > x + (width / 2) - 17 && e.getX() < x + (width / 2) + 18 && e.getY() > y + (height / 2) - 60 && e.getY() < y + (height / 2) + 25) {

                    building.upgrade();
                }

                    //Move
                else if (e.getX() > x + (width / 2) - 65 && e.getX() < x + (width / 2) - 30 && e.getY() > y + (height / 2) - 10 && e.getY() < y + (height / 2) + 25) {

                }
            }

            else {

                    //Destroy
                if (e.getX() > x + (width / 2) - 17 && e.getX() < x + (width / 2) + 18 && e.getY() > y + (height / 2) + 28 && e.getY() < y + (height / 2) + 63) {

                    pathManagement.destroy(posX, posY);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

            if(e.getX() > x + (width / 2) - 17 && e.getX() < x + (width / 2) + 18 && e.getY() > y + (height / 2) - 60 && e.getY() < y + (height / 2) - 25) drawPrice = true;
            else drawPrice = false;
        }

        @Override
        public void update(double delta) {

        }
    }
