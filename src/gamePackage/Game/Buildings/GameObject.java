package gamePackage.Game.Buildings;

import engine.graphics.interfaces.GraphicalObject;
import engine.toolBox.DrawHelper;

import java.awt.event.MouseEvent;

    public abstract class GameObject implements GraphicalObject {

                //Attribute
            protected int x;
            protected int y;

            protected int width;
            protected int height;

                //Referenzen

        public GameObject() {


        }

        public GameObject(int x, int y, int width, int height) {

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        /**
         * Mit dieser Methode kann abgefragt werden, ob ein Mausklick innerhalb eines bestimmten bereiches ist.
         * @return true (Wenn in einem bestimmten Bereich geklickt wurde) false wenn nicht
         */
        public boolean isInside(MouseEvent e, int x, int y, int width, int height) {

            if(e.getX() > x && e.getX() < x + width && e.getY() > y && e.getY() < y + height) return true; return false;
        }

        @Override
        public void update(double delta) {

        }

        @Override
        public void draw(DrawHelper draw) {

        }

            //---------- GETTER AND SETTER ---------- \\
        public int getX() {

            return x;
        }

        public int getY() {

            return y;
        }
    }
