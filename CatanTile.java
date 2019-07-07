// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import graph.Node;
import model.Direction;
import model.Location;
import model.Resource;
import model.ResourceCounter;
import model.Road;
import model.Settlement;
import model.Tile;

/**
 * The main Tile class
 *
 * @author Julian Mackay
 *
 */
public class CatanTile implements Tile{

	private final Integer id;
	private final Map<Location, Settlement> settlements;
	private final Map<Direction, Road> roads;
	private final Node<Direction, Tile> currentNode;
	private Resource resource;
	private ResourceCounter resourceCounter;

	public CatanTile(Integer id, Node<Direction, Tile> currentNode) {
		this.id = id;
		this.settlements = new LinkedHashMap<>();
		this.roads = new LinkedHashMap<>();
		this.currentNode = currentNode;
	}
	
	@Override
	public Integer getID() {
		return this.id;
	}

	@Override
	public Boolean setResource(Resource r) {
		resource = r;
		return true;
		
	}

	@Override
	public Resource getResource() {
		return this.resource;
	}

	@Override
	public Integer getResourceNumber() {
		return resourceCounter.getNumber();
	}

	@Override
	public Boolean setResourceCounter(ResourceCounter resourceCounter) {
		if(resource.equals(Resource.DESERT)) {
			this.resourceCounter = ResourceCounter.NONE;
		} else {
			this.resourceCounter = resourceCounter;
		}
		return true;
	}
	
	public Boolean setResourceCounter2(ResourceCounter rc,List<ResourceCounter> pool) {
		if(resource.equals(Resource.DESERT)) {
			this.resourceCounter = ResourceCounter.NONE;
			pool.add(0, rc);
		} else {
			this.resourceCounter = rc;
		}
		return true;
	}
	

	@Override
	public Boolean hasSettlement(Location loc) {
		if(settlements.containsKey(loc)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean addSettlement(Settlement s, Location loc) {
		settlements.put(loc,s);
		Map<Direction, Node<Direction, Tile>> neighbors = currentNode.getNeighbors();
		//Depending on where the settlement is placed depends on where it sits on the neighboring tiles
		if(loc == Location.NORTH) {
			if(neighbors.containsKey(Direction.NORTHEAST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.NORTHEAST).getValue();
				ct1.settlements.put(Location.SOUTHEAST,s);
			}
			if(neighbors.containsKey(Direction.NORTHWEST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.NORTHWEST).getValue();
				ct1.settlements.put(Location.SOUTHWEST,s);
			}
		}
		if(loc == Location.NORTHEAST) {
			if(neighbors.containsKey(Direction.NORTHEAST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.NORTHEAST).getValue();
				ct1.settlements.put(Location.SOUTH,s);
			}
			if(neighbors.containsKey(Direction.EAST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.EAST).getValue();
				ct1.settlements.put(Location.NORTHWEST,s);
			}
		}
		if(loc == Location.NORTHWEST) {
			if(neighbors.containsKey(Direction.NORTHWEST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.NORTHWEST).getValue();
				ct1.settlements.put(Location.SOUTH,s);
			}
			if(neighbors.containsKey(Direction.WEST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.WEST).getValue();
				ct1.settlements.put(Location.NORTHEAST,s);
			}
		}
		if(loc == Location.SOUTH) {
			if(neighbors.containsKey(Direction.SOUTHEAST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.SOUTHEAST).getValue();
				ct1.settlements.put(Location.NORTHWEST,s);
			}
			if(neighbors.containsKey(Direction.SOUTHWEST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.SOUTHWEST).getValue();
				ct1.settlements.put(Location.NORTHEAST,s);
			}
		}
		if(loc == Location.SOUTHEAST) {
			if(neighbors.containsKey(Direction.EAST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.EAST).getValue();
				ct1.settlements.put(Location.SOUTHWEST,s);
			}
			if(neighbors.containsKey(Direction.SOUTHEAST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.SOUTHEAST).getValue();
				ct1.settlements.put(Location.NORTH,s);
			}
		}
		if(loc == Location.SOUTHWEST) {
			if(neighbors.containsKey(Direction.WEST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.WEST).getValue();
				ct1.settlements.put(Location.SOUTHEAST,s);
			}
			if(neighbors.containsKey(Direction.SOUTHWEST)) {
				CatanTile ct1 =  (CatanTile) neighbors.get(Direction.SOUTHWEST).getValue();
				ct1.settlements.put(Location.NORTH,s);
			}
		}
		
		return true;
	}

	@Override
	public Boolean hasRoad(Direction dir) {
		if(roads.containsKey(dir)) {
			return true;
		}
		return false;
	}

	@Override
	public Boolean addRoad(Road r, Direction dir) {
		if(!roads.containsKey(dir)) {
			roads.put(dir,r);
			return true;
		}
		return false;
	}

	@Override
	public List<Settlement> getSettlements(){
		List<Settlement> l = new ArrayList<>();
		for(Location loc : settlements.keySet()) {
			l.add(settlements.get(loc));
		}
		return l;
	}

	@Override
	public String toString() {
		return this.id.toString(); 
	}

}