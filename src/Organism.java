import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;

//1 = stomach - yellow
//2 = mouth - purple
//3 = mover - green
//4 = front eye - violate
//5 = right eye - violate
//6 = bottom eye - violate
//7 = left eye - violate
//8 = killer - red
//9 = shell - orange


public class Organism {
    int key;
    int generation;
    int age;
    int[][] cells;
    List<Organism> organismsList;
    PVector pos;
    float boardWidth, boardHeight;
    int BOARD_WIDTH, BOARD_HEIGHT;
    PVector offset;
    int mouthSize;
    boolean hasShell;
    float rotation;
    float speed;
    float maxSpeed;
    int maxEnergy;
    float energy;
    int maxHealth;
    float health;
    float seeDistance;
    float distanceToFood;
    float distanceToOrg;
    int angleToFood;
    int angleToOrg;
    float reproductiveUrge;
    float mutationChance;
    float mutationSize;
    float metabolismCost;
    float rotationAmount;
    int[][] viewAngles;
    int clock;
    int maxClock;
    float blockAmount;
    float metabolismDivideAmount;
    NeuralNetwork neuralNetwork;


    public Organism(float divAmount) {
        this.boardWidth = 1125;
        this.boardHeight = 750;
        this.BOARD_WIDTH = (int)this.boardWidth;
        this.BOARD_HEIGHT = (int)this.boardHeight;
        this.organismsList = new ArrayList<>();
        this.mouthSize = 0;
        this.offset = new PVector(0, 0);
        this.age = 0;
        this.generation = 0;
        this.rotation = (int)(Math.random()*360);
        this.speed = 0;
        this.hasShell = false;
        this.maxSpeed = 0;
        this.seeDistance = 50;
        this.maxHealth = 0;
        this.health = this.maxHealth;
        this.maxEnergy = 0;
        this.energy = this.maxEnergy;
        this.distanceToFood = 0;
        this.distanceToOrg = 0;
        this.angleToFood = 0;
        this.angleToOrg = 0;
        this.reproductiveUrge = 0;
        this.cells = new int[2][2];
        this.cells[0][0] = 4;
        this.cells[1][0] = 2;
        this.cells[0][1] = 3;
        this.cells[1][1] = 1;
//        this.cells[0][0] = (int)(Math.random()*10);
//        this.cells[1][0] = (int)(Math.random()*10);
//        this.cells[0][1] = (int)(Math.random()*10);
//        this.cells[1][1] = (int)(Math.random()*10);
        this.rotationAmount = 0;
        this.clock = 0;
        this.maxClock = 60;
        this.mutationChance = (float)Math.random();
        this.mutationSize = (float)Math.random();
        this.metabolismCost = 0;
        this.viewAngles = new int[4][2];
        this.pos = new PVector((float)(Math.random()*this.boardWidth), (float)(Math.random()*this.boardHeight));
        neuralNetwork = new NeuralNetwork(key, 9,
                3,
                this.speed,
                this.maxSpeed,
                this.rotation,
                this.health,
                this.maxHealth,
                this.energy,
                this.maxEnergy,
                this.distanceToFood,
                this.distanceToOrg,
                this.angleToFood,
                this.angleToOrg,
                this.reproductiveUrge,
                this.seeDistance
                );
//        addCell(2, -1, 0);
        this.metabolismDivideAmount = divAmount;
        this.metabolismCost = getMetabolismCost();
        this.blockAmount = 0;
        neuralNetwork.getOrganismsList(organismsList);

    }

    public void update(float boardWidth, float boardHeight, PVector offset) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.offset = offset;
        neuralNetwork.getOrganismsList(organismsList);
        checkCollision();

        this.rotation += (neuralNetwork.nodes.get(10).outputValue * 4) -2;
        if (this.rotation > 360) {
            this.rotation = 0;
        } else if (this.rotation < 0) {
            this.rotation = 360;
        }
        this.reproductiveUrge = (float)neuralNetwork.nodes.get(11).outputValue;
        this.speed = (float)neuralNetwork.nodes.get(9).outputValue*maxSpeed;
//        System.out.println(maxSpeed);
        this.pos.x -= (Math.sin(Math.toRadians(-this.rotation)) * this.speed)/5;
        this.pos.y -= (Math.cos(Math.toRadians(-this.rotation)) * this.speed)/5;
        if (this.clock > this.maxClock) {
            this.age ++;
            this.energy -= this.metabolismCost/this.metabolismDivideAmount;
            if (this.energy > this.maxEnergy) this.energy = this.maxEnergy;
            if (this.energy <=0 ) {
                this.energy = 0;
                this.health -= 1;
            } else {
                this.health += .5;
                if (this.health > this.maxHealth) {
                    this.health = this.maxHealth;
                }
            }
            this.clock = 0;
        }
        neuralNetwork.seeDistance = this.seeDistance;
        neuralNetwork.rotation = this.rotation;
        neuralNetwork.distanceToFood = this.distanceToFood;
        neuralNetwork.distanceToOrg = this.distanceToOrg;
        neuralNetwork.angleToFood = this.angleToFood;
        neuralNetwork.angleToOrg = this.angleToOrg;
        neuralNetwork.maxEnergy = this.maxEnergy;
        neuralNetwork.energy = this.energy;
        neuralNetwork.maxHealth = this.maxHealth;
        neuralNetwork.health = this.health;
        neuralNetwork.speed = this.speed;
        neuralNetwork.maxSpeed = this.maxSpeed;
        neuralNetwork.reproductiveUrge = this.reproductiveUrge;


