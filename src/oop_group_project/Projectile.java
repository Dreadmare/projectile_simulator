package oop_group_project;

public abstract class Projectile implements ProjectileBehaviour {
	protected double velocity;
	protected double angle;
	protected double mass;
    protected double dragCoeff;
	protected final double GRAVITY = 9.81;
	
	public Projectile(double velocity, double angle, double mass, double dragCoeff) {
		this.velocity = velocity;
		this.angle = angle;
		this.mass = mass;
		this.dragCoeff = dragCoeff;
	}
	
	@Override
    public abstract double calculateRange();

    @Override
    public double calculateMaxHeight() {
        double radians = Math.toRadians(angle);
        return (Math.pow(velocity * Math.sin(radians), 2)) / (2 * GRAVITY);
    }

    public String getDetails() {
        return String.format("Type: %s | Vel: %.1f m/s | Angle: %.1f°", velocity, angle);
    }

    @Override
    public String getPhysicsData() {
        return String.format("Mass: %.2f kg | Drag: %.2f", mass, dragCoeff);
    }

    // Getters for DatabaseManager to use
    public double getVelocity() { return velocity; }
    public double getAngle() { return angle; }
    public double getMass() { return mass; }
    public double getDragCoefficient() { return dragCoeff; }
}
