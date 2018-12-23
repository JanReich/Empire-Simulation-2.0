package gamePackage.Game.Buildings;

import engine.graphics.interfaces.LiteInteractableObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

    public class BuildingTemplate implements LiteInteractableObject {

                //Attribute
            private int x;
            private int y;
            private int width;
            private int height;

            private boolean drawPrice;

                //Referenzen
            private BufferedImage move;
            private BufferedImage destroy;
            private BufferedImage priceList;
            private BufferedImage upgradeButton;

            private Building building;
            private BuildingSystem buildingSystem;

        public BuildingTemplate(BuildingSystem buildingSystem, Building building, int x, int y, int width, int height) {

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.move = ImageHelper.getImage("res/images/Gui/Shop/move.png");
            this.destroy = ImageHelper.getImage("res/images/Gui/Shop/Destroy.png");
            this.priceList = ImageHelper.getImage("res/images/Gui/Shop/PriceItem.png");
            this.upgradeButton = ImageHelper.getImage("res/images/Gui/Shop/UpgradeButton.png");

            this.building = building;
            this.buildingSystem = buildingSystem;
        }

        @Override
        public void draw(DrawHelper draw) {

            draw.setColour(new Color(175, 175, 175, 175));
            draw.fillHollowCircle(x + (width / 2), y + (height / 2), 50, 10);

                draw.drawImage(move, x + (width / 2) - 65, y + (height / 2) - 10, 35, 35);
            if(building.isDestroyable())
                draw.drawImage(destroy, x + (width / 2) - 17, y + (height / 2) + 28, 35, 35);

            if(building.isUpgradable()) {
                draw.drawImage(upgradeButton, x + (width / 2) - 17, y + (height / 2) - 60, 35, 35);

                if (drawPrice) {

                    draw.drawImage(priceList, x + (width / 2) - 47, y + (height / 2));

                    draw.setColour(Color.BLACK);
                    UpgradeInformation information = building.getUpgradeinformation();
                    final DecimalFormat separator = new java.text.DecimalFormat("##,###");
                    draw.drawString(separator.format(information.getCoinCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 18);
                    draw.drawString(separator.format(information.getStoneCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 33);
                    draw.drawString(separator.format(information.getWoodCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 48);
                    draw.drawString(separator.format(information.getWorkerCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 63);
                    draw.drawString(separator.format(information.getWheatCost()), x + (width / 2) - 47 + 40, y + (height / 2) + 78);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

                //Destroy
            if (e.getX() > x + (width / 2) - 17 && e.getX() < x + (width / 2) + 18 && e.getY() > y + (height / 2) + 28 && e.getY() < y + (height / 2) + 63 && building.isDestroyable()) {

                buildingSystem.destroy(building);
            }

                //Upgrade
            else if (e.getX() > x + (width / 2) - 17 && e.getX() < x + (width / 2) + 18 && e.getY() > y + (height / 2) - 60 && e.getY() < y + (height / 2) + 25 && building.isUpgradable()) {

                building.setLevel(building.getLevel() + 1);
                UpgradeInformation information = building.getUpgradeinformation();
                if(information.getUpgradeSize()[0] != building.getFieldWidth() || building.getFieldHeight() != information.getUpgradeSize()[1]) {

                    building.setMoving(true);
                    building.setUpgrading(true);
                    buildingSystem.setBuildingMode(true);
                    buildingSystem.updateFieldInformation(building.getFieldX(), building.getFieldY(), building.getFieldWidth(), building.getFieldHeight(), 0);
                    building.setFieldWidth(information.getUpgradeSize()[0]);
                    building.setFieldHeight(information.getUpgradeSize()[1]);
                } else {

                    buildingSystem.updateFieldInformation(building.getFieldX(), building.getFieldY(), building.getFieldWidth(), building.getFieldHeight(), 0);
                    buildingSystem.upgrade(building, building.getX(), building.getY());
                }
            }

                //Move
            else if (e.getX() > x + (width / 2) - 65 && e.getX() < x + (width / 2) - 30 && e.getY() > y + (height / 2) - 10 && e.getY() < y + (height / 2) + 25) {

                building.setMoving(true);
                buildingSystem.setBuildingMode(true);
                buildingSystem.updateFieldInformation(building.getFieldX(), building.getFieldY(), building.getFieldWidth(), building.getFieldHeight(), 0);
            }

            buildingSystem.setTemplate(null);
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
