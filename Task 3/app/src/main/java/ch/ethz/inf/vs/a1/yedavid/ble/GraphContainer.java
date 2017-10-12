package ch.ethz.inf.vs.a1.yedavid.ble;

public interface GraphContainer {

    /**
     * Add values to the underlying graph with the corresponding index.
     *
     * @param xIndex The x index.
     * @param values The values. If there is more than one value there should be several series.
     */
    void addValues(double xIndex, float[] values);

    /**
     * Get all values currently displayed in the graph.
     *
     * @return A matrix containing the values in the right order (oldest values first).
     *         The rows are the samples, the columns the values for each series (i.e. dimensions). A row contains a single sample.
     *         E.g. for acceleration (3 values per sample):
     *         1.5 2.4 3.5
     *         3.0 5.0 5.8
     *         4.5 6.7 2.1
     *         7.5 5.4 1.2
     *         ... ... ...
     */
    float[][] getValues();

}