package energyModels.force.compute;

import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;
import energyModels.tree.BHTree;

import threadedSim.action.AActionTaskFactory;
import threadedSim.action.ALocalActionTask;

public class SignedFruchForce extends AActionTaskFactory<CoordinatesPeer>{
	private final double posEdgeScaling;
	private final double negEdgeScaling;
	private final double neutralContScaling;
	private static BHTree bhTree = null;
	public static boolean BHMode = false;
		
	
	public static void setBhTree(BHTree bhTree) {
		SignedFruchForce.bhTree = bhTree;
	}

	public SignedFruchForce(double posEdgeScaling, double negEdgeScaling,
			double neutralContScaling) {
		super();
		this.posEdgeScaling = posEdgeScaling;
		this.negEdgeScaling = negEdgeScaling;
		this.neutralContScaling = neutralContScaling;
	}
	
	@Override
	public Runnable createTask(CoordinatesPeer peer) {
		return new RepAttEdgeForceTask(peer, this.getSem());
	}
	
	public class RepAttEdgeForceTask extends ALocalActionTask<CoordinatesPeer>{
		public RepAttEdgeForceTask(CoordinatesPeer peer, Semaphore semaphore){
			super(peer,semaphore);
		}

		@Override
		protected void doAction() {
			// attract
			CartesianCoordinate thisPos = this.peer.getCoordinate();
			for (Entry<CoordinatesPeer, Double> logicalNeighbor : this.peer
					.getAttractNeighbors().entrySet()) {
				CartesianCoordinate logicalNeighborPosition = logicalNeighbor.getKey()
						.getCoordinate();
				double d = thisPos.distance(logicalNeighborPosition);
				d = Math.max(d, 0.0001);
				thisPos.storeBodyForce(logicalNeighbor.getKey(), this.peer, d*d*posEdgeScaling*logicalNeighbor.getValue());
			}
			// repulse
			for (Entry<CoordinatesPeer, Double> pushNeighbor : this.peer.getRepulseNeighbors().entrySet()) {
				if (!this.peer.equals(pushNeighbor)) {
					CartesianCoordinate pushNeighborPosition = pushNeighbor.getKey()
							.getCoordinate();
					double d = thisPos.distance(pushNeighborPosition);
					d = Math.max(d, 0.0001);
					thisPos.storeBodyForce(pushNeighbor.getKey(), this.peer, (-1 / d)*negEdgeScaling*pushNeighbor.getValue());
				}
			}
			//baseline force
			for(CoordinatesPeer neutralNeighbor: this.peer.getNeutralNeighbors()){
				if(!this.peer.equals(neutralNeighbor)){
					CartesianCoordinate neutralNeighborPosition = neutralNeighbor.getCoordinate();
					double d = thisPos.distance(neutralNeighborPosition);
					d = Math.max(d, 0.0001);
					thisPos.storeBodyForce(neutralNeighbor, this.peer, d*d-((1.0/d)*neutralContScaling));
				}
			}
			
			if(BHMode){
				bhTree.updateForceSignedFruch(this.peer,neutralContScaling);
			}
		}
	}
}
