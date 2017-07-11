package util.avaliacao;

import java.util.Comparator;

public class ProbabilidadeComparator implements Comparator<double[]> {

	private int indiceComparacao;
	private Ordem ordem;
	
	public ProbabilidadeComparator(int indice, Ordem ordem)
	{
		this.indiceComparacao = indice;
		this.ordem = ordem;
	}
	
	
	@Override
	public int compare(double[] arg0, double[] arg1) {
		
		if(ordem == Ordem.CRESCENTE){
			return Double.compare(arg0[indiceComparacao], arg1[indiceComparacao]);
		}
		else
		{
			return Double.compare(arg1[indiceComparacao], arg0[indiceComparacao]);
		}
	}
	
	public static enum Ordem
	{
		CRESCENTE,
		DESCRESCENTE
	}

}
