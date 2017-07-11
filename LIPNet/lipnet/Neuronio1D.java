package lipnet;
import java.util.HashMap;
import java.util.TreeMap;


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
	
	public Neuronio1D clone(int tamanho) {
		Neuronio1D retorno = new Neuronio1D(posicao,camada,tamanho);
		retorno.pesos = new double[tamanho];
		retorno.setPesoBias(this.pesoBias);
		
		for (int i = 0; i < this.pesos.length; i++) {
			retorno.pesos[i] = this.pesos[i];
		}
		
		return retorno;
	}
	
	public Neuronio1D(int posicao, int camada, int tamanho) {
		this.posicao = posicao;
		this.camada = camada;
		this.pesos = new double[tamanho];
		this.gradientes = new double[tamanho];
		this.gradientesAnterior = new double[tamanho];
		this.deltaAnterior = new double[tamanho];
		this.atualizaAnterior = new double[tamanho];
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
		//tangente hiperbolica
		this.saida = Math.tanh(somatorio);
		
		// sigmoide logistica
		//this.saida = 1/(1+Math.pow(Math.E, -1*somatorio));
	}
	
	public void funcaoAtivacaoDerivadaSaida(Double somatorio, int quantidadeEntradas,int n, Double erro) {
		// tangente hiperbolica
		Double funcao = 1 - Math.pow(Math.tanh(somatorio),2);
		
		// sigmoide logistica
		//double funcao = 1/(1+Math.pow(Math.E, -somatorio))*(1-1/(1+Math.pow(Math.E, -somatorio)));
		
		this.sensibilidade = erro*funcao;
		if(Double.isNaN(sensibilidade)) {
			sensibilidade = 0.0;
		}
	}
	
	public void funcaoAtivacaoDerivadaOculta(Double somatorio, Double somatorioErro) {
		// tangente hiperbolica
				Double funcao = 1 - Math.pow(Math.tanh(somatorio),2);
				
		// sigmoide logistica
				//double funcao = 1/(1+Math.pow(Math.E, -somatorio))*(1-1/(1+Math.pow(Math.E, -somatorio)));
				
		this.sensibilidade = somatorioErro*funcao;
		if(Double.isNaN(sensibilidade)) {
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
	
}
