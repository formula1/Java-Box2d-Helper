package entity_managers;


import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public interface CollisionManager {
	
	public abstract void preSolve(Contact contact, Manifold point_manifold, boolean is_fix_a);
	public abstract void beginContact(Contact contact, boolean is_fix_a);
	public abstract void endContact(Contact contact, boolean is_fix_a);
	public abstract void postSolve(Contact contact, ContactImpulse impulse, boolean is_fix_a);
	


}
