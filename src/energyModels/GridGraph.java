package energyModels;

import energyModels.graph.EdgeType;
import energyModels.graph.Graph;

public class GridGraph {

	public GridGraph() {
		super();
	}
	
	public Graph buildGridGraph(int rows, int columns){
		Graph graph = new Graph();
		for(int i = 0; i < rows; i++){
			for(int j= 0; j < columns; j++){
				int vertexNum = i * columns+j;
				graph.addVertex(vertexNum);
			}
		}
		
		//adding horizontal edges.
		for(int i = 0; i < rows; i++){
			for(int j = 0; j  < columns -1; j++){
				int head = i*columns+j;
				int tail = i*columns+j+1;
				graph.addEdge(new EdgeType(), head, tail);
			}
		}
		
		//adding vertical edges.
		for(int j = 0; j < columns; j++){
			for(int i = 0; i < rows - 1; i++){
				int head = i*columns+j;
				int tail = (i+1)*columns+j;
				graph.addEdge(new EdgeType(), head, tail);
			}
		}
		return graph;
	}
}
