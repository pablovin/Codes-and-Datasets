package rede;
import java.util.List;

public class Camada2D {

	private List<Neuronio2D> neuronios;
	private int camada;
	private boolean primeiraCamada;
	
	public Camada2D(List<Neuronio2D> neuronios, int camada, boolean primeiraCamada) {
		super();
		this.neuronios = neuronios;
		this.camada = camada;
		this.primeiraCamada = primeiraCamada;
	}

	public List<Neuronio2D> getNeuronios() {
		return neuronios;
	}

	public void atualizaSaidaNeuronios() {
		
		//Passo 1: Excitatório
		for (Neuronio2D neuronio : neuronios) {
			
			double somatorio = neuronio.getPesoBias() + neuronio.calculaSomatorioNeuronio();
			neuronio.funcaoAtivacao(somatorio);
		}
		
		
		if(SPNN.ativaInibicaoLateral){
			// Passo 2: Inibitório
			for (Neuronio2D neuronio : neuronios) {
				double somatorio = 0;
				somatorio = neuronio.calculaSomatorioInibitorioNeuronio();			
				neuronio.setSomatorioEntrada(neuronio.getSomatorioEntrada()-somatorio);
			}
	
			for (Neuronio2D neuronio : neuronios) {
				neuronio.funcaoAtivacao(neuronio.getSomatorioEntrada());
			}
		
		}
		
	}
	
	public void atualizaSensibilidadeSaida(double[] erro) {		
		
		for (int i = 0; i < neuronios.size(); i++) {
			Neuronio2D neuronio = neuronios.get(i);
			neuronio.funcaoAtivacaoDerivada(neuronio.getSomatorioEntrada(), erro[i]);
		}
		
		//Desnecessário Inibição Lateral pois sensibilidade de transição é para apenas 1 neurônio 2D, logo não há inibição
	}
	
	public void atualizaSensibilidadesDeTransicao(Neuronio1D[] saidas) {		
		
		int pos = 0;
		for (Neuronio2D neuronio: neuronios){
			
			double somatorioErro = 0;
			for (Neuronio1D neuronio1D : saidas) {
				somatorioErro += neuronio1D.getSensibilidade() * neuronio1D.getPesos()[pos];
			}
			
			neuronio.setSomatorioErro(somatorioErro);
			pos++;
		}
		
		//Desnecessário pois sensibilidade de transição é para apenas 1 neurônio 2D, logo não há inibição
		if(SPNN.ativaInibicaoLateral){
			for (Neuronio2D neuronio: neuronios){
				double somatorioErro = neuronio.calculaErroInibitorio();
				neuronio.setSomatorioErro(neuronio.getSomatorioErro() - somatorioErro);
			}
		}
		
		for (Neuronio2D neuronio: neuronios){
			
			neuronio.funcaoAtivacaoDerivada(neuronio.getSomatorioEntrada(), neuronio.getSomatorioErro());
		
		}
		
		
		
	}
	
	public void atualizaSensibilidadesOcultas() {
		
				
		for (Neuronio2D neuronio: neuronios){
			neuronio.setSomatorioErro(neuronio.calculaErroPonderado());
		}
		
		if(SPNN.ativaInibicaoLateral){		
			for (Neuronio2D neuronio: neuronios){
				double somatorioErro = neuronio.calculaErroInibitorio();				
				neuronio.setSomatorioErro(neuronio.getSomatorioErro()-somatorioErro);
			}	
		}
		
		for (Neuronio2D neuronio: neuronios){
				neuronio.funcaoAtivacaoDerivada(neuronio.getSomatorioEntrada(), neuronio.getSomatorioErro());
			
		}
		
		
		
	}
	

	
	
	public void calculaGradientesPesos() {
		for (Neuronio2D neuronio: neuronios) {
			double somatorio = neuronio.calculaErroPonderado();
			neuronio.setGradiente(neuronio.getGradiente() + (neuronio.getSaida() * somatorio));
		}
	}

	public void calculaGradientesBias() {
		for (Neuronio2D neuronio: neuronios) {
			neuronio.setGradienteBias(neuronio.getGradienteBias() + neuronio.getSensibilidade());
		}
	}
	
	
	public void atualizaPesos(double max, double min, double nPos, double nNeg) {
		// System.out.println("Peso-Camada 2-D "+this.getPosicaoCamada());
		for (Neuronio2D neuronio: neuronios) {
			neuronio.atualizaPesos(max, min, nPos, nNeg);
		}
	}
	
	public void atualizaBias(double max, double min, double nPos, double nNeg) {
		// System.out.println("Bias-Camada 2-D "+this.getPosicaoCamada());
		for (Neuronio2D neuronio: neuronios) {
			neuronio.atualizaBias(max, min, nPos, nNeg);
		}
	}
	
	private void zeraGradienteBias() {
		for (Neuronio2D neuronio: neuronios) {
			neuronio.setGradienteBias(0.0);			
		}
	}

	private void zeraGradientePesos() {
		for (Neuronio2D neuronio: neuronios) {
			neuronio.setGradiente(0.0);
		}
	}

	public void zeraGradientes() {
		this.zeraGradienteBias();
		this.zeraGradientePesos();
	}

	public int getCamada() {
		return camada;
	}

	public void zeraMemoria() {
		for (Neuronio2D neuronio : neuronios) {
			neuronio.zeraMemoria();
		}
	}

	
	
}
