package energyModels.coord;



public interface IProvidesPosition<C extends ICoordinate<C>> {
	public C getCoordinate();

	public void setCoordinate(C c);
}
