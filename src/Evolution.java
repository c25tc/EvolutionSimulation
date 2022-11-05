import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// todo: check for button collision [x]
// todo: make a deep copy of neural network [x]
// todo: do collisions between two organisms (for killing) [x]
// todo: add setting data like number or organisms etc. []
// todo: add a start screen where you can change certain values like number of organisms or food etc. []

public class Evolution extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Evolution", args);
    }

    int SCREEN_WIDTH = 1125, SCREEN_HEIGHT = 750;
    int SETTINGS_WIDTH = 250;
    int year;
    int clock;
    int maxClock;
    float boardWidth, boardHeight;
    PVector offset;
    boolean mouseDown;
    int[] mouseDownPos;
    String[] inputNodes;
    int startingNumberOrganisms;
    int maxOrganisms;
    int totalOrganisms;
    List<Organism> organismsList;
    List<Food> foodList;
    int maxFood;
    int cellSize;
    int organismLookAtNum;
    Organism lookAtOrg;
    float xoff = 0.0f;
    int speed;
    private static final DecimalFormat dfZero = new DecimalFormat("0.000");

    // settings variables
    boolean drawStuff;
    boolean showOrganismSettings;
    float[][] buttonPositions;
    String[] buttonNames;
    int buttonAmount;
    float reproductionHealthNeeded;
    float reproductionEnergyNeeded;
    float reproductionEnergyCost;
    float reproductionUrgeNeeded;
    int reproductionAgeNeeded;
    int autoReproduceAge;
    float metabolismDivideAmount;
    int autoKillAge;



    // ----------- processing functions ---------------
    public void settings() {
        size(SCREEN_WIDTH + SETTINGS_WIDTH, SCREEN_HEIGHT);
    }

    public void setup() {
        // settings values
        drawStuff = true;
        showOrganismSettings = true;
        buttonPositions = new float[50][4];
        buttonNames = new String[50];
        buttonAmount = 0;
        reproductionHealthNeeded = .80f;
        reproductionEnergyNeeded = 15;
        reproductionEnergyCost = 10;
        reproductionUrgeNeeded = .70f;
        reproductionAgeNeeded = 30;
        autoReproduceAge = 200;
        metabolismDivideAmount = 3;
        autoKillAge = 300;

        // other variable assignments
        year = 0;
        clock = 0;
        maxClock = 60;
        speed = 1;
        cellSize = 2;
        startingNumberOrganisms = 50;
        totalOrganisms = 0;
        maxOrganisms = 50;
        maxFood = 1000;
        organismsList = new ArrayList<>();
        foodList = new ArrayList<>();
        boardWidth = SCREEN_WIDTH;
        boardHeight = SCREEN_HEIGHT;
        offset = new PVector(0, 0);
        mouseDown = false;
        mouseDownPos = new int[4];
        for (int i = 0; i < startingNumberOrganisms; i++) {
            organismsList.add(new Organism(metabolismDivideAmount));
            organismsList.get(i).getOrganismsList(organismsList);
            organismsList.get(i).boardWidth = boardWidth;
            organismsList.get(i).boardHeight = boardHeight;
            organismsList.get(i).key = totalOrganisms;
            totalOrganisms++;
        }
//        for (int i = 0; i < maxFood; i++) {
//            float x = (SCREEN_HEIGHT * (float) Math.sqrt(Math.random())) + (SCREEN_WIDTH - SCREEN_HEIGHT) / 2f;
//            float y = SCREEN_HEIGHT * (float) Math.sqrt(Math.random());
//            foodList.add(new Food((int) (Math.random() * SCREEN_WIDTH), (int) (Math.random() * SCREEN_HEIGHT), (int) (Math.random() * 5) + 1, 0, i));
//        }
        while (foodList.toArray().length < maxFood) {
            PVector threePos = new PVector(SCREEN_HEIGHT*(float)Math.random() - SCREEN_HEIGHT/2f, (float)Math.random()*SCREEN_HEIGHT - SCREEN_HEIGHT/2f, (float)Math.random()*SCREEN_HEIGHT - SCREEN_HEIGHT/2f);
            if ((threePos.x*threePos.x + threePos.y*threePos.y + threePos.z*threePos.z) < (SCREEN_HEIGHT/2f*SCREEN_HEIGHT/2f)) {
                foodList.add(new Food((int)threePos.x + SCREEN_WIDTH/2, (int)threePos.y + SCREEN_HEIGHT/2, (int) (Math.random() * 5) + 1, 0, 0));
            }
        }
        inputNodes = new String[]{"rotation", "speed", "energy", "health", "reproductive urge", "distance to food", "distance to organism", "angle to food", "angle to organism", "age", "generation"};
        organismLookAtNum = 0;
        lookAtOrg = organismsList.get(0);
        noStroke();


        // make buttons
        addButton(SCREEN_WIDTH + 30, SCREEN_HEIGHT - 60, 90, 30, "organisms");
        addButton(SCREEN_WIDTH + 130, SCREEN_HEIGHT - 60, 90, 30, "data");
        addButton(SCREEN_WIDTH + 30,60, 90, 30, "show board");
        addButton(SCREEN_WIDTH + 130,60, 90, 30, "hide board");
    }

    public void draw() {
        background(119, 141, 169);
        rotate(0);
        if (drawStuff) drawBoard();
        for (int i = 0; i < speed; i++) {
            clock++;
            if (this.clock > this.maxClock) {
                year++;
                clock = 0;
            }
            updateFood();
            updateOrganisms();
        }
        if (drawStuff) {
            drawFood();
            drawOrganisms();
        }
        drawControls();
    }

    // -----x----- processing functions -------x-------
    // ----------- custom functions ---------------
    public void drawControls() {
        noStroke();
        fill(65, 90, 119, 90);
        rect(SCREEN_WIDTH + 10, 10, SETTINGS_WIDTH - 20, SCREEN_HEIGHT - 20, 10);
        // draw year
        stroke(0);
        fill(0);
        textSize(20);
        text("year: " + year, SCREEN_WIDTH + 2*SETTINGS_WIDTH/3f, 35);
        textSize(10);

        if (showOrganismSettings) {
            // draw nodes
            int yIndexInput = 0;
            int yIndexHidden = 0;
            int yIndexOutput = 0;

            System.out.println(organismsList.toArray().length);
            if (organismsList.toArray().length == 0) {
                speed = 0;
                System.out.println("EVERYONE DIED");
            }
            if (!organismsList.contains(lookAtOrg)) {
                lookAtOrg = organismsList.get(0);
            }

            // draw nodes
            noStroke();
            for (int i = 0; i < lookAtOrg.neuralNetwork.nodes.toArray().length; i++) {
                Node node = lookAtOrg.neuralNetwork.nodes.get(i);
                if (node.type == 1) {
                    fill(255, 198, 255);
                    node.xPos = SCREEN_WIDTH + SETTINGS_WIDTH / 4f;
                    node.yPos = 100 + yIndexInput * 15;
                    circle(node.xPos, node.yPos, 10);
                    yIndexInput++;
                } else if (node.type == 2) {
                    fill(189, 178, 255);
                    node.xPos = SCREEN_WIDTH + 3 * SETTINGS_WIDTH / 4f;
                    node.yPos = 140 + yIndexOutput * 18;
                    circle(node.xPos, node.yPos, 10);
                    yIndexOutput++;
                } else if (node.type == 3) {
                    float totalX = 0;
                    float totalY = 0;
                    float numberX = 0;
                    float numberY = 0;
                    for (Connection connection : lookAtOrg.neuralNetwork.connections) {
                        if (connection.in == node.key || connection.out == node.key) {
                            if (connection.in == node.key) {
                                totalX += lookAtOrg.neuralNetwork.nodes.get(connection.out).xPos;
                                numberX++;
                                totalY += lookAtOrg.neuralNetwork.nodes.get(connection.out).yPos;
                                numberY++;
                            } else {
                                totalX += lookAtOrg.neuralNetwork.nodes.get(connection.in).xPos;
                                numberX++;
                                totalY += lookAtOrg.neuralNetwork.nodes.get(connection.in).yPos;
                                numberY++;
                            }

                        }
                    }
                    node.xPos = totalX / numberX;
                    if (node.xPos == SCREEN_WIDTH + SETTINGS_WIDTH / 4f) node.xPos = SCREEN_WIDTH + 2 * SETTINGS_WIDTH / 4f;
                    node.yPos = totalY / numberY;
                    fill(202, 255, 191);
                    circle(node.xPos, node.yPos, 10);
                    yIndexHidden++;
                }
                fill(0);
                text(dfZero.format(node.outputValue) + "", node.xPos, node.yPos);
            }
            // draw connections
            for (int i = 0; i < lookAtOrg.neuralNetwork.connections.toArray().length; i++) {
                Connection connection = lookAtOrg.neuralNetwork.connections.get(i);
                List<Node> nodes = lookAtOrg.neuralNetwork.nodes;
                stroke(0);
                line(nodes.get(connection.in).xPos, nodes.get(connection.in).yPos, nodes.get(connection.out).xPos, nodes.get(connection.out).yPos);
                fill(0);
                text(dfZero.format(connection.weight) + "", (nodes.get(connection.in).xPos + nodes.get(connection.out).xPos) / 2, (nodes.get(connection.in).yPos + nodes.get(connection.out).yPos) / 2);
            }
            yIndexInput = 0;
            //"rotation", "speed", "energy", "health", "reproductive urge", "distance to food", "distance to organism", "angle to food", "angle to organism"
            for (int i = 0; i < inputNodes.length; i++) {
                String text = "";
                NeuralNetwork network = lookAtOrg.neuralNetwork;
                fill(0);
                text(inputNodes[yIndexInput], SCREEN_WIDTH + 40, 300 + yIndexInput * 20);
                if (yIndexInput == 0) text = network.rotation + "";
                else if (yIndexInput == 1) text = network.speed + " / " + network.maxSpeed;
                else if (yIndexInput == 2) text = network.energy + " / " + network.maxEnergy;
                else if (yIndexInput == 3) text = network.health + " / " + network.maxHealth;
                else if (yIndexInput == 4) text = network.reproductiveUrge + " / 1";
                else if (yIndexInput == 5)
                    text = network.distanceToFood + " / " + lookAtOrg.seeDistance;
                else if (yIndexInput == 6)
                    text = network.distanceToOrg + " / " + lookAtOrg.seeDistance;
                else if (yIndexInput == 7) text = network.angleToFood + "";
                else if (yIndexInput == 8) text = network.angleToOrg + "";
                else if (yIndexInput == 9) text = lookAtOrg.age + "";
                else if (yIndexInput == 10) text = lookAtOrg.generation + "";
                text(text, SCREEN_WIDTH + 160, 300 + yIndexInput * 20);
                yIndexInput++;
            } // draw stats
            // draw menu

        } else {
            // draw menu
            fill(140, 154, 182);
            if (drawStuff) fill(157, 172, 192);
            drawButton("show board", 10);
            fill(140, 154, 182);
            if (!drawStuff) fill(157, 172, 192);
            drawButton("hide board", 10);

        }
        // draw menu
        fill(140, 154, 182);
        if (showOrganismSettings) fill(157, 172, 192);
        drawButton("organisms", 12);
        fill(140, 154, 182);
        if (!showOrganismSettings) fill(157, 172, 192);
        drawButton("data", 28);

    }
    public void addButton(int x, int y, int width, int height, String name) {
        buttonPositions[buttonAmount][0] = x;
        buttonPositions[buttonAmount][1] = y;
        buttonPositions[buttonAmount][2] = width;
        buttonPositions[buttonAmount][3] = height;
        buttonNames[buttonAmount] = name;
        buttonAmount ++;
    }
    public void drawButton(String name, int xOff) {
        int index = Arrays.asList(buttonNames).indexOf(name);
        noStroke();
        rect(buttonPositions[index][0], buttonPositions[index][1], buttonPositions[index][2], buttonPositions[index][3], 10);
        fill(0, 0 ,0);
        stroke(0);
        textSize(15);
        text(name, buttonPositions[index][0] + xOff, buttonPositions[index][1] + 20);
        textSize(10);
    }

    public void drawBoard() {
        fill(216, 222, 230);
        rotate(0);
        noStroke();
        rect(offset.x - 5, offset.y - 5, boardWidth + 10, boardHeight + 10, 10);
    }

    public void updateFood() {
        if (foodList.toArray().length < maxFood) {
            PVector threePos = new PVector(SCREEN_HEIGHT*(float)Math.random() - SCREEN_HEIGHT/2f, (float)Math.random()*SCREEN_HEIGHT - SCREEN_HEIGHT/2f, (float)Math.random()*SCREEN_HEIGHT - SCREEN_HEIGHT/2f);
            if ((threePos.x*threePos.x + threePos.y*threePos.y + threePos.z*threePos.z) < (SCREEN_HEIGHT/2f*SCREEN_HEIGHT/2f)) {
                foodList.add(new Food((int)threePos.x + SCREEN_WIDTH/2, (int)threePos.y + SCREEN_HEIGHT/2, (int) (Math.random() * 5) + 1, 0, 0));
            }
        }
    }

    public void updateOrganisms() {
        List<Organism> removedOrganisms = new ArrayList<Organism>();
        List<Organism> addedOrganisms = new ArrayList<Organism>();
        if (organismsList.toArray().length == 0) {
            speed = 0;
            System.out.println("EVERYONE DIED");
        }
        if (!organismsList.contains(lookAtOrg)) {
            lookAtOrg = organismsList.get(0);
        }

        for (Organism org : organismsList) {
            org.update(boardWidth, boardHeight, offset);
            org.getOrganismsList(organismsList);
            PVector center = new PVector((org.cells.length * cellSize) / 2f, (org.cells[0].length * cellSize) / 2f);
            PVector pos = new PVector(org.pos.x + center.x, org.pos.y + center.y);
            float closestFood = org.seeDistance;
            int closestFoodNum = 0;
            // check if it should die
            if (org.health <= 0 || org.age > autoKillAge) {
                removedOrganisms.add(org);
                foodList.add(new Food((int) org.pos.x, (int) org.pos.y, org.cells.length + org.cells[0].length, 1, 10));
            }
            if ((org.health / org.maxHealth > reproductionHealthNeeded && org.energy > reproductionEnergyNeeded && org.reproductiveUrge > reproductionUrgeNeeded && org.age > reproductionAgeNeeded) || org.age + 1 % autoReproduceAge == 0) { // CREATE REPRODUCE!
                totalOrganisms++;
                org.energy -= 10;
                Organism newOrg = new Organism(metabolismDivideAmount);
                newOrg.generation = org.generation + 1;
                int[][] newCells = new int[org.cells.length][];
                for (int i = 0; i < org.cells.length; i++) // deep copying it
                    newCells[i] = org.cells[i].clone();
                newOrg.cells = newCells;
                newOrg.pos = org.pos.copy();
                newOrg.pos.add((int) (Math.random() * 40) - 20, (int) (Math.random() * 40) - 20);
                newOrg.seeDistance = org.seeDistance;
                newOrg.mutationChance = org.mutationChance;
                newOrg.mutationSize = org.mutationSize;
                newOrg.getOrganismsList(organismsList);
                newOrg.boardWidth = boardWidth;
                newOrg.boardHeight = boardHeight;
                newOrg.key = totalOrganisms;
                totalOrganisms++;
                newOrg.neuralNetwork = org.neuralNetwork.makeDeepCopy();
                newOrg.mutate();
                newOrg.metabolismCost = newOrg.getMetabolismCost();
                addedOrganisms.add(newOrg);
            }
            // check closest food
            List<Food> removedFoods = new ArrayList<Food>();
            for (Food food : foodList) {
                float dist = pos.dist(food.pos);
                // find distance and angle of nearest food
                if (dist < closestFood) {
                    float angle = degrees(atan2(pos.y - food.pos.y, pos.x - food.pos.x));
                    //            angle += org.rotation;
                    angle = (angle + 180 - org.rotation) % 360;
                    if (dist < org.seeDistance) {
                        for (int i = 0; i < org.viewAngles.length; i++) {
                            if ((angle - 45 + 180) % 360 > org.viewAngles[i][0] && (angle - 45 + 180) % 360 < org.viewAngles[i][1]) {
                                closestFood = dist;
                                closestFoodNum = foodList.indexOf(food);
                                break;
                            }
                        }

                    }
                }
                // check collision with food and mouth
                if (dist < Math.max(org.cells.length * cellSize + 4, org.cells[0].length * cellSize + 4)) {

                    PVector cellPos = new PVector(0, 0);
                    float radius;
                    for (int x = 0; x < org.cells.length; x++) {
                        for (int y = 0; y < org.cells[x].length; y++) {
                            cellPos = cellPos.set(-(x * cellSize + (cellSize / 2f)) + (org.cells.length * cellSize), y * cellSize + (cellSize / 2f));
                            radius = center.dist(cellPos);
                            float angle = (degrees(atan2(cellPos.y - center.y, cellPos.x - center.x)));
                            float newY = sin(radians((org.rotation - angle - 90) % 360)) * radius;
                            float newX = cos(radians((org.rotation - angle - 90) % 360)) * radius;
                            //1 = stomach - yellow
                            //2 = mouth - purple
                            //3 = mover - green
                            //4 = front eye - violate
                            //5 = right eye - violate
                            //6 = bottom eye - violate
                            //7 = left eye - violate
                            //8 = killer
                            //9 = shell
                            PVector centerCellPos = new PVector(pos.x + newY, pos.y + newX);
//                            circle(centerCellPos.x, centerCellPos.y, 5);
                            if (org.cells[x][y] == 2) {
                                fill(100, 97, 160);
                                if (centerCellPos.dist(food.pos) < (food.size + cellSize + 1)) {
                                    fill(255, 0, 0);
                                    org.energy += min(org.mouthSize * 5, food.size * 5);
                                    if (org.energy > org.maxEnergy) {
                                        org.energy = org.maxEnergy;
                                    }
                                    if (food.size - min(org.mouthSize, food.size) <= 0) {
//                                        foodList.remove(food);
                                        removedFoods.add(food);
                                    } else {
                                        food.size -= min(org.mouthSize, food.size);
                                    }

                                }
                                noStroke();
//                            circle((boardWidth / (float) SCREEN_WIDTH) * (pos.x + newY) + offset.x, (boardWidth / (float) SCREEN_WIDTH) * (pos.y - newX) + offset.y, (boardWidth / (float) SCREEN_WIDTH) * 3);


                            }
                        }
                    }
                }
            }
//            foodList.removeAll(removedFoods);
                float angle = degrees(atan2(pos.y - foodList.get(closestFoodNum).pos.y, pos.x - foodList.get(closestFoodNum).pos.x));
                angle = (angle + 180) % 360;
                if (closestFood == 50) angle = (org.rotation - 90) % 360;
                float orgX = cos(radians(angle)) * closestFood;
                float orgY = sin(radians(angle)) * closestFood;
                if (lookAtOrg == org) {
                    stroke(0);
                    line((boardWidth / (float) SCREEN_WIDTH) * pos.x + offset.x, (boardWidth / (float) SCREEN_WIDTH) * pos.y + offset.y, (boardWidth / (float) SCREEN_WIDTH) * (pos.x + orgX) + offset.x, (boardWidth / (float) SCREEN_WIDTH) * (pos.y + orgY) + offset.y);
                    fill(0);
                    if (closestFoodNum != 0)
                        circle((boardWidth / (float) SCREEN_WIDTH) * (foodList.get(closestFoodNum).pos.x) + offset.x, (boardWidth / (float) SCREEN_WIDTH) * (foodList.get(closestFoodNum).pos.y) + offset.y, 5);
                }

                angle = (angle - org.rotation + 90) % 360;
                if (angle < 0) angle += 360;
                org.distanceToFood = closestFood;
                org.angleToFood = (int) angle;
                foodList.removeAll(removedFoods);

                float closestOrg = org.seeDistance;
                int closestOrgNum = 0;
                for (Organism organism : organismsList) {
                    PVector organismCenter = new PVector((organism.cells.length * cellSize) / 2f, (organism.cells[0].length * cellSize) / 2f);
                    PVector organismPos = new PVector(organism.pos.x + organismCenter.x, organism.pos.y + organismCenter.y);

                    float organismsDist = pos.dist(organism.pos);
                    if (organismsDist < closestOrg && organismsDist > 3.0) {
                        angle = degrees(atan2(pos.y - organism.pos.y, pos.x - organism.pos.x));
                        angle = (angle + 180 - org.rotation) % 360;
                        if (organismsDist < org.seeDistance) {
                            for (int i = 0; i < org.viewAngles.length; i++) {
                                if ((angle - 45 + 180) % 360 > org.viewAngles[i][0] && (angle - 45 + 180) % 360 < org.viewAngles[i][1]) {
                                    closestOrg = organismsDist;
                                    closestOrgNum = organismsList.indexOf(organism);
                                    break;
                                }
                            }

                        }

                    }
                    if (organismsDist < Math.max(org.cells.length * cellSize + 4, org.cells[0].length * cellSize + 4)) {

                        PVector cellPos = new PVector(0, 0);
                        float radius;
                        for (int x = 0; x < org.cells.length; x++) {
                            for (int y = 0; y < org.cells[x].length; y++) {
                                cellPos = cellPos.set(-(x * cellSize + (cellSize / 2f)) + (org.cells.length * cellSize), y * cellSize + (cellSize / 2f));
                                radius = center.dist(cellPos);
                                float cellAngle = (degrees(atan2(cellPos.y - center.y, cellPos.x - center.x)));
                                float newY = sin(radians((org.rotation - cellAngle - 90) % 360)) * radius;
                                float newX = cos(radians((org.rotation - cellAngle - 90) % 360)) * radius;
                                PVector centerCellPos = new PVector(pos.x + newY, pos.y + newX);
                                if (org.cells[x][y] == 8) {
                                    for (int cellX = 0; cellX < organism.cells.length; cellX++) {
                                        for (int cellY = 0; cellY < organism.cells[0].length; cellY++) {
                                            cellPos = cellPos.set(-(cellX * cellSize + (cellSize / 2f)) + (organism.cells.length * cellSize), cellY * cellSize + (cellSize / 2f));
                                            radius = organismCenter.dist(cellPos);
                                            float orgCellAngle = (degrees(atan2(cellPos.y - organismCenter.y, cellPos.x - organismCenter.x)));
                                            float newOrgY = sin(radians((org.rotation - orgCellAngle - 90) % 360)) * radius;
                                            float newOrgX = cos(radians((org.rotation - orgCellAngle - 90) % 360)) * radius;
                                            PVector newOrgCenterCellPos = new PVector(organismPos.x + newOrgY, organismPos.y + newOrgX);
                                            if (organism.cells[cellX][cellY] != 9 && organism.key != org.key) {
                                                if (newOrgCenterCellPos.dist(centerCellPos) < (cellSize+1)*2){
                                                    organism.health -= 10;
                                                    org.energy += 5;
                                                }
                                            }
                                        }
                                    }
                                }


                                noStroke();
//                            circle((boardWidth / (float) SCREEN_WIDTH) * (pos.x + newY) + offset.x, (boardWidth / (float) SCREEN_WIDTH) * (pos.y - newX) + offset.y, (boardWidth / (float) SCREEN_WIDTH) * 3);


                            }
                        }
                    }
                }


                angle = degrees(atan2(pos.y - (organismsList.get(closestOrgNum).pos.y + center.y), pos.x - (organismsList.get(closestOrgNum).pos.x + center.x)));
                angle = (angle + 180) % 360;
                if (closestOrg == 50) angle = (org.rotation - 90) % 360;
                orgX = cos(radians(angle)) * closestOrg;
                orgY = sin(radians(angle)) * closestOrg;
                if (lookAtOrg == org) {
                    stroke(255);
                    line((boardWidth / (float) SCREEN_WIDTH) * pos.x + offset.x, (boardWidth / (float) SCREEN_WIDTH) * pos.y + offset.y, (boardWidth / (float) SCREEN_WIDTH) * (pos.x + orgX) + offset.x, (boardWidth / (float) SCREEN_WIDTH) * (pos.y + orgY) + offset.y);
                    fill(255);
                    circle((boardWidth / (float) SCREEN_WIDTH) * (organismsList.get(closestOrgNum).pos.x + center.x) + offset.x, (boardWidth / (float) SCREEN_WIDTH) * (organismsList.get(closestOrgNum).pos.y + center.y) + offset.y, 5);
                }
                angle = (angle - org.rotation + 90) % 360;
                if (angle < 0) angle += 360;
                org.distanceToOrg = closestOrg;
                org.angleToOrg = (int) angle;

        }
            organismsList.removeAll(removedOrganisms);
            organismsList.addAll(addedOrganisms);


    }

    public void drawOrganisms() {
        noStroke();
        float cellDimX = (boardWidth / (float) SCREEN_WIDTH) * cellSize;
        float cellDimY = (boardHeight / (float) SCREEN_HEIGHT) * cellSize;
        for (int i = 0; i < organismsList.toArray().length; i++) {
            organismsList.get(i).getOrganismsList(organismsList);
//            organismsList.get(i).rotationAmount = noise(xoff) * 30 - 15;
            noFill();
            stroke(0);
            if (organismsList.get(i) == lookAtOrg)
                circle((boardWidth / (float) SCREEN_WIDTH) * (organismsList.get(i).pos.x + (organismsList.get(i).cells.length / 2f) * cellSize) + offset.x, (boardHeight / (float) SCREEN_HEIGHT) * (organismsList.get(i).pos.y + (organismsList.get(i).cells[0].length / 2f) * cellSize) + offset.y, 10 * cellDimX);
            noStroke();
            for (int x = 0; x < organismsList.get(i).cells.length; x++) {
                for (int y = 0; y < organismsList.get(i).cells[x].length; y++) {
                    Organism org = organismsList.get(i);
                    if (org.cells[x][y] == 0) {
                        noFill();
                    } else if (org.cells[x][y] == 1) {
                        fill(253, 255, 182);
                    } else if (org.cells[x][y] == 2) {
                        fill(189, 178, 255);
                    } else if (org.cells[x][y] == 3) {
                        fill(202, 255, 191);
                    } else if (org.cells[x][y] == 4 || org.cells[x][y] == 5 || org.cells[x][y] == 6 || org.cells[x][y] == 7) {
                        fill(255, 198, 255);
                    } else if (org.cells[x][y] == 8) {
                        fill(255, 173, 173);
                    } else if (org.cells[x][y] == 9) {
                        fill(255, 214, 165);
                    }
                    noStroke();
                    pushMatrix();
                    translate((boardWidth / (float) SCREEN_WIDTH) * (org.pos.x + (org.cells.length / 2f) * cellSize) + offset.x, (boardHeight / (float) SCREEN_HEIGHT) * (org.pos.y + (org.cells[x].length / 2f) * cellSize) + offset.y);
                    rotate(radians(organismsList.get(i).rotation));
                    noStroke();
                    rect(x * cellDimX - (org.cells.length * cellDimX / 2f), y * cellDimY - (org.cells[x].length * cellDimY / 2f), cellDimX, cellDimY);
                    if (org.cells[x][y] == 4 || org.cells[x][y] == 5 || org.cells[x][y] == 6 || org.cells[x][y] == 7) {
                        PVector offsets = new PVector(cellDimX / 3f, 0);
                        if (org.cells[x][y] == 5) offsets.set(cellDimX / 2f, cellDimY / 3f);
                        if (org.cells[x][y] == 6) offsets.set(cellDimX / 3f, cellDimY / 2f);
                        if (org.cells[x][y] == 7) offsets.set(0, cellDimX / 4f);
                        fill(0);
                        if (org.cells[x][y] == 4 || org.cells[x][y] == 6)
                            rect(x * cellDimX - (org.cells.length * cellDimX / 2f) + offsets.x, y * cellDimY - (org.cells[x].length * cellDimY / 2f) + offsets.y, cellDimX / 3, cellDimY / 2);
                        else
                            rect(x * cellDimX - (org.cells.length * cellDimX / 2f) + offsets.x, y * cellDimY - (org.cells[x].length * cellDimY / 2f) + offsets.y, cellDimX / 2, cellDimY / 3);
                    }
                    popMatrix();
                }
            }
        }
    }

    public void drawFood() {
        noStroke();
        for (Food food : foodList) {
            if (food.type == 0) fill(0, 175, 185);
            else if (food.type == 1) fill(240, 113, 103);
            circle(((boardWidth / (float) SCREEN_WIDTH) * (food.pos.x)) + offset.x, ((boardHeight / (float) SCREEN_HEIGHT) * (food.pos.y)) + offset.y, (boardWidth / (float) SCREEN_WIDTH) * food.size);
        }
    }

    // -------x---- custom functions ------x---------
    // ----------- mouse event functions ---------------
    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
        if (mouseX < SCREEN_WIDTH) {
            boardWidth += e * (SCREEN_WIDTH / 100f);
            boardHeight += e * (SCREEN_HEIGHT / 100f);
            offset.x -= e * ((mouseX) / 100f);
            offset.y -= e * ((mouseY) / 100f);
        }
    }

    public void mousePressed() {
        if (mouseX < SCREEN_WIDTH) { // in the board area
            mouseDownPos[0] = mouseX;
            mouseDownPos[1] = mouseY;
            mouseDownPos[2] = (int) offset.x;
            mouseDownPos[3] = (int) offset.y;
            mouseDown = true;
            for (Organism org :
                    organismsList) {
                if (Math.abs((mouseX - offset.x) / (boardWidth / (float) SCREEN_WIDTH) - (org.pos.x + (org.cells.length * cellSize) / 2f)) < 10 && Math.abs((mouseY - offset.y) / (boardHeight / (float) SCREEN_HEIGHT) - (org.pos.y + (org.cells[0].length * cellSize) / 2f)) < 10) {
//                    organismLookAtNum = org.key;
                    lookAtOrg = org;
                }
            }

        }
    }

    public void mouseClicked() {
        for (int i = 0; i < buttonPositions.length; i++) {
            float[] pos = buttonPositions[i];
            if (mouseX > pos[0] && mouseX < pos[0]+pos[2] && mouseY > pos[1] && mouseY < pos[1]+pos[3]) {
                if (Objects.equals(buttonNames[i], "organisms")) showOrganismSettings = true;
                if (Objects.equals(buttonNames[i], "data")) showOrganismSettings = false;
                if (Objects.equals(buttonNames[i], "show board")) drawStuff = true;
                if (Objects.equals(buttonNames[i], "hide board")) drawStuff = false;
            }
        }
    }

    public void mouseDragged() {
        if (mouseX < SCREEN_WIDTH) { // in the board area
            offset.x = mouseDownPos[2] - (mouseDownPos[0] - mouseX);
            offset.y = mouseDownPos[3] - (mouseDownPos[1] - mouseY);
        }

    }

    public void mouseReleased() {
        mouseDown = false;
    }

    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == UP && speed < 20) speed++;
            if (keyCode == DOWN && speed > 1) speed--;
        }
    }
    // -----x------ mouse event functions -------x--------
}