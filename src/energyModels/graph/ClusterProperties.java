package energyModels.graph;

public class ClusterProperties {
	private static final int NO_INDEX = -1;
	private static final int NO_ID = -1;
	private double posEdgeProb;
	private double negEdgeProb;
	private double noEdgeProb;
	private int clusterSize;
	private int firstIndex = NO_INDEX;
	private int lastIndex = NO_INDEX;
	private int clusterID= NO_ID;
	
	public ClusterProperties(double posEdgeProb, double negEdgeProb,int nbVertices) throws RuntimeException{
		if(posEdgeProb>=0 && posEdgeProb<=1 && negEdgeProb>=0 && negEdgeProb<=1 && (negEdgeProb+posEdgeProb)>=0
				&& (negEdgeProb+posEdgeProb)<=1){
		this.posEdgeProb = posEdgeProb;
		this.negEdgeProb = negEdgeProb;
		this.noEdgeProb = 1-(posEdgeProb+negEdgeProb);
		this.clusterSize = nbVertices;
		}
		else{
			throw new RuntimeException("Cluster probabilities are not correct.");
		}
	}
	
	public ClusterProperties(int nbVertices){
		this.posEdgeProb = 0.0;
		this.negEdgeProb = 0.0;
		this.noEdgeProb = 1.0;
		this.clusterSize = nbVertices;
	}
	
	public ClusterProperties(){
		this.posEdgeProb = 0.0;
		this.negEdgeProb = 0.0;
		this.noEdgeProb = 1.0;
		this.clusterSize = 0;
	}
	public double getPosEdgeProb() {
		return posEdgeProb;
	}
	
	public double getNegEdgeProb() {
		return negEdgeProb;
	}
	
	public double getNoEdgeProb() {
		return noEdgeProb;
	}
	public void setProbabilities(double posEdgeProb, double negEdgeProb) throws Exception{
		if(posEdgeProb>0 && posEdgeProb<1 && negEdgeProb>0 && negEdgeProb<1 && (negEdgeProb+posEdgeProb)>0
				&& (negEdgeProb+posEdgeProb)<1){
		this.posEdgeProb = posEdgeProb;
		this.negEdgeProb = negEdgeProb;
		this.noEdgeProb = 1-(posEdgeProb+negEdgeProb);
		}else
		{
			throw new RuntimeException("Cluster probabilities are not correct.");
		}
	}
	public int getClusterSize() {
		return clusterSize;
	}
	public void setClusterSize(int clusterSize) {
		this.clusterSize = clusterSize;
	}

	public int getFirstIndex() {
		return firstIndex;
	}

	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}

	public int getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}
}
