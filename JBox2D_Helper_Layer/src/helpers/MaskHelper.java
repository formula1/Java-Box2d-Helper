package helpers;

import java.util.ArrayList;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import assets.FixtureData;

public class MaskHelper {

	World world;
	int nextcat;
	ArrayList<Integer> categories = new ArrayList<Integer>();
	ArrayList<Integer> masks = new ArrayList<Integer>();
	
	public MaskHelper(World world){
		this.world = world;
		categories.add(0b1);
		masks.add(0xFFFF);
	}
	
	public void turnOn(Body b){
		Fixture f = b.getFixtureList();
		while((f=f.getNext()) != null) turnOn(f);
	}
	
	public void turnOff(Body b){
		Fixture f = b.getFixtureList();
		do turnOff(f);
		while((f=f.getNext()) != null);
	}
	
	public void turnOn(Fixture f){
		int index;
		if(f.getUserData() == null) index = 0;
		else index = ((FixtureData)f.getUserData()).filterindex;

		Filter fil = f.getFilterData();
		fil.categoryBits = categories.get(index);
		fil.maskBits = masks.get(index);
		f.setFilterData(fil);
	}
	
	public void turnOff(Fixture f){
		Filter fil = f.getFilterData();
		fil.categoryBits = 0x0000;
		fil.maskBits = 0x0000;
		f.setFilterData(fil);
		
	}
	
	
	public int createNewMask(Body b, int doesnt_hit, boolean notself){
		Fixture f = b.getFixtureList();
		int fi = createNewMask(f, doesnt_hit, notself);
		while((f=f.getNext()) != null) setMask(f, fi);
		return fi;
	}

	public int createNewMask(Fixture f, int doesnt_hit, boolean notself){
		FixtureData fd;
		if(f.getUserData() != null) fd = (FixtureData)f.getUserData();
		else fd = new FixtureData();
		
		fd.filterindex = createNewMask(doesnt_hit, notself);
		f.setUserData(fd);
		setMask(f,fd.filterindex);
		return fd.filterindex;
	}

	public int createNewMask(int doesnt_hit, boolean notself){
		int mask = 0xFFFF;
		mask = mask ^ doesnt_hit;
		if(notself) mask = mask ^ (int)Math.pow(2, categories.size());
		
		categories.add((int)Math.pow(2,categories.size()));
		masks.add(mask);
		
		return categories.size()-1;
	}
	
	public void setMask(Body b, int filterindex){
		Fixture f = b.getFixtureList();
		do setMask(f, filterindex);
		while((f=f.getNext()) != null);
	}
	
	public void setMask(Fixture f, int filterindex){
		FixtureData fd;
		if(f.getUserData() != null) fd = (FixtureData)f.getUserData();
		else fd = new FixtureData();
		
		Filter fil = f.getFilterData();
		fil.categoryBits = categories.get(filterindex);
		fil.maskBits = masks.get(filterindex);
		f.setFilterData(fil);
		fd.filterindex = filterindex;
		
		System.out.println(filterindex);
		
		f.setUserData(fd);
	}
	
	
	public Filter turnOff(Filter filter){
		filter.categoryBits = 0;
		filter.maskBits = 0;
		
		return filter;
	}
	
}
