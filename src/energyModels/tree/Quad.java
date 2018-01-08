package energyModels.tree;

public class Quad{
	double xmin;
	double xmax;
	double ymin;
	double ymax;
	double length;
	
	public Quad(double xmin, double xmax, double ymin, double ymax){
		this.length = Math.max(xmax - xmin, ymax - ymin);
		this.xmin = ((xmin+xmax)/2.0) - (0.5*length);
		this.xmax = ((xmin+xmax)/2.0) + (0.5*length);
		this.ymin = ((ymin+ymax)/2.0) - (0.5*length);
		this.ymax = ((ymin+ymax)/2.0) + (0.5*length);
	}
	
	public boolean contains(double x, double y){
		if((xmin <= x) && (x <= xmax)&&(ymin <= y) && (y <= ymax)){
			return true;
		}else{
			return false;
		}
	}
	
	public Quad NW(){
		return new Quad(this.xmin,this.xmin + 0.5*this.length,this.ymax - 0.5*this.length, this.ymax);
	}
	
	public Quad NE(){
		return new Quad(this.xmax - 0.5*this.length,this.xmax,this.ymax - 0.5*this.length, this.ymax);
	}
	
	public Quad SW(){
		return new Quad(this.xmin,this.xmin + 0.5*this.length,this.ymin, this.ymin + 0.5*this.length);
	}
	
	public Quad SE(){
		return new Quad(this.xmax - 0.5*length,this.xmax,this.ymin, this.ymin + 0.5*this.length);
	}
	
	public String toString(){
		return this.xmin + "\t" + this.xmax + "\t" + this.ymin + "\t" + this.ymax + "\t" + this.length; 
	}

}
