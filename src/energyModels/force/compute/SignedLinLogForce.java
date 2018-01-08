package energyModels.force.compute;

import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;
import energyModels.tree.BHTree;

import threadedSim.action.AActionTaskFactory;
import threadedSim.action.ALocalActionTask;

public class SignedLinLogForce extends AActionTaskFactory<CoordinatesPeer>{
	private final double posEdgeScaling;
	private final double negEdgeScaling;
	private final double neutralContScaling;
	private static BHTree bhTree = null;
	public static boolean BHMode = false;
	
	
	public SignedLinLogForce(double posEdgeScaling, double negEdgeScaling,
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
	
	public static void setBhTree(BHTree bhTree) {
		SignedLinLogForce.bhTree = bhTree;
	}
	
	public class RepAttEdgeForceTask extends ALocalActionTask<CoordinatesPeer>{
		public RepAttEdgeForceTask(CoordinatesPeer peer, Semaphore semaphore){
			super(peer,semaphore);
		}

		@Override
		protected void doAction() {
			// attract
			CartesianCoordinate thisPos = this.peer.getCoordinate();
			for (Entry<CoordinatesPeer,Double> logicalNeighbor : this.peer
					.getAttractNeighbors().entrySet()){
				CartesianCoordinate logicalNeighborPosition = logicalNeighbor.getKey()
						.getCoordinate();
				double d = thisPos.distance(logicalNeighborPosition);
				//double d = thisPos.distanceMinkowski(logicalNeighborPosition,10.0);
				d = Math.max(d, 0.0000001);
				//thisPos.storeForce(logicalNeighborPosition, 1.0*posEdgeScaling*logicalNeighbor.getValue());
				//thisPos.storeForceMinkowski(logicalNeighborPosition, 1.0*posEdgeScaling, 10);
				thisPos.storeBodyForce(logicalNeighbor.getKey(), this.peer, 1.0*posEdgeScaling*logicalNeighbor.getValue());
				//thisPos.storeBodyForceMinkowski(logicalNeighbor.getKey(), this.peer, 1.0*posEdgeScaling, 10);
			}
			
			// repel
			for (Entry<CoordinatesPeer,Double> pushNeighbor : this.peer.getRepulseNeighbors().entrySet()) {
				if (!this.peer.equals(pushNeighbor)) {
					CartesianCoordinate pushNeighborPosition = pushNeighbor.getKey()
							.getCoordinate();
					double d = thisPos.distance(pushNeighborPosition);
					//double d = thisPos.distanceMinkowski(pushNeighborPosition,10.0);
					d = Math.max(d, 0.0000001);
					//thisPos.storeForce(pushNeighborPosition, (-1 / d)*negEdgeScaling*pushNeighbor.getValue());
					thisPos.storeBodyForce(pushNeighbor.getKey(), this.peer, (-1 / d)*negEdgeScaling*pushNeighbor.getValue());
					//thisPos.storeForceMinkowski(pushNeighborPosition, (-1 / d)*negEdgeScaling, 10);
				}
			}
			
			//neutral force
			for(CoordinatesPeer neutralNeighbor: this.peer.getNeutralNeighbors()){
				if(!this.peer.equals(neutralNeighbor)){
					CartesianCoordinate neutralNeighborPosition = neutralNeighbor.getCoordinate();
					double d = thisPos.distance(neutralNeighborPosition);
					//double d = thisPos.distanceMinkowski(neutralNeighborPosition,10.0);
					d = Math.max(d, 0.0001);
					//thisPos.storeForce(neutralNeighborPosition, 1.0	-((1.0/d))*neutralContScaling);
					thisPos.storeBodyForce(neutralNeighbor,this.peer, 1.0-((1.0/d))*neutralContScaling);
					//thisPos.storeForce(neutralNeighborPosition, (1.0*this.peer.getNegIndegree()*neutralNeighbor.getNegIndegree())
					//		-((1.0/d)*neutralContScaling*this.peer.getPosIndegree()*neutralNeighbor.getPosIndegree()));
					//thisPos.storeForceMinkowski(neutralNeighborPosition, 1.0-((1.0/d)*neutralContScaling),10);
				}
			}
			
			//Barnes and Hut neutral force
			if(BHMode){
				bhTree.updateForceSignedLinLog(this.peer, neutralContScaling);
			}
		}
	}

}
