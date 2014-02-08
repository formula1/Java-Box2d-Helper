package helpers;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import assets.FixtureData;

import entity_managers.CollisionManager;

public abstract class BodyHelper implements BodyDefCallback, FixtureDefCallback{

	public static World world;
	public static float standard_friction;
	public static float standard_density;
	
	public BodyHelper(World world, float standard_friction, float standard_density){
		BodyHelper.world = world;
		BodyHelper.standard_friction = standard_friction;
		BodyHelper.standard_density = standard_density;
	}
	
	/*
	 * Default Usage
	 * 
	 * BodyDef def = SetupBody(position, body_instruction);
	 *  Body ret = world.createBody(def);
	 * 
	 * FixtureDef temp = createFix(shape, fixture_instruction)
	 * ret.createFixture(temp)
	 * 
	 * return ret;
	 * 
	 */

	public Body createFromArguments(Vec2 position, Shape[] shapes, BodyType bodytype, BodyDefCallback body_instruction, FixtureDefCallback fixture_instruction, CollisionManager colm){
		Body groundbody = world.createBody(setupBody(position, bodytype, body_instruction));

		FixtureDef temp;
		for(Shape s : shapes){
			temp = createFix(s, fixture_instruction);
			if(colm != null)temp.userData = new FixtureData(colm);
			groundbody.createFixture(temp);
		}
		
		return groundbody;
	}
	public Body createFromArguments(Vec2 position, Shape[] shapes, BodyType bodytype, BodyDefCallback body_instruction, CollisionManager colm){
		return createFromArguments(position, shapes, bodytype, body_instruction, this, colm);
	}
	public Body createFromArguments(Vec2 position, Shape[] shapes, BodyType bodytype, FixtureDefCallback fixture_instruction,CollisionManager colm){
		return createFromArguments(position, shapes, bodytype, this, fixture_instruction, colm);
	}
	public Body createFromArguments(Vec2 position, Shape[] shapes, BodyType bodytype, CollisionManager colm){
		return createFromArguments(position, shapes, bodytype, this, this, colm);
	}

	public Body createFromArguments(Vec2 position, Shape[] shapes, BodyType bodytype, BodyDefCallback body_instruction, FixtureDefCallback fixture_instruction){
		return createFromArguments(position,shapes, bodytype, body_instruction, fixture_instruction, null);
	}
	public Body createFromArguments(Vec2 position, Shape[] shapes, BodyType bodytype, BodyDefCallback body_instruction){
		return createFromArguments(position, shapes, bodytype, body_instruction, this,null);
	}
	public Body createFromArguments(Vec2 position, Shape[] shapes, BodyType bodytype, FixtureDefCallback fixture_instruction){
		return createFromArguments(position, shapes, bodytype, this, fixture_instruction,null);
	}
	public Body createFromArguments(Vec2 position, Shape[] shapes, BodyType bodytype){
		return createFromArguments(position, shapes, bodytype, this, this,null);
	}

	
	
	public Body createFromArguments(Vec2 position, Shape shape, BodyType bodytype, BodyDefCallback body_instruction, FixtureDefCallback fixture_instruction, CollisionManager colm){
		Body groundbody = world.createBody(setupBody(position, bodytype, body_instruction));

		FixtureDef temp = createFix(shape, fixture_instruction);
		if(colm != null)temp.userData = new FixtureData(colm);
		groundbody.createFixture(temp);
		
		return groundbody;
	}
	public Body createFromArguments(Vec2 position, Shape shape, BodyType bodytype, BodyDefCallback body_instruction, CollisionManager colm){
		return createFromArguments(position, shape, bodytype, body_instruction, this, colm);
	}
	public Body createFromArguments(Vec2 position, Shape shape, BodyType bodytype, FixtureDefCallback fixture_instruction, CollisionManager colm){
		return createFromArguments(position, shape, bodytype, this, fixture_instruction, colm);
	}
	public Body createFromArguments(Vec2 position, Shape shape, BodyType bodytype, CollisionManager colm){
		return createFromArguments(position, shape, bodytype, this, this, colm);
	}
	public Body createFromArguments(Vec2 position, Shape shape, BodyType bodytype, BodyDefCallback body_instruction, FixtureDefCallback fixture_instruction){
		return createFromArguments(position, shape, bodytype, body_instruction, fixture_instruction, null);		
	}

	public Body createFromArguments(Vec2 position, Shape shape, BodyType bodytype, BodyDefCallback body_instruction){
		return createFromArguments(position, shape, bodytype, body_instruction, this, null);
	}
	public Body createFromArguments(Vec2 position, Shape shape, BodyType bodytype, FixtureDefCallback fixture_instruction){
		return createFromArguments(position, shape, bodytype, this, fixture_instruction, null);
	}
	public Body createFromArguments(Vec2 position, Shape shape, BodyType bodytype){
		return createFromArguments(position, shape, bodytype, this, this, null);
	}

	
	
	public BodyDef setupBody(Vec2 position, BodyType bodytype, BodyDefCallback instruction){
		BodyDef bd = new BodyDef();
		bd.type = bodytype;
		bd.angle = 0;
		bd.position.set(position.x, position.y);
		bd.userData = null;
		
		bd = instruction.bodyDefCallback(bd);
		
		return bd;
	}
	
	public BodyDef setupBody(Vec2 position, BodyType bodytype){
		return setupBody(position, bodytype, this);
	}

	
	public FixtureDef createFix(Shape shape, FixtureDefCallback instruction){
		FixtureDef boxing = new FixtureDef();
		boxing.shape = shape;
		boxing.friction = standard_friction;
		boxing.density = standard_density;
		return instruction.fixDefCallback(boxing);
	}

	public FixtureDef createFix(Shape shape){
		return createFix(shape, this);
	}

	
	public static Shape circle(float radius){
		CircleShape gBox = new CircleShape();
		gBox.setRadius(radius);
		return gBox;
	}
	
	public static Shape rectangle(Vec2 width_height, Vec2 offset){
		PolygonShape gBox = new PolygonShape();
		gBox.setAsBox(width_height.x, width_height.y, offset, 0);
		return gBox;		
	}
	public static Shape rectangle(Vec2 width_height){
		return rectangle(width_height, new Vec2(0,0));
		
	}
	public static Shape square(float side_length, Vec2 offset){
		return rectangle(new Vec2(side_length,side_length), offset);
		
	}
	public static Shape square(float side_length){
		return rectangle(new Vec2(side_length,side_length), new Vec2(0,0));
		
	}

	
	public static Shape edge(Vec2 vertex1,Vec2 vertex2){
		EdgeShape e = new EdgeShape();
		e.set(vertex1, vertex2);
		return e;
		
	}



	
}
