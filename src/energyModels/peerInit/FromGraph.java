package energyModels.peerInit;

import java.util.HashMap;
import java.util.Map;

import threadedSim.init.IPeerInitializer;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.VertexType;
import energyModels.peer.CoordinatesPeer;

public class FromGraph implements IPeerInitializer<CoordinatesPeer> {

	private Graph g;
	private Map<Integer, CoordinatesPeer> map;

	public FromGraph(Graph g) {
		this.g = g;
		this.map = new HashMap<Integer, CoordinatesPeer>();
	}

	@Override
	public void initialize(CoordinatesPeer peer) {
		double posIndegree = 0;
		//List<CoordinatesPeer> addList = peer.getAttractNeighbors();
		VertexType thisVertex = this.g.getVertex(peer.getPeerId());
		for (VertexType neighbor : this.g.getGraph().getNeighbors(thisVertex)) {
			EdgeType edge = this.g.getGraph().findEdge(thisVertex, neighbor);
			if(edge.getWeight() < 0.0){
				throw new RuntimeException("The edge weight is negative.");
			}
			posIndegree += edge.getWeight();
			if (this.map.containsKey(neighbor.getVertexNum())) {
				//addList.add(this.map.get(neighborId));
				//this.map.get(neighborId).getAttractNeighbors().add(peer);
				peer.getAttractNeighbors().put(this.map.get(neighbor.getVertexNum()), edge.getWeight());
				this.map.get(neighbor.getVertexNum()).getAttractNeighbors().put(peer, edge.getWeight());
			}
		}
		peer.setPosIndegree(posIndegree);
		peer.setIndegree(posIndegree);
		this.map.put(peer.getPeerId(), peer);
	}
}
