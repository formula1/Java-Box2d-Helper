package assets;

import entity_managers.CollisionManager;

public class FixtureData {
	public CollisionManager collision_manager;
	public Object game_specific;
	public int filterindex = 0;
	
	public FixtureData(){
	}

	
	public FixtureData(CollisionManager new_collision_manager){
		collision_manager = new_collision_manager;
	}

	public FixtureData(CollisionManager new_collision_manager, Object new_game_specific){
		collision_manager = new_collision_manager;
		game_specific = new_game_specific;
	}
	
	public FixtureData(CollisionManager new_collision_manager, Object new_game_specific, int filterindex){
		collision_manager = new_collision_manager;
		game_specific = new_game_specific;
		this.filterindex = filterindex;
	}


}
