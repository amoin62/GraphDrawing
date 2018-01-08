package energyModels.graph;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class Graph{
	private UndirectedSparseGraph<VertexType, EdgeType> graph;
	private Map<Integer,VertexType> vertexMap;
		
	public Graph() {
		super();
		graph = new UndirectedSparseGraph<VertexType, EdgeType>();
		vertexMap = new HashMap<Integer, VertexType>();
	}
	
	public Graph(Graph g) {
		super();
		graph = g.getGraph();
		vertexMap = g.vertexMap;
	}
	public void addVertex(Integer vertexID){
		VertexType vertex = new VertexType(vertexID);
		graph.addVertex(vertex);
		vertexMap.put(vertexID, vertex);
	}
	
	public void addVertex(Integer vertexID, Color color){
		VertexType vertex = new VertexType(vertexID);
		vertex.setColor(color);
		graph.addVertex(vertex);
		vertexMap.put(vertexID, vertex);
	}
	
	public void addVertex(Integer vertexID, float mass){
		VertexType vertex = new VertexType(vertexID,mass);
		graph.addVertex(vertex);
		vertexMap.put(vertexID, vertex);
	}
	
	public VertexType getVertex(Integer vertexID){
		VertexType vertex = this.vertexMap.get(vertexID);
		if(vertex != null){
			return vertex;
		}else{
			throw new RuntimeException("Vertex num " + vertexID + " does not exist in the graph.");
		}
	}
	
	public void addEdge(EdgeType edge, Integer v1, Integer v2){
		VertexType vertex1 = this.getVertex(v1);
		VertexType vertex2 = this.getVertex(v2);
		if(vertex1 == null){
			throw new RuntimeException("Vertex" + v1 + " does not exist in the graph.");
		}
		if(vertex2 == null){
			throw new RuntimeException("Vertex" + v2 + " does not exist in the graph.");
		}
		this.graph.addEdge(edge, vertex1, vertex2);
	}

	public UndirectedSparseGraph<VertexType, EdgeType> getGraph() {
		return graph;
	}
	
	public double getIndegree(VertexType v){
		double res = 0.0;
		for(EdgeType edge : this.graph.getIncidentEdges(v)){
			res += Math.abs(edge.getWeight());
		}
		v.setDegree(res);
		return res;
	}
	
}
