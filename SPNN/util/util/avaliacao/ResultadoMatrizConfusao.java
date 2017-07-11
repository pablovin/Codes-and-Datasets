package util.avaliacao;

public class ResultadoMatrizConfusao extends Resultado{

	public final double[][] matrizConfusao;

	public final double txAcerto;
	
	public ResultadoMatrizConfusao(double[][] matriz) {
		
		this.matrizConfusao = matriz;
		
		double total = 0;
		double ac = 0;
		
		for (int i = 0; i < matrizConfusao.length; i++) {
			
			ac += matrizConfusao[i][i];
			
			for (int j = 0; j < matrizConfusao[i].length; j++) {
				
				total += matrizConfusao[i][j];
				
			}
		}
		
		this.txAcerto = ac/total;
		
	}
	
	@Override
	public String getResultado() {
		
		String res = "";
		
		for (int i = 0; i < matrizConfusao.length; i++) {
			for (int j = 0; j < matrizConfusao[i].length; j++) {
				
				res += matrizConfusao[i][j]+";";
				
			}
			res += "\n";
		}
		
		return res;
	}
	
	
	
}
