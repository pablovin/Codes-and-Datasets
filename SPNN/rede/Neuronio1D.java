package rede;

import util.redeneural.base.FuncaoAtivacao;

public class Neuronio1D {
	
	private int posicao;
	
	private int camada;
	
	private Double saida = 0.0;
	
	private Double gradienteBias = 0.0;
	
	private Double pesoBias = 0.0;
	
	private double[] pesos;
	
	private Double sensibilidade = 0.0;
	
	private double[] gradientes;
	
	private double[] gradientesAnterior;
	
	private double[] deltaAnterior;
	
	private double[] atualizaAnterior;
	
	private Double deltaAnteriorBias = 0.1;
	
	private Double gradienteAnteriorBias = 0.0;
	
	private Double atualizaAnteriorBias = 0.0;
	
	private FuncaoAtivacao funcaoAtivacao;

	public String print() {
		String retorno = posicao+";"+camada+";"+pesoBias+"!";
		for (int i = 0; i < pesos.length; i++) {
			if(i < pesos.length - 1) {
				retorno+=pesos[i]+";";
			} else {
				retorno+=pesos[i];
			}
		}
		return retorno;
	}
	
	public void limpaMemoria() {
		this.gradientes = null;
		this.sensibilidade = null;
		this.gradienteBias = null;
		this.gradienteAnteriorBias = null;
		this.gradientesAnterior = null;
		this.deltaAnterior = null;
		this.deltaAnteriorBias = null;
		this.atualizaAnterior = null;
	}
	
	public void zeraMemoria(){
		for (int i = 0; i < gradientesAnterior.length; i++) {
			gradientesAnterior[i] = 0.0;
			deltaAnterior[i] = 0.1;
			atualizaAnterior[i] = 0.0;
			pesos[i] = 0.0;
		}
		deltaAnteriorBias = 0.1;
		gradienteAnteriorBias = 0.0;
		atualizaAnteriorBias = 0.0;
		pesoBias = 0.0;	
		
		saida = 0.0;		
		sensibilidade = 0.0;
	}
	
	public Neuronio1D clone(int tamanho) {
		Neuronio1D retorno = new Neuronio1D(posicao,camada,tamanho, funcaoAtivacao);
		retorno.pesos = new double[tamanho];
		retorno.setPesoBias(this.pesoBias);
		
		for (int i = 0; i < this.pesos.length; i++) {
			retorno.pesos[i] = this.pesos[i];
		}
		
		return retorno;
	}
	
	public Neuronio1D(int posicao, int camada, int tamanho, FuncaoAtivacao funcao) {
		this.posicao = posicao;
		this.camada = camada;
		this.pesos = new double[tamanho];
		this.gradientes = new double[tamanho];
		this.gradientesAnterior = new double[tamanho];
		this.deltaAnterior = new double[tamanho];
		this.atualizaAnterior = new double[tamanho];
		this.funcaoAtivacao = funcao;
		for (int i = 0; i < gradientesAnterior.length; i++) {
			gradientesAnterior[i] = 0.0;
			deltaAnterior[i] = 0.1;
			atualizaAnterior[i] = 0.0;
		}
	}
	
	public double[] getGradientes() {
		return gradientes;
	}
	
	public void zeraGradientes() {
		for (int i = 0; i < gradientes.length; i++) {
			gradientes[i] = 0;
		}
	}
	
	public void setGradientes(double[] gradientes) {
		this.gradientes = gradientes;
	}

	public Double getSensibilidade() {
		return sensibilidade;
	}

	public void setSensibilidade(Double sensibilidade) {
		this.sensibilidade = sensibilidade;
	}
	
	public void funcaoAtivacao(Double somatorio) {
		this.saida = funcaoAtivacao.funcaoAtivacao(somatorio);	
	}
	
	
	public void funcaoAtivacaoDerivada(Double somatorio, Double somatorioErro) {
		double funcao = funcaoAtivacao.funcaoAtivacaoDerivada(somatorio);		
		this.sensibilidade = somatorioErro*funcao;
		if(Double.isNaN(sensibilidade)) {
			System.out.println("Sensibilidade NAN: "+somatorioErro+" , "+funcao+" , "+somatorio);
			sensibilidade = 0.0;
		}
	}
		
	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	public Double getSaida() {
		return saida;
	}

	public void setSaida(Double saida) {
		this.saida = saida;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + camada;
		result = PRIME * result + posicao;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Neuronio1D other = (Neuronio1D) obj;
		if (camada != other.camada)
			return false;
		if (posicao != other.posicao)
			return false;
		return true;
	}

	public int getCamada() {
		return camada;
	}

	public void setCamada(int camada) {
		this.camada = camada;
	}

	public double[] getPesos() {
		return pesos;
	}

	public void setPesos(double[] pesos) {
		this.pesos = pesos;
	}

	public Double getPesoBias() {
		return pesoBias;
		//return 0.0;
	}

	public void setPesoBias(Double pesoBias) {
		this.pesoBias = pesoBias;
	}

	public Double getGradienteBias() {
		return gradienteBias;
	}

	public void setGradienteBias(Double gradienteBias) {
		if(Double.isNaN(gradienteBias)) {
			System.out.println("Entra7");
		}
		this.gradienteBias = gradienteBias;
	}

