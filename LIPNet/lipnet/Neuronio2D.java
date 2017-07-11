package lipnet;

public class Neuronio2D {

	private int posX;
	
	private int posY;
	
	private int camada;
	
	private Double saida = 0.0;
	
	private Double pesoBias = 0.0;
	
	private Double gradienteBias = 0.0;
	
	private Double pesoAssociado = 0.0;
	
	private Double sensibilidade = 0.0;
	
	private Double gradiente = 0.0;
	
	private Double deltaAnterior = 0.1;
	
	private Double gradienteAnterior = 0.0;

	private Double deltaAnteriorBias = 0.1;
	
	private Double gradienteAnteriorBias = 0.0;
	
	private Double atualizaAnterior = 0.0;
	
	private Double atualizaAnteriorBias = 0.0;
	
	boolean primeiraCamada = false;
	
	private Double somatorioEntrada;
	
	private Double somatorioErro;
	
	public String print() {
		String retorno = posX+";"+posY+";"+camada+";"+pesoBias+";"+pesoAssociado;
		return retorno;
	}
	
	public Neuronio2D(int posX, int posY, int camada, boolean primeiraCamada) {
		this.posY = posY;
		this.posX = posX;
		this.camada = camada;
		this.primeiraCamada = primeiraCamada;
	}
	
	public Neuronio2D clone() {
		Neuronio2D retorno = new Neuronio2D(posX,posY,camada,primeiraCamada);
		retorno.setPesoBias(pesoBias);
		retorno.setPesoAssociado(pesoAssociado);
		return retorno;
	}
	
	public void funcaoAtivacao(Double somatorio) {
		//tangente hiperbolica
		this.saida = Math.tanh(somatorio);
		
		// sigmoide logistica
		//this.saida = 1/(1+Math.pow(Math.E, -1*somatorio));
		
		this.somatorioEntrada = somatorio;
	}
	
	public void funcaoAtivacaoDerivadaSaida(Double somatorio, Double somatorioErro) {
		// tangente hiperbolica
			Double funcao = 1 - Math.pow(Math.tanh(somatorio),2);
				
		// sigmoide logistica
			//double funcao = 1/(1+Math.pow(Math.E, -somatorio))*(1-1/(1+Math.pow(Math.E, -somatorio)));
		
		this.sensibilidade = somatorioErro*funcao;
		if(Double.isNaN(sensibilidade)) {
			sensibilidade = 0.0;
		}
	}
	
	public void funcaoAtivacaoDerivada(Double somatorio, Double somatorioErro) {
		// tangente hiperbolica
			Double funcao = 1 - Math.pow(Math.tanh(somatorio),2);
				
		// sigmoide logistica
			//double funcao = 1/(1+Math.pow(Math.E, -somatorio))*(1-1/(1+Math.pow(Math.E, -somatorio)));
				
		this.sensibilidade = somatorioErro*funcao;
		if(Double.isNaN(sensibilidade)) {
			sensibilidade = 0.0;
		}
	}
	
	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public Double getPesoAssociado() {
		return pesoAssociado;
	}

	public void setPesoAssociado(Double pesoAssociado) {
		this.pesoAssociado = pesoAssociado;
	}

	public Double getSaida() {
		return saida;
	}

	public void setSaida(Double saida) {
		this.saida = saida;
	}

	public int getCamada() {
		return camada;
	}

	public void setCamada(int camada) {
		this.camada = camada;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + camada;
		result = PRIME * result + posX;
		result = PRIME * result + posY;
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
		final Neuronio2D other = (Neuronio2D) obj;
		if (camada != other.camada)
			return false;
		if (posX != other.posX)
			return false;
		if (posY != other.posY)
			return false;
		return true;
	}

	public Double getGradiente() {
		return gradiente;
	}

	public void setGradiente(Double gradiente) {
		this.gradiente = gradiente;
	}

	public Double getSensibilidade() {
		return sensibilidade;
	}

	public void setSensibilidade(Double sensibilidade) {
		this.sensibilidade = sensibilidade;
	}

	public Double getPesoBias() {
		if(primeiraCamada) {
			pesoBias = 0.0;
			return 0.0;
		} else {
			return pesoBias;
		}
	}

	public void setPesoBias(Double pesoBias) {
		this.pesoBias = pesoBias;
	}

	public Double getGradienteBias() {
		return gradienteBias;
	}

	public void setGradienteBias(Double gradienteBias) {
		this.gradienteBias = gradienteBias;
	}

	public Double getDeltaAnterior() {
		return deltaAnterior;
	}

	public void setDeltaAnterior(Double deltaAnterior) {
		this.deltaAnterior = deltaAnterior;
	}

	public Double getGradienteAnterior() {
		return gradienteAnterior;
	}

	public void setGradienteAnterior(Double gradienteAnterior) {
		this.gradienteAnterior = gradienteAnterior;
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

	public Double getAtualizaAnterior() {
		return atualizaAnterior;
	}

	public void setAtualizaAnterior(Double atualizaAnterior) {
		this.atualizaAnterior = atualizaAnterior;
	}

	public Double getAtualizaAnteriorBias() {
		return atualizaAnteriorBias;
	}

	public void setAtualizaAnteriorBias(Double atualizaAnteriorBias) {
		this.atualizaAnteriorBias = atualizaAnteriorBias;
	}

	public boolean isPrimeiraCamada() {
		return primeiraCamada;
	}

	public void setPrimeiraCamada(boolean primeiraCamada) {
		this.primeiraCamada = primeiraCamada;
	}

	public Double getSomatorioEntrada() {
		return somatorioEntrada;
	}

	public void setSomatorioEntrada(Double somatorioEntrada) {
		this.somatorioEntrada = somatorioEntrada;
	}

	public Double getSomatorioErro() {
		return somatorioErro;
	}

	public void setSomatorioErro(Double somatorioErro) {
		this.somatorioErro = somatorioErro;
	}

	public static void main(String[] args) {
		double somatorio = 0;
		 System.out.println(1/(1+Math.pow(Math.E, -1*somatorio)));
		 System.out.println(Math.tanh(somatorio));
		System.out.println(1/(1+Math.pow(Math.E, -somatorio))*(1-1/(1+Math.pow(Math.E, -somatorio))));
	}
	
}
