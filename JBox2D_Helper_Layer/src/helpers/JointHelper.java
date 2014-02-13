package helpers;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class JointHelper {

	World world;
	public JointHelper(World world){
		this.world = world;
	}
	public RevoluteJoint revoluteJoint(Body A, Body B, Vec2 offset){
		RevoluteJointDef r = new RevoluteJointDef();
		r.bodyA = A;
		r.bodyB = B;
		r.collideConnected = false;
		r.localAnchorA = offset;
		r.localAnchorB = new Vec2();
		
		return (RevoluteJoint)world.createJoint(r);
	}
	
	public RevoluteJoint revoluteJoint(Body A, Body B) {
		return revoluteJoint(A, B, B.getWorldCenter().sub(A.getWorldCenter()));
	}

	
	public PrismaticJoint prismaticJoint(Body A, Body B){
		PrismaticJointDef r = new PrismaticJointDef();
		r.bodyA = A;
		r.bodyB = B;
		r.collideConnected = false;
		Vec2 diff = B.getWorldCenter().sub(A.getPosition());
		r.localAxisA.set(diff);
		r.localAxisA.normalize();
		
		System.out.println("difference: "+diff.length());
		r.localAnchorA.set(diff.mul(.5f));
		r.localAnchorB.set(diff.mul(-.5f));

		r.enableLimit = true;
		r.upperTranslation = diff.length()/2;
		r.lowerTranslation = -diff.length()/2;
		
		r.maxMotorForce = diff.length()*(A.getMass()+B.getMass())*60;
		PrismaticJoint ret = (PrismaticJoint)world.createJoint(r);

		
		return ret;
	}
	
	public void rotateToAngle(RevoluteJoint joint, float angle){
		
	}
	
	public Object downTheTree(Body start, JointCallback callback){
		ArrayList<Body> joints_todo = new ArrayList<Body>();
		ArrayList<Body> bodies_done = new ArrayList<Body>();
		joints_todo.add(start);
		JointEdge j;
		Joint jo;
		Body cur;

		while(joints_todo.size() > 0){
			cur = joints_todo.get(0);
			j = cur.getJointList();
			while(j != null){
				jo = j.joint;
				if(!bodies_done.contains(jo.getBodyA())){
					joints_todo.add(jo.getBodyA());
					callback.processBody(jo.getBodyA());
					bodies_done.add(jo.getBodyA());
				}else if(!bodies_done.contains(jo.getBodyB())){
					joints_todo.add(jo.getBodyB());
					callback.processBody(jo.getBodyB());
					bodies_done.add(jo.getBodyB());
				}
				j = j.next;
			}
			joints_todo.remove(0);
		}
		
		return callback.additive;
	}
	
	public static void moveAtoDistance(PrismaticJoint joint, float distance){
    	float total_ang = joint.getBodyB().getAngle();
    	
    	//Geting the joint angle
    	total_ang += (float)Math.atan2(joint.getLocalAxisA().y, joint.getLocalAxisA().x);
    	total_ang += (float)Math.PI;
    	
    	//getting the angle as a vector
    	Vec2 dir = new Vec2((float)Math.cos(total_ang), (float)Math.sin(total_ang));
    	
    	//setting the amount
    	dir = dir.mul(distance);
    	
    	//making sure we're ecounting for previous velocities
    	dir.sub(joint.getBodyA().getLinearVelocity());
    	
    	//making it momentum based
    	dir = dir.mul(joint.getBodyA().getMass());
    	
    	
    	
    	joint.getBodyA().applyLinearImpulse(dir, joint.getBodyA().getWorldCenter());

	}
	
	public boolean rtweenB(RevoluteJoint joint, float point){
		if(point < joint.getLowerLimit() || point > joint.getUpperLimit()) throw new Error("you got a problem || ul:"+joint.getUpperLimit()+", p:"+point+", ll:"+joint.getLowerLimit());
		Body B = joint.getBodyB();
    	
    	float angle = point - joint.getJointAngle() - joint.getJointSpeed()*1/60;

    	if(joint.getBodyB().isFixedRotation()) throw new Error("can't apply tween to fixed Rotation Body");
    	
    	float accelleration = joint.getMaxMotorTorque()/joint.getBodyB().getMass();
    	float MaxVel = Math.signum(angle)*(float)Math.sqrt(Math.abs(angle)*accelleration);

    	float curspeed = joint.getJointSpeed();

    	
    	float velDiff;
    	
    	
    	System.out.println("angle: "+angle);
    	System.out.println("accell: "+accelleration);
    	System.out.println("maxvel: "+MaxVel);
    	System.out.println("curspeed: "+curspeed);
    	if(Math.signum(MaxVel) == Math.signum(curspeed) && Math.abs(MaxVel) < Math.abs(curspeed)-accelleration){
    		return false; 
		}else{
/*			
			if(Math.signum(MaxVel) == Math.signum(curspeed)){
				System.out.println("right direction");
				if(Math.abs(MaxVel) < Math.abs(curspeed)) System.out.print(" decellerate");
				else System.out.print(" accellerate");
			}
*/			
    		velDiff = Math.signum(MaxVel - curspeed)*Math.min(Math.abs(MaxVel - curspeed), accelleration);
    	}
    	System.out.println("velDiff: "+velDiff);
    	B.applyAngularImpulse(velDiff);
    	
    	return true;

    	
	}


	public boolean tweenB(PrismaticJoint joint, float point){
		if(point < joint.getLowerLimit() || point > joint.getUpperLimit()) throw new Error("you got a problem");
		Body B = joint.getBodyB();
    	
    	/*
    	 * Things I need to worry about,
    	 * -Negative Distances (going backward not forward)
    	 * -When the current velocity is the opposite Sign of the distance (makes it so that I need to apply full accelleration)
    	 * -Coming to a stop (I need to apply negative accelleration despite the fact I'm moving in the right direction) 
    	 * 
    	 * 
    	 */
    	//Getting the Total Distance between the two points
    	float dist = point - joint.getJointTranslation() - joint.getJointSpeed()*1/60;
    	
    	//Getting the Maximum Accelleration that can be done to body B
    	float accelleration = joint.getMaxMotorForce()/(B.getMass()+joint.getBodyA().getMass());
    	
    	//Gets 	Maximum velocity that I can hit over that distance, 
    	//or 	Minimum velocity I need to be at to stop at that specific distance
    	//Note that this is number independent since even though distance may be negative, Accelleration/Decelleration is not
    	float MaxVel = (float)Math.sqrt(Math.abs(dist)*accelleration);

    	//Need to Make sure MaxVel is in the Direction of endpoint
    	MaxVel *= Math.signum(dist);
    	//curspeed may be going towards the end point or away from endpoint
    	float curspeed = joint.getJointSpeed();

    	
    	float velDiff;
    	//If they are going in the same direction and Maxspeed is less then curspeed
    	if(Math.signum(MaxVel) == Math.signum(curspeed) && Math.abs(MaxVel) < Math.abs(curspeed)-accelleration){
    		return false; //You're not going to make it with current accelleration
		}else{
			//finding out the difference between desired and current, bat we can only do as much as we can accellerate to
			//this is also able to calculate when the current velocity is going in the opposite direction of desired point
			//this also calculates slowing down
/*			
			if(Math.signum(MaxVel) == Math.signum(curspeed)){
				System.out.println("right direction");
				if(Math.abs(MaxVel) < Math.abs(curspeed)) System.out.print(" decellerate");
				else System.out.print(" accellerate");
			}
*/			
    		velDiff = Math.signum(MaxVel - curspeed)*Math.min(Math.abs(MaxVel - curspeed), accelleration);
    	}
    	
    	//need to calculate the angle the joint is going in
    	Vec2 angle = calculatePrismaticAngle(joint);
    	//we apply the Velocity Differrence and also multiply the mass to make it a force
    	angle = angle.mul(B.getMass()*velDiff);
//    	angle = angle.add(world.getGravity().mul(-B.getMass()));
    	
//    	System.out.println(angle);
    	//Appling the force
    	B.applyLinearImpulse(angle, B.getWorldCenter());
    	
    	//you can make it
    	return true;

    	
	}
	
	public static Vec2 calculatePrismaticAngle(PrismaticJoint joint){
    	float total_ang = joint.getBodyA().getAngle();
    	
    	total_ang += (float)Math.atan2(joint.getLocalAxisA().y, joint.getLocalAxisA().x);
    	Vec2 dir = new Vec2((float)Math.cos(total_ang), (float)Math.sin(total_ang));
    	dir.normalize();
    	return dir;
	}
	
	public void moveBtoPoint(PrismaticJoint joint, float point, float max){
		
		if(point < joint.getLowerLimit() || point > joint.getUpperLimit()) throw new Error("you got a problem");
		Body B = joint.getBodyB();
		
		//finding where we will be next tick
    	float current = joint.getJointTranslation();
    	float expected =  joint.getJointSpeed()/60f;

    	float dist = point - current - expected;
    	dist = Math.signum(dist)*Math.min(max, Math.abs(dist));
		
    	float total_ang = joint.getBodyA().getAngle();
    	
    	//Geting the joint angle
    	total_ang += (float)Math.atan2(joint.getLocalAxisA().y, joint.getLocalAxisA().x);
    	
    	//getting the angle as a vector
    	Vec2 dir = new Vec2((float)Math.cos(total_ang), (float)Math.sin(total_ang));
    	dir.normalize();
    	
    	//setting the amount
    	dir = dir.mul(dist*60).add(world.getGravity().mul(-1f/60f));
    	
    	
    	B.applyLinearImpulse(dir.mul(B.getMass()), B.getWorldCenter());
        
	}
	
}
