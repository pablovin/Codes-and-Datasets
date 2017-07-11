package rede;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import util.redeneural.base.FuncaoAtivacao;


public class Neuronio2D {
	
	private List<Neuronio2D> campoReceptivo;
	
	private List<Neuronio2D> campoDeInfluencia;
	
	private HashMap<Neuronio2D, Integer> campoInibitorio;
	
	private double posX;
	
	private double posY;
	
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
	
	private boolean primeiraCamada = false;
	
	private Double somatorioEntrada;
	
	private Double somatorioErro;
	
	private FuncaoAtivacao funcaoAtivacao;
	
	private boolean normaliza;
	
	public String print() {
		String retorno = posX+";"+posY+";"+camada+";"+pesoBias+";"+pesoAssociado;
		return retorno;
	}
	
	public Neuronio2D(double posX, double posY, int camada, List<Neuronio2D> campoReceptivo, boolean primeiraCamada, FuncaoAtivacao funcao, boolean normaliza) {
		this.posY = posY;
		this.posX = posX;
		this.camada = camada;
		this.campoReceptivo = campoReceptivo;
		this.campoDeInfluencia = new ArrayList<Neuronio2D>();
		//atualiza campo de influencia
		if(campoReceptivo != null){
			for (Neuronio2D neuronio : campoReceptivo) {
				neuronio.adicionaNeuronioDeInfluencia(this);
			}
		}
		this.primeiraCamada = primeiraCamada;
		this.funcaoAtivacao = funcao;
		this.normaliza = normaliza;
	}
	
	public Neuronio2D clone() {
		Neuronio2D retorno = new Neuronio2D(posX,posY,camada, campoReceptivo, primeiraCamada, funcaoAtivacao, normaliza);
		retorno.setPesoBias(pesoBias);
		retorno.setPesoAssociado(pesoAssociado);
		return retorno;
	}
	
	public void funcaoAtivacao(Double somatorio) {
		
		this.saida = funcaoAtivacao.funcaoAtivacao(somatorio);		
		this.somatorioEntrada = somatorio;
	}
	
