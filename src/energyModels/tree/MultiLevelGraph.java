package energyModels.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spectralGD.Spec;

import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompColMatrix;

import edu.uci.ics.jung.graph.util.Pair;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.VertexType;

public class MultiLevelGraph {
	public boolean MIVS;
	public boolean EC;
	private static short numCounter = 1;
	private short num;
	private Graph fineGraph;
	private Graph coarseGraph;
	private Map<VertexType,VertexType> pro;
	
	public MultiLevelGraph(Graph fineGraph) {
		super();
		this.fineGraph = fineGraph;
		
		/*Spec spec = new Spec();
		Matrix m = spec.buildMatrixFromGraph(fineGraph);
		this.fineGraph =  spec.buildGraphFromMatrix(m);*/
		
		this.coarseGraph = new Graph();
		this.pro = new HashMap<VertexType, VertexType>();
		this.num = numCounter;
		this.MIVS = false;
		this.EC = false;
		numCounter++;
	}

	//Heavy Edge Collapsing
	public void HEC(){
		this.EC = true;
		this.MIVS = false;
		int counter = 0;
		List<VertexType> vertices = new ArrayList<VertexType>(fineGraph.getGraph().getVertices());
		Collections.shuffle(vertices);
		for(VertexType vertex : vertices){
			if(!vertex.isMatched()){
				vertex.setMatched(true);
				float mass = vertex.getMass();
				double maxWeight = 0.0;
				VertexType v = null;
				for(VertexType neighbor : fineGraph.getGraph().getNeighbors(vertex)){
					EdgeType e = fineGraph.getGraph().findEdge(vertex, neighbor);
					if((!neighbor.isMatched()) && (e.getWeight() > maxWeight)){
						maxWeight = e.getWeight();
						v = neighbor;
					}
				}
				if(v != null){
					mass += v.getMass();
					v.setMatched(true);
				}
				coarseGraph.addVertex(counter, mass);
				//coarseGraph.addVertex(counter, 1.0f);
				pro.put(vertex, coarseGraph.getVertex(counter));
				if(v != null){
					pro.put(v, coarseGraph.getVertex(counter));
				}
				counter++;
			}
		}
		
		//Adding the edges to the coarser graph.
		for(EdgeType edge : fineGraph.getGraph().getEdges()){
			Pair<VertexType> pair = fineGraph.getGraph().getEndpoints(edge);
			VertexType coarseVertex1 = pro.get(pair.getFirst());
			VertexType coarseVertex2 = pro.get(pair.getSecond());
			if(!coarseVertex1.equals(coarseVertex2)){
				EdgeType e = coarseGraph.getGraph().findEdge(coarseVertex1, coarseVertex2);
				if(e != null){
					double newWeight = e.getWeight() + edge.getWeight();
					e.setWeight(newWeight);
				}else{
					e = new EdgeType(edge.getWeight());
					coarseGraph.addEdge(e, coarseVertex1.getVertexNum(), coarseVertex2.getVertexNum());
				}
				
			}
		}
		System.out.println("MultiLevel Type = EC");
		System.out.println("Graph vertex count = " + coarseGraph.getGraph().getVertexCount());
		System.out.println("Graph edge count = " + coarseGraph.getGraph().getEdgeCount());
	}
	
