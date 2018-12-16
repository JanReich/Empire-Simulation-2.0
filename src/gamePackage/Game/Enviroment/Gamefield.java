package gamePackage.Game.Enviroment;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.abitur.datenbanken.mysql.QueryResult;
import engine.graphics.Display;
import engine.graphics.interfaces.GraphicalObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;
import engine.toolBox.SpriteSheet;
import gamePackage.Game.BackEnd.Player;
import gamePackage.Game.Buildings.Building;
import gamePackage.Game.Buildings.PathManagement;
import gamePackage.Game.Buildings.PlaceableObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
            private String[][] fieldInformation;

            private Player player;
            private Display display;
            private DatabaseConnector connector;
            private ArrayList<Building> buildings;
            private PathManagement pathManagement;

        public Gamefield(DatabaseConnector connector, Display display, Player player) {

            this.player = player;
            this.display = display;
            this.connector = connector;
            this.screenWidth = display.getWidth();
            this.screenHeight = display.getHeight();
            boat = ImageHelper.getImage("res/images/Environment/Boat.png");
            steg = ImageHelper.getImage("res/images/Environment/steg.png");
            groundTiles = new SpriteSheet(ImageHelper.getImage("res/images/Environment/groundTile.png"), 4, 4, true);

            fieldInformation = new String[amountOfFields][amountOfFields];
            for (int row = 0; row < fieldInformation.length; row++)
                for (int col = 0; col < fieldInformation[row].length; col++)
                    fieldInformation[row][col] = "O";

            buildings = new ArrayList<>();
            pathManagement = new PathManagement(display, this);
            display.getActivePanel().drawObjectOnPanel(pathManagement, 150);
            loadBuildings();
        }

        private void loadBuildings() {

            connector.executeStatement("" +
                    "SELECT Type, Level, Position FROM JansEmpire_Buildings WHERE Mail = '" + player.getMail() + "';");
            QueryResult result = connector.getCurrentQueryResult();

            for (int i = 0; i < result.getRowCount(); i++) {

                String[] position = result.getData()[i][2].split("-");
                if(result.getData()[i][0].equalsIgnoreCase("Path")) {

                    pathManagement.addPath(Integer.parseInt(position[0]), Integer.parseInt(position[1]));
                } else {

                    connector.executeStatement("SELECT Size FROM JansEmpire_StaticBuildings WHERE Type = '" + result.getData()[i][0] + "' AND Level = '" + result.getData()[i][1] + "'");

                    String[] size = connector.getCurrentQueryResult().getData()[0][0].split("x");
                    Building building = new Building(connector, display, this, result.getData()[i][0], Integer.parseInt(result.getData()[i][1]), Integer.parseInt(position[0]), Integer.parseInt(position[1]), Integer.parseInt(size[0]), Integer.parseInt(size[1]));
                    display.getActivePanel().drawObjectOnPanel(building, 150 - i);
                }
            }
        }

        /**
         * Diese Methode zerstört ein übergebenes Gebäude
         * @param object
         */
        public void destroyPlaceableObject(PlaceableObject object) {

            if(object instanceof Building) {

                display.getActivePanel().removeObjectFromPanel(object);
                buildings.remove(object);

                int fromX = (object.getX() - offsetX) / fieldSquareSize;
                int fromY = (object.getY() - offsetY) / fieldSquareSize;
                for(int x = fromX; x < object.getWidth() / fieldSquareSize + fromX; x++)
                    for (int y = fromY; y < object.getHeight() / fieldSquareSize + fromY; y++)
                        fieldInformation[x][y] = "O";

                connector.executeStatement("DELETE FROM JansEmpire_Buildings WHERE Mail = '" + player.getMail() + "' AND Position = '" + fromX + "-" + fromY + "';");
            }
        }

        public boolean isMenuActive() {

            boolean temp = false;

            for(Building building : buildings) {

                if(building.getBuildingOverlay() != null) temp = true;
            }
            return temp;
        }

        @Override
        public void draw(DrawHelper draw) {

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

                for (int row = 0; row < amountOfFields; row++) {
                    for (int col = 0; col < amountOfFields; col++) {

                        if (fieldInformation[row][col].equalsIgnoreCase("O"))
                            draw.setColour(Color.GRAY.brighter());
                        else
                            draw.setColour(Color.RED.brighter());

                        draw.fillRec(offsetX + (row * fieldSquareSize), offsetY + (col * fieldSquareSize), fieldSquareSize, fieldSquareSize);
                    }
                }

                    draw.setColour(Color.BLACK.brighter());
                for (int i = 0; i < amountOfFields; i++)
                    draw.drawLine(offsetX, offsetY + (fieldSquareSize * i), offsetX + (amountOfFields * fieldSquareSize), offsetY + (fieldSquareSize * i));

                for (int i = 0; i < amountOfFields; i++)
                    draw.drawLine(offsetX + (fieldSquareSize * i), offsetY, offsetX + (fieldSquareSize * i), offsetY + (amountOfFields * fieldSquareSize));
                draw.drawLine(offsetX, offsetY + (fieldSquareSize * amountOfFields), offsetX + (fieldSquareSize * amountOfFields), offsetY + (fieldSquareSize * amountOfFields));
                draw.drawLine(offsetX + (fieldSquareSize * amountOfFields), offsetY, offsetX + (fieldSquareSize * amountOfFields), offsetY + (fieldSquareSize * amountOfFields));
            }
            draw.drawImage(boat, 75, 905);
            draw.drawImage(steg, 295, 805);
        }

        public void setBuildingMode(boolean buildingMode) {

            this.buildingMode = buildingMode;
        }

        public int[] getField(int x, int y) {

            if(x < offsetX || y < offsetY || x > offsetX + (fieldSquareSize * amountOfFields) || y > offsetY + (fieldSquareSize * amountOfFields)) return null;

            int tempFieldX = (x - offsetX) / fieldSquareSize;
            int tempFieldY = (y - offsetY) / fieldSquareSize;

            int[] position = new int[2];
            position[0] = tempFieldX;
            position[1] = tempFieldY;
            return position;
        }

        public boolean checkFields(int fromX, int fromY, int width, int height) {

            for (int x = fromX; x < fromX + width; x++) {
                for (int y = fromY; y < fromY + height; y++) {

                    if (fieldInformation.length > x && fieldInformation[x].length > y) {
                        if (fieldInformation[x][y].equalsIgnoreCase("X")) {

                            return false;
                        }
                    } else return false;
                }
            }
            return true;
        }

        public void simulateDestroy(int fromX, int fromY, int width, int height) {

            for(int x = fromX; x < width + fromX; x++)
                for (int y = fromY; y < height + fromY; y++)
                    fieldInformation[x][y] = "O";
        }

        public void simulateBuild(int fromX, int fromY, int width, int height) {

            for(int x = fromX; x < width + fromX; x++)
                for (int y = fromY; y < height + fromY; y++)
                    fieldInformation[x][y] = "X";
        }

        @Override
        public void update(double delta) {

        }

        public void addBuilding(Building building) {

            SwingUtilities.invokeLater(() -> buildings.add(building));
        }

        public int getAmountOfFields() {

            return amountOfFields;
        }

        public int getFieldSquareSize() {

                return fieldSquareSize;
            }

        public int getOffsetX() {

            return offsetX;
        }

        public int getOffsetY() {

            return offsetY;
        }

        public Player getPlayer() {

            return player;
        }
    }
