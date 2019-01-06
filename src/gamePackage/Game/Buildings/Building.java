package gamePackage.Game.Buildings;

import engine.graphics.interfaces.LiteInteractableObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.SourceHelper.ImageHelper;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

    public class Building extends PlaceableObject implements LiteInteractableObject{

                //Attribute
            private int level;

            private boolean moving;
            private boolean building;
            private boolean upgrading;

            private boolean upgradeable;

                //Referenzen
            private String type;
            private BufferedImage image;
            private BuildingSystem buildingSystem;
            private UpgradeInformation upgradeinformation;

        public Building(BuildingSystem buildingSystem, String type, int fieldX, int fieldY, int fieldWidth, int fieldHeight, int level) {

            super(buildingSystem, fieldX, fieldY, fieldWidth, fieldHeight);

            this.type = type;
            this.level = level;
            this.buildingSystem = buildingSystem;
            loadImage();
        }

        public Building(BuildingSystem buildingSystem, String type, int fieldWidth, int fieldHeight) {

            super(buildingSystem, fieldWidth, fieldHeight);

            this.level = 1;
            this.type = type;
            this.buildingSystem = buildingSystem;

            loadImage();
            moving = true;
            building = true;
        }

        /**
         * In dieser Methode wird das Build für den entsprechenden Gebäudetyp geladen
         */
        private void loadImage() {

            image = ImageHelper.getImage("res/images/Buildings/" + type + "/" + type + "_St" + level + ".png");
        }

        @Override
        public void draw(DrawHelper draw) {

            if(!moving) draw.drawImage(image, offsetX + (fieldX * fieldSquareSize), offsetY + (fieldY * fieldSquareSize), fieldWidth * fieldSquareSize, fieldHeight * fieldSquareSize);
            else draw.drawImage(image, x, y, fieldWidth * fieldSquareSize, fieldHeight * fieldSquareSize);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            if(isInside(e, offsetX + (fieldX * fieldSquareSize), offsetY + (fieldY * fieldSquareSize), fieldWidth * fieldSquareSize, fieldHeight * fieldSquareSize) && !buildingSystem.isQuestBookOpen()) {

                if(!upgrading && !building && !moving) {

                    buildingSystem.setTemplate(this);
                }
            }

            if(building) {

                int[] position = buildingSystem.getField(e.getX(), e.getY());
                if(position != null && buildingSystem.isFieldEmpty(position[0], position[1], fieldWidth, fieldHeight)) {

                    fieldX = position[0];
                    fieldY = position[1];

                    moving = false;
                    building = false;
                    buildingSystem.saveBuilding(this);
                } else buildingSystem.removeSelectedBuilding(this);
            } else if(moving && !upgrading) {

                buildingSystem.move(this, e.getX(), e.getY());
            } else if(moving  && upgrading) {

                buildingSystem.upgrade(this, e.getX(), e.getY());
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

            if(moving) {

                x = e.getX();
                y = e.getY();
            }
        }

            //---------- GETTER AND SETTER ---------- \\

        public int getLevel() {

            return level;
        }

        public String getType() {

            return type;
        }

        public String getPosition() {

            return fieldX + "-" + fieldY;
        }

        public boolean isUpgradable()  {

            return upgradeable;
        }

        public boolean isDestroyable() {

            if(type.equalsIgnoreCase("Castle")) return false; return true;
        }

        public void setLevel(int level) {

            this.level = level;
            loadImage();
        }

        public void setMoving(boolean moving) {

            this.moving = moving;
        }

        public void setUpgradeable(boolean temp) {

            upgradeable = temp;
        }

        public void setUpgrading(boolean upgrading) {

            this.upgrading = upgrading;
        }

        public UpgradeInformation getUpgradeinformation() {

            return upgradeinformation;
        }

        public void setUpgradeinformation(UpgradeInformation upgradeInformation) {

            this.upgradeinformation = upgradeInformation;
        }
    }
