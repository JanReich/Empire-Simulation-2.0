package gamePackage.loginManagement;

import engine.graphics.interfaces.GraphicalObject;
import engine.toolBox.DrawHelper;

import java.awt.*;

    public class LoadingScreen implements GraphicalObject {

                //Attribute

                //Referenzen

        public LoadingScreen() {

        }

        @Override
        public void update(double delta) {

        }

        @Override
        public void draw(DrawHelper draw) {

            draw.setStroke(5);
            draw.setFont(new Font("Roboto", Font.BOLD, 80));
            draw.drawString("Empire Simulation",(1000 - draw.getFontWidth("Empire Simulation")) / 2, 150);

            draw.setColour(Color.ORANGE);
            draw.fillRoundRec(200, 775, 625, 100, 25);

            draw.setStroke(10);
            draw.setColour(Color.BLACK);
            draw.drawRoundRec(200, 775, 625, 100, 25);
        }
    }
