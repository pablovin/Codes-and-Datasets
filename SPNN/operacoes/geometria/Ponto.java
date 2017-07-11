package operacoes.geometria;

public class Ponto {
	private double x;
	private double y;
	
	public Ponto(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Ponto(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object p){
		if(p instanceof Ponto){
			Ponto pt = (Ponto) p;
			return this.x == pt.x && this.y == pt.y;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return (int) (x+y);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	
	
}
