package gamePackage.Game.Quest;

import engine.abitur.datenbanken.mysql.DatabaseConnector;
import engine.abitur.datenbanken.mysql.QueryResult;
import engine.graphics.Display;
import engine.graphics.interfaces.LiteInteractableObject;
import engine.toolBox.DrawHelper;
import engine.toolBox.ImageHelper;
import gamePackage.Game.BackEnd.Player;
import gamePackage.Game.GameManagement;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

    public class QuestBook implements LiteInteractableObject {

                //Attribute
            private boolean opened;

                //Referenzen
            private BufferedImage bookIcon;
            private BufferedImage questMenu;

            private Player player;
            private Display display;
            private ArrayList<Quest> quests;
            private GameManagement management;
            private DatabaseConnector connector;


        public QuestBook(DatabaseConnector connector, Display display, GameManagement management, Player player) {

            this.player = player;
            this.display = display;
            this.connector = connector;
            this.management = management;
            this.quests = new ArrayList<>();

            bookIcon = ImageHelper.getImage("res/images/Questbook/QuestbookIcon.png");
            questMenu = ImageHelper.getImage("res/images/Questbook/EmptyQuestbook.png");

            loadQuests();
        }

        /**
         * In dieser Methode werden alle Quest aus der Datenbank geladen, die der User
         * noch nicht vollendet hat.
         */
        private void loadQuests() {

            connector.executeStatement("" +
                    "SELECT JansEmpire_QuestList.Name, JansEmpire_QuestList.Amount, JansEmpire_QuestList.Level, JansEmpire_Tasks.Type, JansEmpire_Tasks.Object, JansEmpire_QuestList.RewardID, JansEmpire_UserQuestList.ID FROM JansEmpire_UserQuestList " +
                    "INNER JOIN JansEmpire_QuestList ON JansEmpire_QuestList.QuestID = JansEmpire_UserQuestList.QuestID " +
                    "INNER JOIN JansEmpire_Tasks ON JansEmpire_QuestList.TaskID = JansEmpire_Tasks.TaskID " +
                    "WHERE Mail = '" + player.getMail() +"' AND Done = 'false';");
            QueryResult result = connector.getCurrentQueryResult();

            for (int i = 0; i < result.getRowCount(); i++) {

                Quest quest = new Quest(this, Integer.parseInt(result.getData()[i][6]), result.getData()[i][3], result.getData()[i][4], result.getData()[i][0],Integer.parseInt(result.getData()[i][5]) , Integer.parseInt(result.getData()[i][2]), Integer.parseInt(result.getData()[i][1]), i);
                quests.add(quest);
                display.getActivePanel().drawObjectOnPanel(quest, 201);
            }
        }

        /**
         * Mit dieser Methode kann eine Quest ausgewählt werden.
         */
        public void setActiveQuest(int questIndex) {

            for(Quest quest : quests) {

                quest.setActive(false);
            }
            quests.get(questIndex).setActiveQuest(true);
        }

        /**
         * Mit dieser Methode kann das Questmenü, sowie sämtliche Quests geschlossen werden.
         */
        public void close() {

            opened = false;
            for (Quest quest : quests) {

                quest.setActive(false);
                quest.setActiveQuest(false);
            }
        }

        @Override
        public void draw(DrawHelper draw) {

                //Draw Questbook-Icon
            draw.drawImage(bookIcon, 930, 740, 75, 75);

            if(opened) {

                draw.drawImage(questMenu, 250, 200, 500, 650);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            if(e.getX() > 930 && e.getX() < 1005 && e.getY() > 740 && e.getY() < 815) {

                management.closeShop();
                management.setBuildingMode(false);
                opened = true;

                int temp = Math.min(7, quests.size());
                for (int i = 0; i < temp; i++)
                    quests.get(i).setActive(true);
            }

            if(e.getX() > 680 && e.getX() < 743 && e.getY() > 207 && e.getY() < 270 && opened) {

                close();
            }
        }

        private void removeQuest(Quest quest, int index) {

            quests.remove(quest);

            for (int i = index; i < quests.size(); i++)
                quests.get(i).setQuestIndex(quests.get(i).getQuestIndex() - 1);
        }

        public void refresh(String type) {

            for (Quest quest : quests) {

                quest.refresh(type);
            }
        }

        @Override
        public void update(double delta) {

        }

        @Override
        public void mouseMoved(MouseEvent event) {

        }

                // ---------- GETTER AND SETTER ---------- \\
        public boolean isOpened() {

            return opened;
        }


        public class Quest implements LiteInteractableObject {

                    //Attribute
                private boolean active;
                private boolean activeQuest;

                private int level;
                private int amount;
                private int rewardID;
                private int questIndex;
                private int amountReady;
                private int databaseIndex;

                    //Referenzen
                private String name;
                private String object;
                private String questType;
                private String[] rewards;
                private QuestBook questBook;

                private BufferedImage questBar;
                private BufferedImage finished;
                private BufferedImage questTemplate;
                private BufferedImage donateTemplate;

            public Quest(QuestBook questBook, int databaseIndex, String questType, String object, String name, int rewardID, int level, int amount, int index) {

                this.name = name;
                this.level = level;
                this.amount = amount;
                this.object = object;
                this.questIndex = index;
                this.rewardID = rewardID;
                this.questBook = questBook;
                this.questType = questType;
                this.databaseIndex = databaseIndex;

                this.rewards = new String[3];

                this.finished = ImageHelper.getImage("res/images/Questbook/finish.png");
                this.questBar = ImageHelper.getImage("res/images/Questbook/Questbar.png");
                this.donateTemplate = ImageHelper.getImage("res/images/Questbook/donateBar.png");
                this.questTemplate = ImageHelper.getImage("res/images/Questbook/QuestTemplate.png");

                refresh("all");
                generateRewards();
            }

            @Override
            public void draw(DrawHelper draw) {

                if(active && !activeQuest) {

                    draw.drawImage(questBar, 273, 369 + (questIndex * 65), 454, 65);

                    draw.setColour(Color.BLACK);
                    draw.setFont(new Font("Roboto", Font.PLAIN, 20));
                    draw.drawString(name, 365, 410 + (questIndex * 65));
                }

                else if(!active && activeQuest) {

                    draw.drawImage(questTemplate, 273, 269, 454, 562);
                    if(questType.equalsIgnoreCase("Donate")) draw.drawImage(donateTemplate, 273, 485, 454, 68);

                    int max;
                    if(questType.equalsIgnoreCase("Donate")) max = 365;
                    else max = 430;

                    draw.setColour(new Color(0,102,0));
                    double questProgress = (double) amountReady / (double) amount;
                    questProgress = (int) (max * questProgress);
                    if(questProgress > max) questProgress = max;
                    draw.fillRec(285, 521, (int) questProgress, 21);
                    draw.setColour(Color.BLACK);
                    for (int i = 0; i < rewards.length; i++) {

                        if(rewards[i] != null) {

                            String[] temp = rewards[i].split(":");
                            draw.drawImage(getImage(temp[0]), 313 + (140 * i), 738, 94, 82);
                            draw.drawString(temp[1], 313 + (140 * i) + (94 - draw.getFontWidth(temp[1])) / 2, 815);
                        }
                    }

                    draw.setColour(Color.BLACK);
                    draw.setFont(new Font("Roboto", Font.PLAIN, 30));
                    draw.drawString(name, 273 + ((450 - draw.getFontWidth(name)) / 2), 315);

                    draw.setColour(Color.BLACK);
                    draw.setFont(new Font("Roboto", Font.PLAIN, 12));
                    draw.drawString("Hier entsteht eine Questbeschreibung, mit Zeilenumbruch!", 340, 360);

                    draw.setColour(Color.BLACK);
                    draw.setFont(new Font("Roboto", Font.PLAIN, 20));

                    if(questType.equalsIgnoreCase("Build")) {

                        switch(object) {

                            case "Woodcutter":

                                if(amount == 1) draw.drawString("Bau ein Holzfäller Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level == 1) draw.drawString("Baue " + amount + " Holzfäller auf Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level != 1) draw.drawString("Upgrade " + amount + " Holzfäller auf Stufe " + level + ":", 280, 508);
                                break;
                            case "Stonemason":

                                if(amount == 1) draw.drawString("Baue ein Steinmetz Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level == 1) draw.drawString("Baue " + amount + " Steinmetze auf Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level != 1) draw.drawString("Upgrade " + amount + " Steinmetze auf Stufe " + level + ":", 280, 508);
                                break;
                            case "Farmhouse":

                                if(amount == 1) draw.drawString("Bau ein Farmhaus Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level == 1) draw.drawString("Baue " + amount + " Farnhäuser auf Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level != 1) draw.drawString("Upgrade " + amount + " Farnhäuser auf Stufe " + level + ":", 280, 508);
                                break;
                            case "Warehouse":

                                if(amount == 1) draw.drawString("Bau Lagerhaus Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level == 1) draw.drawString("Baue Lagerhaus auf Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level != 1) draw.drawString("Upgrade Lagerhaus auf Stufe " + level + ":", 280, 508);
                                break;
                            case "House":

                                if(amount == 1) draw.drawString("Bau " + amount + " Wohnhaus Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level == 1) draw.drawString("Baue " + amount + " Wohnhäuser auf Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level != 1) draw.drawString("Upgrade " + amount + " Wohnhäuser auf Stufe " + level + ":", 280, 508);
                                break;
                            case "Kaserne":

                                if(amount == 1) draw.drawString("Bau Kaserne Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level == 1) draw.drawString("Baue Kaserne auf Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level != 1) draw.drawString("Upgrade Kaserne auf Stufe " + level + ":", 280, 508);
                                break;
                            case "GuardHouse":

                                if(amount == 1) draw.drawString("Bau " + amount + " Wachhaus Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level == 1) draw.drawString("Baue " + amount + " Wachhäuser auf Stufe " + level + ":", 280, 508);
                                else if(amount > 1 && level != 1) draw.drawString("Upgrade " + amount + " Wachhäuser auf Stufe " + level + ":", 280, 508);
                                break;
                            case "Castle":

                                draw.drawString("Upgrade Burg auf Stufe " + level + ":", 280, 508);
                                break;
                        }
                    } else if(questType.equalsIgnoreCase("Collect")) {

                        switch (object) {

                            case "Coins":
                                draw.drawString("Sammel " + amount + " Münzen:", 280, 508);
                                break;
                        }
                    } else if(questType.equalsIgnoreCase("Donate")) {

                        switch (object) {

                            case "Wood":
                                draw.drawString("Spende " + amount + " Holz:", 280, 508);
                                break;
                            case "Stone":
                                draw.drawString("Spende " + amount + " Stein:", 280, 508);
                                break;
                            case "Wheat":
                                draw.drawString("Spende " + amount + " Weizen:", 280, 508);
                                break;
                            case "Coins":
                                draw.drawString("Spende " + amount + " Münzen:", 280, 508);
                                break;
                        }
                    }
                    draw.drawString(amountReady + "/" + amount, 273 + ((450 - draw.getFontWidth(amountReady + "/" + amount)) / 2), 538);
                    if(amountReady >= amount) draw.drawImage(finished, 700, 800, 60, 99);
                }
            }

            public void refresh(String type) {

                amountReady = 0;
                if(questType.equalsIgnoreCase("Build")) {

                    if(type.equalsIgnoreCase(object) || type.equalsIgnoreCase("all")) {

                        connector.executeStatement("" +
                                "SELECT Level FROM JansEmpire_Buildings WHERE Mail = '" + player.getMail() + "' AND Type = '" + object + "' AND Level >= '" + level + "';");
                        QueryResult result = connector.getCurrentQueryResult();
                        amountReady = result.getRowCount();
                    }
                }
            }

            private void generateRewards() {

                connector.executeStatement("" +
                        "SELECT xpReward, coinReward, woodReward, stoneReward, wheatReward FROM JansEmpire_QuestRewards WHERE RewardID = '" + rewardID + "'");

                ArrayList<String> list = new ArrayList<>();
                QueryResult result = connector.getCurrentQueryResult();

                for (int col = 0; col < result.getColumnCount(); col++) {

                    if(Integer.parseInt(result.getData()[0][col]) > 0) {

                        if(col == 0)
                            list.add("xpIcon:" + result.getData()[0][col]);
                        else if(col == 1)
                            list.add("coinIcon:" + result.getData()[0][col]);
                        else if(col == 2)
                            list.add("woodIcon:" + result.getData()[0][col]);
                        else if(col == 3)
                            list.add("stoneIcon:" + result.getData()[0][col]);
                        else if(col == 4)
                            list.add("wheatIcon:" + result.getData()[0][col]);
                    }
                }

                for (int i = 0; i < list.size(); i++)
                    if(i <= (rewards.length - 1))
                        rewards[i] = list.get(i);
            }

            private BufferedImage getImage(String path) {

                return ImageHelper.getImage("res/images/Questbook/" + path + ".png");
            }

            private void payRewards() {

                int xp = 0;
                int wood = 0;
                int coin = 0;
                int stone = 0;
                int wheat = 0;

                for (int i = 0; i < rewards.length; i++) {

                    if(rewards[i] != null) {

                        String[] temp = rewards[i].split(":");
                        if(temp[0].contains("wood"))
                            wood = Integer.parseInt(temp[1]);
                        else if(temp[0].contains("stone"))
                            stone = Integer.parseInt(temp[1]);
                        else if(temp[0].contains("wheat"))
                            wheat = Integer.parseInt(temp[1]);
                        else if(temp[0].contains("coin"))
                            coin = Integer.parseInt(temp[1]);
                        else if(temp[0].contains("xp"))
                            xp = Integer.parseInt(temp[1]);
                    }
                }
                player.addXP(xp);
                player.deposit(wood, stone, wheat, coin, 0);
                close();

                connector.executeStatement("" +
                        "UPDATE JansEmpire_UserQuestList SET Done = 'true' WHERE Mail = '" + player.getMail() + "' AND ID = '" + databaseIndex + "';");
                questBook.removeQuest(this, questIndex);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (e.getX() > 273 && e.getX() < 727 && e.getY() > 369 + (questIndex * 65) && e.getY() < 424 + (questIndex * 65) && active) {

                    questBook.setActiveQuest(questIndex);
                } else if (e.getX() > 700 && e.getX() < 760 && e.getY() > 800 && e.getY() < 899 && activeQuest && amountReady >= amount) {

                    if(questType.equalsIgnoreCase("Build")) {

                        payRewards();
                    } else if(questType.equalsIgnoreCase("Donate") && amountReady >= amount) {

                        if (object.equalsIgnoreCase("Coins")) {

                            player.payResources(0, 0, 0, amount, 0);
                            amountReady = amount;

                            connector.executeStatement("" +
                                    "UPDATE JansEmpire_UserQuestList SET Done = 'true' WHERE Mail = '" + player.getMail() + "' AND ID = '" + databaseIndex + "';");
                        } else if (object.equalsIgnoreCase("Wheat")) {


                            player.payResources(0, 0, amount, 0, 0);
                            amountReady = amount;

                            connector.executeStatement("" +
                                    "UPDATE JansEmpire_UserQuestList SET Done = 'true' WHERE Mail = '" + player.getMail() + "' AND ID = '" + databaseIndex + "';");
                        } else if (object.equalsIgnoreCase("Wood")) {

                            player.payResources(amount, 0, 0, 0, 0);
                            amountReady = amount;

                            connector.executeStatement("" +
                                    "UPDATE JansEmpire_UserQuestList SET Done = 'true' WHERE Mail = '" + player.getMail() + "' AND ID = '" + databaseIndex + "';");
                        } else if (object.equalsIgnoreCase("Stone")) {

                            player.payResources(0, amount, 0, 0, 0);
                            amountReady = amount;

                            connector.executeStatement("" +
                                    "UPDATE JansEmpire_UserQuestList SET Done = 'true' WHERE Mail = '" + player.getMail() + "' AND ID = '" + databaseIndex + "';");
                        }

                        close();
                        payRewards();
                        questBook.removeQuest(this, questIndex);
                    }
                }

                if(e.getX() >  665 && e.getX() < 720 && e.getY() > 510 && e.getY() < 550 && activeQuest && questType.equalsIgnoreCase("Donate")) {

                    if (object.equalsIgnoreCase("Coins")) {

                        if (player.checkGoods(0, 0, 0, amount, 0)) {

                            amountReady = amount;
                        } else amountReady = player.getCoins();
                    } else if (object.equalsIgnoreCase("Wheat")) {

                        if (player.checkGoods(0, 0, amount, 0, 0)) {

                            amountReady = amount;
                        } else amountReady = player.getWheat();
                    } else if (object.equalsIgnoreCase("Wood")) {

                        if (player.checkGoods(amount, 0, 0, 0, 0)) {

                            amountReady = amount;
                        } else amountReady = player.getWood();
                    } else if (object.equalsIgnoreCase("Stone")) {

                        if (player.checkGoods(0, amount, 0, 0, 0)) {

                            amountReady = amount;
                        } else amountReady = player.getStone();
                    }
                }
            }

            @Override
            public void update(double delta) {

            }

            @Override
            public void mouseMoved(MouseEvent event) {

            }

                // ---------- GETTER AND SETTER \\
            public void setActive(boolean active) {

                this.active = active;
            }

            public void setActiveQuest(boolean activeQuest) {

                this.activeQuest = activeQuest;
            }

            public void setQuestIndex(int index) {

                this.questIndex = index;
            }

            public int getQuestIndex() {

                return questIndex;
            }
        }
    }
