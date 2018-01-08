package energyModels.graph.buildGraph;

import java.util.HashSet;
import java.util.Set;

import energyModels.data.rater.Film;
import energyModels.data.rater.User;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;

public class BuildGraphFromRaters implements IBuildGraph<User>{
	
	private Graph graph;
	private Set<Integer> itemVertexSet;
	private Set<Integer> userVertexSet;
	private static double k;
	
	public BuildGraphFromRaters(double k){
		super();
		this.graph = new Graph();
		this.itemVertexSet = new HashSet<Integer>();
		this.userVertexSet = new HashSet<Integer>();
		BuildGraphFromRaters.k = k;
	}
	
	@Override
	public void addPeerToGraph(User p){
		if (!userVertexSet.contains(p.getPeerId())){
			graph.addVertex(p.getPeerId());
			userVertexSet.add(p.getPeerId());
			for(Film f: p.getProfile().getItems()){
				double diff= p.getProfile().getValue(f) - p.getProfile().getAverage();
				if(!itemVertexSet.contains(-f.getNum())){
					graph.addVertex(-f.getNum());
					itemVertexSet.add(-f.getNum());
				}
				if(diff>k){
					EdgeType edge = new EdgeType(true,diff);
					graph.addEdge(edge, p.getPeerId(), -f.getNum());
				}else if (diff < -k){
					//we put the absolute value of the negative weight as the edge weight. The attraction/repulsion is
					//determined by edge sign.
					EdgeType edge = new EdgeType(false,-diff);
					graph.addEdge(edge, p.getPeerId(), -f.getNum());
				}
			}
		}
		else{
			System.err.println("The Peer exists already in the graph!");
		}
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public final void clear() {
		this.itemVertexSet = null;
		this.userVertexSet = null;
		
	}
}
