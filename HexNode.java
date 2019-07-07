// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

import graph.Node;
import model.Direction;

/**
 *
 * A graph where each node has up to 6 edges.
 *
 * @author Julian Mackay
 *
 * @param <V>
 *            the value stored in each node
 */
public class HexNode<V> implements Node<Direction, V> {

	private final Map<Direction, Node<Direction, V>> neighbors=new LinkedHashMap<>();
	private V v;

	public HexNode(V v) {
		this.v = v;
	}

	public HexNode() {}

	@Override
	public V getValue() {
		return v;
	}

	@Override
	public Boolean setValue(V v) {
		if (this.v != null) {return false;}
		this.v = v;
		return true;
	}

	@Override
	public Node<Direction, V> go(Direction direction) {
		if (!this.neighbors.containsKey(direction)) {return null;}
		return this.neighbors.get(direction);
	}

	@Override
	public Boolean connect(Direction direction, Node<Direction, V> n) {
		if(n.hasNeighbor(direction)) {
			return false;
		}
		Node<Direction,V> topLeft = n;
		int nw = 0;
		int ne = 0;
		int w = 0;
		//Find the Top Left tile
		while(topLeft.hasNeighbor(Direction.NORTHWEST)) {
			topLeft = topLeft.go(Direction.NORTHWEST);
			nw++;
		}
		while(topLeft.hasNeighbor(Direction.NORTHEAST)) {
			topLeft = topLeft.go(Direction.NORTHEAST);
			ne++;
		}
		while(topLeft.hasNeighbor(Direction.WEST)) {
			topLeft = topLeft.go(Direction.WEST);
			w++;
		}
		if(w>2) {
			return false;
		}
		if(ne>2) {
			return false;
		}
		if(w + ne + nw>4) {
			return false;
		}
		//Add neighbors to each node
		add(direction,n);
		n.add(direction.inverse(), this);
		//Go around in both directions connecting nodes that are next to each other
		Direction loop1 = direction.antiClockwise();
		HexNode<V> loop1N = this;
		Direction loop2 = direction.clockwise();
		HexNode<V> loop2N = this;
		int steps = 0;
		Direction nDirection;
		while(loop1N.hasNeighbor(loop1)) {
			steps++;
			if(steps > 5 ) {
				break;
			}
			loop1N = (HexNode<V>) loop1N.go(loop1);
			loop1 = loop1.clockwise();
			nDirection = loop1.clockwise();
			loop1N.add(nDirection, n);
			n.add(nDirection.inverse(),loop1N);
		}
		while(loop2N.hasNeighbor(loop2)) {
			steps++;
			if(steps > 5 ) {
				break;
			}
			loop2N = (HexNode<V>) loop2N.go(loop2);
			loop2 = loop2.antiClockwise();
			nDirection = loop2.antiClockwise();
			loop2N.add(nDirection, n);
			n.add(nDirection.inverse(),loop2N);
		}
		
		loop1 = direction.inverse().antiClockwise();
		loop1N = this;
		loop2 = direction.inverse().clockwise();
		loop2N =  this;
		steps =0;
		//Check for completing a circle in both directions
		while(loop1N.hasNeighbor(loop1)) {
			steps++;
			loop1N = (HexNode<V>) loop1N.go(loop1);
			loop1 = loop1.antiClockwise();
			nDirection = loop1.antiClockwise();
			if(steps==4) {
				loop1N.add(loop1, n);
				n.add(loop1.inverse(), loop1N);
				break;
			}
			
		}
		steps =0;
		
		while(loop2N.hasNeighbor(loop2)) {
			steps++;
			loop2N = (HexNode<V>) loop2N.go(loop2);
			loop2 = loop2.clockwise();
			nDirection = loop2.clockwise();
			if(steps==4) {
				loop2N.add(loop2, n);
				n.add(loop2.inverse(), loop2N);
				break;
			}
			
		}
		return true;
	}

