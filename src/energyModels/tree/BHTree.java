package energyModels.tree;

import java.util.List;

import energyModels.coord.CartesianCoordinate;
import energyModels.peer.CoordinatesPeer;

public class BHTree{
	public static int NODE_ID = 0;
	public static double TETA = 0.5;
	public CoordinatesPeer body;
	public Quad quad;
	private BHTree nw;
	private BHTree ne;
	private BHTree sw;
	private BHTree se;
	private int nodeID;
	public BHTree(Quad q){
		super();
		this.quad = q;
		this.body = new CoordinatesPeer(-1);
		this.body.setMass(0.0);
		this.nw=null;
		this.ne=null;
		this.sw=null;
		this.se=null;
		this.nodeID = NODE_ID;
		NODE_ID++;
	}
	
	public void insert(CoordinatesPeer peer){
		double xPeer = peer.getCoordinate().getPosition()[0];
		double yPeer = peer.getCoordinate().getPosition()[1];
		if(this.quad.contains(xPeer, yPeer)){
			if(this.body.mass==0){
				this.update(peer);
			}else if(this.isExternalNode()){
				CoordinatesPeer b = this.body;
				Quad nwQuad = new Quad(quad.xmin, quad.xmin + (quad.length / 2), quad.ymax - (quad.length / 2)
						, quad.ymax);
				Quad neQuad = new Quad(quad.xmax - (quad.length / 2), quad.xmax, quad.ymax - (quad.length / 2)
						, quad.ymax);
				Quad swQuad = new Quad(quad.xmin, quad.xmin + (quad.length / 2), quad.ymin, quad.ymin + 
						(quad.length / 2));
				Quad seQuad = new Quad(quad.xmax - (quad.length / 2), quad.xmax, quad.ymin, quad.ymin + 
						(quad.length / 2));
				nw = new BHTree(nwQuad);
				nw.insert(b);
				nw.insert(peer);
				ne = new BHTree(neQuad);
				ne.insert(b);
				ne.insert(peer);
				sw = new BHTree(swQuad);
				sw.insert(b);
				sw.insert(peer);
				se = new BHTree(seQuad);
				se.insert(b);
				se.insert(peer);
				this.update(peer);
			}else{
				this.update(peer);
				nw.insert(peer);
				ne.insert(peer);
				sw.insert(peer);
				se.insert(peer);
			}
		}
	}
	
	private boolean isExternalNode(){
		if(nw == null && ne == null && sw == null && se == null){
			return true;
		}else{
			return false;
		}
	}
	
	private void update(CoordinatesPeer peer){
		double xPeer = peer.getCoordinate().getPosition()[0];
		double yPeer = peer.getCoordinate().getPosition()[1];
		double x = this.body.getCoordinate().getPosition()[0];
		double y = this.body.getCoordinate().getPosition()[1];
		x = (x*this.body.mass + xPeer*peer.mass)/(this.body.mass + peer.mass);
		y = (y*this.body.mass + yPeer*peer.mass)/(this.body.mass + peer.mass);
		double[] newPos = new double[2];
		newPos[0] = x;
		newPos[1] = y;
		this.body.setCoordinate(new CartesianCoordinate(newPos));
		this.body.mass += peer.mass;
	}
	
	public static double[] getMargins(List<CoordinatesPeer> peers){
		double[] margins = new double[4];
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double ymax = Double.NEGATIVE_INFINITY;
		for(CoordinatesPeer p : peers){
			if(p.getCoordinate().getPosition()[0] < xmin){
				xmin = p.getCoordinate().getPosition()[0] ;
			}
			if(p.getCoordinate().getPosition()[0] > xmax){
				xmax = p.getCoordinate().getPosition()[0] ;
			}
			if(p.getCoordinate().getPosition()[1] < ymin){
				ymin = p.getCoordinate().getPosition()[1] ;
			}
			if(p.getCoordinate().getPosition()[1] > ymax){
				ymax = p.getCoordinate().getPosition()[1] ;
			}
		}
		margins[0] = xmin;
		margins[1] = xmax;
		margins[2] = ymin;
		margins[3] = ymax;
		return margins;
	}
	
