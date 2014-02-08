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
	
	public PrismaticJoint prismaticJoint(Body A, Body B){
		PrismaticJointDef r = new PrismaticJointDef();
		r.bodyA = A;
		r.bodyB = B;
		r.collideConnected = false;
		r.localAxisA.set(new Vec2(0,-1));
		r.localAxisA.normalize();
		
		Vec2 diff = B.getWorldCenter().sub(A.getPosition());
		
		r.localAnchorA.set(0,0);
		r.localAnchorB.set(diff);
		
		
		PrismaticJoint ret = (PrismaticJoint)world.createJoint(r);
		ret.enableLimit(true);
		ret.setLimits(diff.length(), 0);
		
		
		return ret;
	}
	
	public Object downTheTree(Body start, JointCallback callback){
		ArrayList<Body> todo = new ArrayList<Body>();
		ArrayList<Body> done = new ArrayList<Body>();
		todo.add(start);
		JointEdge j;
		Joint jo;
		Body cur;

		while(todo.size() > 0){
			cur = todo.get(0);
			done.add(cur);
			j = start.getJointList();
			while(j != null){
				jo = j.joint;
				if(!done.contains(jo.getBodyA())){
					todo.add(jo.getBodyA());
					callback.processBody(jo.getBodyA());
				}else if(!done.contains(jo.getBodyB())){
					todo.add(jo.getBodyB());
					callback.processBody(jo.getBodyB());
				}
				j = j.next;
			}
			todo.remove(0);
		}
		
		return callback.additive;
	}
	
}