	@Override
	public Boolean isConnected(Direction direction, Node<Direction, V> n) {
		//If it is in the neighbors then it is connected
		if(neighbors.containsKey(direction)) {
			if(neighbors.get(direction).equals(n)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Boolean add(Direction direction, Node<Direction, V> n) {
		if(!neighbors.containsKey(direction)) {
			neighbors.put(direction, n);
			return true;
		} else {
			return false;
		}
		
	}

	@Override
	public Boolean hasNeighbor(Direction dir) {
		if(neighbors.containsKey(dir)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Node<Direction, V> fillNeighborhood() {
		//Create new nodes to go around the neighborhood if there isn't something there already
		HexNode<V> node1 = new HexNode<>();
		HexNode<V> node2 = new HexNode<>();
		HexNode<V> node3 = new HexNode<>();
		HexNode<V> node4 = new HexNode<>();
		HexNode<V> node5 = new HexNode<>();
		HexNode<V> node6 = new HexNode<>();
		if(hasNeighbor(Direction.NORTHWEST)) {
			node1 = (HexNode<V>) this.go(Direction.NORTHWEST);
		}
		if(hasNeighbor(Direction.NORTHEAST)) {
			node2 = (HexNode<V>) this.go(Direction.NORTHEAST);
		}
		if(hasNeighbor(Direction.EAST)) {
			node3 = (HexNode<V>) this.go(Direction.EAST);
		}
		if(hasNeighbor(Direction.SOUTHEAST)) {
			node4 = (HexNode<V>) this.go(Direction.SOUTHEAST);
		}
		if(hasNeighbor(Direction.SOUTHWEST)) {
			node5 = (HexNode<V>) this.go(Direction.SOUTHWEST);
		}
		if(hasNeighbor(Direction.WEST)) {
			node6 = (HexNode<V>) this.go(Direction.WEST);
		}
		//Connect them all up/ 
		node1.connect(Direction.EAST, node2);
		node2.connect(Direction.SOUTHEAST, node3);
		node3.connect(Direction.SOUTHWEST, node4);
		node4.connect(Direction.WEST, node5);
		node5.connect(Direction.NORTHWEST, node6);
		node6.connect(Direction.EAST, this);
		return this;
	}

	@Override
	public HexNode<V> generate(Integer depth) {
		List<Node<Direction, V>> in = new ArrayList();
		List<Node<Direction, V>> tempIn = new ArrayList();
		in.add(this);
		for(int i =1;i<depth;i++) {
			tempIn.clear();
			//For the inner nodes add neighbors around them if there isn't already something there
			for(Node<Direction,V> n : in) {
				for(Direction d: Direction.values()) {
					if(!n.hasNeighbor(d)) {
						HexNode<V> node1 = new HexNode<>();
						n.connect(d, node1);
						tempIn.add(node1);
					}
				}
			}
			in.clear();
			for(Node<Direction,V> n : tempIn) {
				in.add(n);
			}
		}
		return this;
	}
	

	
	@Override
	public List<Node<Direction, V>> toList() {
		List<Node<Direction, V>> l = new ArrayList();
		Node<Direction,V> current = this;
		Node<Direction,V> nextLine = this;
		
		//Find the top left node
		while(current.hasNeighbor(Direction.NORTHWEST)) {
			current = current.go(Direction.NORTHWEST);
		}
		while(current.hasNeighbor(Direction.NORTHEAST)) {
			current = current.go(Direction.NORTHEAST);
		}
		while(current.hasNeighbor(Direction.WEST)) {
			current = current.go(Direction.WEST);
		}
		nextLine = current;
		while(nextLine != null) {
			current = nextLine;
			//Find the begining of the next time
			if(current.hasNeighbor(Direction.SOUTHWEST)) {
				nextLine = current.go(Direction.SOUTHWEST);
			} else if(current.hasNeighbor(Direction.SOUTHEAST)) {
				nextLine = current.go(Direction.SOUTHEAST);
			} else {
				nextLine = null;
			}
			l.add(current);
			//Keep going east until reaching the end of a row
			while(current.hasNeighbor(Direction.EAST)) {
				current = current.go(Direction.EAST);
				l.add(current);
			}
		}
		return l;
	}
	
	public List<Node<Direction,V>> toListClockwise() {
		List<Node<Direction, V>> l = new ArrayList();
		Node<Direction,V> current = this;
		Node<Direction,V> nextLine = this;
		//Find the top left
		while(current.hasNeighbor(Direction.NORTHWEST)) {
			current = current.go(Direction.NORTHWEST);
		}
		while(current.hasNeighbor(Direction.NORTHEAST)) {
			current = current.go(Direction.NORTHEAST);
		}
		while(current.hasNeighbor(Direction.WEST)) {
			current = current.go(Direction.WEST);
		}
		nextLine = current;
		//Go around clockwise
		while(nextLine != null) {
			current = nextLine;
			if(current.hasNeighbor(Direction.SOUTHEAST) && !l.contains(current.go(Direction.SOUTHEAST))) {
				nextLine = current.go(Direction.SOUTHEAST);
			} else {
				nextLine = null;
			}
			l.add(current);
			while(current.hasNeighbor(Direction.EAST) && !l.contains(current.go(Direction.EAST))) {
				current = current.go(Direction.EAST);
				l.add(current);
			}
			while(current.hasNeighbor(Direction.SOUTHEAST) && !l.contains(current.go(Direction.SOUTHEAST))) {
				current = current.go(Direction.SOUTHEAST);
				l.add(current);
			}
			while(current.hasNeighbor(Direction.SOUTHWEST) && !l.contains(current.go(Direction.SOUTHWEST))) {
				current = current.go(Direction.SOUTHWEST);
				l.add(current);
			}
			while(current.hasNeighbor(Direction.WEST) && !l.contains(current.go(Direction.WEST))) {
				current = current.go(Direction.WEST);
				l.add(current);
			}
			while(current.hasNeighbor(Direction.NORTHWEST) && !l.contains(current.go(Direction.NORTHWEST))) {
				current = current.go(Direction.NORTHWEST);
				l.add(current);
			}
			while(current.hasNeighbor(Direction.NORTHEAST) && !l.contains(current.go(Direction.NORTHEAST))) {
				current = current.go(Direction.NORTHEAST);
				l.add(current);
			}
		}
		return l;
	}
	
	@Override
	public Stream<Node<Direction, V>> stream() {
		List<Node<Direction, V>> l= toList();
		return l.stream();
	}

	@Override
	public Stream<Node<Direction, V>> clockwiseStream() {
		List<Node<Direction, V>> l= toListClockwise();
		return l.stream();
	}

	@Override
	public String toString() {
		return v==null?"*"+this.hashCode():v.toString();
	}
	@Override
	public Boolean isValid() {
		assert this.collectAll().stream().allMatch(n->isValidOne(n));
		return true;
	}
	/**
	 * Very general algorithm to collect all the nodes of a graph
	 * @return
	 */
	private Set<Node<Direction,V>> collectAll(){
		Set<Node<Direction,V>> res=new LinkedHashSet<>();
		collectAll(this,res);
		return res;
	}
	private static<V> void collectAll(Node<Direction,V> n,Set<Node<Direction,V>> acc){
		if(n==null) {return;}
		if(acc.contains(n)) {return;}
		acc.add(n);
		Stream.of(Direction.values()).forEach(d->collectAll(n.go(d),acc));
	}

	public static<V> Boolean isValidOne(Node<Direction,V> n) {
		Predicate<Predicate<Direction>> ns=
			p->Stream.of(Direction.values())
				.filter(d->n.hasNeighbor(d))
				.allMatch(p);
		assert ns.test(d->n.go(d)!=n);
		assert ns.test(d->n.go(d).go(d.inverse())==n);
		assert ns.test(d->{
			Node<Direction, V> n1 = n.go(d);
			Node<Direction, V> n2 = n.go(d.clockwise());
			assert n1!=null;
			if(n2==null) {return true;}
			assert n1.go(d.inverse().antiClockwise())==n2:
				"surrunding pieces not well connected";
			return true;
		});
		assert ns.test(d->{
			Node<Direction, V> n1 = n.go(d);
			Node<Direction, V> n2 = n.go(d.antiClockwise());
			assert n1!=null;
			if(n2==null) {return true;}
			assert n1.go(d.inverse().clockwise())==n2;
			return true;
		});
		return true;
	}
	
	/**
	 * Get Neighbors
	 */
	public Map<Direction, Node<Direction, V>> getNeighbors(){
		return neighbors;
	}

}