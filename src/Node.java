public class Node {
    int type; // 1: sensor, 2: output, 3: hidden
    int key;
    float xPos;
    float yPos;
    float totalInput;
    int totalInputCount;
    double inputValue;
    double outputValue;
    float bias;
    public Node(int type, int key, float inputValue) {
        this.totalInputCount = 0;
        this.type = type;
        this.key = key;
        this.inputValue = inputValue;
        this.bias = (float)Math.random();
    }
    public void evaluate() {
        if (type == 1) {
            this.outputValue = this.inputValue;
        } else if (type == 2) {
            this.inputValue = this.totalInput/this.totalInputCount + this.bias;
            this.outputValue = sigmoid_activation(this.inputValue);
        } else if (type == 3) {
            this.inputValue = this.totalInput/this.totalInputCount + this.bias;
            this.outputValue = sigmoid_activation(this.inputValue);
        }

    }
    public double sigmoid_activation(double z){
        double x = Math.max(-60.0, Math.min(60.0, 5.0 * z));
        return 1.0 / (1.0 + Math.exp(-x));
    }
    public double relu_activation(double z) {
        if (z > 0.0) return z;
        return 0;
    }
    public Node makeDeepCopy() {
        Node newNode = new Node(this.type, this.key, 0);
        newNode.bias = this.bias;
        return newNode;
    }

}
