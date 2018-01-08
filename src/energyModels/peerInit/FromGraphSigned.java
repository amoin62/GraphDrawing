package energyModels.peerInit;

import java.util.HashMap;
import java.util.Map;
import threadedSim.init.IPeerInitializer;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.VertexType;
import energyModels.peer.CoordinatesPeer;

public class FromGraphSigned implements IPeerInitializer<CoordinatesPeer>{
	private Graph g;
	private Map<Integer, CoordinatesPeer> map;
	
	public FromGraphSigned(Graph g) {
		this.g = g;
		this.map = new HashMap<Integer, CoordinatesPeer>();
	}

	@Override
	public void initialize(CoordinatesPeer peer) {
		/*List<CoordinatesPeer> addListPosEdge = peer.getAttractNeighbors();
		List<CoordinatesPeer> addListNegEdge = peer.getRepulseNeighbors();*/
		/*Set<Entry<CoordinatesPeer, Double>> addListPosEdge = peer.getAttractNeighbors().entrySet();
		Set<Entry<CoordinatesPeer, Double>> addListNegEdge = peer.getRepulseNeighbors().entrySet();*/
		
		double indegree = 0;
		double posIndegree = 0;
		double negIndegree = 0;
		VertexType thisVertex = this.g.getVertex(peer.getPeerId());
		for (VertexType neighbor : this.g.getGraph().getNeighbors(thisVertex)) {
			//the weight is positive for both attractive and repulsive edges;
			EdgeType edge = this.g.getGraph().findEdge(thisVertex, neighbor);
			if(edge.isEdgeType()){
				posIndegree += edge.getWeight();
			}else{
				negIndegree += edge.getWeight();
			}
			indegree += edge.getWeight();
			
			if (this.map.containsKey(neighbor.getVertexNum())) {
				//EdgeType edge = this.g.findEdge(peer.getPeerId(), neighborId);
				if(edge.isEdgeType()){
					//addListPosEdge.add(this.map.get(neighborId));
					//this.map.get(neighborId).getAttractNeighbors().add(peer);
					peer.getAttractNeighbors().put(this.map.get(neighbor.getVertexNum()), edge.getWeight());
					this.map.get(neighbor.getVertexNum()).getAttractNeighbors().put(peer, edge.getWeight());
				}
				//This part should be commented for unsigned LinLog model
				else{
					//addListNegEdge.add(this.map.get(neighborId));
					//this.map.get(neighborId).getRepulseNeighbors().add(peer);
					peer.getRepulseNeighbors().put(this.map.get(neighbor.getVertexNum()), edge.getWeight());
					this.map.get(neighbor.getVertexNum()).getRepulseNeighbors().put(peer, edge.getWeight());
				}
			}
		}
		
		peer.setPosIndegree(posIndegree);
		peer.setNegIndegree(negIndegree);
		peer.setIndegree(indegree);
		this.map.put(peer.getPeerId(), peer);
	}
	
}
