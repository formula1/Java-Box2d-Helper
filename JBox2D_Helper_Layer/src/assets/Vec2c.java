package assets;

import org.jbox2d.common.Vec2;

public class Vec2c extends Vec2u{

	public Vec2c(float x, float y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}
	public Vec2c(Vec2u vec) {
		super(vec.x, vec.y);
		// TODO Auto-generated constructor stub
	}
	public Vec2c(Vec2 vec) {
		super(vec.x, vec.y);
		// TODO Auto-generated constructor stub
	}

	
	public Vec2c add(Vec2 vec){
		this.x += vec.x;
		this.y += vec.y;
		return this;
	}
	
	public Vec2c mul(Vec2 vec){
		this.x *= vec.x;
		this.y *= vec.y;
		return this;
	}
	
	public Vec2 asVec2(){
		return new Vec2(x,y);
	}

}
