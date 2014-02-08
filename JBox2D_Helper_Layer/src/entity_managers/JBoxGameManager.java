package entity_managers;

import java.util.ArrayList;


import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import helpers.BodyHelper;
import abstracts.Game;
import assets.FixtureData;

public abstract class JBoxGameManager extends Game implements ContactListener{
	static BodyHelper helper;

	int velIterations=6;
	int posIterations=2;
	public static World world;

	static ArrayList<Body> to_die = new ArrayList<Body>();
	
	
	public JBoxGameManager(Vec2 grav){
		super();
		world = new World(grav);
		world.setContactListener(this);
		
	}
		
	public abstract void doAI(long time);
	
	public void time(long time) {
		to_die = new ArrayList<Body>();
		doAI(time);
		world.step(((float)time/1000f),velIterations,posIterations);
		cleanUp();
	}


	public static void kill(Body b){
		to_die.add(b);
	}
	public void cleanUp(){
		ArrayList<Object> alreadyHandled = new ArrayList<Object>();
		for(Body b : to_die){
			if(b.getUserData() != null && !alreadyHandled.contains(b.getUserData())){
				deleteAssociated(b.getUserData());
				alreadyHandled.add(b.getUserData());
			}
			world.destroyBody(b);
		}
	}
	
	public void beginContact(Contact contact) {
		FixtureData adata = (FixtureData)contact.getFixtureA().getUserData();
		FixtureData bdata = (FixtureData)contact.getFixtureB().getUserData();
		
		if(adata != null && adata.collision_manager != null){
			adata.collision_manager.beginContact(contact, true);
		}
		if(bdata != null && bdata.collision_manager != null){
			bdata.collision_manager.beginContact(contact, false);
		}
	}

	public void endContact(Contact contact) {
		FixtureData adata = (FixtureData)contact.getFixtureA().getUserData();
		FixtureData bdata = (FixtureData)contact.getFixtureB().getUserData();
		
		if(adata != null && adata.collision_manager != null){
			adata.collision_manager.endContact(contact, true);
		}
		if(bdata != null && bdata.collision_manager != null){
			bdata.collision_manager.endContact(contact, false);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		FixtureData adata = (FixtureData)contact.getFixtureA().getUserData();
		FixtureData bdata = (FixtureData)contact.getFixtureB().getUserData();
		
		if(adata != null && adata.collision_manager != null){
			adata.collision_manager.preSolve(contact, oldManifold, true);
		}
		if(bdata != null && bdata.collision_manager != null){
			bdata.collision_manager.preSolve(contact, oldManifold, false);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		FixtureData adata = (FixtureData)contact.getFixtureA().getUserData();
		FixtureData bdata = (FixtureData)contact.getFixtureB().getUserData();
		
		if(adata != null && adata.collision_manager != null){
			adata.collision_manager.postSolve(contact, impulse, true);
		}
		if(bdata != null && bdata.collision_manager != null){
			bdata.collision_manager.postSolve(contact, impulse, false);
		}
	}
	
}
