package configs;
import rede.SPNNPesosConfig;
import util.banco.bancoGeral.TipoProblema;
import util.otimizacao.io.Config;
import util.redeneural.base.FuncaoAtivacao;


public class SPNNConfig extends Config{

	private int[] qtNeuroniosPorCamada;
	private int qtInicialPontos;
	private boolean quadInscrito;
	private FuncaoAtivacao funcaoUltimaCamada;
	private FuncaoAtivacao funcaoDemaisCamadas;
	private TipoProblema tipoProblema;
	private SPNNPesosConfig pesosConfig;
	private int randPesos, randEstrutura;
	private boolean normaliza;
	private int qtRedesComite;

	private boolean camadaSaida, crossEntropy;

	boolean ativaInibicaoLateral,normalizaSaidaInibitoria;
	double pesoInibitorio,raioInibitorio;
	
	
	public SPNNConfig(String fileTeste, String file, boolean ehTeste, int qtInicialPontos,int[] qtNeuroniosPorCamada,
			boolean quadInscrito,FuncaoAtivacao funcaoUltimaCamada, FuncaoAtivacao funcaoDemaisCamadas,
			TipoProblema tipoProblema, SPNNPesosConfig pesosConfig, int randPesos, int randEstrutura, boolean normaliza,
			int qtRedesComite, boolean camadaSaida, boolean crossEntropy, 
			boolean ativaInibicaoLateral, boolean normalizaSaidaInibitoria, double pesoInibitorio, double raioInibitorio) {
		super(fileTeste, file, ehTeste);
		this.qtNeuroniosPorCamada = qtNeuroniosPorCamada;
		this.qtInicialPontos = qtInicialPontos;
		this.quadInscrito = quadInscrito;
		this.funcaoUltimaCamada = funcaoUltimaCamada;
		this.funcaoDemaisCamadas = funcaoDemaisCamadas;
		this.tipoProblema = tipoProblema;
		this.pesosConfig = pesosConfig;
		this.randPesos = randPesos;
		this.randEstrutura = randEstrutura;
		this.normaliza = normaliza;
		this.qtRedesComite = qtRedesComite;
		this.camadaSaida = camadaSaida;
		this.crossEntropy = crossEntropy;

		this.ativaInibicaoLateral = ativaInibicaoLateral;
		this.normalizaSaidaInibitoria = normalizaSaidaInibitoria;
		this.pesoInibitorio = pesoInibitorio;
		this.raioInibitorio = raioInibitorio;
	}


	@Override
	public String toStringConfig() {
		String separador = "\r\n";
		
		String retorno = "SPNN"+separador+"======= CONFIGURAÇÕES DA OTIMIZAÇÃO ========" +separador;
		
		retorno += "quantidadeSimulacoes="+Constantes.OTIMIZACAO_QUANTIDADE_SIMULACOES + separador;
	
		retorno += "Base=" + Constantes.BANCO;
		
		retorno += " (Treino = "+toString(Constantes.treino)+", Validação = "+toString(Constantes.validacao)+", Teste = "+toString(Constantes.teste)+")"+separador;
		
		retorno += "Split Treino = "+Constantes.splitTreino+" , SplitTeste = "+Constantes.splitTeste+separador;

		retorno += "Quantidade de imagens treino por época = "+ Constantes.qtImagensTreinoPorEpoca + separador;
		
		retorno += "Normalização=" + Constantes.NORMALIZACAO_IMAGEM_INICIO + " - " +Constantes.NORMALIZACAO_IMAGEM_FIM + separador;
		
		retorno += "TipoProblema=" + this.tipoProblema + separador;
		
		retorno += "Qt redes Comitê="+this.qtRedesComite + separador;
		
		retorno += "======= CONFIGURAÇÕES DA REDE NEURAL ========"+separador;
		
		retorno += "qtInicialPontos=" + qtInicialPontos + separador;
		
		if(camadaSaida){
			
			int[] vetor = new int[qtNeuroniosPorCamada.length+1];
			for (int i = 0; i < vetor.length-1; i++) {
				vetor[i] = qtNeuroniosPorCamada[i];
			}
			vetor[vetor.length-1] = 2;			
			
			retorno += "qtd neurônios por camada=" + toString(vetor) + separador;
		}else{
			retorno += "qtd neurônios por camada=" + toString(qtNeuroniosPorCamada) + separador;
		}
		
		retorno += "ativa inibição lateral=" + ativaInibicaoLateral +  separador;
		
		if(ativaInibicaoLateral){
			retorno += "normaliza inibição lateral="+normalizaSaidaInibitoria + separador;
			retorno += "peso inibitório="+pesoInibitorio + separador;
			retorno += "raio inibitório="+raioInibitorio + separador;
		}
		
		retorno += "cross entropy=" + crossEntropy + separador; 
				
		retorno += "quadrado inscrito = "+ this.quadInscrito + separador;
		
		retorno += "função de Ativação Ultima Camada=" + this.funcaoUltimaCamada + separador;
		
		retorno += "função de Ativação Demais Camadas=" + this.funcaoDemaisCamadas + separador;
		
		retorno += "Quantidade de épocas=" + Constantes.QUANTIDADE_EPOCAS + separador;
		
		retorno += "Inicialização de Pesos = \n" + this.pesosConfig.toString() + separador;
		
		retorno += "RandPesos=" +this.randPesos + separador;
		
		retorno += "RandEstrutura (kmeans e gerador)=" +this.randEstrutura + separador;
		
		retorno += "Normaliza Saída Campo Receptivo ="+this.normaliza + separador;
		
		return retorno;
	}

	public String toString(int vetor){
		return toString(new int[]{vetor});
	}
	
	public String toString(int[] vetor){
		String s = null;
		if(vetor != null)
		{
			s = "[";
			for (int i : vetor) {
				s += i+" ";
			}
			s += "]";
		}
		return s;
	}
	
	
	
}
