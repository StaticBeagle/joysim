package com.wildbitsfoundry;

import com.wildbitsfoundry.etk4j.math.linearalgebra.MatrixSparse;

public abstract class CircuitElement {
    int node1;
    int node2;

    String id;

    public CircuitElement(String id, int node1, int node2) {
        this.id = id;
        this.node1 = node1;
        this.node2 = node2;
    }

    public abstract void stamp(MatrixSparse mnaMatrix, double[] solutionVector);
}
