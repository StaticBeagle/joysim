package com.wildbitsfoundry;

import com.wildbitsfoundry.etk4j.math.linearalgebra.MatrixSparse;

public class Capacitor extends CircuitElement {

    double capacitance;
    double previousVoltage1;
    double previousVoltage2;
    double previousCurrent;

    public Capacitor(String id, int node1, int node2, double capacitance, double initialVoltage) {
        super(id, node1, node2);
        this.capacitance = capacitance;
        this.previousVoltage1 = initialVoltage;
        this.previousVoltage2 = initialVoltage;
    }

    @Override
    public void stamp(MatrixSparse mnaMatrix, double[] solutionVector, double dt, IntegrationMethod integrationMethod) {
        int n1 = node1;
        int n2 = node2;
        double gC = 0;
        double iEq = 0;
        switch (integrationMethod) {
            case TRAPEZOIDAL -> {
                gC = 2 * capacitance / dt;
                iEq = gC * previousVoltage1 + previousCurrent;
            }
            case BACKWARDS_EULER -> {
                gC = capacitance / dt;
                iEq = gC * previousVoltage1;
            }
            case GEAR_2 -> {
                gC = 3 * capacitance / (2 * dt);
                iEq = 2 * capacitance * previousVoltage1 / dt - capacitance * previousVoltage2 / (2 * dt);
            }
        }

        if(n1 != 0) {
            mnaMatrix.unsafeSet(n1 - 1, n1 -1, mnaMatrix.unsafeGet(n1 - 1, n1 - 1) + gC);
        }
        if(n2 != 0) {
            mnaMatrix.unsafeSet(n2 - 1, n2 -1, mnaMatrix.unsafeGet(n2 - 1, n2 - 1) + gC);
        }
        if(n1 != 0 && n2 != 0) {
            mnaMatrix.unsafeSet(n1 - 1, n2 -1, mnaMatrix.unsafeGet(n1 - 1, n2 - 1) - gC);
            mnaMatrix.unsafeSet(n2 - 1, n1 -1, mnaMatrix.unsafeGet(n2 - 1, n1 - 1) - gC);
        }

        if(n1 != 0) {
            solutionVector[n1 - 1] = iEq;
        }
        if(n2 != 0) {
            solutionVector[n2 - 1] = -iEq;
        }
    }

    @Override
    public void updateMemory(double[] solutionVector, double dt, IntegrationMethod integrationMethod) {
        int n1 = node1;
        int n2 = node2;
        double gC = 0;
        switch (integrationMethod) {
            case TRAPEZOIDAL -> gC = 2 * capacitance / dt;
            case BACKWARDS_EULER -> gC = capacitance / dt;
            case GEAR_2 -> gC = 3 * capacitance / (2 * dt);
        }

        if(n1 != 0) {
            double voltageDifference = solutionVector[n1 - 1] - previousVoltage1;
            previousVoltage2 = previousVoltage1;
            previousVoltage1 = solutionVector[n1 - 1];
            previousCurrent = gC * voltageDifference - previousCurrent;
        }
        if(n2 != 0) {
            double voltageDifference = solutionVector[n2 - 1] - previousVoltage1;
            previousVoltage2 = previousVoltage1;
            previousVoltage1 = solutionVector[n2 - 1];
            previousCurrent = gC * voltageDifference - previousCurrent;
        }
    }
}
