package util.banco.bancoGeral;

public class ProblemaMulticlasse extends TipoProblema {

	public ProblemaMulticlasse(int qtClasses) {
		super(qtClasses);
	}

	@Override
	public double[] geraSaida(int classe, int rodada) {
		
		double[] saida = new double[getTamanhoSaida()];
		int indSaida = geraTipoSaida(classe, rodada);
		saida[indSaida] = 1;
		
		return saida;
		
	}

	@Override
	public int getTamanhoSaida() {
		return qtClasses;
	}

	@Override
	public int getQuantidadeSaidas() {
		return qtClasses;
	}

	@Override
	public int getQuantidadeTotalRodadas() {
		return 1;
	}

	@Override
	public int geraTipoSaida(int classe, int rodada) {
		return classe;
	}
	
	public String toString(){
		return "Problema Multiclasse";
	}

	@Override
	public boolean getCrossEntropy() {
		return true;
	}

	@Override
	public int classifica(double[][] outputs) {
		
		if(getQuantidadeTotalRodadas() != outputs.length)
			return -1;
		
		int classe = 0;
		double max = outputs[0][classe];
		
		for (int i = 1; i < outputs[0].length; i++) {
			
			if(outputs[0][i] > max){
				max = outputs[0][i];
				classe = i;
			}
			
		}
		
		return classe;
	}


}
