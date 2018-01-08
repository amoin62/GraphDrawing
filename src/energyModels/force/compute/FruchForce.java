package energyModels.force.compute;

import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import threadedSim.action.AActionTaskFactory;
import threadedSim.action.ALocalActionTask;
import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;
import energyModels.tree.BHTree;

public class FruchForce extends AActionTaskFactory<CoordinatesPeer>{
	private final double posEdgeScaling;
	private final double neutralContScaling;
	private static BHTree bhTree = null;
	public static boolean BHMode = false;
	public static int counter = 0;
	public FruchForce(double posEdgeScaling, double neutralContScaling){
		super();
		this.posEdgeScaling = posEdgeScaling;
		this.neutralContScaling = neutralContScaling;
	}
	
	public static void setBhTree(BHTree bhTree) {
		FruchForce.bhTree = bhTree;
	}
	
	@Override
	public Runnable createTask(CoordinatesPeer peer) {
		return new FruchForceTask(peer,this.getSem());
	}
	
	public class FruchForceTask extends
	ALocalActionTask<CoordinatesPeer> {

		public FruchForceTask(CoordinatesPeer peer, Semaphore sem) {
			super(peer, sem);
		}

		@Override
		protected void doAction() {
			CartesianCoordinate thisPos = this.peer.getCoordinate();
			counter++;
			
			// attract
			for (Entry<CoordinatesPeer,Double> logicalNeighbor : this.peer
					.getAttractNeighbors().entrySet()) {
				CartesianCoordinate logicalNeighborPosition = logicalNeighbor
						.getKey().getCoordinate();
				double d = thisPos.distance(logicalNeighborPosition);
				d = Math.max(d, 0.0000001);
				thisPos.storeBodyForce(logicalNeighbor.getKey(), peer, d*d*posEdgeScaling*logicalNeighbor.getValue());
			}
			// repulse
			for (CoordinatesPeer pushNeighbor : this.peer.getNeutralNeighbors()){
				if (!this.peer.equals(pushNeighbor)) {
					CartesianCoordinate pushNeighborPosition = pushNeighbor
							.getCoordinate();
					double d = thisPos.distance(pushNeighborPosition);
					//double d = thisPos.distanceMinkowski(pushNeighborPosition,20.0);
					d = Math.max(d, 0.0000001);
					thisPos.storeBodyForce(pushNeighbor, peer, (-1 / d) * neutralContScaling);
				}
			}
			//Barnes and Hut neutral force
			if(BHMode){
				bhTree.updateForceFruch(this.peer,neutralContScaling);
			}
			
		}
		
	}

}
