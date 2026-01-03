package oop_group_project;

public class StandardBall extends Projectile {
    public StandardBall(double velocity, double angle) {
        super(velocity, angle, 0.5, 0.47);
    }


    public StandardBall(double velocity, double angle, double mass, double dragCoeff) {
        super(velocity, angle, mass, dragCoeff);
    }
    
    @Override
    public double calculateRange() {
        double radian = Math.toRadians(angle);
        double baseRange = (Math.pow(velocity, 2) * Math.sin(2 * radian)) / GRAVITY;
        double airEffect = 1.0 - (dragCoeff / (mass * 10)); 
        return baseRange * airEffect;
    }
    
    @Override
    public String getPhysicsData() {
        return super.getPhysicsData() + " | Shape: Sphere";
    }
}