	public double[] getDeltaAnterior() {
		return deltaAnterior;
	}

	public void setDeltaAnterior(double[] deltaAnterior) {
		this.deltaAnterior = deltaAnterior;
	}

	public Double getDeltaAnteriorBias() {
		return deltaAnteriorBias;
	}

	public void setDeltaAnteriorBias(Double deltaAnteriorBias) {
		this.deltaAnteriorBias = deltaAnteriorBias;
	}

	public Double getGradienteAnteriorBias() {
		return gradienteAnteriorBias;
	}

	public void setGradienteAnteriorBias(Double gradienteAnteriorBias) {
		this.gradienteAnteriorBias = gradienteAnteriorBias;
	}

	public double[] getGradientesAnterior() {
		return gradientesAnterior;
	}

	public void setGradientesAnterior(double[] gradientesAnterior) {
		this.gradientesAnterior = gradientesAnterior;
	}

	public double[] getAtualizaAnterior() {
		return atualizaAnterior;
	}

	public void setAtualizaAnterior(double[] atualizaAnterior) {
		this.atualizaAnterior = atualizaAnterior;
	}

	public Double getAtualizaAnteriorBias() {
		return atualizaAnteriorBias;
	}

	public void setAtualizaAnteriorBias(Double atualizaAnteriorBias) {
		this.atualizaAnteriorBias = atualizaAnteriorBias;
	}
	
	public void atualizaPesos(double max, double min, double nPos, double nNeg) {
		
		//Atualizando Bias
		if(true){
			double sinal = Math.signum(this.getGradienteBias()*this.getGradienteAnteriorBias());
			double sinalGrad = Math.signum(this.getGradienteBias());
			if(sinal > 0) {
				double delta = Math.min(this.getDeltaAnteriorBias()*nPos, max);
				this.setDeltaAnteriorBias(delta);
				double atualiza = (-sinalGrad)*delta;
				this.setPesoBias(this.getPesoBias()+atualiza);
				
				this.setGradienteAnteriorBias(this.getGradienteBias());
				this.setAtualizaAnteriorBias(atualiza);
			} else if(sinal < 0) {
				double delta = Math.max(this.getDeltaAnteriorBias()*nNeg, min);
				this.setDeltaAnteriorBias(delta);
				this.setGradienteAnteriorBias(0.0);
			} else {
				double delta = this.getDeltaAnteriorBias();
				double atualiza = (-sinalGrad)*delta;
				this.setPesoBias(this.getPesoBias()+atualiza);
				this.setGradienteAnteriorBias(this.getGradienteBias());
				this.setAtualizaAnteriorBias(atualiza);
			}
		}
		//Atualizando pesos
		for(int i = 0; i < this.getGradientes().length; i++) {
			//neuronio.getPesos()[i] = neuronio.getPesos()[i]+(taxaAprendizagem*neuronio.getGradientes()[i]/quantidade);
			double sinal = Math.signum(this.getGradientes()[i]*this.getGradientesAnterior()[i]);
			double sinalGrad = Math.signum(this.getGradientes()[i]);
			if(sinal > 0) {
				double delta = Math.min(this.getDeltaAnterior()[i]*nPos, max);
				this.getDeltaAnterior()[i] = delta;
				double atualiza = (-sinalGrad)*delta;
				this.getPesos()[i] = this.getPesos()[i]+atualiza;
				//System.out.println("1D "+neuronio.getPosicao()+"|"+i+" - Sinal positivo: ajustou com "+atualiza+" | novo peso = "+neuronio.getPesos()[i]);
				this.getGradientesAnterior()[i] = this.getGradientes()[i];
				this.getAtualizaAnterior()[i] = atualiza;
			} else if(sinal < 0) {
				double delta = Math.max(this.getDeltaAnterior()[i]*nNeg, min);
				this.getDeltaAnterior()[i] = delta;
				//neuronio.getPesos()[i] = neuronio.getPesos()[i]-neuronio.getAtualizaAnterior()[i];
				//System.out.println("1D "+neuronio.getPosicao()+"|"+i+" - Sinal negativo: ajustou com "+-neuronio.getAtualizaAnterior()[i]+" | novo peso = "+neuronio.getPesos()[i]);
				this.getGradientesAnterior()[i] = 0.0;
				this.getAtualizaAnterior()[i] = 0.0;
			} else {
				double delta = this.getDeltaAnterior()[i];
				double atualiza = (-sinalGrad)*delta;
				this.getPesos()[i] = this.getPesos()[i]+atualiza;
				//System.out.println("1D "+neuronio.getPosicao()+"|"+i+" - Sinal zero: ajustou com "+atualiza+" | novo peso = "+neuronio.getPesos()[i]);
				this.getGradientesAnterior()[i] = this.getGradientes()[i];
				this.getAtualizaAnterior()[i] = atualiza;
			}	
		}	
		
	}
	
	public static void main(String[] args) {
		double somatorio = 2;
		double d = Math.tanh(2/(1+Math.exp(-2*somatorio))-1);
		System.out.println(d);
		System.out.println(Math.tanh(somatorio));
	}

	public String toStringRede() {
		String ret = "{"+pesos[0]+","+pesoBias+"}";
		return ret;
	}
	
}
