package energyModels.peerInit;

import java.util.HashMap;
import java.util.Map;

import threadedSim.init.IPeerInitializer;
import edu.uci.ics.jung.graph.UndirectedGraph;
import energyModels.graph.EdgeType;
import energyModels.peer.CoordinatesPeer;

public class FromGraphDual implements IPeerInitializer<CoordinatesPeer>{
	
		private UndirectedGraph<Integer, EdgeType> g;
		private Map<Integer, CoordinatesPeer> map;

		public FromGraphDual(UndirectedGraph<Integer, EdgeType> g) {
			this.g = g;
			this.map = new HashMap<Integer, CoordinatesPeer>();
		}
		@Override
		public void initialize(CoordinatesPeer peer) {
			//List<CoordinatesPeer> addList = peer.getRepulseNeighbors();
			double negIndegree = 0;
			for (Integer neighborId : this.g.getNeighbors(peer.getPeerId())) {
				EdgeType edge = this.g.findEdge(peer.getPeerId(),neighborId);
				negIndegree += edge.getWeight();
				if (this.map.containsKey(neighborId)) {
					//addList.add(this.map.get(neighborId));
					//this.map.get(neighborId).getRepulseNeighbors().add(peer);
					peer.getRepulseNeighbors().put(this.map.get(neighborId), edge.getWeight());
					this.map.get(neighborId).getRepulseNeighbors().put(peer, edge.getWeight());
				}
			}
			peer.setIndegree(negIndegree);
			peer.setNegIndegree(negIndegree);
			this.map.put(peer.getPeerId(), peer);
		}
}