	//Uniform Edge Collapsing
	public void UEC(){
		this.EC = true;
		this.MIVS = false;
		int counter = 0;
		List<VertexType> vertices = new ArrayList<VertexType>(fineGraph.getGraph().getVertices());
		Collections.shuffle(vertices);
		for(VertexType vertex : vertices){
			if(!vertex.isMatched()){
				vertex.setMatched(true);
				float mass = vertex.getMass();
				double minDegree = Double.POSITIVE_INFINITY;
				VertexType v = null;
				for(VertexType neighbor : fineGraph.getGraph().getNeighbors(vertex)){
					double degree = fineGraph.getIndegree(neighbor);
					if((!neighbor.isMatched()) && (degree < minDegree)){
						minDegree = degree;
						v = neighbor;
					}
				}
				if(v != null){
					mass += v.getMass();
					v.setMatched(true);
				}
				
				coarseGraph.addVertex(counter, mass);
				pro.put(vertex, coarseGraph.getVertex(counter));
				if(v != null){
					pro.put(v, coarseGraph.getVertex(counter));
				}
				counter++;
			}
		}
		
		//Adding the edges to the coarser graph.
		for(EdgeType edge : fineGraph.getGraph().getEdges()){
			Pair<VertexType> pair = fineGraph.getGraph().getEndpoints(edge);
			VertexType coarseVertex1 = pro.get(pair.getFirst());
			VertexType coarseVertex2 = pro.get(pair.getSecond());
			if(!coarseVertex1.equals(coarseVertex2)){
				EdgeType e = coarseGraph.getGraph().findEdge(coarseVertex1, coarseVertex2);
				if(e != null){
					double newWeight = e.getWeight() + edge.getWeight();
					e.setWeight(newWeight);
				}else{
					e = new EdgeType(edge.getWeight());
					coarseGraph.addEdge(e, coarseVertex1.getVertexNum(), coarseVertex2.getVertexNum());
				}
				
			}
		}
		System.out.println("MultiLevel Type = EC");
		System.out.println("Graph vertex count = " + coarseGraph.getGraph().getVertexCount());
		System.out.println("Graph edge count = " + coarseGraph.getGraph().getEdgeCount());
	}
	public void MIVS(){
		this.EC = false;
		this.MIVS = true;
		
		//computing the maximal independent vertex set.
		System.out.println("computing the maximal independent vertex set");
		List<VertexType> mivs = new ArrayList<VertexType>();
		List<VertexType> list = new ArrayList<VertexType>(this.fineGraph.getGraph().getVertices());
		Collections.shuffle(list);
		for(VertexType entry : list){
			if(!entry.isMatched()){
				entry.setMatched(true);
				mivs.add(entry);
				entry.setMivs(true);
				for(VertexType neighbor : this.fineGraph.getGraph().getNeighbors(entry)){
					neighbor.setMatched(true);
				}
			}
		}
		
		//adding the maximal independent vertex set to the coarser graph.
		int counter = 0;
		System.out.println("adding the maximal independent vertex set to the coarser graph");
		for(VertexType v : mivs){
			//this.coarseGraph.addVertex(v.getVertexNum());
			this.coarseGraph.addVertex(counter);
			//VertexType coarseVertex = this.coarseGraph.getVertex(v.getVertexNum());
			VertexType coarseVertex = this.coarseGraph.getVertex(counter);
			pro.put(v, coarseVertex);
			counter++;
		}
		
		//adding the edges to the coarser graph.
		//Note : all vertices must be matched by this stage.
		counter = 0;
		System.out.println("adding edges in distance 2");
		System.out.println("MIVS size = " + mivs.size());
		for(VertexType v : mivs){
			//VertexType coarseHead = this.coarseGraph.getVertex(v.getVertexNum());
			VertexType coarseHead = pro.get(v);
			for(VertexType neighbor : this.fineGraph.getGraph().getNeighbors(v)){
				for(VertexType n : this.fineGraph.getGraph().getNeighbors(neighbor)){
					if(n.isMivs() && (!n.equals(v))){
						//VertexType coarseTail = this.coarseGraph.getVertex(n.getVertexNum());
						VertexType coarseTail = pro.get(n);
						if(this.coarseGraph.getGraph().findEdge(coarseHead, coarseTail) == null){
							this.coarseGraph.getGraph().addEdge(new EdgeType(), coarseHead, coarseTail);
						}
					}
				}
			}
			counter++;
			if(counter % 1000 == 0){
				System.out.println("Two-hop distances computed for " + counter + " vertices.");
			}
		}
		System.out.println("Two-hop distances computed for " + counter + " vertices.");
		counter = 0;
		System.out.println("End of adding edges in distance 2");
		
		System.out.println("Adding edges in distance 3");
		System.out.println("Number of edges = " + this.fineGraph.getGraph().getEdgeCount());
		for(EdgeType fineEdge : this.fineGraph.getGraph().getEdges()){
			Pair<VertexType> pair = this.fineGraph.getGraph().getEndpoints(fineEdge);
			if((!pair.getFirst().isMivs())&&(!pair.getSecond().isMivs())){
				Set<VertexType> firstMivsNeighbors = new HashSet<VertexType>();
				Set<VertexType> secondMivsNeighbors = new HashSet<VertexType>();
				for(VertexType neighbor : this.fineGraph.getGraph().getNeighbors(pair.getFirst())){
					if(neighbor.isMivs()){
						firstMivsNeighbors.add(neighbor);
					}
				}
				for(VertexType neighbor : this.fineGraph.getGraph().getNeighbors(pair.getSecond())){
					if(neighbor.isMivs()){
						secondMivsNeighbors.add(neighbor);
					}
				}
				for(VertexType v1 : firstMivsNeighbors){
					//VertexType coarseVertex1 = this.coarseGraph.getVertex(v1.getVertexNum());
					VertexType coarseVertex1 = pro.get(v1);
					for(VertexType v2 : secondMivsNeighbors){
						//VertexType coarseVertex2 = this.coarseGraph.getVertex(v2.getVertexNum());
						VertexType coarseVertex2 = pro.get(v2);
						if((this.coarseGraph.getGraph().findEdge(coarseVertex1,coarseVertex2) == null) && (!coarseVertex1.equals(coarseVertex2))){
							this.coarseGraph.getGraph().addEdge(new EdgeType(), coarseVertex1, coarseVertex2);
						}
					}
				}
			}
			counter++;
			if(counter % 1000 == 0){
				System.out.println("Three-hop distances computed for " + counter + " edges.");
			}
		}
		System.out.println("Three-hop distances computed for " + counter + " edges.");
		System.out.println("End of adding edges in distance 3");
		//adding the edges to the coarser graph.
		
		 
		/*System.out.println("Building the neighborhoods.");
		List<Set<VertexType>> regions = new ArrayList<Set<VertexType>>();
		for(VertexType v : mivs){
			Set<VertexType> neighborhood = new HashSet<VertexType>();
			neighborhood.add(v);
			for(VertexType neighbor : this.fineGraph.getGraph().getNeighbors(v)){
				neighborhood.add(neighbor);
				for(VertexType n : this.fineGraph.getGraph().getNeighbors(neighbor)){
					neighborhood.add(n);
				}
			}
			regions.add(neighborhood);
		}
		int c = 0;
		System.out.println("MIVS size = " +mivs.size());
		for(int i = 0; i < regions.size(); i++){
			VertexType coarseSource = this.pro.get(mivs.get(i));
			Set<VertexType> firstRegion = regions.get(i);
			for(int j = i + 1; j < regions.size(); j++){
				VertexType coarseTarget = this.pro.get(mivs.get(j));
				Set<VertexType> secondRegion = regions.get(j);
				for(VertexType v : firstRegion){
					if(secondRegion.contains(v)){
						if(this.coarseGraph.getGraph().findEdge(coarseSource, coarseTarget) == null){
							EdgeType edge = new EdgeType();
							this.coarseGraph.getGraph().addEdge(edge, coarseSource, coarseTarget);
							break;
						}
					}
				}
			}
			c++;
			if(c % 50 == 0){
				System.out.println("number of computed = " + c);
			}
		}*/
		
		/*System.out.println("adding the edges to the coarser graph.");
		DijkstraShortestPath<VertexType,EdgeType> alg = new DijkstraShortestPath<VertexType, EdgeType>(this.fineGraph.getGraph());
		System.out.println("dijkstra computed.");
		System.out.println("MIVS size = " +mivs.size());
		int c = 0;
		for(VertexType source : mivs){
			VertexType coarseSource = this.pro.get(source);
			for(VertexType target : mivs){
				VertexType coarseTarget = this.pro.get(source);
				if(!source.equals(target)){
					double distance = alg.getDistance(source, target).doubleValue();
					if(distance < 4.0){
						EdgeType edge = new EdgeType();
						this.coarseGraph.getGraph().addEdge(edge, coarseSource, coarseTarget);
					}
				}
			}
			c++;
			if(c % 2 == 0){
				System.out.println("number of computed = " + c);
			}
		}*/
		System.out.println("MultiLevel Type = MIVS");
		System.out.println("Graph vertex count = " + coarseGraph.getGraph().getVertexCount());
		System.out.println("Graph edge count = " + coarseGraph.getGraph().getEdgeCount());
	}
	
