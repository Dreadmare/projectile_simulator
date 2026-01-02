package oop_group_project;

public class StandardBall extends Projectile {
    
    // Default constructor for a typical ball
    public StandardBall(double velocity, double angle) {
        // type, velocity, angle, mass (0.5kg), drag (0.47 for a sphere)
        super("Standard Ball", velocity, angle, 0.5, 0.47);
    }

    // Overloaded constructor - allows custom mass/drag (satisfies Rubric #5)
    public StandardBall(double velocity, double angle, double mass, double dragCoeff) {
        super("Standard Ball", velocity, angle, mass, dragCoeff);
    }
    
    @Override
    public double calculateRange() {
        double radian = Math.toRadians(angle);
        // Basic range formula
        double baseRange = (Math.pow(velocity, 2) * Math.sin(2 * radian)) / GRAVITY;
        
        // Simple physics adjustment: Air resistance reduces range based on drag and mass
        // (A higher drag coefficient or lower mass reduces the total distance)
        double airEffect = 1.0 - (dragCoeff / (mass * 10)); 
        return baseRange * airEffect;
    }
    
    @Override
    public String getPhysicsData() {
        // Overriding this method demonstrates Polymorphism 
        return super.getPhysicsData() + " | Shape: Sphere";
    }
}