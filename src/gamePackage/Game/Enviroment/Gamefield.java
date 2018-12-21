package gamePackage.Game.Enviroment;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.graphics.Display;
import engine.graphics.interfaces.GraphicalObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;
import engine.toolBox.SpriteSheet;
import gamePackage.Game.BackEnd.Player;
import gamePackage.Game.Buildings.BuildingSystem;

import java.awt.*;
import java.awt.image.BufferedImage;

    public class Gamefield implements GraphicalObject {

                //Attribute - Tile-Information
            private final int offsetX = 150;
            private final int offsetY = 100;
            private final int amountOfFields = 28;
            private final int fieldSquareSize = 25;

            private int screenWidth;
            private int screenHeight;
            private boolean buildingMode;

                //Referenzen
            private BufferedImage steg;
            private BufferedImage boat;
            private SpriteSheet groundTiles;

            private Player player;
            private Display display;
            private DatabaseConnector connector;
            private BuildingSystem buildingSystem;

        public Gamefield(DatabaseConnector connector, Display display, Player player) {

            this.player = player;
            this.display = display;
            this.connector = connector;
            this.screenWidth = display.getWidth();
            this.screenHeight = display.getHeight();

            boat = ImageHelper.getImage("res/images/Environment/Boat.png");
            steg = ImageHelper.getImage("res/images/Environment/steg.png");
            groundTiles = new SpriteSheet(ImageHelper.getImage("res/images/Environment/groundTile.png"), 4, 4, true);

            buildingSystem = new BuildingSystem(connector, display, player,this);
        }

        @Override
        public void draw(DrawHelper draw) {

            //Im folgenden Abschnitt dieser Methode wird der Hintergrund auf der Z-Ebene 1 Gezeichnet

                //Draw Water
            for (int row = 0; row < (screenWidth + 100) / fieldSquareSize; row++)
                for (int col = 0; col < (screenHeight + 100) / fieldSquareSize; col++)
                    draw.drawImage(groundTiles.getSubImage(3, 1), -50 + (row * fieldSquareSize), -50 + (col * fieldSquareSize), fieldSquareSize);

                //Draw LeftSide
            draw.drawImage(groundTiles.getSubImage(0, 0), offsetX - fieldSquareSize, offsetY - fieldSquareSize, fieldSquareSize);
            draw.drawImage(groundTiles.getSubImage(0, 2), offsetX - fieldSquareSize, offsetY + (amountOfFields * fieldSquareSize), fieldSquareSize);
            draw.drawImage(groundTiles.getSubImage(0, 3), offsetX - fieldSquareSize, offsetY + (amountOfFields * fieldSquareSize) + fieldSquareSize, fieldSquareSize);
            for (int row = 0; row < amountOfFields; row++)
                draw.drawImage(groundTiles.getSubImage(0, 1), offsetX - fieldSquareSize, offsetY + (row * fieldSquareSize), fieldSquareSize);

                //Draw RightSite
            draw.drawImage(groundTiles.getSubImage(2, 0), offsetX + (amountOfFields * fieldSquareSize), offsetY - fieldSquareSize, fieldSquareSize);
            draw.drawImage(groundTiles.getSubImage(2, 2), offsetX + (amountOfFields * fieldSquareSize), offsetY + (amountOfFields * fieldSquareSize), fieldSquareSize);
            draw.drawImage(groundTiles.getSubImage(2, 3), offsetX + (amountOfFields * fieldSquareSize), offsetY + (amountOfFields * fieldSquareSize) + fieldSquareSize, fieldSquareSize);
            for (int row = 0; row < amountOfFields; row++)
                draw.drawImage(groundTiles.getSubImage(2, 1), offsetX + (amountOfFields * fieldSquareSize), offsetY + (row * fieldSquareSize), fieldSquareSize);

                //Draw Bottom
            for (int col = 0; col < amountOfFields; col++) {

                draw.drawImage(groundTiles.getSubImage(1, 2), offsetX + (col * fieldSquareSize), offsetY + (amountOfFields * fieldSquareSize), fieldSquareSize);
                draw.drawImage(groundTiles.getSubImage(1, 3), offsetX + (col * fieldSquareSize), offsetY + (amountOfFields * fieldSquareSize) + fieldSquareSize, fieldSquareSize);
            }

                //Draw Top
            for (int col = 0; col < amountOfFields; col++)
                draw.drawImage(groundTiles.getSubImage(1, 0), offsetX + (col * fieldSquareSize), offsetY - fieldSquareSize, fieldSquareSize);

            if(!buildingMode) {

                for (int col = 0; col < amountOfFields; col++)
                    for (int row = 0; row < amountOfFields; row++)
                        draw.drawImage(groundTiles.getSubImage(1, 1), offsetX + (col * fieldSquareSize), offsetY + (row * fieldSquareSize), fieldSquareSize, fieldSquareSize);
            } else {

                int [][] fieldInformation = buildingSystem.getFieldInformation();
                for (int row = 0; row < amountOfFields; row++) {
                    for (int col = 0; col < amountOfFields; col++) {

                        if (fieldInformation[row][col] == 0)
                            draw.setColour(Color.GRAY.brighter());
                        else
                            draw.setColour(Color.RED.brighter());

                        draw.fillRec(offsetX + (row * fieldSquareSize), offsetY + (col * fieldSquareSize), fieldSquareSize, fieldSquareSize);
                    }
                }

                draw.setColour(Color.BLACK.brighter());
                for (int row = 0; row < amountOfFields; row++) {
                    for (int col = 0; col < amountOfFields; col++) {

                        draw.drawRec(offsetX + (row * fieldSquareSize), offsetY + (col * fieldSquareSize), fieldSquareSize, fieldSquareSize);
                    }
                }
            }
            draw.drawImage(boat, 75, 905);
            draw.drawImage(steg, 295, 805);
        }

        /**
         * Mit dieser Methode kann das Overlay in der Burg bearbeitet werden.
         * Wenn BuildingMode == true, dann wird das Kollisionsmodell der Gebäude angezeigt
         * Ansonsten ist der Hintergrund weiterhin Grass
         */
        public void setBuildingMode(boolean buildingMode) {

            this.buildingMode = buildingMode;
        }

        @Override
        public void update(double delta) {

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

        public BuildingSystem getBuildingSystem() {

            return buildingSystem;
        }
    }
