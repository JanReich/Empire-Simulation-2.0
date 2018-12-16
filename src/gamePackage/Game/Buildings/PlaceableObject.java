package gamePackage.Game.Buildings;

import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;
import gamePackage.Game.Enviroment.Gamefield;

import java.awt.event.MouseEvent;

    public abstract class PlaceableObject extends GameObject {

                //Attribute
            private boolean build;
            private boolean moving;

            protected boolean isDestroyable;
            protected boolean isUpgradeable;

                //Referenzen
            private Gamefield gamefield;

        public PlaceableObject(Gamefield gamefield, String type, int width, int height) {

            super("res/images/Buildings/" + type + "/" + type + "_St1.png", 0, 0, width * gamefield.getFieldSquareSize(), height * gamefield.getFieldSquareSize());

            build = true;
            moving = true;
            this.gamefield = gamefield;

            if(!type.equalsIgnoreCase("Castle"))
                isDestroyable = true;
        }

        public PlaceableObject(Gamefield gamefield, String type, int level, int x, int y, int width, int height) {

            super("res/images/Buildings/" + type + "/" + type + "_St" + level + ".png", gamefield.getOffsetX() + x * gamefield.getFieldSquareSize(), gamefield.getOffsetY() + y * gamefield.getFieldSquareSize(), width * gamefield.getFieldSquareSize(), height * gamefield.getFieldSquareSize());

            build = false;
            moving = false;
            this.gamefield = gamefield;

            if(!type.equalsIgnoreCase("Castle"))
                isDestroyable = true;
        }

        public void move() {

            moving = true;
            gamefield.setBuildingMode(true);
        }

        public void build(int x, int y) {

            this.x = x;
            this.y = y;
            build = false;
            moving = false;
        }

        public void destroy() {

            gamefield.destroyPlaceableObject(this);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

                //Moving buildings
            if(moving && !build) {

                int[] fields = gamefield.getField(e.getX(), e.getY());
                if(fields != null) {

                    x = fields[0] * gamefield.getFieldSquareSize() + gamefield.getOffsetX();
                    y = fields[1] * gamefield.getFieldSquareSize() + gamefield.getOffsetY();
                } else {

                    moving = false;
                }
            }
        }

        public void loadImage(String type, int level) {

            image = ImageHelper.getImage("res/images/Buildings/" + type + "/" + type + "_St" + level + ".png");
        }

        @Override
        public void mouseMoved(MouseEvent e) {

            if(moving) {

                x = e.getX();
                y = e.getY();
            }
        }

        @Override
        public void draw(DrawHelper draw) {

            draw.drawImage(image, x, y, width, height);
        }

        public boolean isDestroyable() {

            return isDestroyable;
        }

        public boolean isUpgradeable() {

            return isUpgradeable;
        }
    }
