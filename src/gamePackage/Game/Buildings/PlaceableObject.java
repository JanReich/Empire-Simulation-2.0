package gamePackage.Game.Buildings;

    public abstract class PlaceableObject extends GameObject {

                //Attribute
            protected int fieldX;
            protected int fieldY;
            protected int offsetX;
            protected int offsetY;

            protected int fieldWidth;
            protected int fieldHeight;
            protected int fieldSquareSize;

                //Referenzen
            private BuildingSystem buildingSystem;


        public PlaceableObject(BuildingSystem buildingSystem, int fieldX, int fieldY, int fieldWidth, int fieldHeight) {

            super(buildingSystem.getOffsetX() + (fieldX * buildingSystem.getFieldSquareSize()), buildingSystem.getOffsetY() + (fieldY * buildingSystem.getFieldSquareSize()), fieldWidth * buildingSystem.getFieldSquareSize(), fieldHeight * buildingSystem.getFieldSquareSize());

            this.fieldX = fieldX;
            this.fieldY = fieldY;
            this.fieldWidth = fieldWidth;
            this.fieldHeight = fieldHeight;
            this.buildingSystem = buildingSystem;

            this.offsetX = buildingSystem.getOffsetX();
            this.offsetY = buildingSystem.getOffsetY();
            this.fieldSquareSize = buildingSystem.getFieldSquareSize();
        }

        public PlaceableObject(BuildingSystem buildingSystem, int fieldWidth, int fieldHeight) {

            super(0, 0, fieldWidth * buildingSystem.getFieldSquareSize(), fieldHeight * buildingSystem.getFieldSquareSize());

            this.fieldWidth = fieldWidth;
            this.fieldHeight = fieldHeight;
            this.buildingSystem = buildingSystem;

            this.offsetX = buildingSystem.getOffsetX();
            this.offsetY = buildingSystem.getOffsetY();
            this.fieldSquareSize = buildingSystem.getFieldSquareSize();
        }

            //---------- GETTER AND SETTER ---------- \\
        public int getFieldX() {

            return fieldX;
        }

        public int getFieldY() {

            return fieldY;
        }

        public int getFieldWidth() {

            return fieldWidth;
        }

        public int getFieldHeight() {

            return fieldHeight;
        }

        public void setFieldX(int fieldX) {

            this.fieldX = fieldX;
        }

        public void setFieldY(int fieldY) {

            this.fieldY = fieldY;
        }

        public void setFieldWidth(int fieldWidth) {

            this.fieldWidth = fieldWidth;
        }

        public void setFieldHeight(int fieldHeight) {

            this.fieldHeight = fieldHeight;
        }
    }
