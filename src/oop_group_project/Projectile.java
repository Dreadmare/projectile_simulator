package oop_group_project;

public abstract class Projectile implements ProjectileBehaviour {
	protected String type;
	protected double velocity;
	protected double angle;
	protected final double GRAVITY = 9.81;
	
	public Projectile(String type, double velocity, double angle) {
		this.type = type;
		this.velocity = velocity;
		this.angle = angle;
	}
	
	public String getDetails() {
		return "Type: "+type+ " | Velocity: "+velocity+"m/s | Angle: "+angle+"°0";
	}
}
