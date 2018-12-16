package engine;

import engine.graphics.Display;
import gamePackage.loginManagement.StateManager;

import java.awt.*;

    public class MainProgram {

                //Attribute

                //Referenzen
            private Display display;

        public MainProgram() {

            display = new Display();
            new StateManager(display);
        }

        public static void main(String[] args) {

            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {

                    new MainProgram();
                }
            });
        }
    }
