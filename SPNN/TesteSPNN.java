import java.util.ArrayList;
import operacoes.MapaProbabilidade;
import rede.SPNN;
import rede.SPNNPesosConfig;
import rede.TreinaSPNN;
import util.ConstantesUtil;
import util.avaliacao.ResultadoAUC;
import util.banco.bancoGeral.BancoUtil;
import util.banco.bancoGeral.TipoProblema;
import util.banco.bancoGeral.base.BancoDeImagens;
import util.banco.bancoGeral.base.BancoDeImagens.TIPO_BANCO;
import util.redeneural.base.FuncaoAtivacao;
import configs.Constantes;
import configs.SPNNConfig;
import configs.SPNNEscritor;


public class TesteSPNN {


	static boolean registraValidTeste = true;
	
	public static void main(String[] args) {
		
			double media = 0;
		
			int[] qtNeuroniosPorCamada = Constantes.QUANTIDADE_NEURONIOS_POR_CAMADA;//qtNeurPorCamada[conf2];//
			int qtInicialMaxPontos = Constantes.QUANTIDADE_INICIAL_MAXIMA_DE_PONTOS;//qtInicialMaxPts[conf];//
			boolean quadInscrito = Constantes.QUADRADO_INSCRITO;//quadInscritos[conf];//
			FuncaoAtivacao funcaoUltimaCamada = Constantes.FUNCAO_ATIVACAO_ULTIMA_CAMADA;//funcoesUltimaCamada[conf];//
			FuncaoAtivacao funcaoDemaisCamadas = Constantes.FUNCAO_ATIVACAO_DEMAIS_CAMADAS;//funcoesDemaisCamadas[conf];// 
			TipoProblema tipo = Constantes.TIPO_PROBLEMA;//tipos[conf];
			
			int randPesos = Constantes.RAND_PESOS;
			int randEstrutura = Constantes.RAND_ESTRUTURA;//randEstruturas[conf];//
			
			boolean normaliza = Constantes.NORMALIZA_SAIDA_CAMPO_RECEPTIVO;
			
			boolean camadaSaida =  Constantes.CAMADA_SAIDA_ADICIONA;
			boolean crossEntropy =  Constantes.CAMADA_SAIDA_CROSS_ENTROPY;
			
			boolean ativaInibicaoLateral = Constantes.INIBICAO_LATERAL_ATIVA;
			boolean normalizaSaidaInibitoria = Constantes.INIBICAO_LATERAL_NORMALIZA;//normalizas[conf];//
			double pesoInibitorio = Constantes.INIBICAO_LATERAL_PESO;//Constantes.INIBICAO_LATERAL_PESO/(Math.pow(2*raios[conf]+1,2));//
			double raioInibitorio = Constantes.INIBICAO_LATERAL_RAIO;// raios[conf];//

			
			SPNNPesosConfig inicializacaoPesosConfig = Constantes.inicializacaoPesosConfig;
			
			SPNNConfig config = new SPNNConfig(Constantes.FILE_TESTE, Constantes.FILE,
					Constantes.EH_TESTE, qtInicialMaxPontos, qtNeuroniosPorCamada, 
					quadInscrito, funcaoUltimaCamada, funcaoDemaisCamadas, 
					tipo, inicializacaoPesosConfig, randPesos, randEstrutura, normaliza, 1, camadaSaida, crossEntropy,
					ativaInibicaoLateral,normalizaSaidaInibitoria,pesoInibitorio,raioInibitorio);
			
			BancoDeImagens bancoImagens;
			try {
				
				SPNNEscritor escritor = new SPNNEscritor(config, Constantes.QUANTIDADE_EPOCAS, Constantes.OTIMIZACAO_QUANTIDADE_SIMULACOES,1);
				
				System.out.println("Lendo base de dados");
				
				bancoImagens = BancoUtil.ler(Constantes.BANCO, Constantes.BANCO_CONFIG, true);
			
				System.out.println("Gerando mapa de probabilidade");
				
				double[][][] mapaProbabilidade = MapaProbabilidade.criarMapaProbabilidade(bancoImagens, tipo);			
				
				int qtSimulacoes = Constantes.OTIMIZACAO_QUANTIDADE_SIMULACOES;
				
				int qtVez = qtSimulacoes / Constantes.THREAD_QUANTIDADE_SIMULACOES_POR_VEZ;
				int qtVezUlt = qtSimulacoes % Constantes.THREAD_QUANTIDADE_SIMULACOES_POR_VEZ;
				qtVez = qtVezUlt == 0? qtVez: qtVez+1;
				qtVezUlt = qtVezUlt == 0? Constantes.THREAD_QUANTIDADE_SIMULACOES_POR_VEZ: qtVezUlt;
				
				for (int i = 0, total = 0; i < qtVez; i++) 
				{
					
					int qt = Constantes.THREAD_QUANTIDADE_SIMULACOES_POR_VEZ;
					if(i == qtVez - 1)
					{
						qt = qtVezUlt;
					}
					
					ThreadSPNN[] threads = new ThreadSPNN[qt];
					
					for (int j = 0; j < qt; j++, total++) {	
					
						System.out.println("******* Simulação "+total+" *******");
						threads[j] = new ThreadSPNN(bancoImagens,mapaProbabilidade,escritor, total, 
								qtInicialMaxPontos, qtNeuroniosPorCamada, quadInscrito, funcaoUltimaCamada, 
								funcaoDemaisCamadas, tipo, inicializacaoPesosConfig, randPesos/**(total+1)*/, randEstrutura/**(total+1)*/, 
								normaliza, camadaSaida, crossEntropy,ativaInibicaoLateral,normalizaSaidaInibitoria,pesoInibitorio,raioInibitorio);
					
						startThread(threads[j]);
						
						
					}
					
					waitThreads(threads);
					
					for (ThreadSPNN threadSPNN : threads) {
						media += threadSPNN.resultado;
					}
					
					
				}			
				
				escritor.writeMapaCamposEntrada();
				escritor.writeConvergenciaRede();
				escritor.closeAll();
				
				System.out.println("Media Teste = "+media/qtSimulacoes);
							
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
		//}
			
	}
	
	
	
	static class ThreadSPNN extends Thread{
		
		private BancoDeImagens bancoImagens;
		private double[][][] mapaProbabilidade;
		private SPNNEscritor escritor;
		private int simulacao;
		
		
		private int[] qtNeuronios;
		private int qtInicialPontos;
		
		private boolean quadInscrito;
		private FuncaoAtivacao funcaoUltimaCamada;
		private FuncaoAtivacao funcaoDemaisCamadas;
		private TipoProblema tipoProblema;
		private SPNNPesosConfig inicializacaoPesosConfig;
		
		private int randPesos, randEstrutura;
		private boolean normaliza;
		
		double resultado;
		
		private boolean camadaSaida, crossEntropy;
		
		boolean ativaInibicaoLateral,normalizaSaidaInibitoria;
		double pesoInibitorio,raioInibitorio;
		
		public ThreadSPNN(BancoDeImagens bancoImagens, double[][][] mapaProbabilidade, SPNNEscritor escritor, int sim,
				int qtInicialPontos, int[] qtNeuroniosPorCamada,
				boolean quadInscrito, FuncaoAtivacao funcaoUltimaCamada, FuncaoAtivacao funcaoDemaisCamadas, TipoProblema tipoProblema, 
				SPNNPesosConfig inicializacaoPesosConfig, int randPesos, int randEstrutura, boolean normaliza, boolean camadaSaida, boolean crossEntropy,
				boolean ativaInibicaoLateral, boolean normalizaSaidaInibitoria, double pesoInibitorio, double raioInibitorio){
			this.bancoImagens = bancoImagens;
			this.mapaProbabilidade = mapaProbabilidade;
			this.escritor = escritor;
			this.simulacao = sim;
			this.qtInicialPontos = qtInicialPontos;
			this.qtNeuronios = qtNeuroniosPorCamada;
			this.quadInscrito = quadInscrito;
			this.funcaoUltimaCamada = funcaoUltimaCamada;
			this.funcaoDemaisCamadas = funcaoDemaisCamadas;
			this.tipoProblema = tipoProblema;
			this.inicializacaoPesosConfig = inicializacaoPesosConfig;
			this.randPesos = randPesos;
			this.randEstrutura = randEstrutura;
			this.normaliza = normaliza;
			this.camadaSaida = camadaSaida;
			this.crossEntropy = crossEntropy;
			
			this.ativaInibicaoLateral = ativaInibicaoLateral;
			this.normalizaSaidaInibitoria = normalizaSaidaInibitoria;
			this.pesoInibitorio = pesoInibitorio;
			this.raioInibitorio = raioInibitorio;
		}
		
		public void run(){
			try {

			ArrayList<double[][]> bancoTreinoPos = bancoImagens.getBase(TIPO_BANCO.TREINO, 0).getImagens();
			ArrayList<double[][]> bancoTreinoNeg = bancoImagens.getBase(TIPO_BANCO.TREINO, 1).getImagens();
			ArrayList<double[][]> bancoValidPos = bancoImagens.getBase(TIPO_BANCO.VALIDACAO, 0).getImagens();
			ArrayList<double[][]> bancoValidNeg = bancoImagens.getBase(TIPO_BANCO.VALIDACAO, 1).getImagens();
			ArrayList<double[][]> bancoTestePos = bancoImagens.getBase(TIPO_BANCO.TESTE, 0).getImagens();
			ArrayList<double[][]> bancoTesteNeg = bancoImagens.getBase(TIPO_BANCO.TESTE, 1).getImagens();	
			
			
			double[][] conv = new double[Constantes.QUANTIDADE_EPOCAS][3];
			int rodada = 0;
			System.out.println("Criando estrutura da rede");
			
			SPNN spnn = null;
			//try{
				spnn = new SPNN(qtInicialPontos, qtNeuronios,
					mapaProbabilidade[rodada], funcaoUltimaCamada, funcaoDemaisCamadas, quadInscrito, 
					inicializacaoPesosConfig, randPesos, randEstrutura, normaliza, camadaSaida, crossEntropy,
					ativaInibicaoLateral,normalizaSaidaInibitoria,pesoInibitorio,raioInibitorio);
			/*}catch(Exception e){
				e.printStackTrace();
				//try again
				spnn = new SPNN(qtInicialPontos, qtNeuronios,
						mapaProbabilidade[rodada], funcaoUltimaCamada, funcaoDemaisCamadas, quadInscrito, 
						inicializacaoPesosConfig, randPesos, randEstrutura, normaliza, camadaSaida, crossEntropy);		
			}*/
			
			System.out.println("Treinando.");
			
			//TreinaSPNN.treina(spnn, tipoProblema, Constantes.QUANTIDADE_EPOCAS, bancoTreinoPos, bancoTreinoNeg,
				//	Constantes.qtImagensTreinoPorEpoca, rodada);	
			TreinaSPNN.treina(spnn, tipoProblema, Constantes.QUANTIDADE_EPOCAS, bancoTreinoPos, bancoTreinoNeg,
						bancoValidPos, bancoValidNeg, bancoTestePos, bancoTesteNeg,
						Constantes.qtImagensTreinoPorEpoca, rodada, conv, registraValidTeste);
			

			String caminhoSPNNRoc = escritor.getTempPath("SPNNRoc");
			
			
			ResultadoAUC resTreino = TreinaSPNN.avalia(spnn, bancoTreinoPos, bancoTreinoNeg, tipoProblema);
			//ResultadoAUC resValid = TreinaSPNN.avalia(spnn, bancoValidPos, bancoValidNeg, tipoProblema);
			ResultadoAUC resTeste = TreinaSPNN.avalia(spnn, bancoTestePos, bancoTesteNeg, tipoProblema, caminhoSPNNRoc, simulacao+"");
			
			System.out.println("Treino = "+resTreino.auc);
			//System.out.println("Valid = "+resValid.auc);
			System.out.println("Teste = "+resTeste.auc);			

			this.resultado = resTeste.auc;
					
			escritor.writeBoxplot(resTeste.auc);
			escritor.writeTime(simulacao);//debug
			escritor.writeResult(resTeste,0);
			escritor.adicionarEstruturaArvore(spnn.toStringEstruturaArvore(),simulacao+"");
			escritor.adicionarEstruturaCamadas(spnn.toStringEstruturaCamadas(), simulacao+"");
			escritor.writeMapaCamposEntrada(spnn.getMapaDeCampos(), simulacao+"", true);
			
			escritor.writeConvergenciaRede(conv,simulacao+"");
			
			} catch (Exception e) {
				e.printStackTrace();
			}	
			
		}
		
		
	}
	

	private static void startThread(Thread t)
	{
		util.thread.ThreadUtil.startThread(t, Constantes.THREAD_ATIVA_SIMULACAO);
	}
	
	private static void waitThreads(Thread[] threads) throws InterruptedException
	{
		util.thread.ThreadUtil.waitThreads(threads, Constantes.THREAD_ATIVA_SIMULACAO);
	}

	
}
