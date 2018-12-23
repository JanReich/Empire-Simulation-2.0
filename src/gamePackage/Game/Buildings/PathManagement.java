package gamePackage.Game.Buildings;

import engine.graphics.interfaces.LiteInteractableObject;
import engine.graphics.interfaces.MouseObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

    public class PathManagement implements LiteInteractableObject, MouseObject {

                //Attribute
            private int movingX;
            private int movingY;
            private boolean building;

                //Referenzen
            private int[][] pathInformation;

                //Path
            private BufferedImage end0;
            private BufferedImage end1;
            private BufferedImage end2;
            private BufferedImage end3;

            private BufferedImage pathK;
            private BufferedImage pathS;
            private BufferedImage pathW;

            private BufferedImage curv0;
            private BufferedImage curv1;
            private BufferedImage curv2;
            private BufferedImage curv3;

            private BufferedImage pathT0;
            private BufferedImage pathT1;
            private BufferedImage pathT2;
            private BufferedImage pathT3;

            private BufferedImage pathNormal;
            private BuildingSystem buildingSystem;
            private UpgradeInformation information;

        public PathManagement(BuildingSystem buildingSystem) {

            this.buildingSystem = buildingSystem;
            this.information = buildingSystem.generateUpgradeInformation("Path");

            loadResources();
            pathInformation = new int[buildingSystem.getAmountOfFields()][buildingSystem.getAmountOfFields()];
        }

        /**
         * In dieser Methode werden die benötigenten Ressourcen für das
         * Path-System geladen. Diese Ressourcen müssen auch nicht
         * aktualisiert werden!
         */
        private void loadResources() {

            this.end0 = ImageHelper.getImage("res/Path/End0.png");
            this.end1 = ImageHelper.getImage("res/Path/End1.png");
            this.end2 = ImageHelper.getImage("res/Path/End2.png");
            this.end3 = ImageHelper.getImage("res/Path/End3.png");

            this.pathK = ImageHelper.getImage("res/Path/PathK.png");
            this.pathS = ImageHelper.getImage("res/Path/PathS.png");
            this.pathW = ImageHelper.getImage("res/Path/PathW.png");

            this.curv0 = ImageHelper.getImage("res/Path/Curv0.png");
            this.curv1 = ImageHelper.getImage("res/Path/Curv1.png");
            this.curv2 = ImageHelper.getImage("res/Path/Curv2.png");
            this.curv3 = ImageHelper.getImage("res/Path/Curv3.png");

            this.pathT0 = ImageHelper.getImage("res/Path/PathT0.png");
            this.pathT1 = ImageHelper.getImage("res/Path/PathT1.png");
            this.pathT2 = ImageHelper.getImage("res/Path/PathT2.png");
            this.pathT3 = ImageHelper.getImage("res/Path/PathT3.png");

            this.pathNormal = ImageHelper.getImage("res/Path/PathNormal.png");
        }

        /**
         * Mit dieser Methode kann ein PathTile geladen werden.
         * Dazu muss lediglich die Position des Tiles übergeben werden.
         *
         * Die Überprüfungen, ob ein Weg hier gebaut werden kann, muss hier nicht
         * durchgeführt werden, da in dieser Methode nur das laden von Wegen aus der
         * Datenbank durchgeführt wird. Das bauen wird von einer anderen Methode
         * übernommen.
         */
        public void loadPath(int fieldX, int fieldY) {

                //Es existiert noch kein Weg
            if(pathInformation[fieldX][fieldY] == 0) {

                pathInformation[fieldX][fieldY] = 1;
                buildingSystem.updateFieldInformation(fieldX, fieldY, 1, 1, 1);
            }
        }

        @Override
        public void update(double delta) {

        }

        @Override
        public void draw(DrawHelper draw) {

            for (int x = 0; x < pathInformation.length; x++) {
                for (int y = 0; y < pathInformation[x].length; y++) {

                    if (pathInformation[x][y] == 1) {

                        if (x != 0 && x != (buildingSystem.getAmountOfFields() - 1) && y != 0 && y != (buildingSystem.getAmountOfFields() - 1)) {

                            BufferedImage image = getImage(pathInformation[x - 1][y], pathInformation[x][y - 1], pathInformation[x + 1][y], pathInformation[x][y + 1]);
                            draw.drawImage(image, buildingSystem.getOffsetX() + (x * buildingSystem.getFieldSquareSize()), buildingSystem.getOffsetY() + (y * buildingSystem.getFieldSquareSize()), buildingSystem.getFieldSquareSize(), buildingSystem.getFieldSquareSize());
                        } else {

                            int tile0;
                            int tile1;
                            int tile2;
                            int tile3;

                            if(x == 0) tile0 = 0;
                            else tile0 = pathInformation[x - 1][y];
                            if(y == 0) tile1 = 0;
                            else tile1 = pathInformation[x][y - 1];
                            if(x == buildingSystem.getAmountOfFields() - 1) tile2 = 0;
                            else tile2 = pathInformation[x + 1][y];
                            if(y == buildingSystem.getAmountOfFields() - 1) tile3 = 0;
                            else tile3 = pathInformation[x][y + 1];

                            draw.drawImage(getImage(tile0, tile1, tile2, tile3), buildingSystem.getOffsetX() + (x * buildingSystem.getFieldSquareSize()), buildingSystem.getOffsetY() + (y * buildingSystem.getFieldSquareSize()), buildingSystem.getFieldSquareSize(), buildingSystem.getFieldSquareSize());
                        }
                    }
                }
            }

            if(building) {

                draw.drawImage(getImage(0, 0, 0, 0), movingX, movingY, buildingSystem.getFieldSquareSize());
            }
        }

        public BufferedImage getImage(int tile0, int tile1, int tile2, int tile3) {

                //End-Of-Path
            if(tile0 == 1 && tile1 == 0 && tile2 == 0 && tile3 == 0)
                return end2;
            else if(tile0 == 0 && tile1 == 1 && tile2 == 0 && tile3 == 0)
                return end3;
            else if(tile0 == 0 && tile1 == 0 && tile2 == 1 && tile3 == 0)
                return end0;
            else if(tile0 == 0 && tile1 == 0 && tile2 == 0 && tile3 == 1)
                return end1;

                //Kreuzung
            else if(tile0 == 1 && tile1 == 1 && tile2 == 1 && tile3 == 1)
                return pathK;
                //Waagerecht
            else if(tile0 == 1 && tile1 == 0 && tile2 == 1 && tile3 == 0)
                return pathW;
                //Senkrecht
            else if(tile0 == 0 && tile1 == 1 && tile2 == 0 && tile3 == 1)
                return pathS;

                //Curve
            else if(tile0 == 0 && tile1 == 0 && tile2 == 1 && tile3 == 1)
                return curv0;
            else if(tile0 == 1 && tile1 == 0 && tile2 == 0 && tile3 == 1)
                return curv1;
            else if(tile0 == 1 && tile1 == 1 && tile2 == 0 && tile3 == 0)
                return curv2;
            else if(tile0 == 0 && tile1 == 1 && tile2 == 1 && tile3 == 0)
                return curv3;

                //T-Curve
            else if(tile0 == 0 && tile1 == 1 && tile2 == 1 && tile3 == 1)
                return pathT0;
            else if(tile0 == 1 && tile1 == 0 && tile2 == 1 && tile3 == 1)
                return pathT1;
            else if(tile0 == 1 && tile1 == 1 && tile2 == 0 && tile3 == 1)
                return pathT2;
            else if(tile0 == 1 && tile1 == 1 && tile2 == 1 && tile3 == 0)
                return pathT3;

            else return pathNormal;
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

            if(building) {

                buildingSystem.buildPath(e.getX(), e.getY());
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

            if(building) {

                movingX = e.getX();
                movingY = e.getY();
            }
        }

        public void setBuilding(boolean build) {

            this.building = build;
        }

        public UpgradeInformation getUpgradeInformation() {

            return information;
        }
    }
