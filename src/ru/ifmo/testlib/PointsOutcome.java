package ru.ifmo.testlib;

public class PointsOutcome extends Outcome {
    double points;
    boolean isInt;
    public PointsOutcome(double points, String comment) {
        super(Type.POINTS, comment);
        this.points = points;
        this.isInt = false;
    }
    public PointsOutcome(int points, String comment) {
        super(Type.POINTS, comment);
        this.points = points;
        this.isInt = true;
    }

    public double getPoints() {
        return points;
    }

    @Override
    public String getComment() {
        return (isInt ? ("" + (int)points) : String.format("%.10f ", points)) + super.getComment();
    }
}
