package util.banco.bancoGeral;

public abstract class TipoProblema {

	final int qtClasses;
	
	public TipoProblema(int qtClasses){
		this.qtClasses = qtClasses;
	}
	
	public abstract int getQuantidadeTotalRodadas();
	public abstract double[] geraSaida(int classe, int rodada);
	public abstract int getTamanhoSaida();
	public abstract int getQuantidadeSaidas();
	public abstract int geraTipoSaida(int classe, int rodada);
	public abstract boolean getCrossEntropy();
	public abstract int classifica(double[][] outputs);
	public abstract String toString();

}
