import java.util.ArrayList;
import java.util.List;

// inputs: energy, health, current speed, distance and angle to nearest: food, wall, organism
// outputs: speed, turn, reproduce
public class NeuralNetwork {
    List<Node> nodes;
    List<Connection> connections;
//    Organism[] organisms;
    List<Organism> organismsList;
    int key;
    int inputNum;
    int outputNum;
    int hiddenNum;
    int nodeNumber;
    int connectionNumber;
    float rotation;
    float speed;
    float maxSpeed;
    int maxEnergy;
    float energy;
    int maxHealth;
    float health;
    float distanceToFood;
    float distanceToOrg;
    int angleToFood;
    int angleToOrg;
    float reproductiveUrge;
    float seeDistance;

    public NeuralNetwork(int key, int inputNum, int outputNum, float speed, float maxSpeed, float rotation, float health, int maxHealth, float energy, int maxEnergy,
                         float distanceToFood,
                         float distanceToOrg,
                         int angleToFood,
                         int angleToOrg,
                         float reproductiveUrge,
                         float seeDistance) {
        this.key = key;
        this.organismsList = new ArrayList<>();;
        this.nodeNumber = 0;
        this.connectionNumber = 10;
        this.inputNum = inputNum;
        this.outputNum = outputNum;
        this.hiddenNum = 0;
        this.rotation = rotation;
        this.speed = speed;
        this.maxSpeed = maxSpeed;
        this.maxEnergy = maxEnergy;
        this.energy = energy;
        this.maxHealth = maxHealth;
        this.health = health;
        this.distanceToFood = distanceToFood;
        this.distanceToOrg = distanceToOrg;
        this.angleToFood = angleToFood;
        this.angleToOrg = angleToOrg;
        this.reproductiveUrge = reproductiveUrge;
        this.seeDistance = seeDistance;
        this.nodes = new ArrayList<Node>();
        this.connections = new ArrayList<Connection>();
        for (int i = 0; i < this.inputNum; i++) { // add input nodes
            if (i == 0) nodes.add(new Node(1, nodeNumber, (float) this.rotation / 360));
            else if (i == 1) nodes.add(new Node(1, nodeNumber, this.speed / this.maxSpeed));
            else if (i == 2) nodes.add(new Node(1, nodeNumber, (float) this.energy / this.maxEnergy));
            else if (i == 3) nodes.add(new Node(1, nodeNumber, (float) this.health / this.maxHealth));
            else if (i == 4) nodes.add(new Node(1, nodeNumber, this.reproductiveUrge));
            else if (i == 5) nodes.add(new Node(1, nodeNumber, this.distanceToFood/this.seeDistance));
            else if (i == 6) nodes.add(new Node(1, nodeNumber, this.distanceToOrg/this.seeDistance));
            else if (i == 7) nodes.add(new Node(1, nodeNumber, this.angleToFood));
            else if (i == 8) nodes.add(new Node(1, nodeNumber, this.angleToOrg));
            nodeNumber++;
        }
        for (int i = 0; i < this.outputNum; i++) { // add output nodes
            nodes.add(new Node(2, nodeNumber, 0));
            nodeNumber++;
        }
//        nodes.add(new Node(3, nodeNumber, 0));
//        nodeNumber++;
//        nodes.add(new Node(3, nodeNumber, 0));
//        nodeNumber++;
//        nodes.add(new Node(3, nodeNumber, 0));
//        nodeNumber++;
        for (int i = 0; i < connectionNumber; i++) { // randomly add connections
            int start = (int) (Math.random() * inputNum);
            int end = (int) (Math.random() * outputNum) + inputNum;
            connections.add(new Connection(start, end));
        }
//        connections.add(new Connection(0, 10));
//        connections.add(new Connection(8, 11));
//        connections.add(new Connection(3, 12));
//        connections.add(new Connection(12, 9));
//        connections.add(new Connection(12, 10));
//        connections.add(new Connection(12, 11));
    }

    public void evaluate() {
        for (Node node: this.nodes) {
            node.totalInput = 0;
            node.totalInputCount = 0;
        }
        for (int i = 0; i < this.inputNum; i++) { // add input nodes
            if (i == 0) nodes.get(i).inputValue = this.rotation/360f;
            else if (i == 1) nodes.get(i).inputValue = this.speed/this.maxSpeed;
            else if (i == 2) nodes.get(i).inputValue = (float) this.energy / this.maxEnergy;
            else if (i == 3) nodes.get(i).inputValue = (float) this.health / this.maxHealth;
            else if (i == 4) nodes.get(i).inputValue = (float) this.reproductiveUrge;
            else if (i == 5) nodes.get(i).inputValue = this.distanceToFood/this.seeDistance;
            else if (i == 6) nodes.get(i).inputValue = this.distanceToOrg/this.seeDistance;
            else if (i == 7) nodes.get(i).inputValue = this.angleToFood/360f;
            else if (i == 8) nodes.get(i).inputValue = this.angleToOrg/360f;

        }
//        for (Node node: this.nodes) {
//            if (node.type == 1) {
//                node.evaluate();
//                for (Connection con : connections) {
//                    if (con.in == this.nodes.indexOf(node)) {
//                        this.nodes.get(con.out).inputValue
//                    }
//                }
//            }
//        }

        for (Node node : nodes) {
            if (node.type == 1) {
                node.evaluate();
            }
        }
        for (Connection con : connections) {
            if (nodes.get(con.out).type == 3) {
                nodes.get(con.out).totalInput += nodes.get(con.in).outputValue * con.weight;
                nodes.get(con.out).totalInputCount ++;
            }
        }
        for (Node node : nodes) {
            if (node.type == 3) {
                node.evaluate();
            }
        }
        for (Connection con : connections) {
            if (nodes.get(con.out).type == 2) {
                nodes.get(con.out).totalInput += nodes.get(con.in).outputValue * con.weight;
                nodes.get(con.out).totalInputCount ++;
            }
        }
        for (Node node : nodes) {
            if (node.type == 2) {
                node.evaluate();
            }
        }

    }



    public void getOrganismsList(List<Organism> organisms) {
        organismsList = organisms;
    }
    public NeuralNetwork makeDeepCopy() {
        NeuralNetwork newNetwork = new NeuralNetwork(this.key, 9,
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
        newNetwork.nodes = new ArrayList<Node>();
        for (Node node: this.nodes) {
            newNetwork.nodes.add(node.makeDeepCopy());
        }
        newNetwork.connections = new ArrayList<Connection>();
        for (Connection connection: this.connections) {
            newNetwork.connections.add(connection.makeDeepCopy());
        }
        return newNetwork;
    }

}

