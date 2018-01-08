package energyModels;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import spectralGD.Spec;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import energyModels.coord.CartesianCoordinate;
import energyModels.graph.Graph;
import energyModels.graph.GraphVisualizer;
import energyModels.graph.VertexType;
import energyModels.peer.CoordinatesPeer;

public class HDE {
	private Graph graph;
	int numPivots;
	public HDE(Graph graph, int numPivots){
		this.graph = graph;
		this.numPivots = numPivots;
	}
	
	public List<CoordinatesPeer>  highDimDraw(double epsilon){
		List<double[]> highDimEmbedding = this.highDimEmbedding();
		List<Vector> coordinates = this.pca(highDimEmbedding, 2, epsilon); 
		List<CoordinatesPeer> coPeers = new ArrayList<CoordinatesPeer>();
		for(VertexType v : this.graph.getGraph().getVertices()){
			double[] coorArray = new double[coordinates.size()];
			for(int i = 0; i < coorArray.length; i++){
				coorArray[i] = coordinates.get(i).get(v.getVertexNum());
			}
			CartesianCoordinate coor = new CartesianCoordinate(coorArray);
			CoordinatesPeer coPeer = new CoordinatesPeer(v.getVertexNum());
			coPeer.setCoordinate(coor);
			coPeers.add(coPeer);
		}
		return coPeers;
	}
	//The input to this method is a list of m dimensions each with n coordinates.
	//The output is k vectors of n coordinates.
	private List<Vector>  pca(List<double[]> highDimEmbedding, int nbLowDimensions, double epsilon){
		Matrix x = new DenseMatrix(highDimEmbedding.size(), highDimEmbedding.get(0).length);
		for(int i = 0; i < highDimEmbedding.size(); i++){
			for(int j = 0; j < highDimEmbedding.get(i).length; j++){
				x.set(i, j, highDimEmbedding.get(i)[j]);
			}
		}
		double[] mean = new double[x.numRows()];
		for(int i = 0; i < mean.length; i++){
			mean[i] = 0.0;
		}
		for(MatrixEntry e : x){
			mean[e.row()] += e.get();
		}
		for(MatrixEntry e : x){
			e.set(e.get() - (mean[e.row()] / x.numColumns()));
		}
		
		Matrix s = new DenseMatrix(x.numRows(), x.numRows());
		Matrix xTranpose = new DenseMatrix(x.numColumns(), x.numRows());
		x.transpose(xTranpose);
		x.mult(xTranpose, s);
		s.scale(1.0 / x.numColumns());
		List<Vector> eigenVectors = this.PowerIteration(s, nbLowDimensions, epsilon);
		List<Vector> lowCoordinates = new ArrayList<Vector>();
		for(int i =0 ; i < eigenVectors.size(); i++){
			Vector coor = new DenseVector(xTranpose.numRows());
			Vector eigenVector = eigenVectors.get(i);
			xTranpose.mult(eigenVector, coor);
			lowCoordinates.add(coor);
		}
		return lowCoordinates;
	}
	
	// The output is k (nbLowDimensions) vectors of m coordinates. 
	private List<Vector> PowerIteration(Matrix m, int nbLowDimensions, double epsilon){
		System.out.println("start of power iteration");
		List<Vector> eigenVectors = new ArrayList<Vector>();
		for(int i = 0; i < nbLowDimensions; i++){
			double[] initial = new double[m.numRows()];
			Random random = new Random();
			for(int j = 0; j < initial.length ; j++){
				initial[j] = random.nextDouble() - 0.5;
			}
			Vector eigenVector = new DenseVector(initial);
			double norm = eigenVector.norm(Vector.Norm.Two);
			eigenVector.scale(1.0 / norm);
			double condition = 0.0;
			while (condition < 1.0 - epsilon){
				Vector eigenVectorCopy = eigenVector.copy();
				for(int j = 0; j <= i - 1 ; j++){
					Vector previousEigenVector = eigenVectors.get(j);
					double dotProduct = eigenVectorCopy.dot(previousEigenVector);
					eigenVectorCopy.add(-dotProduct, previousEigenVector);
				}
				m.mult(eigenVectorCopy,eigenVector);
				norm = eigenVector.norm(Vector.Norm.Two);
				eigenVector.scale(1.0 / norm);
				condition = eigenVector.dot(eigenVectorCopy);
				System.out.println("condition = " + condition);
			}
			eigenVectors.add(eigenVector);
		}
		System.out.println("end of PowerIteration.");
		return eigenVectors;
	}
	
