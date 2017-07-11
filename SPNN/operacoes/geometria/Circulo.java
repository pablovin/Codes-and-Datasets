package operacoes.geometria;

import operacoes.geometria.Circulo;

public class Circulo {
	
	private Ponto centro;
	private double raio;
	
	public Circulo(Ponto centro, double raio){
		this.centro = centro;
		this.raio = raio;
	}
	
	
	public Circulo(double[] centro, double raio){
		this.centro = new Ponto(centro[0],centro[1]);
		this.raio = raio;
	}

	
	
	public Ponto getCentro() {
		return centro;
	}


	public double getRaio() {
		return raio;
	}


	@Override
	public boolean equals(Object obj){
		
		if(obj instanceof Circulo){
			Circulo circ = (Circulo)obj;
			return this.centro.equals(circ.centro) && this.raio == circ.raio;
		}
		return false;
	
	}
	
	@Override
	public int hashCode(){
		
		return (int) (centro.getX()*raio);
	
	}
	
}
