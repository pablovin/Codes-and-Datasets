package rede;

public abstract class SPNNBase {

	public abstract void treina(double[][] entrada, double[] saidaEsperada) ;
	public abstract double[] calculaSaidaRede(double[][] entrada);
	
	abstract void atualizaPesos();
	abstract void zeraGradientes();
}
