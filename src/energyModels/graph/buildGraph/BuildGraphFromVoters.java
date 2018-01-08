package energyModels.graph.buildGraph;

import java.util.ArrayList;
import java.util.List;

import energyModels.data.voter.Voter;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.VertexType;

public class BuildGraphFromVoters implements IBuildGraph<Voter>{
	public static int nbPosEdges = 0;
	public static int nbNegEdges = 0;
	Graph graph;
	List<Integer> knownVoters;
		
	public BuildGraphFromVoters(Graph graph){
		this.graph = graph;
		this.knownVoters = new ArrayList<Integer>();
	}
	
	//It is recommended that the vertices are added to the graph before using this method.
	//For the multilevel algorithm, vertices MUST have been added to the graph before calling this method.
	@Override
	public void addPeerToGraph(Voter voter) {
		VertexType voterVertex = graph.getVertex(voter.getPeerId());
		for(Voter v : voter.getProfile().getItems()){
			VertexType vVertex = graph.getVertex(v.getPeerId());
			if((this.graph.getGraph().findEdge(vVertex, voterVertex) == null) && (vVertex.getVertexNum() != voterVertex.getVertexNum())){
				double vote = voter.getProfile().getValue(v);
				if(vote > 0.0){
					EdgeType edge = new EdgeType(true, vote);
					this.graph.addEdge(edge, v.getPeerId(), voter.getPeerId());
					nbPosEdges++;
				}else{
					EdgeType edge = new EdgeType(false, -vote);
					this.graph.addEdge(edge, v.getPeerId(), voter.getPeerId());
					nbNegEdges++;
				}
			}
		}
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public void clear() {
		this.knownVoters = null;
	}

	
}