	public void MIVS2(){
		this.EC = false;
		this.MIVS = true;
		
		System.out.println("computing the maximal independent vertex set");
		List<VertexType> mivs = new ArrayList<VertexType>();
		List<VertexType> list = new ArrayList<VertexType>(this.fineGraph.getGraph().getVertices());
		Collections.shuffle(list);
		for(VertexType entry : list){
			if(!entry.isMatched()){
				entry.setMatched(true);
				mivs.add(entry);
				entry.setMivs(true);
				for(VertexType neighbor : this.fineGraph.getGraph().getNeighbors(entry)){
					neighbor.setMatched(true);
				}
			}
		}
		
		//adding the maximal independent vertex set to the coarser graph.
		System.out.println("adding the maximal independent vertex set to the coarser graph");
		for(VertexType v : mivs){
			this.coarseGraph.addVertex(v.getVertexNum());
			VertexType coarseVertex = this.coarseGraph.getVertex(v.getVertexNum());
			pro.put(v, coarseVertex);
		}
		
		this.addMivsEdges(fineGraph, coarseGraph);
	}
	
	private void addMivsEdges(Graph fineGraph, Graph coarseGraph){
		
		//saving the indices of fine and coarse graphs in the map.
		System.out.println("Saving the indices of fine and coarse graphs in the map.");
		Map<VertexType,Integer> fineIndices = new HashMap<VertexType, Integer>();
		Map<VertexType,Integer> coarseIndices = new HashMap<VertexType, Integer>();
		Map<Integer,VertexType> coarseInverseIndices = new HashMap<Integer, VertexType>();
		int counter = 0; 
		for(VertexType v : fineGraph.getGraph().getVertices()){
			fineIndices.put(v, counter);
			counter++;
		}
		counter = 0;
		for(VertexType v : coarseGraph.getGraph().getVertices()){
			coarseIndices.put(v, counter);
			coarseInverseIndices.put(counter, v);
			counter++;
		}
		
		//computing P matrix.
		System.out.println("computing P matrix.");
		Matrix P = new FlexCompColMatrix(fineGraph.getGraph().getVertexCount(), coarseGraph.getGraph().getVertexCount());
		for(VertexType v : fineGraph.getGraph().getVertices()){
			int fineIndex = fineIndices.get(v);
			if(v.isMivs()){
				VertexType vCoarse = coarseGraph.getVertex(v.getVertexNum());
				int coarseIndex = coarseIndices.get(vCoarse);
				P.set(fineIndex, coarseIndex, 1.0);
			}else{
				int nbMivsNeighbors = 0;
				for(VertexType neighbor : fineGraph.getGraph().getNeighbors(v)){
					if(neighbor.isMivs()){
						nbMivsNeighbors++;
					}
				}
				for(VertexType neighbor : fineGraph.getGraph().getNeighbors(v)){
					if(neighbor.isMivs()){
						VertexType vCoarse = coarseGraph.getVertex(neighbor.getVertexNum());
						int coarseIndex = coarseIndices.get(vCoarse);
						P.set(fineIndex, coarseIndex, 1.0 / nbMivsNeighbors);
					}
				}
			}
		}
		
		//computing A_{f} matrix.
		System.out.println("computing A_{f} matrix.");
		Matrix A_f = new FlexCompColMatrix(fineGraph.getGraph().getVertexCount(), fineGraph.getGraph().getVertexCount());
		for(EdgeType edge : fineGraph.getGraph().getEdges()){
			Pair<VertexType> pair = fineGraph.getGraph().getEndpoints(edge);
			VertexType head = pair.getFirst();
			VertexType tail = pair.getSecond();
			int headIndex = fineIndices.get(head);
			int tailIndex = fineIndices.get(tail);
			A_f.set(headIndex, tailIndex, edge.getWeight());
			A_f.set(tailIndex, headIndex, edge.getWeight());
		}
		
		//Galerkin product
		System.out.println("computing Galerkin product.");
		Matrix P_Copy = P.copy();
		Matrix P_T = new FlexCompColMatrix(P.numColumns(), P.numRows());
		P.transpose(P_T);
		FlexCompColMatrix GAux = new FlexCompColMatrix(fineGraph.getGraph().getVertexCount(), coarseGraph.getGraph().getVertexCount()); 
		A_f.mult(P_Copy, GAux);	
		FlexCompColMatrix G = new FlexCompColMatrix(coarseGraph.getGraph().getVertexCount(),coarseGraph.getGraph().getVertexCount());
		P_T.mult(GAux, G);
		for(int i = 0; i < coarseGraph.getGraph().getVertexCount();i++){
			G.set(i, i, 0.0);
		}
		
		//setting the edges of the coarser graph.
		System.out.println("Setting the edges of the coarser graph.");
		for(MatrixEntry e : G){
			if(e.get() == 0.0){
				System.err.println("row = " + e.row() + " column = " + e.column());
			}else{
				VertexType head = coarseInverseIndices.get(e.row());
				VertexType tail = coarseInverseIndices.get(e.column());
				EdgeType edge  = new EdgeType(e.get());
				System.out.println("edge weight = " + edge.getWeight());
				coarseGraph.getGraph().addEdge(edge, head, tail);
			}
		}
		
	}
	
