package gamePackage.Game.Enviroment;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.abitur.datenbanken.mysql.QueryResult;
import engine.graphics.Display;
import engine.graphics.interfaces.LiteInteractableObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;
import gamePackage.Game.BackEnd.Player;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

    public class Shop implements LiteInteractableObject {

                //Attribute
            private int shopPage;
            private int shopState;
            private boolean active;

                //Referenzen
            private Display display;
            private Gamefield gamefield;
            private BufferedImage shopGUI;
            private BufferedImage shopGUI2;
            private DatabaseConnector connector;
            private ArrayList<ShopItem> shopItems;

        public Shop(Display display, Player player, DatabaseConnector connector, Gamefield gamefield) {

            this.display = display;
            this.connector = connector;
            this.gamefield = gamefield;

            this.shopGUI = ImageHelper.getImage("res/images/Gui/Shop/ShopGUI.png");
            this.shopGUI2 = ImageHelper.getImage("res/images/Gui/Shop/ShopGUI2.png");

            setShopItems(player);
        }

        private void setShopItems(Player player) {

            shopItems = new ArrayList<>();
            ShopItem item;

            item = new ShopItem(connector, player,this, 0, 0, 0, false, "Woodcutter");
            display.getActivePanel().drawObjectOnPanel(item, 110);
            shopItems.add(item);

            item = new ShopItem(connector, player, this, 0, 0, 1, true, "Warehouse");
            display.getActivePanel().drawObjectOnPanel(item, 109);
            shopItems.add(item);

            item = new ShopItem(connector, player, this, 0, 0, 2, false, "Farmhouse");
            display.getActivePanel().drawObjectOnPanel(item, 108);
            shopItems.add(item);

            item = new ShopItem(connector, player, this, 0, 0, 3, false, "Stonemason");
            display.getActivePanel().drawObjectOnPanel(item, 107);
            shopItems.add(item);

            item = new ShopItem(connector, player, this, 0, 0, 4, false, "House");
            display.getActivePanel().drawObjectOnPanel(item, 106);
            shopItems.add(item);

            item = new ShopItem(connector, player, this, 0, 1, 0, false, "Woodcutter");
            display.getActivePanel().drawObjectOnPanel(item, 110);
            shopItems.add(item);

            item = new ShopItem(connector, player, this, 1, 0, 0, true, "Kaserne");
            display.getActivePanel().drawObjectOnPanel(item, 110);
            shopItems.add(item);

            item = new ShopItem(connector, player, this, 1, 0, 1, false, "GuardHouse");
            display.getActivePanel().drawObjectOnPanel(item, 1098);
            shopItems.add(item);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

                //Shop button
            if(e.getX() > 885 && e.getX() < 950 && e.getY() > 935 && e.getY() < 995) {

                setActive(!active);
                gamefield.setBuildingMode(active);
            }

            if(active) {

                    //Close
                if(isInside(e, 730, 810, 50, 40)) {

                    active = false;
                    gamefield.setBuildingMode(false);
                }

                    //Next Page
                else if(isInside(e, 740, 905, 20, 35)) {

                    shopPage += 1;
                }

                    //Previous Page
                else if(isInside(e, 235, 900, 15, 40)) {

                    shopPage -= 1;
                }

                    //Change State
                else if(isInside(e,215, 810, 50, 30)) { shopState = 0; shopPage = 0; }
                else if(isInside(e,315, 810, 50, 30)) { shopState = 1; shopPage = 0; }
            }
        }


        @Override
        public void draw(DrawHelper draw) {

            if(active) {

                if(shopState == 0) draw.drawImage(shopGUI, 200, 800, 600, 200);
                else if(shopState == 1) draw.drawImage(shopGUI2, 200, 800, 600, 200);
            }
        }

        @Override
        public void mouseMoved(MouseEvent event) {

        }

        @Override
        public void update(double delta) {

        }

        private boolean isInside(MouseEvent e, int x, int y, int width, int height) {

            if(e.getX() > x && e.getX() < x + width && e.getY() > y && e.getY() < y + height) return true; return false;
        }
        public int getShopPage() {

            return shopPage;
        }

        public int getShopState() {

            return shopState;
        }

        public boolean isActive() {

            return active;
        }

        public void setActive(boolean active) {

            this.active = active;
        }



        public class ShopItem implements LiteInteractableObject {

                    //Attribute
                private int index;
                private final int x = 268;
                private final int y = 862;

                private int shopPage;
                private int shopState;
                private boolean build;
                private boolean unique;
                private boolean showPrices;
                private boolean isAvailable;

                private int woodCost;
                private int stoneCost;
                private int wheatCost;
                private int coinCost;
                private int workerCost;

                    //Referenzen
                private Shop shop;
                private String type;
                private String[] size;
                private Player player;
                private BufferedImage item;
                private BufferedImage priceItem;
                private DatabaseConnector connector;

            public ShopItem(DatabaseConnector connector, Player player, Shop shop, int shopState, int shopPage, int index, boolean unique, String type) {

                this.type = type;
                this.shop = shop;
                this.index = index;
                this.unique = unique;
                this.player = player;
                this.shopPage = shopPage;
                this.shopState = shopState;
                this.connector = connector;
                priceItem = ImageHelper.getImage("res/images/Gui/Shop/PriceItem.png");

                if(!unique) {

                    isAvailable = true;
                    item = ImageHelper.getImage("res/images/Gui/Shop/ShopItem" + type + ".png");
                }
                else generateInformation();
                generateCost();
            }

            public void generateInformation() {

                if(unique) {

                    connector.executeStatement("SELECT ID FROM JansEmpire_Buildings WHERE Type = '" + type + "' AND Mail = '" + player.getMail() + "'");
                    if(connector.getCurrentQueryResult().getRowCount() >= 1) {

                        isAvailable = false;
                        item = ImageHelper.getImage("res/images/Gui/Shop/ShopItem" + type + "Max.png");
                    } else {

                        isAvailable = true;
                        item = ImageHelper.getImage("res/images/Gui/Shop/ShopItem" + type + ".png");
                    }
                }
            }

            private void generateCost() {

                connector.executeStatement("SELECT WoodCost, StoneCost, WheatCost, CoinCost, WorkerAmount, Size FROM JansEmpire_StaticBuildings WHERE Type = '" + type + "' AND Level = '1';");
                QueryResult result = connector.getCurrentQueryResult();

                woodCost = Integer.parseInt(result.getData()[0][0]);
                stoneCost = Integer.parseInt(result.getData()[0][1]);
                wheatCost = Integer.parseInt(result.getData()[0][2]);
                coinCost = Integer.parseInt(result.getData()[0][3]);
                workerCost = Integer.parseInt(result.getData()[0][4]);

                String temp = result.getData()[0][5];
                size = temp.split("x");
            }

            @Override
            public void draw(DrawHelper draw) {

                if (shop.isActive() && shop.getShopPage() == shopPage && shop.getShopState() == shopState) {

                    draw.drawImage(item, x + (88 * index), y, 93, 128);

                    draw.setColour(Color.BLACK);
                    final DecimalFormat separator = new java.text.DecimalFormat("##,###");
                    if(isAvailable) draw.drawString(separator.format(stoneCost) + "", x + (88 * index) + 43, y + 103);
                    if(isAvailable) draw.drawString(separator.format(woodCost) + "", x + (88 * index) + 43, y + 118);

                    if (showPrices && isAvailable) {

                        draw.drawImage(priceItem, x + (88 * index) + 40, y + 40, 96 * 1.3, 73 * 1.3);

                        draw.setColour(Color.BLACK);
                        draw.drawString(separator.format(coinCost), x + (88 * index) + 85, y + 58);
                        draw.drawString(separator.format(stoneCost), x + (88 * index) + 85, y + 74);
                        draw.drawString(separator.format(woodCost), x + (88 * index) + 85, y + 90);
                        draw.drawString(separator.format(workerCost), x + (88 * index) + 85, y + 106);
                        draw.drawString(separator.format(wheatCost), x + (88 * index) + 85, y + 122);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {


            }

            @Override
            public void mouseMoved(MouseEvent e) {

                if(shop.getShopState() == shopState && shop.getShopPage() == shopPage) {

                    if (e.getX() > x + (88 * index) && e.getX() < x + (88 * (index + 1)) && e.getY() > y && e.getY() < y + 128)
                        showPrices = true;
                    else showPrices = false;
                }
            }

            @Override
            public void update(double delta) {

            }
        }
    }
