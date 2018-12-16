package gamePackage.Game.Buildings;

import engine.graphics.interfaces.LiteInteractableObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

    public abstract class GameObject implements LiteInteractableObject {

                //Attribute
            protected int x;
            protected int y;
            protected int width;
            protected int height;

            protected BufferedImage image;

                //Referenzen


        public GameObject(String path, int x, int y, int width, int height) {

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            image = ImageHelper.getImage(path);
        }

        @Override
        public void mouseReleased(MouseEvent event) {

        }

        @Override
        public void mouseMoved(MouseEvent event) {

        }

        @Override
        public void update(double delta) {

        }

        @Override
        public void draw(DrawHelper draw) {

        }

        public int getX() {

            return x;
        }

        public int getY() {

            return y;
        }

        public int getWidth() {

            return width;
        }

        public int getHeight() {

            return height;
        }

        public String getSize() {

            return width / 25 + ":" + height / 25;
        }
    }
