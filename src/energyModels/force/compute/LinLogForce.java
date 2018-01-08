package energyModels.force.compute;

import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;
import energyModels.tree.BHTree;

import threadedSim.action.AActionTaskFactory;
import threadedSim.action.ALocalActionTask;

public class LinLogForce extends AActionTaskFactory<CoordinatesPeer> {
	private static double posEdgeScaling;
	private static double neutralContScaling;
	private static BHTree bhTree = null;
	public static boolean BHMode = false;
	public static int counter = 0;
	
	public LinLogForce(double posEdgeScaling, double neutralContScaling) {
		super();
		LinLogForce.posEdgeScaling = posEdgeScaling;
		LinLogForce.neutralContScaling = neutralContScaling;
	}

	@Override
	public Runnable createTask(CoordinatesPeer peer) {
		return new LinLogForceTask(peer, this.getSem());
	}

	@Override
	public Object getResult() {
		return null;
	}

	public static void setBhTree(BHTree bhTree) {
		LinLogForce.bhTree = bhTree;
	}
	
	public static class LinLogForceTask extends
			ALocalActionTask<CoordinatesPeer> {

		public LinLogForceTask(CoordinatesPeer peer, Semaphore sem) {
			super(peer, sem);
		}

		@Override
		protected void doAction() {
			CartesianCoordinate thisPos = this.peer.getCoordinate();
			counter++;
			/*if(counter < (Main.nbCycles)/2 && counter > Main.nbCycles/4){
				int period = (Main.nbCycles/4)/300;
				if(counter % period == 0){
					// attract
					for (Entry<CoordinatesPeer,Double> logicalNeighbor : this.peer
							.getAttractNeighbors().entrySet()) {
						CartesianCoordinate logicalNeighborPosition = logicalNeighbor
								.getKey().getCoordinate();
						thisPos.storeForce(logicalNeighborPosition, 1.0* posEdgeScaling*logicalNeighbor.getValue());
					}
				}else{
					// attract
					for (Entry<CoordinatesPeer,Double> logicalNeighbor : this.peer
							.getAttractNeighbors().entrySet()) {
						CartesianCoordinate logicalNeighborPosition = logicalNeighbor
								.getKey().getCoordinate();
						thisPos.storeForce(logicalNeighborPosition, 1.0* posEdgeScaling*logicalNeighbor.getValue());
					}
					if(BHMode){
						bhTree.updateForceModel3(this.peer);
					}
				}
			}else{
				// attract
				for (Entry<CoordinatesPeer,Double> logicalNeighbor : this.peer
						.getAttractNeighbors().entrySet()) {
					CartesianCoordinate logicalNeighborPosition = logicalNeighbor
							.getKey().getCoordinate();
					thisPos.storeForce(logicalNeighborPosition, 1.0* posEdgeScaling*logicalNeighbor.getValue());
				}
				if(BHMode){
					bhTree.updateForceModel3(this.peer);
				}
			}*/
			
			// attract
			for (Entry<CoordinatesPeer,Double> logicalNeighbor : this.peer
					.getAttractNeighbors().entrySet()) {
				/*CartesianCoordinate logicalNeighborPosition = logicalNeighbor
						.getKey().getCoordinate();*/
				thisPos.storeBodyForce(logicalNeighbor.getKey(), this.peer, 1.0* posEdgeScaling*logicalNeighbor.getValue());
			}
			// repulse
			for (CoordinatesPeer pushNeighbor : this.peer.getNeutralNeighbors()){
				if (!this.peer.equals(pushNeighbor)) {
					CartesianCoordinate pushNeighborPosition = pushNeighbor
							.getCoordinate();
					double d = thisPos.distance(pushNeighborPosition);
					//double d = thisPos.distanceMinkowski(pushNeighborPosition,20.0);
					d = Math.max(d, 0.0001);
					thisPos.storeBodyForce(pushNeighbor, this.peer, (-1 / d) * neutralContScaling);
				}
			}
			//Barnes and Hut neutral force
			if(BHMode){
				bhTree.updateForceModel3(this.peer, neutralContScaling);
			}
		}
		

	}
}
