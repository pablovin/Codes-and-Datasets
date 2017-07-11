package util.banco.bancoGeral;

public class ProblemaUmContraTodos extends TipoProblema {


	public ProblemaUmContraTodos(int qtClasses) {
		super(qtClasses);
	}

	@Override
	public int getTamanhoSaida() {
		return 2;
	}
	
	@Override
	public int getQuantidadeSaidas() {
		return 2;
	}

	@Override
	public int getQuantidadeTotalRodadas() {
		return qtClasses;
	}

	@Override
	public double[] geraSaida(int classe, int rodada) {
		
		int indiceSaida = geraTipoSaida(classe, rodada);
		int qtSaidas = getTamanhoSaida();
		
		double[] saida = new double[qtSaidas];
		saida[indiceSaida] = 1;
		
		return saida;
		
	}

	@Override
	public int geraTipoSaida(int classe, int rodada) {
		
		if(rodada == classe){
			return 0;
		}
		else{
			return 1;
		}
	
	}

	public String toString(){
		return "Problema Um contra Todos";
	}

	@Override
	public boolean getCrossEntropy() {
		return true;
	}

	@Override
	public int classifica(double[][] outputs) {
		
		int qtRodadas = outputs.length;
		int qtSaidas = outputs[0].length;
		if(getQuantidadeSaidas() != qtSaidas){			
			return -1;			
		}
		
		int classe = 0;
		double max = outputs[classe][0];
		
		for (int i = 1; i < qtRodadas; i++) {
			if(outputs[i][0] > max){
				max = outputs[i][0];
				classe = i;
			}			
		}
		
		return classe;
	}	
	
}
