package energyModels;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import energyModels.coord.CartesianCoordinate;
import energyModels.force.apply.ForceApply;
import energyModels.graph.ClusterProperties;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.GraphCluster;
import energyModels.graph.GraphVisualizer;
import energyModels.graph.InterClusterProperties;
import energyModels.graph.VertexType;
import energyModels.peer.CoordinatesPeer;
import energyModels.peerInit.FromGraphSigned;
import energyModels.tree.MultiLevelGraph;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import spectralGD.Spec;
import threadedSim.Simulator;

public class SpectralMain {
	public static double scale = 15000;
	public static double initialEpsilon = 0.00001;
	public static double rate = 0.5;
	public static double k =10000.0;
	public static void main(String[] args) {
		//String fileName = "coater1.mtx";
		//String fileName = "s1rmq4m1.mtx";
		//String fileName = "G49.mtx";
		//String fileName = "flower_8_1.mtx";
		//String fileName = "mesh.mtx";
		//String fileName = "1138_bus.mtx";
		//String fileName = "wiki-Vote.mtx";
		//String fileName = "jagmesh8.mtx";
		//String fileName = "jagmesh1.mtx";
		//String fileName = "cavity04.mtx";
		//String fileName = "finance256.mtx";
		//String fileName = "finan512.mtx";
		//String fileName = "pkustk01.mtx";
		//String fileName = "blckhole.mtx";
		//String fileName = "plskz362.mtx";
		//String fileName = "poli3.mtx";
		//String fileName = "bcsstm07.mtx";
		//String fileName = "dwt_1005.mtx";
		//String fileName = "Franz3.mtx";
		//String fileName = "Harvard500.mtx";
		//String fileName = "California.mtx";
		//String fileName = "EVA.mtx";
		//String fileName = "rdist3a.mtx";
		//String fileName = "rajat22.mtx";
		//String fileName = "utm1700b.mtx";
		//String fileName = "tube2.mtx";
		//String fileName = "utm3060.mtx";
		//String fileName = "bbmat.mtx";
		//String fileName = "shyy41.mtx";
		//String fileName = "3D_28984_Tetra.mtx";
		//String fileName = "mesh2e1.mtx";
		//String fileName = "G12.mtx";
		//String fileName = "can_229.mtx";
		//String fileName = "can_61.mtx";
		//String fileName = "can_838.mtx";
		//String fileName = "cegb3024.mtx";
		//String fileName = "nasa1824.mtx";
		//String fileName = "kaufhold.mtx";
		//String fileName = "crack.mtx";
		//String fileName = "Alemdar.mtx";
		//String fileName = "nnc666.mtx";
		//String fileName = "can_292.mtx";
		//String fileName = "bcsstk24.mtx";
		//String fileName = "nasa2146.mtx";
		//String fileName = "shuttle_eddy.mtx";
		//String fileName = "skirt.mtx";
		//String fileName = "cavity17.mtx";
		//String fileName = "cavity24.mtx";
		//String fileName = "cavity06.mtx";
		//String fileName = "cavity01.mtx";
		//String fileName = "tsyl201.mtx";
		//String fileName = "thermal.mtx";
		//String fileName = "nasa4704.mtx";
		//String fileName = "nasa2910.mtx";
		//String fileName = "crystk01.mtx";
		//String fileName = "rbsa480.mtx";
		//String fileName = "bfwb398.mtx";
		//String fileName = "L.mtx";
		//String fileName = "raefsky5.mtx";
		//String fileName = "bcsstk31.mtx";
		//String fileName = "vibrobox.mtx";
		//String fileName = "uk.mtx";
		//String fileName = "3elt.mtx";
		//String fileName = "airfoil1.mtx";
		//String fileName = "stufe.mtx";
		//String fileName = "ukerbe1.mtx";
		//String fileName = "fxm3_6.mtx";
		//String fileName = "barth5.mtx";
		String fileName = "lock1074.mtx";
		//String fileName = "dw256A.mtx";
		//String fileName = "bcsstm36.mtx";
		//String fileName = "pkustk12.mtx";
		//String fileName = "pkustk09.mtx";
		//String fileName = "fe_4elt2.mtx";
		//String fileName = "fe_ocean.mtx";
		//String fileName = "G34.mtx";
		
				
		Spec spec = new Spec();
		Matrix m = spec.readMatrix(fileName);
		for(MatrixEntry e : m){
			e.set(1.0);
		}
		Matrix laplacian = spec.laplacianFromAdjacency(m);
		Graph graph = spec.buildGraphFromMatrix(m);
		
		
		//Grid graph example.
		/*GridGraph grid = new GridGraph();
		Graph graph = grid.buildGridGraph(50,50);
		Matrix m = spec.buildMatrixFromGraph(graph);
		Matrix laplacian = spec.laplacianFromAdjacency(m);*/
		//end of grid graph example.
		
		long start = System.currentTimeMillis();
		//2 clusters example
		/*Graph graph = new Graph();
		int numOfClusters = 2;
		int sizeOfClusters = 50; //better to be even.
		for(int i = 0; i < numOfClusters; i++){
			for(int j = 0; j < sizeOfClusters; j++){
				if (i==0)
					graph.addVertex((i*sizeOfClusters)+j, Color.BLUE);
				if (i==1)
					graph.addVertex((i*sizeOfClusters)+j, Color.GREEN);
			}
		}
		for(int i = 0; i < numOfClusters; i++){
			for(int j = 0; j < sizeOfClusters; j++){
				for(int k = j + 1; k < sizeOfClusters; k++){
					graph.addEdge(new EdgeType(), (i*sizeOfClusters) + j, (i*sizeOfClusters)+ k);
				}
			}
		}
		for(int i = 0; i < sizeOfClusters; i = i + 2){
			graph.addEdge(new EdgeType(), i , sizeOfClusters + i);
		}
		Matrix m = spec.buildMatrixFromGraph(graph);
		Matrix laplacian = spec.laplacianFromAdjacency(m);*/
		//end of 2 clusters example
		
		//4 clusters signed linlog model example
		/*List<ClusterProperties> clusterList = new ArrayList<ClusterProperties>();
		List<InterClusterProperties> interClusterList = new ArrayList<InterClusterProperties>();
		ClusterProperties cluster1 = new ClusterProperties(0.990,0.0,20);
		clusterList.add(cluster1);
		ClusterProperties cluster2 = new ClusterProperties(0.990,0.0,20);
		clusterList.add(cluster2);
		ClusterProperties cluster3 = new ClusterProperties(0.990,0.0,20);
		clusterList.add(cluster3);
		ClusterProperties cluster4 = new ClusterProperties(0.990,0.0,20);
		clusterList.add(cluster4);
		ClusterProperties cluster5 = new ClusterProperties(0.0,0.0,50);
		clusterList.add(cluster5);
		
		
		InterClusterProperties cluster12 = new InterClusterProperties(cluster1,cluster2,0.05,0.0);
		interClusterList.add(cluster12);
		InterClusterProperties cluster13 = new InterClusterProperties(cluster1,cluster3,0.0,0.0);
		interClusterList.add(cluster13);
		InterClusterProperties cluster14 = new InterClusterProperties(cluster1,cluster4,0.05,0.0);
		interClusterList.add(cluster14);
		InterClusterProperties cluster23 = new InterClusterProperties(cluster2,cluster3,0.05,0.0);
		interClusterList.add(cluster23);
		InterClusterProperties cluster24 = new InterClusterProperties(cluster2,cluster4,0.00,0.0);
		interClusterList.add(cluster24);
		InterClusterProperties cluster34 = new InterClusterProperties(cluster3,cluster4,0.05,0.0);
		interClusterList.add(cluster34);
		
		GraphCluster graphCluster = new GraphCluster(clusterList, interClusterList);
		Graph graph = graphCluster.cluster();
		Matrix m = spec.buildMatrixFromGraph(graph);
		Matrix laplacian = spec.laplacianFromAdjacency(m);*/
		//End of 4 cluster signed linlog model example
		
		//caveman graph example
		/*Graph graph = new Graph();
		int clusterSize = 5;
		int nbClusters = 15;
		for(int i = 0; i < nbClusters * clusterSize; i++){
			graph.addVertex(i);
		}
		for(int i = 0; i < nbClusters; i++){
			for(int j = 0; j < clusterSize; j++){
				for(int k = j + 1; k < clusterSize; k++){
					graph.addEdge(new EdgeType(), i * clusterSize + j, i * clusterSize + k);
				}
				graph.getGraph().removeEdge(graph.getGraph().findEdge(graph.getVertex(i*clusterSize), graph.getVertex(i*clusterSize+1)));
			}
		}
		for(int i = 0; i < nbClusters - 1; i++){
			graph.addEdge(new EdgeType(), i*clusterSize + clusterSize - 1, (i + 1)*clusterSize);
		}
		graph.addEdge(new EdgeType(), clusterSize * nbClusters - 1, 0);
		Matrix m = spec.buildMatrixFromGraph(graph);
		Matrix laplacian = spec.laplacianFromAdjacency(m);*/
		//end of caveman graph example
		
		
		//line graph
		/*Graph graph = new Graph();
		int lineLength = 2000;
		for(int i = 0; i < lineLength; i++){
			graph.addVertex(i);
		}
		for(int i = 0; i < lineLength - 1; i++){
			graph.getGraph().addEdge(new EdgeType(), graph.getVertex(i), graph.getVertex(i+1));
		}
		Matrix m = spec.buildMatrixFromGraph(graph);
		Matrix laplacian = spec.laplacianFromAdjacency(m);*/
		//end of line graph
		
		//circle graph
		/*Graph graph = new Graph();
		int circleLength = 1000;
		int nbCircles = 1;
		for(int i = 0; i < nbCircles * circleLength; i++){
			graph.addVertex(i);
		}
		
		for(int j = 0; j < nbCircles; j++){
			for(int i =0; i < circleLength - 1; i++){
				graph.getGraph().addEdge(new EdgeType(), graph.getVertex((j*circleLength) + i), graph.getVertex((j*circleLength)+i+1));
			}
			graph.getGraph().addEdge(new EdgeType(),graph.getVertex(j*circleLength),graph.getVertex((j*circleLength) + circleLength-1));
		}
		
		int lineLength = 0;
		for(int i = nbCircles * circleLength; i < nbCircles * circleLength + lineLength; i++){
			graph.addVertex(i);
		}
		for(int i = nbCircles * circleLength; i < nbCircles * circleLength + lineLength - 1; i++){
			graph.getGraph().addEdge(new EdgeType(), graph.getVertex(i), graph.getVertex(i+1));
		}	
		Matrix m = spec.buildMatrixFromGraph(graph);
		Matrix laplacian = spec.laplacianFromAdjacency(m);*/
		//end of circle graph
		

		//flower graph
		/*Graph graph = new Graph();
		int circleLength = 50;
		int nbCircles = 4;
		for(int i = 0; i < nbCircles * circleLength; i++){
			graph.addVertex(i);
		}
		
		for(int j = 0; j < nbCircles; j++){
			for(int i =0; i < circleLength - 1; i++){
				graph.getGraph().addEdge(new EdgeType(), graph.getVertex((j*circleLength) + i), graph.getVertex((j*circleLength)+i+1));
			}
			graph.getGraph().addEdge(new EdgeType(),graph.getVertex(j*circleLength),graph.getVertex((j*circleLength) + circleLength-1));
		}
		
		graph.addVertex((nbCircles * circleLength));
		VertexType centralVertex = graph.getVertex(nbCircles * circleLength); 
		for(int i = 0; i < nbCircles; i++){
			graph.getGraph().addEdge(new EdgeType(), centralVertex, graph.getVertex(i*circleLength));
		}
		Matrix m = spec.buildMatrixFromGraph(graph);
		Matrix laplacian = spec.laplacianFromAdjacency(m);*/
		//end of flower graph
		List<MultiLevelGraph> multiList = MultiLevelGraph.multiLevel(graph);
	
		//drawing the coarsest graph.
		Map<VertexType, CartesianCoordinate> coordinatesMap = 
			new HashMap<VertexType, CartesianCoordinate>();
		Graph coarsest = multiList.get(multiList.size() - 1).getCoarseGraph();
		Matrix coarsestAdjacency = spec.buildMatrixFromGraph(coarsest);
		Matrix coarsestLaplacian = spec.laplacianFromAdjacency(coarsestAdjacency);
		Matrix mass = spec.buildMassFromGraph(coarsest);
		double epsilon = initialEpsilon;
		Vector[] eigenVectors = spec.computeFirstEigenVectorsOfLaplacian(mass,null,coarsestLaplacian, 3, epsilon);
				
		List<CoordinatesPeer> vertices = new ArrayList<CoordinatesPeer>();
		for(VertexType v : coarsest.getGraph().getVertices()){
			CoordinatesPeer coPeer = new CoordinatesPeer(v.getVertexNum());
			double[] coorArray = new double[2];
			coorArray[0] = eigenVectors[1].get(v.getVertexNum());
			coorArray[1] = eigenVectors[2].get(v.getVertexNum());
			CartesianCoordinate coor = new CartesianCoordinate(coorArray);
			coPeer.setCoordinate(coor);
			vertices.add(coPeer);
		}
		int level = 0;
		GraphVisualizer gv = new GraphVisualizer(coarsest,vertices,null, scale);
		gv.visualize(-1,multiList.size()-level);
		level++;
		for(CoordinatesPeer peer : vertices){
			coordinatesMap.put(coarsest.getVertex(peer.getPeerId()), peer.getCoordinate());
		}
		
		//algo
		for(int j = 0; j < multiList.size(); j++){
			epsilon *= rate;
			//vertices = new ArrayList<CoordinatesPeer>();
			MultiLevelGraph currentMulti = multiList.get(multiList.size() - j - 1);
			Graph fineGraph = currentMulti.getFineGraph();
			mass = spec.buildMassFromGraph(fineGraph);
			m = spec.buildMatrixFromGraph(fineGraph);
			laplacian = spec.laplacianFromAdjacency(m);
			/*vertexCount = currentMulti.getFineGraph().getGraph().getVertexCount();
			edgeCount = currentMulti.getFineGraph().getGraph().getEdgeCount();
			step = (vertexCount * vertexCount) 
			/ (((posEdgeScale * edgeCount ) + (vertexCount * vertexCount))*posEdgeScale);*/
			
			Vector[] initialEigenVectors = new DenseVector[2];
			initialEigenVectors[0] = new DenseVector(fineGraph.getGraph().getVertexCount());
			initialEigenVectors[1] = new DenseVector(fineGraph.getGraph().getVertexCount());
			if(currentMulti.EC){
				for(VertexType vertex: fineGraph.getGraph().getVertices()){
					VertexType v = currentMulti.getPro().get(vertex);
					CartesianCoordinate coorMap = coordinatesMap.get(v);
					CartesianCoordinate coor = new CartesianCoordinate(coorMap);
					CartesianCoordinate random = new CartesianCoordinate();
					random.times(CartesianCoordinate.minMoveDistance/2.0);
					//coor.plus(random);
					initialEigenVectors[0].set(vertex.getVertexNum(), coor.getPosition()[0]);
					initialEigenVectors[1].set(vertex.getVertexNum(), coor.getPosition()[1]);
					/*CoordinatesPeer coPeer = new CoordinatesPeer(vertex.getVertexNum());
					coPeer.setMass(vertex.getMass());
					coPeer.setCoordinate(coor);
					vertices.add(coPeer);*/
				}
			}else if(currentMulti.MIVS){
				for(VertexType vertex: fineGraph.getGraph().getVertices()){
					Set<VertexType> coarseVertices = currentMulti.prolongateMIVS(vertex);
					CartesianCoordinate coor = new CartesianCoordinate(new double[]{0.0,0.0});
					for(VertexType coarseVertex : coarseVertices){
						CartesianCoordinate c = coordinatesMap.get(coarseVertex);
						coor.plus(c);
					}
					coor.times(1.0 / coarseVertices.size());
					CartesianCoordinate random = new CartesianCoordinate();
					random.times(CartesianCoordinate.minMoveDistance/2.0);
					//coor.plus(random);
					initialEigenVectors[0].set(vertex.getVertexNum(), coor.getPosition()[0]);
					initialEigenVectors[1].set(vertex.getVertexNum(), coor.getPosition()[1]);
					/*CoordinatesPeer coPeer = new CoordinatesPeer(vertex.getVertexNum());
					coPeer.setMass(vertex.getMass());
					coPeer.setCoordinate(coor);
					vertices.add(coPeer);*/
				}
			}else{
				throw new RuntimeException("The MultiLevel object has no type!");
			}
			eigenVectors = spec.computeFirstEigenVectorsOfLaplacian(mass,initialEigenVectors,laplacian, 3, epsilon);
			vertices = new ArrayList<CoordinatesPeer>();
			for(VertexType v : fineGraph.getGraph().getVertices()){
				CoordinatesPeer coPeer = new CoordinatesPeer(v.getVertexNum());
				double[] coorArray = new double[2];
				coorArray[0] = eigenVectors[1].get(v.getVertexNum());
				coorArray[1] = eigenVectors[2].get(v.getVertexNum());
				CartesianCoordinate coor = new CartesianCoordinate(coorArray);
				coPeer.setCoordinate(coor);
				vertices.add(coPeer);
			}
			gv = new GraphVisualizer(fineGraph,vertices,null, scale);
			System.out.println("vertices = " + fineGraph.getGraph().getVertexCount() 
					+ " edges = " + fineGraph.getGraph().getEdgeCount());
			gv.visualize(-1,multiList.size()-level);
			level++;
			coordinatesMap.clear();
			for(CoordinatesPeer peer : vertices){
				coordinatesMap.put(currentMulti.getFineGraph().getVertex(peer.getPeerId()), peer.getCoordinate());
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("execution time = " + (end - start) / 1000.0);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("time.txt",true));
			out.write(fileName + "\t" + (end - start) / 1000.0 + "\t" + + initialEpsilon + "\t" + rate + "\n");
			out.flush();
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