	//returns a list m vectors each containing n coordinates. 
	private List<double[]>  highDimEmbedding(){
		//choosing the first pivot randomly.
		List<VertexType> vertexList = new ArrayList<VertexType>(graph.getGraph().getVertices());
		Random r = new Random();
		VertexType pivot = vertexList.get(r.nextInt(vertexList.size()));
		double[] d = new double[graph.getGraph().getVertexCount()];
		for(int i=0; i < d.length; i++){
			d[i] = Double.POSITIVE_INFINITY;
		}
		List<double[]> coordinates = new ArrayList<double[]>(numPivots);
		for(int i = 0; i < numPivots; i++){
			double[] bfs = this.BFS(pivot);
			coordinates.add(bfs); 
			for(int j = 0; j < d.length; j++){
				d[j] = Math.min(d[j], bfs[j]);
			}
			int maxIndex = 0;
			double max = Double.NEGATIVE_INFINITY;
			for(int j = 0; j < d.length; j++){
				if(d[j] > max){
					max = d[j];
					maxIndex = j;
				}
			}
			pivot = graph.getVertex(maxIndex);
		}
		System.out.println("end of highDimEmbedding");
		return coordinates;
	}
	
	private double[] BFS(VertexType p){
		int level = 0;
		double[] distances = new double[graph.getGraph().getVertexCount()];
		Set<VertexType> pList = new HashSet<VertexType>();
		pList.add(p);
		this.BFS(pList, distances, level);
		return distances;
	}
	
	private void BFS(Set<VertexType> currentVertices, double[] distances, int level){
		if(currentVertices.size() != 0){
			Set<VertexType> neighbors = new HashSet<VertexType>();
			for(VertexType v : currentVertices){
				distances[v.getVertexNum()] = level;
				//We are sure from the previous recurse that the current vertices are unmatched.
				v.setMatched(true);
				for(VertexType neighbor : graph.getGraph().getNeighbors(v)){
					if(!neighbor.isMatched()){
						neighbors.add(neighbor);
					}
				}
			}
			level++;
			BFS(neighbors, distances, level);
		}else{
			//resetting all vertices as unmatched for the computation of the next dimensions.
			for(VertexType v : graph.getGraph().getVertices()){
				v.setMatched(false);
			}
		}
	}
	
	public static void main(String[] args){
		final double epsilon = 0.01;
		final double scale = 0.8;
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
		//String fileName = "lock1074.mtx";
		//String fileName = "dw256A.mtx";
		//String fileName = "bcsstm36.mtx";
		//String fileName = "pkustk12.mtx";
		//String fileName = "pkustk09.mtx";
		//String fileName = "fe_4elt2.mtx";
		String fileName = "fe_ocean.mtx";
		//String fileName = "G34.mtx";
		
		
		Spec spec = new Spec();
		Matrix m = spec.readMatrix(fileName);
		Graph graph = spec.buildGraphFromMatrix(m);
		
		//Grid graph example.
		/*GridGraph grid = new GridGraph();
		Graph graph = grid.buildGridGraph(30,30);*/
		//end of grid graph example.
		
		long start = System.currentTimeMillis();
		HDE hde = new HDE(graph, 50);
		List<CoordinatesPeer> coordinatesPeers = hde.highDimDraw(epsilon);
		GraphVisualizer gv = new GraphVisualizer(graph,coordinatesPeers,null, scale);
		gv.visualize(0,0);
		long end = System.currentTimeMillis();
		System.out.println("execution time = " + (end - start)/1000.0);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("timeHDE.txt",true));
			out.write(fileName + "\t" + (end - start) / 1000.0 + "\t" + + epsilon + "\n");
			out.flush();
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