        neuralNetwork.evaluate();
        this.clock++;
    }
    public float getMetabolismCost() {
//1 = stomach - yellow
//2 = mouth - purple
//3 = mover - green
//4 = front eye - violate
//5 = right eye - violate
//6 = bottom eye - violate
//7 = left eye - violate
//8 = killer
//9 = shell
        float totalCost = 0;
        int stomachNumber = 0;
        int moverNumber = 0;
        int mouthNumber = 0;
        int shellNumber = 0;
        int cellNumber = 0;

        for (int[] cell : cells) {
            for (int i : cell) {
                if (i == 1) {
                    totalCost += .1;
                    stomachNumber++;
                    cellNumber ++;
                }
                if (i == 2) {
                    totalCost += .1;
                    mouthNumber ++;
                    cellNumber ++;
                }
                if (i == 3) {
                    totalCost += .3;
                    moverNumber++;
                    cellNumber ++;
                }
                if (i == 4 || i == 5 || i == 6 || i == 7) {
                    totalCost += .3;
                    cellNumber ++;
                    if (i == 4) {
                        this.viewAngles[0][0] = 0;
                        this.viewAngles[0][1] = 90;
                    }
                    if (i == 5) {
                        this.viewAngles[1][0] = 90;
                        this.viewAngles[1][1] = 180;
                    }
                    if (i == 6) {
                        this.viewAngles[2][0] = 180;
                        this.viewAngles[2][1] = 270;
                    }
                    if (i == 7) {
                        this.viewAngles[3][0] = 270;
                        this.viewAngles[3][1] = 359;
                    }
                }
                if (i == 8) {
                    totalCost += .4;
                    cellNumber ++;
                }
                if (i == 9) {
                    totalCost += .4;
                    shellNumber ++;
                    cellNumber ++;
                    this.hasShell = true;
                }
            }
        }
        this.mouthSize = mouthNumber *2;
        this.maxHealth = cellNumber * 5;
        this.health = this.maxHealth;
        this.maxEnergy = max(stomachNumber * 20,1);
        this.energy = this.maxEnergy/2f;
        this.maxSpeed = max(moverNumber * 2, .2f);
        this.speed = maxSpeed/2;
        this.blockAmount = shellNumber * .2f;


        totalCost += speed/maxSpeed;
        totalCost += (this.neuralNetwork.nodes.toArray().length-11)/10f;
        totalCost += cellNumber/10f;
        return totalCost;
    }
    public void checkCollision() {
        // walls
        if (this.pos.x < 0 || this.pos.x > this.BOARD_WIDTH || this.pos.y < 0 || this.pos.y > this.BOARD_HEIGHT) {
            if (this.pos.x < 0) this.pos.x = 1;
            if (this.pos.y < 0) this.pos.y = 1;
            if (this.pos.x > this.BOARD_WIDTH) this.pos.x = this.BOARD_WIDTH - 1;
            if (this.pos.y > this.BOARD_HEIGHT) this.pos.y = this.BOARD_HEIGHT - 1;
//            if (this.rotation <= 180) this.rotation += 180;
//            else this.rotation -= 180;
            this.rotation = (float)Math.random()*360;
        }
    }
    public void getOrganismsList(List<Organism> organisms) {
        organismsList = organisms;
    }
    public void addCell(int type, int xIndex, int yIndex) {
        int [][] newCells;
        if ((xIndex == -1 || xIndex == this.cells.length)&&!(yIndex==-1 || yIndex==this.cells[0].length)) {
            newCells = new int[this.cells.length+1][this.cells[0].length];
            if (xIndex == -1) {
                for (int x = 0; x < this.cells.length; x++) {
                    System.arraycopy(this.cells[x], 0, newCells[x + 1], 0, this.cells[x].length);
                }
                newCells[xIndex+1][yIndex] = type;
            } else {
                for (int x = 0; x < cells.length; x++) {
                    System.arraycopy(this.cells[x], 0, newCells[x], 0, this.cells[x].length);
                }
                newCells[xIndex][yIndex] = type;
            }
            this.cells = newCells;
        } else if ((yIndex == -1 || yIndex == this.cells[0].length)&&!(xIndex==-1 || xIndex==this.cells.length)) {
            newCells = new int[this.cells.length][this.cells[0].length+1];
            if (yIndex == -1) {
                for (int x = 0; x < this.cells.length; x++) {
                    System.arraycopy(this.cells[x], 0, newCells[x], 1, this.cells[x].length);
                }
                newCells[xIndex][yIndex+1] = type;
            } else {
                for (int x = 0; x < this.cells.length; x++) {
                    System.arraycopy(this.cells[x], 0, newCells[x], 0, this.cells[x].length);
                }
                newCells[xIndex][yIndex] = type;
            }
            this.cells = newCells;
        } else if (xIndex >= 0 && xIndex < this.cells.length && yIndex >= 0 && yIndex < this.cells[0].length && this.cells[xIndex][yIndex] == 0) {
            this.cells[xIndex][yIndex] = type;
        } else {
            System.out.println("ERROR: a cell can not grow there");
        }

    }
    public void mutate() {
        // -------- mutate mutation size and amount ------
        if (Math.random()<this.mutationChance) {
            if (Math.random() > .5) {
                this.mutationChance += Math.random() * this.mutationSize;
                this.mutationSize += Math.random() * this.mutationSize;
            } else {
                this.mutationChance -= Math.random() * this.mutationSize;
                this.mutationSize -= Math.random() * this.mutationSize;
            }
        }

        // ------- mutate brain ---------
        for (Node node: this.neuralNetwork.nodes) { // change biases of nodes
            if (Math.random()<this.mutationChance) {
                if (Math.random() > .5) {node.bias += Math.random()*this.mutationSize;}
                else {node.bias -= Math.random()*this.mutationSize;}
            }
        }
        for (Connection connection: this.neuralNetwork.connections) { // change weight of connections
            if (Math.random()<this.mutationChance) {
                if (Math.random() > .5) {connection.weight += Math.random()*this.mutationSize;}
                else {connection.weight -= Math.random()*this.mutationSize;}
            }
        }
        if (Math.random()<this.mutationChance) { // add a node
            if (Math.random() > 0) {
                Connection randomConnection = this.neuralNetwork.connections.get((int) (Math.random() * this.neuralNetwork.connections.toArray().length - 1));
                if (randomConnection.in < 9 && randomConnection.out > 8 && randomConnection.out < 12) {
                    Node newNode = new Node(3, this.neuralNetwork.nodeNumber, 0);
                    this.neuralNetwork.nodeNumber++;
                    this.neuralNetwork.nodes.add(newNode);
                    Connection newFirstConnection = new Connection(randomConnection.in, this.neuralNetwork.nodes.indexOf(newNode));
                    Connection newSecondConnection = new Connection(this.neuralNetwork.nodes.indexOf(newNode), randomConnection.out);
                    this.neuralNetwork.connections.remove(randomConnection);
                    this.neuralNetwork.connections.add(newFirstConnection);
                    this.neuralNetwork.connections.add(newSecondConnection);
                }

            } else {
//                if (this.neuralNetwork.nodes.toArray().length > 11) {
//                    int removeNode = (int)(Math.random()*(this.neuralNetwork.nodes.toArray().length-12))+12;
//                    System.out.println(removeNode);
//                    this.neuralNetwork.connections.removeIf(connection -> connection.in == removeNode || connection.out == removeNode);
//                    this.neuralNetwork.nodes.remove(removeNode);
//                }
            }

        }
        if (Math.random()<this.mutationChance) { // add a connection
            Connection newConnection;
            if (Math.random() > .5) { // start at input node
                if (Math.random() > .5 && this.neuralNetwork.nodes.toArray().length>11) { // end at hidden node
                    newConnection = new Connection((int)(Math.random()* this.neuralNetwork.inputNum), (int)(Math.random()* (this.neuralNetwork.nodes.toArray().length - (this.neuralNetwork.inputNum+this.neuralNetwork.outputNum))));
                } else { // end at output node
                    newConnection = new Connection((int)(Math.random()* this.neuralNetwork.inputNum), (int)(Math.random()*this.neuralNetwork.outputNum)+this.neuralNetwork.inputNum);
                }
            } else { // start at hidden node
                newConnection = new Connection((int)(Math.random()* (this.neuralNetwork.nodes.toArray().length - (this.neuralNetwork.inputNum+this.neuralNetwork.outputNum -1))), (int)(Math.random()*this.neuralNetwork.outputNum)+this.neuralNetwork.inputNum-1);
            }
            this.neuralNetwork.connections.add(newConnection);

        }

        // ------- mutate body --------
        if (Math.random()<this.mutationChance) {
            //add or remove or change cell
            if (Math.random() > .5) { // remove cell
                this.cells[(int)(Math.random()*this.cells.length)][(int)(Math.random()*this.cells[0].length)] = 0;
            } else {
                addCell((int)(Math.random()*9+1),(int)(Math.random()*(this.cells.length+2))-1, (int)(Math.random()*(this.cells[0].length+2))-1);
            }
        }
    }
}
