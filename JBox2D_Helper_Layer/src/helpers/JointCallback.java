package helpers;

import org.jbox2d.dynamics.Body;

public abstract class JointCallback {

	public Object additive;
	public JointCallback(Object begin){
		this.additive = begin;
	}
	
	public abstract void processBody(Body body);
}