	public void funcaoAtivacaoDerivada(Double somatorio, Double somatorioErro) {
		
		double funcao = funcaoAtivacao.funcaoAtivacaoDerivada(somatorio);
		
		this.sensibilidade = somatorioErro*funcao;
		if(Double.isNaN(sensibilidade)) {
			System.out.println("Sensibilidade NAN: "+somatorioErro+" , "+funcao+" , "+somatorio);
			sensibilidade = 0.0;
		}
	}
	
	
	public double getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public double getPosY() {
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
		double result = 1;
		result = PRIME * result + camada;
		result = PRIME * result + posX;
		result = PRIME * result + posY;
		return (int)result;
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

	public List<Neuronio2D> getCampoReceptivo() {
		return campoReceptivo;
	}
	
	public FuncaoAtivacao getFuncaoAtivacao() {
		return funcaoAtivacao;
	}

	private int getTamanhoCampoDeInfluencia(){
		return this.campoDeInfluencia.size();
	}
	
	private Neuronio2D getCampoDeInfluencia(int i){
		return this.campoDeInfluencia.get(i);
	}
	
	private void adicionaNeuronioDeInfluencia(Neuronio2D n){
		this.campoDeInfluencia.add(n);
	}

	public static void main(String[] args) {
		double somatorio = -0.05;
		 System.out.println(1/(1+Math.pow(Math.E, -1*somatorio)));
		 System.out.println(Math.tanh(somatorio));
	}
	
	double calculaSomatorioNeuronio() {
		
		double somatorio = 0;
		
		for (Neuronio2D neuronioCampoReceptivo : this.getCampoReceptivo()) {			
			somatorio += neuronioCampoReceptivo.getSaida() * neuronioCampoReceptivo.getPesoAssociado();	
		}		
		
		if(normaliza && this.getCampoReceptivo().size() > 0)
			return somatorio/this.getCampoReceptivo().size();
		
		return somatorio;
	}
	
	double calculaSomatorioInibitorioNeuronio() {
		
		double somatorio = 0;
		
		if(campoInibitorio != null && campoInibitorio.size() > 0){
			/*
			List<Neuronio2D> neus = new ArrayList<Neuronio2D>(campoInibitorio.keySet());
			Collections.sort(neus, new Comparator<Neuronio2D>() {

				@Override
				public int compare(Neuronio2D o1, Neuronio2D o2) {
					
					int a= Double.compare(o1.getPosX(), o2.getPosX()); 
					if(a == 0){
						return Double.compare(o1.getPosY(), o2.getPosY()); 
					}
					else 
						return a;
				}
			});			
			*/
			for (Neuronio2D neuInib: campoInibitorio.keySet()) {
				
				double s = neuInib.getSaida() * SPNN.pesoInibitorio;
				
				if(SPNN.normalizaSaidaInibitoria){
					s = s*((double)(campoInibitorio.get(neuInib))/neuInib.getCampoReceptivo().size());
				}
				
				somatorio += s;
				
			}
		}
		
		return somatorio;
	}
	
	
	double calculaErroPonderado() {
		
		double retorno = 0;
		int tamanho = this.getTamanhoCampoDeInfluencia();
		
		for (int i = 0; i < tamanho; i++) {
			Neuronio2D neuronio2d = this.getCampoDeInfluencia(i);
			retorno += neuronio2d.getSensibilidade();
		}
		
		if(normaliza && tamanho > 0){
			return retorno/tamanho;
		}
		return retorno;
	}
	
	public double calculaErroInibitorio() {
		
		double somatorio = 0;
		
		if(campoInibitorio != null && campoInibitorio.size() > 0){
			for (Neuronio2D neuInib: campoInibitorio.keySet()) {
				
				double s = neuInib.getSomatorioErro()*SPNN.pesoInibitorio;		
				
				if(SPNN.normalizaSaidaInibitoria){
					s = s*((double)(campoInibitorio.get(neuInib))/neuInib.getCampoReceptivo().size());
				}
				somatorio += s;
				
			}
		}
		return somatorio;
	}
	
	
	public void atualizaPesos(double max, double min, double nPos, double nNeg) {
		
		double sinal = Math.signum(this.getGradiente() * this.getGradienteAnterior());
		double sinalGrad = Math.signum(this.getGradiente());
		
		if (sinal > 0) {
			
			double delta = Math.min(this.getDeltaAnterior() * nPos,	max);
			this.setDeltaAnterior(delta);
			double atualiza = (-sinalGrad) * delta;
			this.setPesoAssociado(this.getPesoAssociado() + atualiza);
			this.setGradienteAnterior(this.getGradiente());
			
		} else if (sinal < 0) {
			
			double delta = Math.max(this.getDeltaAnterior() * nNeg,	min);
			this.setDeltaAnterior(delta);
			this.setGradienteAnterior(0.0);
			
		} else {
			
			double delta = this.getDeltaAnterior();
			double atualiza = (-sinalGrad) * delta;
			this.setPesoAssociado(this.getPesoAssociado() + atualiza);
			this.setGradienteAnterior(this.getGradiente());
		}
					
	}
	
	public void atualizaBias(double max, double min, double nPos, double nNeg) {
		double sinal = Math.signum(this.getGradienteBias() * this.getGradienteAnteriorBias());
		double sinalGrad = Math.signum(this.getGradienteBias());
		
		if (sinal > 0) {
			
			double delta = Math.min(this.getDeltaAnteriorBias()	* nPos, max);
			this.setDeltaAnteriorBias(delta);
			double atualiza = (-sinalGrad) * delta;
			this.setPesoBias(this.getPesoBias() + atualiza);
			this.setGradienteAnteriorBias(this.getGradienteBias());
			
		} else if (sinal < 0) {
			
			double delta = Math.max(this.getDeltaAnteriorBias()	* nNeg, min);
			this.setDeltaAnteriorBias(delta);
			this.setGradienteAnteriorBias(0.0);
			
		} else {
			
			double delta = this.getDeltaAnteriorBias();
			double atualiza = (-sinalGrad) * delta;
			this.setPesoBias(this.getPesoBias() + atualiza);
			this.setGradienteAnteriorBias(this.getGradienteBias());
		}
		
	
	}
	
	public String toStringRede(){
		String ret = toStringPosicao()+"{"+pesoAssociado+","+pesoBias+"}";
		if(campoReceptivo != null && campoReceptivo.size() > 0){
			ret += "[";
			for (Neuronio2D neuronio : campoReceptivo) {
				ret += neuronio.toStringRede();
			}
			ret += "]";
		}
		ret += ";";
		return ret;
	}
	
	
	public String toStringPosicao(){
		String ret = "("+posX+","+posY+")";
		return ret;		
	}
	
	public String toStringCampos(){
		String ret = "";
		if(campoReceptivo != null && campoReceptivo.size() > 0){
			ret += "[";
			for (Neuronio2D neuronio : campoReceptivo) {
				ret += neuronio.toStringPosicao()+";";
			}
			ret += "]";
		}
		ret += ";";
		return ret;
		
	}

	public void zeraMemoria() {
		
		setDeltaAnteriorBias(0.1);
		setGradienteAnteriorBias(0.0);
		
		setDeltaAnterior(0.1);
		setGradienteAnterior(0.0);
		
		setPesoBias(0.0);
		setPesoAssociado(0.0);
		
		setSaida(0.0);
		setSensibilidade(0.0);
		
	}

	
	
	
	public HashMap<Neuronio2D, Integer> getCampoInibitorio() {
		return campoInibitorio;
	}

	public void setCampoInibitorio(HashMap<Neuronio2D, Integer> campoInibitorio) {
		this.campoInibitorio = campoInibitorio;
	}

	
}
