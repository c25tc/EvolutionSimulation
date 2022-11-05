public class Connection {
    int in;
    int out;
    float weight;
    boolean enabled;

    public Connection(int in, int out) {
        this.in = in;
        this.out = out;
        this.weight = (float)Math.random()*2-1;
        this.enabled = true;
    }
    public Connection makeDeepCopy() {
        Connection newConnection = new Connection(this.in, this.out);
        newConnection.weight = this.weight;
        return newConnection;
    }
}
