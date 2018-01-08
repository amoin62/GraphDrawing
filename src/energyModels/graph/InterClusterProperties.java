package energyModels.graph;

public class InterClusterProperties {
	private ClusterProperties cluster1;
	private ClusterProperties cluster2;
	private double posEdgeProb;
	private double negEdgeProb;
	private double noEdgeProb;
	
	public InterClusterProperties(ClusterProperties cluster1,ClusterProperties cluster2, 
			double posEdgeProb, double negEdgeProb) throws RuntimeException{
		if(posEdgeProb>=0 && posEdgeProb<=1 && negEdgeProb>=0 && negEdgeProb<=1 && (negEdgeProb+posEdgeProb)>=0
				&& (negEdgeProb+posEdgeProb)<=1){
		this.cluster1 = cluster1;
		this.cluster2 = cluster2;
		this.posEdgeProb = posEdgeProb;
		this.negEdgeProb = negEdgeProb;
		this.noEdgeProb = 1-(posEdgeProb+negEdgeProb);}
		else{
			throw new RuntimeException("Inter cluster probabilities are not correct.");
		}
	}

	public ClusterProperties getCluster1() {
		return cluster1;
	}

	public void setCluster1(ClusterProperties cluster1) {
		this.cluster1 = cluster1;
	}

	public ClusterProperties getCluster2() {
		return cluster2;
	}

	public void setCluster2(ClusterProperties cluster2) {
		this.cluster2 = cluster2;
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
				this.noEdgeProb = 1-(posEdgeProb+negEdgeProb);}
		else{
			throw new RuntimeException("Inter cluster probabilities are not correct.");
		}
	}

}
