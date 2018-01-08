package energyModels.force.compute;

import java.util.concurrent.Semaphore;

import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;

import threadedSim.action.AActionTaskFactory;
import threadedSim.action.ALocalActionTask;

public class DualLinLogForce extends AActionTaskFactory<CoordinatesPeer>{

	@Override
	public Runnable createTask(CoordinatesPeer peer) {
		return new DualLinLogForceTask(peer, this.getSem());
	}



	public static class DualLinLogForceTask extends ALocalActionTask<CoordinatesPeer>{

		public DualLinLogForceTask(CoordinatesPeer peer, Semaphore sem) {
			super(peer, sem);
		}

		@Override
		protected void doAction() {
			// attract
			CartesianCoordinate thisPos = this.peer.getCoordinate();
			for (CoordinatesPeer logicalNeighbor : this.peer
					.getAttractNeighbors().keySet()) {
				CartesianCoordinate logicalNeighborPosition = logicalNeighbor
						.getCoordinate();
				thisPos.storeForce(logicalNeighborPosition, 1.);
			}
			// repulse
			for (CoordinatesPeer pushNeighbor : this.peer.getRepulseNeighbors().keySet()) {
				if (!this.peer.equals(pushNeighbor)) {
					CartesianCoordinate pushNeighborPosition = pushNeighbor
							.getCoordinate();
					double d = thisPos.distance(pushNeighborPosition);
					d = Math.max(d, 0.0001);
					thisPos.storeForce(pushNeighborPosition, -1 / d);
				}
			}
			
		}
		
	}
	
}