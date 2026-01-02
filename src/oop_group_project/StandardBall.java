package oop_group_project;

public class StandardBall extends Projectile {
	
	public StandardBall(double velocity, double angle) {
		super("Standard Ball", velocity, angle);
	}
	
	@Override
	public double calculateRange() {
		double radian = Math.toRadians(angle);
		return(Math.pow(velocity, 2)*Math.sin(2*radian)) / GRAVITY;
	}
	
	public double calculateMaxHeight() {
		double radian = Math.toRadians(angle);
		return(Math.pow(velocity,2)*Math.sin(2*radian)) / (2*GRAVITY);
	}
}