	public void updateForceSignedLinLog(CoordinatesPeer peer, double neutralContScaling){
		double d = this.body.getCoordinate().distance(peer.getCoordinate());
		if(d != 0){
			d = Math.max(d, 0.0000001);
			if(this.isExternalNode()){
				if(this.body.mass != 0){
					peer.getCoordinate().storeBodyForce(this.body, peer, 1.0 - (1.0/d)*neutralContScaling);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, peer, 1.0 - (1.0/d)*neutralContScaling, 10.0);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, 1.0	-(1.0/d)*neutralContScaling, 10);
				}
			}else{
				double s = this.quad.length;
				if((s/d)<= TETA){
					peer.getCoordinate().storeBodyForce(this.body, peer, 1.0 - (1.0/d)*neutralContScaling);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, peer, 1.0 - (1.0/d)*neutralContScaling, 10.0);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, 1.0	-(1.0/d)*neutralContScaling, 10);
				}else{
					this.nw.updateForceSignedLinLog(peer,neutralContScaling);
					this.ne.updateForceSignedLinLog(peer,neutralContScaling);
					this.sw.updateForceSignedLinLog(peer,neutralContScaling);
					this.se.updateForceSignedLinLog(peer,neutralContScaling);
				}
			}
		}
	}
	
	public void updateForceSignedHC(CoordinatesPeer peer, double neutralContScaling){
		double d = this.body.getCoordinate().distance(peer.getCoordinate());
		if(d != 0){
			d = Math.max(d, 0.0001);
			if(this.isExternalNode()){
				if(this.body.mass != 0){
					peer.getCoordinate().storeBodyForce(this.body, peer, d-((1.0/(d*d))*neutralContScaling));
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, peer, 1.0 - (1.0/d)*neutralContScaling, 10.0);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, 1.0	-(1.0/d)*neutralContScaling, 10);
				}
			}else{
				double s = this.quad.length;
				if((s/d)<= TETA){
					peer.getCoordinate().storeBodyForce(this.body, peer, d-((1.0/(d*d))*neutralContScaling));
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, peer, 1.0 - (1.0/d)*neutralContScaling, 10.0);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, 1.0	-(1.0/d)*neutralContScaling, 10);
				}else{
					this.nw.updateForceSignedHC(peer,neutralContScaling);
					this.ne.updateForceSignedHC(peer,neutralContScaling);
					this.sw.updateForceSignedHC(peer,neutralContScaling);
					this.se.updateForceSignedHC(peer,neutralContScaling);
				}
			}
		}
	}
	
	public void updateForceSignedDavidson(CoordinatesPeer peer, double neutralContScaling){
		double d = this.body.getCoordinate().distance(peer.getCoordinate());
		if(d != 0){
			d = Math.max(d, 0.0001);
			if(this.isExternalNode()){
				if(this.body.mass != 0){
					peer.getCoordinate().storeBodyForce(this.body, peer, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling));
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, peer, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling));
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling), 10);
				}
			}else{
				double s = this.quad.length;
				if((s/d)<= TETA){
					peer.getCoordinate().storeBodyForce(this.body, peer, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling));
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, peer, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling), 10.0);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling), 10);
				}else{
					this.nw.updateForceSignedDavidson(peer,neutralContScaling);
					this.ne.updateForceSignedDavidson(peer,neutralContScaling);
					this.sw.updateForceSignedDavidson(peer,neutralContScaling);
					this.se.updateForceSignedDavidson(peer,neutralContScaling);
				}
			}
		}
	}
	
	public void updateForceDavidson(CoordinatesPeer peer, double neutralContScaling){
		double d = this.body.getCoordinate().distance(peer.getCoordinate());
		if(d != 0){
			d = Math.max(d, 0.0000001);
			if(this.isExternalNode()){
				if(this.body.mass != 0){
					peer.getCoordinate().storeBodyForce(this.body, peer, (-2 / Math.pow(d, 3.0))*neutralContScaling);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, peer, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling));
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling), 10);
				}
			}else{
				double s = this.quad.length;
				if((s/d)<= TETA){
					peer.getCoordinate().storeBodyForce(this.body, peer, (-2 / Math.pow(d, 3.0))*neutralContScaling);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, peer, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling), 10.0);
					//peer.getCoordinate().storeBodyForceMinkowski(this.body, 2.0*d-((2 / Math.pow(d, 3.0))*neutralContScaling), 10);
				}else{
					this.nw.updateForceDavidson(peer,neutralContScaling);
					this.ne.updateForceDavidson(peer,neutralContScaling);
					this.sw.updateForceDavidson(peer,neutralContScaling);
					this.se.updateForceDavidson(peer,neutralContScaling);
				}
			}
		}
	}
	
	public void updateForceModel3(CoordinatesPeer peer, double neutralContScaling){
		double d = this.body.getCoordinate().distance(peer.getCoordinate());
		if(d != 0){
			d = Math.max(d, 0.0001);
			if(this.isExternalNode()){
				if(this.body.mass != 0){
					peer.getCoordinate().storeBodyForce(this.body, peer, -(1.0/d)*neutralContScaling);
				}
			}else{
				double s = this.quad.length;
				if((s/d)<= TETA){
					peer.getCoordinate().storeBodyForce(this.body, peer, -(1.0/d)*neutralContScaling);
				}else{
					this.nw.updateForceModel3(peer,neutralContScaling);
					this.ne.updateForceModel3(peer,neutralContScaling);
					this.sw.updateForceModel3(peer,neutralContScaling);
					this.se.updateForceModel3(peer,neutralContScaling);
				}
			}
		}
	}
	
	public void updateForceFruch(CoordinatesPeer peer, double neutralContScaling){
		double d = this.body.getCoordinate().distance(peer.getCoordinate());
		if(d != 0){
			d = Math.max(d, 0.0000001);
			if(this.isExternalNode()){
				if(this.body.mass != 0){
					peer.getCoordinate().storeBodyForce(this.body, peer, -(1.0/d)*neutralContScaling);
				}
			}else{
				double s = this.quad.length;
				if((s/d)<= TETA){
					peer.getCoordinate().storeBodyForce(this.body, peer, -(1.0/d)*neutralContScaling);
				}else{
					this.nw.updateForceFruch(peer,neutralContScaling);
					this.ne.updateForceFruch(peer,neutralContScaling);
					this.sw.updateForceFruch(peer,neutralContScaling);
					this.se.updateForceFruch(peer,neutralContScaling);
				}
			}
		}
	}
	
	public void updateForceSignedFruch(CoordinatesPeer peer, double neutralContScaling){
		double d = this.body.getCoordinate().distance(peer.getCoordinate());
		if(d != 0){
			d = Math.max(d, 0.0001);
			if(this.isExternalNode()){
				if(this.body.mass != 0){
					peer.getCoordinate().storeBodyForce(this.body, peer, d*d-((1.0/d)*neutralContScaling));
				}
			}else{
				double s = this.quad.length;
				if((s/d)<= TETA){
					peer.getCoordinate().storeBodyForce(this.body, peer, d*d-((1.0/d)*neutralContScaling));
				}else{
					this.nw.updateForceSignedFruch(peer,neutralContScaling);
					this.ne.updateForceSignedFruch(peer,neutralContScaling);
					this.sw.updateForceSignedFruch(peer,neutralContScaling);
					this.se.updateForceSignedFruch(peer,neutralContScaling);
				}
			}
		}
	}
	
	public void updateForceHC(CoordinatesPeer peer, double neutralContScaling){
		double d = this.body.getCoordinate().distance(peer.getCoordinate());
		if(d != 0){
			d = Math.max(d, 0.0001);
			if(this.isExternalNode()){
				if(this.body.mass != 0){
					peer.getCoordinate().storeBodyForce(this.body, peer, -(1.0/(d*d))*neutralContScaling);
				}
			}else{
				double s = this.quad.length;
				if((s/d)<= TETA){
					peer.getCoordinate().storeBodyForce(this.body, peer, -(1.0/(d*d))*neutralContScaling);
				}else{
					this.nw.updateForceHC(peer,neutralContScaling);
					this.ne.updateForceHC(peer,neutralContScaling);
					this.sw.updateForceHC(peer,neutralContScaling);
					this.se.updateForceHC(peer,neutralContScaling);
				}
			}
		}
	}
	
	public String toString(){
		String res = "";
		if((this.isExternalNode())){
			if(this.body.mass == 0){
				return "";
			}else{
				String res2 = "";
				return (res2 + "Node ID :" + this.nodeID+ "\tcoordinate : " + this.quad.toString() + "\n");
			}
		}else{
			res += this.nw.toString();
			res += this.ne.toString();
			res += this.sw.toString();
			res += this.se.toString();
		return res;
		}
	}
}
