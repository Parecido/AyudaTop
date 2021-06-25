package topologyreader.data;

public class Point {
    private final DataIRC data;
    private final float value;

    public Point(DataIRC data, float value) {
        this.data = data;
        this.value = value;
    }

    public DataIRC getDataIRC() {
        return data;
    }

    public int getIndex() {
        return data.getIndex();
    }

    public float getValue() {
        return value;
    }
}
