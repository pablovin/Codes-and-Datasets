package util.banco.bancoGeral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProblemaPairwiseClassification extends TipoProblema{

	private int totalRodadas;
	
	public ProblemaPairwiseClassification(int qtClasses) {
		super(qtClasses);
		totalRodadas = fatorial(qtClasses-1);
	}

	@Override
	public int getQuantidadeTotalRodadas() {
		return totalRodadas;
	}

	private int fatorial(int num){
		int soma = 0;
		for (int i = num; i > 0; i--) {
			soma += i;
		}
		return soma;
	}
	
	@Override
	public double[] geraSaida(int classe, int rodada) {
		int tipoSaida = geraTipoSaida(classe, rodada);
		double[] saida = null;
		if(tipoSaida >= 0){
			saida = new double[getTamanhoSaida()];
			saida[tipoSaida] = 1;
		}			
		return saida;
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
	public int geraTipoSaida(int classe, int rodada) {
		
		int ultimaClasse = qtClasses-1;
		int contClasse = 0, contIndice = ultimaClasse;
		
		while(contClasse < ultimaClasse){
			
			if(rodada < contIndice){
				if(classe == contClasse)
					return 0;
				else if(classe == contClasse+rodada+1)
					return 1;
				else
					return -1;
			}
			else{
				rodada = rodada - contIndice;
				contIndice--;
				contClasse++;
			}
			
		}
		return -1;
	}

	@Override
	public boolean getCrossEntropy() {
		return false;
	}

	@Override
	public int classifica(double[][] outputs) {
		int qtRodadasPorClasse = getQuantidadeRodadasPorClasse(0);
		List<Integer> classes = new ArrayList<Integer>();
		for (int i = 0; i <= qtRodadasPorClasse; i++) {
			classes.add(i);
		}
		return votacao(classes, outputs);
	}

	private int votacao(List<Integer> classes, double[][] outputs){		
		
		List<Integer> classesCopy = new ArrayList<Integer>();
		for (Integer integer : classes) {
			classesCopy.add(integer);
		}
		
		int tamanhoAnt = 0;
		
		while(classesCopy.size() > 1 && tamanhoAnt != classesCopy.size()){

			int tamanho = classesCopy.size();
			Map<Integer, Integer> mapa = new HashMap<Integer, Integer>();
			
			for (int iclasse1 = 0; iclasse1 < tamanho; iclasse1++) {
				
				for (int iclasse2 = iclasse1+1; iclasse2 < tamanho; iclasse2++) {
					
					int classe1 = classesCopy.get(iclasse1);
					int classe2 = classesCopy.get(iclasse2);
					int rodada = getRodada(classe1, classe2);
					
					if(outputs[rodada][0] > outputs[rodada][1]){
						if(mapa.get(classe1) == null)
							mapa.put(classe1, 1);
						else
							mapa.put(classe1, mapa.get(classe1)+1);
						
					}else if(outputs[rodada][0] < outputs[rodada][1]){
						if(mapa.get(classe2) == null)
							mapa.put(classe2, 1);
						else
							mapa.put(classe2, mapa.get(classe2)+1);
					}
					
				}
				
			}
			
			List<Integer> saidaMax = new ArrayList<Integer>();
			Integer[] keys = new Integer[mapa.keySet().size()];
			mapa.keySet().toArray(keys);
			saidaMax.add(keys[0]);
			
			for (int iclasse = 1; iclasse < keys.length; iclasse++) {
				if(mapa.get(saidaMax.get(0)) < mapa.get(keys[iclasse])){
					saidaMax.clear();
					saidaMax.add(keys[iclasse]);
				}else if(mapa.get(saidaMax.get(0)) == mapa.get(keys[iclasse])){
					saidaMax.add(keys[iclasse]);
				}
			}
			
			tamanhoAnt = classesCopy.size();
			
			classesCopy.clear();
			for (Integer integer : saidaMax) {
				classesCopy.add(integer);
			}
		
		}
		/*
		if(classesCopy.size() > 1){
			System.out.print("C-(");
			for (Integer integer : classesCopy) {
				System.out.print(integer+",");
			}
			System.out.println(")");
		}
		*/
		return classesCopy.get(0);
		
	}
	
	@Override
	public String toString() {
		return "Pairwise Classification";
	}

	public int getQuantidadeRodadasPorClasse(int classe) {
		return qtClasses-1;
	}
	
	public int getRodada(int classe1, int classe2){
		
		if(classe1 > classe2){
			int temp = classe1;
			classe1 = classe2;
			classe2 = temp;
		}
		int contClasse = getQuantidadeRodadasPorClasse(classe1);
		int rodada = 0;
		for (int i = classe1; i > 0; i--) {
			rodada = rodada + contClasse;
			contClasse--;
		}
		rodada += (classe2 - classe1 - 1);
		return rodada;
	}

	public int getIndiceRealRodadaClasse(int rodadaClasse, int classe) {
		
		int acc = qtClasses-1;
		int contClasse = classe;
		int contRodadaClasse = rodadaClasse;
		int rodada = 0;
		
		if(rodadaClasse < 0)
			return -1;

		while(contRodadaClasse > 0  && contClasse > 0){
			
			rodada = rodada + acc;
			acc--;
			contClasse--;
			contRodadaClasse--;
			
		}
		if(rodadaClasse < classe)
			rodada = rodada + contClasse - 1;
		else
			rodada = rodada + contRodadaClasse;
		
		return rodada;

	}

	private int[][] geraMapaPairwise(){
		
		int qtTotalRodadas = getQuantidadeTotalRodadas();
		int qtRodadasPorClasse = getQuantidadeRodadasPorClasse(0);
		int[][] mapa = new int[qtTotalRodadas][];
		
		int rodada = 0;
		for (int i = 0; i < mapa.length; i++) {
			for (int j = i+1; j <= qtRodadasPorClasse; j++) {
				mapa[rodada] = new int[]{i, j};
				rodada++;
				
			}
		}
		
		return mapa;
	}
	
	
	public static void main(String[] args) {
		
		
		int qtClasses = 5;
		ProblemaPairwiseClassification problema = new ProblemaPairwiseClassification(qtClasses);
		int totalRodadas = problema.getQuantidadeRodadasPorClasse(0);
		
		for (int i = 0; i < qtClasses; i++) {
			System.out.println("*** CLASSE "+i);
			for (int j = 0; j < totalRodadas; j++) {
				//int tipo = problema.geraTipoSaida(i, j);
				//System.out.println("Rod "+j+" - "+tipo);
				System.out.println("Rod ("+j+","+i+") = "+problema.getIndiceRealRodadaClasse(j, i));
			}
			
		}
		
		int[][] mapa = problema.geraMapaPairwise();
		for (int i = 0; i < mapa.length; i++) {
			System.out.print(i+": ");
			for (int j = 0; j < mapa[i].length; j++) {
				System.out.print(mapa[i][j]+" , ");
			}
			System.out.println();
		}
		
		
		int classe = problema.classifica(new double[][]{{0.1,0.8},
											{0.7,0.2},
											{0.3,0.2},
											{0.1,0.3},
											{0.2,0.3},
											{0.8,0.2},
											{0.1,0.8},
											{0.7,0.3},
											{0.5,0.2},
											{0.2,0.3}});
		System.out.println("Classe = "+classe);
		
	}
	
}
