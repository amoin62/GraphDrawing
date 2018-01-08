package spectralGD;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import edu.uci.ics.jung.graph.util.Pair;
import energyModels.SpectralMain;
import energyModels.data.voter.Voter;
import energyModels.graph.EdgeType;
import energyModels.graph.Graph;
import energyModels.graph.VertexType;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.io.MatrixInfo;
import no.uib.cipr.matrix.io.MatrixVectorReader;
import no.uib.cipr.matrix.io.MatrixVectorWriter;
import no.uib.cipr.matrix.sparse.CompDiagMatrix;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

public class Spec{
	//reads the Adjacency matrix of a graph from the matrix market format.
	public Matrix readMatrix(String fileName){
		try {
			MatrixVectorReader matrixReader = new MatrixVectorReader(new FileReader(fileName));
			MatrixInfo matrixInfo = matrixReader.readMatrixInfo();
			Matrix matrix = null;
			if(matrixInfo.isPattern()){
				matrix = this.readMMFile(fileName);
			}else{
				Matrix m = new CompRowMatrix(matrixReader);
				matrix = new FlexCompRowMatrix(m);
				//If the matrix is symmetric, MatrixVectorReader only reads upper triangular entries.
				if(matrixInfo.isSkewSymmetric() || matrixInfo.isSymmetric()){
					//Matrix mCopy = matrix.copy();
					//mCopy.transpose();
					//matrix.add(mCopy); 
					for(MatrixEntry e : matrix){
						matrix.set(e.column(), e.row(), e.get());
					}
				}
			}
			return matrix;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//This method is written to write the pattern matrices as MatrixVector class cannot handle 
	//this latter.
	private Matrix readMMFile(String fileName){
		Matrix matrix = null;
		try {
			FileInputStream fis = new FileInputStream(fileName);
			BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
			String line;
			line = bf.readLine();
			StringTokenizer token = new StringTokenizer(line);
			token.nextToken();
			token.nextToken();
			String format = token.nextToken();
			System.out.println("format = " + format);
			String field = token.nextToken();
			System.out.println("field = " + field);
			if(!(field.trim().equals("pattern"))){
				throw new RuntimeException("This method is run only when the matrix is pattern!");
			}
			String symmetry = token.nextToken();
			System.out.println("symmetry = " + symmetry);
			boolean comment = true;
			while(comment){
				line = bf.readLine();
				comment = line.startsWith("%");
			}
			token = new StringTokenizer(line);
			int rows = Integer.parseInt(token.nextToken());
			int columns = Integer.parseInt(token.nextToken());
			//this lines reads the number of edges.
			Integer.parseInt(token.nextToken());
			matrix = new FlexCompRowMatrix(rows, columns);
			int counter = 0;
			
			if((field.trim().equals("pattern")) && (symmetry.trim().equals("general"))){
				while((line=bf.readLine())!= null){
					token = new StringTokenizer(line);
					int i = Integer.parseInt(token.nextToken());
					int j = Integer.parseInt(token.nextToken());
					double weight;
					if(field.equals("pattern")){
						weight = 1.0;
					}else{
						weight = Double.parseDouble(token.nextToken());
					}
					matrix.set(i-1, j-1, weight);
					counter++;
					if(counter % 10000 == 0){
						System.out.println(counter + " edges read.");
					}
				}
				System.out.println(counter + " edges read.");
				//computing A + A^{T} to obtain a symmetric matrix.
				Matrix mCopy = matrix.copy();
				mCopy.transpose();
				matrix.add(mCopy);
			}
			
			if((field.trim().equals("pattern")) && ((symmetry.trim().equals("skew-symmetric")) || (symmetry.trim().equals("symmetric")))){
				while((line=bf.readLine())!= null){
					token = new StringTokenizer(line);
					int i = Integer.parseInt(token.nextToken());
					int j = Integer.parseInt(token.nextToken());
					double weight;
					if(field.equals("pattern")){
						weight = 1.0;
					}else{
						weight = Double.parseDouble(token.nextToken());
					}
					matrix.set(i-1, j-1, weight);
					matrix.set(j-1, i-1, weight);
					counter++;
					if(counter % 10000 == 0){
						System.out.println(counter + " edges read.");
					}
				}
				System.out.println(counter + " edges read.");
			}
			System.out.println("voters size = " + Voter.factory.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return matrix;
	}
	
	//builds a graph from its adjacency matrix.
	public Graph buildGraphFromMatrix(Matrix matrix){
		Graph graph = new Graph();
		for(int i = 0; i < matrix.numRows(); i++){
			graph.addVertex(i);
		}
		for(MatrixEntry e : matrix){
			VertexType v1 = graph.getVertex(e.row());
			VertexType v2 = graph.getVertex(e.column());
			EdgeType edge = graph.getGraph().findEdge(v1, v2);
			if(edge == null){
				graph.getGraph().addEdge(new EdgeType(e.get()), v1, v2);
			}
		}
		return graph;
	}
	
	//builds the Adjacency matrix of a graph.
	public Matrix buildMatrixFromGraph(Graph graph){
		Matrix matrix = 
			new FlexCompRowMatrix(graph.getGraph().getVertexCount(), graph.getGraph().getVertexCount());
		for(EdgeType edge : graph.getGraph().getEdges()){
			Pair<VertexType> pair = graph.getGraph().getEndpoints(edge);
			VertexType v1 = pair.getFirst();
			VertexType v2 = pair.getSecond();
			double weight = edge.getWeight();
			matrix.set(v1.getVertexNum(), v2.getVertexNum(), weight);
			matrix.set(v2.getVertexNum(), v1.getVertexNum(), weight);
		}
		return matrix;
	}
	
	//computes the Laplacian matrix from the Adjacency matrix. 
	//Adjacency matrix must be square symmetric.
	public Matrix laplacianFromAdjacency(Matrix adjacency){
		
		Matrix laplacian = new FlexCompRowMatrix(adjacency);
		for(int i = 0; i < laplacian.numRows(); i++){
			laplacian.set(i, i, 0.0);
		}
		double[] degrees = new double[laplacian.numRows()];
		for(int i = 0; i < laplacian.numRows(); i++){
			degrees[i] = 0.0;
		}
		for(MatrixEntry e : laplacian){
			degrees[e.row()] += e.get();
			laplacian.set(e.row(), e.column(), -e.get());
		}
		for(int i = 0; i < laplacian.numRows(); i++){
			laplacian.set(i, i, degrees[i]);
		}
		
		return laplacian;
	}
	
	public double[] GershgorinBound(Matrix matrix){
		double[] bound = new double[2];
		//computing the Gershgorin bound for the input matrix
		double[] leftBounds = new double[matrix.numRows()];
		double[] rightBounds = new double[matrix.numRows()];
		double[] sumArray = new double[matrix.numRows()];
		for(int i=0; i < leftBounds.length; i++){
			leftBounds[i] = 0.0;
			rightBounds[i] = 0.0;
			sumArray[i] = 0.0;
		}
		for(MatrixEntry e : matrix){
			sumArray[e.row()] += Math.abs(e.get());
		}
		//computing the Gershgorin bound for each row.
		for(int i = 0; i < sumArray.length; i++){
			sumArray[i] -= Math.abs(matrix.get(i, i));
			leftBounds[i] = matrix.get(i, i) - sumArray[i];
			rightBounds[i] = matrix.get(i, i) + sumArray[i];
		}
		//computing the total Gershgorin bound of the matrix.
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0; i < rightBounds.length ; i++){
			if(rightBounds[i] > max){
				max = rightBounds[i];
			}
			if(leftBounds[i] < min){
				min = leftBounds[i];
			}
		}
		bound[0] = min;
		bound[1] = max;
		return bound;
	}
	
	public Matrix buildMassFromGraph(Graph graph){
		Matrix matrix = new CompDiagMatrix(graph.getGraph().getVertexCount(), graph.getGraph().getVertexCount());
		for(VertexType v : graph.getGraph().getVertices()){
			double degree = 0.0;
			for(EdgeType e : graph.getGraph().getIncidentEdges(v)){
				degree += e.getWeight();
				//System.out.println("weight = " + e.getWeight());
			}
			//matrix.set(v.getVertexNum(), v.getVertexNum(), v.getMass());
			if(degree == 0.0)
				degree = 1.0;
			matrix.set(v.getVertexNum(), v.getVertexNum(), 1.0);
			//System.out.println("degree = " + degree);
		}
		return matrix;
	}
	public Vector[] computeFirstEigenVectorsOfLaplacian(Matrix mass, Vector[] initialVectors, Matrix laplacian, 
			int nbEigenVectors, double epsilon){
		//computing L_{cenralized}
		//double[] gershgorinBound = this.GershgorinBound(laplacian);
		//double minGershgorin = gershgorinBound[0];
		//double maxGershgorin = gershgorinBound[1];
		//for(int i = 0; i < laplacian.numRows(); i++){
		//	laplacian.set(i, i, laplacian.get(i, i)+ Math.abs(minGershgorin));
		//}
		/*for(MatrixEntry e : laplacian){
			e.set(e.get() / (maxGershgorin - minGershgorin));
		}*/
		//computing gI - L_{centralized}
		//Matrix reverseMatrix = new FlexCompRowMatrix(laplacian.numRows(),laplacian.numColumns());
		//for(int i = 0; i < reverseMatrix.numRows(); i++){
		//	//reverseMatrix.set(i, i, 1.0);
		//	reverseMatrix.set(i, i, maxGershgorin);
		//}
		//reverseMatrix.add(-1.0, laplacian); 
				
		//computation of the smallest eigenvectors of L_{centralized} using power iteration.
		//The first one is the degenerate solution.
		Vector[] eigenVectors = new DenseVector[nbEigenVectors];
		double[] allOneArray = new double[laplacian.numRows()];
		for(int i = 0; i < mass.numRows(); i++){
			allOneArray[i] = Math.sqrt(mass.get(i, i));
		}
		Vector firstEigenVector = new DenseVector(allOneArray);
		double norm = firstEigenVector.norm(Norm.Two);
		firstEigenVector.scale(1/norm);
		
		//computing m^{1/2} as mass1.
		Matrix mass1 = new CompDiagMatrix(mass);
		for(int i = 0; i < mass.numRows(); i++){
			mass1.set(i, i, Math.sqrt(mass.get(i, i)));
		}
		//computing m^{-1/2} as mass2.
		Matrix mass2 = new CompDiagMatrix(mass);
		for(int i = 0; i < mass.numRows(); i++){
			mass2.set(i, i, 1 / Math.sqrt(mass.get(i, i)));
		}
		
		Matrix b = new FlexCompRowMatrix(laplacian);
		for(MatrixEntry e : b){
			double value = laplacian.get(e.row(), e.column()) * (mass2.get(e.row(), e.row())) * (mass2.get(e.column(), e.column()));
			e.set(value);
		}
		double gersh[] = this.GershgorinBound(b);
		double maxGersh = gersh[1];
		//computing gI - L_{centralized}
		Matrix reverseMatrix = new FlexCompRowMatrix(b.numRows(),b.numColumns());
		for(int i = 0; i < reverseMatrix.numRows(); i++){
			//reverseMatrix.set(i, i, 1.0);
			reverseMatrix.set(i, i, maxGersh);
		}
		reverseMatrix.add(-1.0, b); 
		
		/*double sum = reverseMatrix.numRows();
		//initialization and normalization of the all-one vector (degenerate eigenvector).
		for(int i=0; i < allOneArray.length; i++){
			allOneArray[i] = 1.0 / sum;
		}
		Vector firstEigenVector = new DenseVector(allOneArray);*/
		
		eigenVectors[0] = firstEigenVector;
		for(int i=1; i <= nbEigenVectors-1; i++){
			Vector eigenVector = new DenseVector(b.numRows());
			norm = 0.0;
			if(initialVectors == null){
				//a random initial guess for the eigenvector.
				double[] random = new double[reverseMatrix.numRows()];
				Random r = new Random();
				//sum = 0.0;
				for(int k = 0; k < random.length; k++){
					random[k] = r.nextDouble() - 0.0;
					//sum += Math.abs(random[k]);
				}
				eigenVector = new DenseVector(random);
				//normalization
				norm = eigenVector.norm(Vector.Norm.Two);
				eigenVector.scale(1.0/norm);
			}
			else{
				//eigenVector = initialVectors[i-1];
				mass1.mult(initialVectors[i-1], eigenVector);
				norm = eigenVector.norm(Norm.Two);
				eigenVector.scale(1/norm);
			}
			//double sum = 
			double condition = 0.0;
			while(condition < 1- epsilon){
				Vector eigenVectorCopy = eigenVector.copy();
				//orthogonalization against previous eigenvectors.
				for(int j = 0; j < i; j++){
					double dotProduct = eigenVectors[j].dot(eigenVectorCopy);
					Vector previousEigenVector = eigenVectors[j].copy();
					eigenVectorCopy.add(-dotProduct, previousEigenVector);
				}
				
				double sum = 0.0;
				for(VectorEntry e : eigenVectorCopy){
					sum += e.get();
				}
				
				reverseMatrix.mult(eigenVectorCopy, eigenVector);
				
				//This part is just for computing FlexSpec.
				double k = SpectralMain.k;
				eigenVector.scale(k);
				for(VectorEntry e : eigenVector){
					e.set(e.get() + sum + (eigenVector.size() - 2)*eigenVectorCopy.get(e.index()));
				}
				// just for FlexSpec until here. 
				
				norm = eigenVector.norm(Vector.Norm.Two);
				eigenVector.scale(1.0/norm);
				condition = eigenVector.dot(eigenVectorCopy);
				System.out.println("condition = " + condition + " i = " + i);
			}
			eigenVectors[i] = new DenseVector(eigenVector.size());
			mass2.mult(eigenVector, eigenVectors[i]);
			//eigenVectors[i] = eigenVector;
		}
		return eigenVectors;
	}
	
	public static void main(String[] args){
		String fileName = "jagmesh1.mtx";
		
		Spec spec = new Spec();
		Matrix m = spec.readMatrix(fileName);
		Graph graph = spec.buildGraphFromMatrix(m);
		m = spec.buildMatrixFromGraph(graph);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("output.txt");
			MatrixVectorWriter writer = new MatrixVectorWriter(fos,true);
			System.out.println(m.toString());
			/*for(MatrixEntry e : m){
				writer.
			}*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
	}
}