	public Set<VertexType> prolongateMIVS(VertexType fineVertex){
		if(!this.fineGraph.getGraph().getVertices().contains(fineVertex)){
			throw new RuntimeException("No such a vertex in the fine graph");
		}
		Set<VertexType> res = new HashSet<VertexType>();
				
		if(fineVertex.isMivs()){
			VertexType coarseVertex = this.pro.get(fineVertex);
			res.add(coarseVertex);
			return res;
		}else{
			for(VertexType neighbor : this.fineGraph.getGraph().getNeighbors(fineVertex)){
				if(neighbor.isMivs()){
					VertexType coarseVertex = this.pro.get(neighbor);
					res.add(coarseVertex);
				}
			}
			return res;
		}
	}
	
	public Graph getCoarseGraph() {
		return coarseGraph;
	}
	
	public Graph getFineGraph() {
		return fineGraph;
	}
	
	public Map<VertexType, VertexType> getPro() {
		return pro;
	}
	
	public short getNum() {
		return num;
	}
	
	public static List<MultiLevelGraph> multiLevel(Graph graph){
		int nbDisconnectedFine = 0;
		for(VertexType v : graph.getGraph().getVertices()){
			if(graph.getGraph().getNeighborCount(v) == 0){
				nbDisconnectedFine++;
			}
		}
		System.out.println("Number of disconnected vertices = " + nbDisconnectedFine);
		//coarsening phase.
		List<MultiLevelGraph> multiList = new ArrayList<MultiLevelGraph>();
		MultiLevelGraph multiLevelObj = new MultiLevelGraph(graph);
		multiLevelObj.HEC();
		int levelCounter = 1;
		double gamma = ((double)multiLevelObj.getCoarseGraph().getGraph().getVertexCount() - nbDisconnectedFine) 
		/ (multiLevelObj.getFineGraph().getGraph().getVertexCount()- nbDisconnectedFine);
		System.out.println("gamma = " + gamma);
		int minSize = 10;
		while((gamma < 0.9) && (multiLevelObj.getCoarseGraph().getGraph().getVertexCount() > (nbDisconnectedFine + minSize)) && (levelCounter < 12)){
			
			nbDisconnectedFine = 0;
			for(VertexType v : multiLevelObj.getCoarseGraph().getGraph().getVertices()){
				if(multiLevelObj.getCoarseGraph().getGraph().getNeighborCount(v) == 0){
					nbDisconnectedFine++;
				}
			}
			System.out.println("Number of disconnected vertices = " + nbDisconnectedFine);
			multiList.add(multiLevelObj);
			levelCounter++;
			
			if(gamma < 0.85){
				MultiLevelGraph multi = new MultiLevelGraph(multiLevelObj.getCoarseGraph());
				multi.HEC();
				gamma = ((double)multi.getCoarseGraph().getGraph().getVertexCount() - nbDisconnectedFine) 
				/ (multi.getFineGraph().getGraph().getVertexCount()- nbDisconnectedFine);
				System.out.println("gamma = " + gamma);
				multiLevelObj = multi;
			}else{
				MultiLevelGraph multi = new MultiLevelGraph(multiLevelObj.getCoarseGraph());
				multi.MIVS();
				gamma = ((double)multi.getCoarseGraph().getGraph().getVertexCount() - nbDisconnectedFine) 
				/ (multi.getFineGraph().getGraph().getVertexCount()- nbDisconnectedFine);
				System.out.println("gamma = " + gamma);
				multiLevelObj = multi;
			}
		}
		multiList.add(multiLevelObj);
		//end of coarsening phase.
		return multiList;
	}
	
}
