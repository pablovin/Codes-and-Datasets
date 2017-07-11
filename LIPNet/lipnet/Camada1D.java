package lipnet;
import java.util.HashMap;

public class Camada1D {

	private Neuronio1D[] neuronios;

	private int posicaoCamada;
	
	private int tamanho;

	public Camada1D(int size, int posicaoCamada, int tamanho) {
		this.neuronios = new Neuronio1D[size];
		this.posicaoCamada = posicaoCamada;
		for (int i = 0; i < size; i++) {
			neuronios[i] = new Neuronio1D(i, posicaoCamada, tamanho);
		}
		this.tamanho = tamanho;
	}
	
	public Camada1D clone() {
		Camada1D retorno = new Camada1D(this.neuronios.length,this.posicaoCamada,this.tamanho);
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
	
	public void atualizaSaidaNeuronios(Neuronio1D[] entradas, boolean crossEntropy) {
		double acumuladaSaida = 0;
		for (int i = 0; i < neuronios.length; i++) {
			Neuronio1D neuronio = neuronios[i];
			double somatorio = neuronio.getPesoBias();
			for(int j = 0; j < neuronio.getPesos().length; j++) {
				somatorio += entradas[j].getSaida()
						* neuronio.getPesos()[j];
			}
			neuronio.funcaoAtivacao(somatorio);
			acumuladaSaida += Math.exp(neuronio.getSaida());
		}
		if(crossEntropy) {
			for (int i = 0; i < neuronios.length; i++) {
				Neuronio1D neuronio = neuronios[i];
				neuronio.setSaida(Math.exp(neuronio.getSaida())/acumuladaSaida);
			}
		}
	}
	
	public void atualizaSensibilidadeSaida(int quantidadeEntradas, double[] erro, Neuronio1D[] entradas) {
		for (int i = 0; i < neuronios.length; i++) {
			Neuronio1D neuronio = neuronios[i];
			double somatorio = neuronio.getPesoBias();
			for(int j = 0; j < neuronio.getPesos().length; j++) {
				somatorio += entradas[j].getSaida()
						* neuronio.getPesos()[j];
			}
			//neuronio.funcaoAtivacaoDerivadaSaida(quantidadeEntradas,erro.length, erro[i], somatorio);
			neuronio.funcaoAtivacaoDerivadaSaida(somatorio,quantidadeEntradas,erro.length, erro[i]);
		}
	}
	
	public void atualizaSensibilidadesOcultas(Neuronio1D[] entradas, Neuronio1D[] saidas) {
		for (int i = 0; i < neuronios.length; i++) {
			Neuronio1D neuronio = neuronios[i];
			double somatorio = neuronio.getPesoBias();
			for(int j = 0; j < neuronio.getPesos().length; j++) {
				somatorio += entradas[j].getSaida()
						* neuronio.getPesos()[j];
			}
			//neuronio.funcaoAtivacaoDerivadaOculta(calculaErroPonderado(saidas,neuronio), somatorio);
			neuronio.funcaoAtivacaoDerivadaOculta(somatorio, calculaErroPonderado(saidas,neuronio));
		}
	}
	
	private double calculaErroPonderado(Neuronio1D[] saidas, Neuronio1D neuronio) {
		double retorno = 0;
		for(int i = 0; i < saidas.length; i++) {
		//for (Neuronio1D neuronio1D : saidas) {
			Double tmp = saidas[i].getPesos()[neuronio.getPosicao()];
			if(tmp != null) {
				retorno += tmp*saidas[i].getSensibilidade();
			}
		}
		return retorno;
	}

	public void calculaGradientes(Neuronio1D[] entrada) {
		for (Neuronio1D neuronio : neuronios) {
			for (Neuronio1D neuronio2 : entrada) {
				double ajuste = neuronio.getSensibilidade()*neuronio2.getSaida();
				ajuste += neuronio.getGradientes()[neuronio2.getPosicao()];
				neuronio.getGradientes()[neuronio2.getPosicao()] = ajuste;
			}
			neuronio.setGradienteBias(neuronio.getGradienteBias()+(neuronio.getSensibilidade()));
		}
	}
	
	
	public void atualizaPesos(double max, double min, double nPos, double nNeg) {
		for (Neuronio1D neuronio : neuronios) {
			//Atualizando Bias
			if(true){
				double sinal = Math.signum(neuronio.getGradienteBias()*neuronio.getGradienteAnteriorBias());
				double sinalGrad = Math.signum(neuronio.getGradienteBias());
				if(sinal > 0) {
					double delta = Math.min(neuronio.getDeltaAnteriorBias()*nPos, max);
					neuronio.setDeltaAnteriorBias(delta);
					double atualiza = (-sinalGrad)*delta;
					neuronio.setPesoBias(neuronio.getPesoBias()+atualiza);
					neuronio.setGradienteAnteriorBias(neuronio.getGradienteBias());
					neuronio.setAtualizaAnteriorBias(atualiza);
				} else if(sinal < 0) {
					double delta = Math.max(neuronio.getDeltaAnteriorBias()*nNeg, min);
					neuronio.setDeltaAnteriorBias(delta);
					//neuronio.setPesoBias(neuronio.getPesoBias()-neuronio.getAtualizaAnteriorBias());
					neuronio.setGradienteAnteriorBias(0.0);
				} else {
					double delta = neuronio.getDeltaAnteriorBias();
					double atualiza = (-sinalGrad)*delta;
					neuronio.setPesoBias(neuronio.getPesoBias()+atualiza);
					neuronio.setGradienteAnteriorBias(neuronio.getGradienteBias());
					neuronio.setAtualizaAnteriorBias(atualiza);
				}
			}
			//Atualizando pesos
			for(int i = 0; i < neuronio.getGradientes().length; i++) {
				//neuronio.getPesos()[i] = neuronio.getPesos()[i]+(taxaAprendizagem*neuronio.getGradientes()[i]/quantidade);
				double sinal = Math.signum(neuronio.getGradientes()[i]*neuronio.getGradientesAnterior()[i]);
				double sinalGrad = Math.signum(neuronio.getGradientes()[i]);
				if(sinal > 0) {
					double delta = Math.min(neuronio.getDeltaAnterior()[i]*nPos, max);
					neuronio.getDeltaAnterior()[i] = delta;
					double atualiza = (-sinalGrad)*delta;
					neuronio.getPesos()[i] = neuronio.getPesos()[i]+atualiza;
					neuronio.getGradientesAnterior()[i] = neuronio.getGradientes()[i];
					neuronio.getAtualizaAnterior()[i] = atualiza;
				} else if(sinal < 0) {
					double delta = Math.max(neuronio.getDeltaAnterior()[i]*nNeg, min);
					neuronio.getDeltaAnterior()[i] = delta;
					//neuronio.getPesos()[i] = neuronio.getPesos()[i]-neuronio.getAtualizaAnterior()[i];
					neuronio.getGradientesAnterior()[i] = 0.0;
					neuronio.getAtualizaAnterior()[i] = 0.0;
				} else {
					double delta = neuronio.getDeltaAnterior()[i];
					double atualiza = (-sinalGrad)*delta;
					neuronio.getPesos()[i] = neuronio.getPesos()[i]+atualiza;
					neuronio.getGradientesAnterior()[i] = neuronio.getGradientes()[i];
					neuronio.getAtualizaAnterior()[i] = atualiza;
				}	
			}
			//neuronio.setPesos(novosPesos);
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
	
	public void limpaMemoria() {
		for (Neuronio1D neuronio : neuronios) {
			neuronio.limpaMemoria();
		}
	}
	
}
