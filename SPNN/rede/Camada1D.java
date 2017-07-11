package rede;

import java.util.List;

import util.redeneural.base.FuncaoAtivacao;

public class Camada1D {

	private Neuronio1D[] neuronios;

	private int posicaoCamada;

	private FuncaoAtivacao funcao;
	private boolean crossEntropy;
	
	public Camada1D(int size, int posicaoCamada, int qtPesos, FuncaoAtivacao funcao, boolean crossEntropy) {
		this.neuronios = new Neuronio1D[size];
		this.posicaoCamada = posicaoCamada;
		this.funcao = funcao;
		this.crossEntropy = crossEntropy;
		
		for (int i = 0; i < size; i++) {
			neuronios[i] = new Neuronio1D(i, posicaoCamada, qtPesos, funcao);
		}

	}
	
	public Camada1D clone(int tamanho) {
		Camada1D retorno = new Camada1D(neuronios.length,posicaoCamada,tamanho, funcao, crossEntropy);
		for (int i = 0; i < neuronios.length; i++) {
			retorno.neuronios[i] = neuronios[i].clone(tamanho);
		}
		return retorno;
	}

	public int getPosicaoCamada() {
		return posicaoCamada;
	}

	public void setPosicaoCamada(int posicaoCamada) {
		this.posicaoCamada = posicaoCamada;
	}

	public Neuronio1D[] getNeuronios() {
		return neuronios;
	}

	public void setNeuronios(Neuronio1D[] neuronios) {
		this.neuronios = neuronios;
	}

	public static boolean imprime = false;
	
	public void atualizaSaidaNeuronios(List<Neuronio2D> entradas) {
		double acumuladaSaida = 0;
		
		for (int i = 0; i < neuronios.length; i++) {
			Neuronio1D neuronio = neuronios[i];
			double somatorio = neuronio.getPesoBias();
			for(int j = 0; j < neuronio.getPesos().length; j++) {
				somatorio += entradas.get(j).getSaida()	* neuronio.getPesos()[j];
			}
			neuronio.funcaoAtivacao(somatorio);
			acumuladaSaida += Math.exp(neuronio.getSaida());
		}
		
		if(this.crossEntropy) {
			for (int i = 0; i < neuronios.length; i++) {
				Neuronio1D neuronio = neuronios[i];
				neuronio.setSaida(Math.exp(neuronio.getSaida())/acumuladaSaida);
			}
		}
	}
	
	public void atualizaSensibilidadeSaida(double[] erro, List<Neuronio2D> entradas) {
		for (int i = 0; i < neuronios.length; i++) {
			Neuronio1D neuronio = neuronios[i];
			double somatorio = neuronio.getPesoBias();
			for(int j = 0; j < neuronio.getPesos().length; j++) {
				somatorio += entradas.get(j).getSaida()	* neuronio.getPesos()[j];
			}
			neuronio.funcaoAtivacaoDerivada(somatorio, erro[i]);
		}
	}

	public void calculaGradientes(List<Neuronio2D> entrada) {
		for (Neuronio1D neuronio : neuronios) {
			for (Neuronio2D neuronio2 : entrada) {
				double ajuste = neuronio.getSensibilidade()*neuronio2.getSaida();
				ajuste += neuronio.getGradientes()[0];
				neuronio.getGradientes()[0] = ajuste;
			}
			neuronio.setGradienteBias(neuronio.getGradienteBias()+neuronio.getSensibilidade());
		}
	}
	
	
	public void atualizaPesos(double max, double min, double nPos, double nNeg) {
		for (Neuronio1D neuronio : neuronios) {
			neuronio.atualizaPesos(max, min, nPos, nNeg);
		}
	}
	
	public void zeraGradientes() {
		this.zeraGradientesBias();
		this.zeraGradientesPesos();
	}
	
	private void zeraGradientesPesos() {
		for(Neuronio1D neuronio : neuronios) {
			neuronio.zeraGradientes();
		}
	}
	
	private void zeraGradientesBias() {
		for(Neuronio1D neuronio : neuronios) {
			neuronio.setGradienteBias(0.0);
		}
	}
	
	public void print() {
		System.out.println("Pesos para camada 1-D "+posicaoCamada);
		for(int i = 0; i < neuronios.length; i++) {
			System.out.println("Pesos para neuronio "+i+" | Bias = "+neuronios[i].getPesoBias());
			for(int j = 0; j < neuronios[i].getPesos().length; j++) {
				System.out.print(neuronios[i].getPesos()[j]+"	");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void zeraMemoria() {
		for (Neuronio1D neuronio : neuronios) {
			neuronio.zeraMemoria();
		}
	}
	
}
