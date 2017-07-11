import java.util.ArrayList;
import lipnet.RedeNeural;
import operacoes.MapaProbabilidade;
import rede.SPNN;
import rede.SPNNPesosConfig;
import rede.TreinaSPNN;
import util.avaliacao.ResultadoAUC;
import util.banco.bancoGeral.BancoUtil;
import util.banco.bancoGeral.TipoProblema;
import util.banco.bancoGeral.base.BancoDeImagens;
import util.banco.bancoGeral.base.BancoDeImagens.TIPO_BANCO;
import util.redeneural.base.FuncaoAtivacao;
import configs.Constantes;
import configs.ConstantesPyraLip;
import configs.SPNNComparaConfig;
import configs.SPNNEscritor;


public class TesteComite {
	
	

	public static void main(String[] args) {

		double media = 0;
	
		boolean quadInscrito = Constantes.QUADRADO_INSCRITO;
		FuncaoAtivacao funcaoUltimaCamada = Constantes.FUNCAO_ATIVACAO_ULTIMA_CAMADA;
		FuncaoAtivacao funcaoDemaisCamadas = Constantes.FUNCAO_ATIVACAO_DEMAIS_CAMADAS;
		TipoProblema tipo = Constantes.TIPO_PROBLEMA;
		int randPesos = Constantes.RAND_PESOS;
		int randEstrutura = Constantes.RAND_ESTRUTURA;
		boolean normaliza = Constantes.NORMALIZA_SAIDA_CAMPO_RECEPTIVO;
		int qtComite = Constantes.QUANTIDADE_REDES_COMITE;
		

		boolean camadaSaida =  Constantes.CAMADA_SAIDA_ADICIONA;
		boolean crossEntropy =  Constantes.CAMADA_SAIDA_CROSS_ENTROPY;
		

		boolean ativaInibicaoLateral = Constantes.INIBICAO_LATERAL_ATIVA;
		boolean normalizaSaidaInibitoria = Constantes.INIBICAO_LATERAL_NORMALIZA;
		double pesoInibitorio = Constantes.INIBICAO_LATERAL_PESO;
		double raioInibitorio = Constantes.INIBICAO_LATERAL_RAIO;
		
		
		SPNNPesosConfig inicializacaoPesosConfig = Constantes.inicializacaoPesosConfig;
		
		SPNNConfig config = new SPNNConfig(Constantes.FILE_TESTE, Constantes.FILE,
				Constantes.EH_TESTE, Constantes.QUANTIDADE_INICIAL_MAXIMA_DE_PONTOS, Constantes.QUANTIDADE_NEURONIOS_POR_CAMADA,
				quadInscrito, funcaoUltimaCamada, funcaoDemaisCamadas, 
				tipo, inicializacaoPesosConfig, randPesos, randEstrutura, normaliza, qtComite, camadaSaida, crossEntropy,
				ativaInibicaoLateral,normalizaSaidaInibitoria,pesoInibitorio,raioInibitorio);
		
		
		
		BancoDeImagens bancoImagens;
		try {
			
			SPNNEscritor escritor = new SPNNEscritor(config, Constantes.QUANTIDADE_EPOCAS, Constantes.OTIMIZACAO_QUANTIDADE_SIMULACOES, 1);
			
			System.out.println("Lendo base de dados");
			
			bancoImagens = BancoUtil.ler(Constantes.BANCO, Constantes.BANCO_CONFIG, true);
		
			System.out.println("Gerando mapa de probabilidade");
			
			double[][][] mapaProbabilidade = MapaProbabilidade.criarMapaProbabilidade(bancoImagens, tipo);			
			
			int qtSimulacoes = Constantes.OTIMIZACAO_QUANTIDADE_SIMULACOES;
			
			for(int simulacao = 0; simulacao < qtSimulacoes; simulacao++){
				
				ArrayList<double[][]> bancoTreinoPos = bancoImagens.getBase(TIPO_BANCO.TREINO, 0).getImagens();
				ArrayList<double[][]> bancoTreinoNeg = bancoImagens.getBase(TIPO_BANCO.TREINO, 1).getImagens();
				ArrayList<double[][]> bancoValidPos = bancoImagens.getBase(TIPO_BANCO.VALIDACAO, 0).getImagens();
				ArrayList<double[][]> bancoValidNeg = bancoImagens.getBase(TIPO_BANCO.VALIDACAO, 1).getImagens();
				ArrayList<double[][]> bancoTestePos = bancoImagens.getBase(TIPO_BANCO.TESTE, 0).getImagens();
				ArrayList<double[][]> bancoTesteNeg = bancoImagens.getBase(TIPO_BANCO.TESTE, 1).getImagens();	
				
				
				SPNN[] comite = new SPNN[qtComite];
				
				int rodada = 0;

				String caminhoSPNNRoc = escritor.getTempPath("SPNNRoc");
				
				for (int i = 0; i < qtComite; i++) {
				
					String id = simulacao+"-"+i;
					System.out.println("Criando estrutura da rede "+id);
					
					SPNN spnn = null;
					try{
						spnn = new SPNN(Constantes.QUANTIDADE_INICIAL_MAXIMA_DE_PONTOS, Constantes.QUANTIDADE_NEURONIOS_POR_CAMADA,
							mapaProbabilidade[rodada], funcaoUltimaCamada, funcaoDemaisCamadas, quadInscrito, 
							inicializacaoPesosConfig, randPesos, randEstrutura, normaliza, camadaSaida, crossEntropy,
							ativaInibicaoLateral,normalizaSaidaInibitoria,pesoInibitorio,raioInibitorio);
					}catch(Exception e){
						e.printStackTrace();
						//try again
						spnn = new SPNN(Constantes.QUANTIDADE_INICIAL_MAXIMA_DE_PONTOS, Constantes.QUANTIDADE_NEURONIOS_POR_CAMADA,
								mapaProbabilidade[rodada], funcaoUltimaCamada, funcaoDemaisCamadas, quadInscrito, 
								inicializacaoPesosConfig, randPesos, randEstrutura, normaliza, camadaSaida, crossEntropy,
								ativaInibicaoLateral,normalizaSaidaInibitoria,pesoInibitorio,raioInibitorio);		
					}
					
					comite[i] = spnn;
					
					escritor.writeMapaCamposEntrada(spnn.getMapaDeCampos(), id, true);
					
					System.out.println("Treinando "+id);
					
					//double[][] conv = new double[Constantes.QUANTIDADE_EPOCAS][3];
					TreinaSPNN.treina(spnn, tipo, Constantes.QUANTIDADE_EPOCAS, bancoTreinoPos, bancoTreinoNeg,
						Constantes.qtImagensTreinoPorEpoca, rodada);	
					
					//TreinaSPNN.treina(spnn, tipoProblema, Constantes.QUANTIDADE_EPOCAS, bancoTreinoPos, bancoTreinoNeg,
					//			bancoValidPos, bancoValidNeg, bancoTestePos, bancoTesteNeg,
					//			Constantes.qtImagensTreinoPorEpoca, rodada, conv, false);
					
					//ResultadoAUC resTreino = TreinaSPNN.avalia(spnn, bancoTreinoPos, bancoTreinoNeg, tipo);
					//ResultadoAUC resValid = TreinaSPNN.avalia(spnn, bancoValidPos, bancoValidNeg, tipo);
					ResultadoAUC resTeste = TreinaSPNN.avalia(spnn, bancoTestePos, bancoTesteNeg, tipo);
					
					//double aucTreino = resTreino.auc;
					//double aucValid = resValid.auc;
					double aucTeste = resTeste.auc;
					
					
					//System.out.print("("+id+") TR = "+aucTreino);
					//System.out.print("\tVA = "+aucValid);
					System.out.println("\tTE = "+aucTeste);		
										
				
				}
				
				//ResultadoAUC resTreino = TreinaSPNN.avaliaComiteComMedia(comite, bancoTreinoPos, bancoTreinoNeg, tipo, false, caminhoSPNNRoc, simulacao+"");
				//ResultadoAUC resValid = TreinaSPNN.avaliaComiteComMedia(comite, bancoValidPos, bancoValidNeg, tipo, false, caminhoSPNNRoc, simulacao+"");
				ResultadoAUC resTeste = TreinaSPNN.avaliaComiteComMedia(comite, bancoTestePos, bancoTesteNeg, tipo, true, caminhoSPNNRoc, simulacao+"");
				
				//double aucTreino = resTreino.auc;
				//double aucValid = resValid.auc;
				double aucTeste = resTeste.auc;
				/*
				double aucTreinoVote = TreinaSPNN.avaliaComiteComVotacaoMedia(comite, bancoTreinoPos, bancoTreinoNeg);
				double aucValidVote = TreinaSPNN.avaliaComiteComVotacaoMedia(comite, bancoValidPos, bancoValidNeg);
				double aucTesteVote = TreinaSPNN.avaliaComiteComVotacaoMedia(comite, bancoTestePos, bancoTesteNeg);
				
				double aucTreinoVoteMax = TreinaSPNN.avaliaComiteComVotacaoProbabilidadeMax(comite, bancoTreinoPos, bancoTreinoNeg);
				double aucValidVoteMax = TreinaSPNN.avaliaComiteComVotacaoProbabilidadeMax(comite, bancoValidPos, bancoValidNeg);
				double aucTesteVoteMax = TreinaSPNN.avaliaComiteComVotacaoProbabilidadeMax(comite, bancoTestePos, bancoTesteNeg);
				*/
				//ResultadoAUC resTreinoProd = TreinaSPNN.avaliaComiteComProduto(comite, bancoTreinoPos, bancoTreinoNeg, tipo, false, caminhoSPNNRoc, simulacao+"-prod");
				//ResultadoAUC resValidProd = TreinaSPNN.avaliaComiteComProduto(comite, bancoValidPos, bancoValidNeg, tipo, false, caminhoSPNNRoc, simulacao+"-prod");
				ResultadoAUC resTesteProd = TreinaSPNN.avaliaComiteComProduto(comite, bancoTestePos, bancoTesteNeg, tipo, true, caminhoSPNNRoc, simulacao+"-prod");
				
				//double aucTreinoProd = resTreinoProd.auc;
				//double aucValidProd = resValidProd.auc;
				double aucTesteProd = resTesteProd.auc;
				
				
				System.out.println("******* Simulação "+simulacao+" *******");
				/*
				System.out.println("Treino = "+aucTreino+" - "+aucTreinoVote+" - "+aucTreinoVoteMax+" - "+aucTreinoProd);
				System.out.println("Valid = "+aucValid+" - "+aucValidVote+" - "+aucValidVoteMax+" - "+aucValidProd);
				System.out.println("Teste = "+aucTeste+" - "+aucTesteVote+" - "+aucTesteVoteMax+" - "+aucTesteProd);	
				*/
				//escritor.writeBoxplot(aucTeste+"\t"+aucTesteVote+"\t"+aucTesteVoteMax+"\t"+aucTesteProd);
				
				
				
				escritor.writeResult(resTeste, 0);
				
				media += aucTeste;
				
			}
			
			
			
			
			/*
			
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
					threads[j] = new ThreadSPNN(bancoImagens,mapaProbabilidade,escritor, total, quadInscrito, funcaoUltimaCamada, 
							funcaoDemaisCamadas, tipo, inicializacaoPesosConfig, randPesos, randEstrutura,
							normaliza, qtComite, camadaSaida, crossEntropy);
				
					startThread(threads[j]);
					
					
				}
				
				waitThreads(threads);
				
				for (ThreadSPNN threadSPNN : threads) {
					media += threadSPNN.resultado;
				}
				
				
			}			*/
			
			//escritor.writeMapaCamposEntrada();
			//escritor.writeConvergenciaRede();
			escritor.closeAll();
			
			System.out.println("Media Teste Comitê Final = "+media/qtSimulacoes);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	
	
	static class ThreadSPNN extends Thread{
		
		private BancoDeImagens bancoImagens;
		private double[][][] mapaProbabilidade;
		private SPNNEscritor escritor;
		private int simulacao;
		private boolean quadInscrito;
		private TipoProblema tipoProblema;
		private FuncaoAtivacao funcaoUltimaCamada;
		private FuncaoAtivacao funcaoDemaisCamadas;
		private SPNNPesosConfig inicializacaoPesosConfig;
		
		private int randPesos, randEstrutura;
		private boolean normaliza;
		
		private int qtComite;
		
		double resultado;
		
		private boolean camadaSaida, crossEntropy;
	
		boolean ativaInibicaoLateral,normalizaSaidaInibitoria;
		double pesoInibitorio,raioInibitorio;
		
		
		public ThreadSPNN(BancoDeImagens bancoImagens, double[][][] mapaProbabilidade, SPNNEscritor escritor, int sim,
				boolean quadInscrito, FuncaoAtivacao funcaoUltimaCamada, FuncaoAtivacao funcaoDemaisCamadas, TipoProblema tipoProblema, 
				SPNNPesosConfig inicializacaoPesosConfig, int randPesos, int randEstrutura, boolean normaliza, int qtComite,
				boolean camadaSaida, boolean crossEntropy, 
				boolean ativaInibicaoLateral, boolean normalizaSaidaInibitoria, double pesoInibitorio, double raioInibitorio){
			this.bancoImagens = bancoImagens;
			this.mapaProbabilidade = mapaProbabilidade;
			this.escritor = escritor;
			this.simulacao = sim;
			this.quadInscrito = quadInscrito;
			this.funcaoUltimaCamada = funcaoUltimaCamada;
			this.funcaoDemaisCamadas = funcaoDemaisCamadas;
			this.tipoProblema = tipoProblema;
			this.inicializacaoPesosConfig = inicializacaoPesosConfig;
			this.randPesos = randPesos;
			this.randEstrutura = randEstrutura;
			this.normaliza = normaliza;
			this.qtComite = qtComite;
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
			
			
			SPNN[] comite = new SPNN[qtComite];
			int rodada = 0;

			String caminhoSPNNRoc = escritor.getTempPath("SPNNRoc");
			
			for (int i = 0; i < qtComite; i++) {
			
				String id = simulacao+"-"+i;
				System.out.println("Criando estrutura da rede "+id);
				
				SPNN spnn = null;
				try{
					spnn = new SPNN(Constantes.QUANTIDADE_INICIAL_MAXIMA_DE_PONTOS, Constantes.QUANTIDADE_NEURONIOS_POR_CAMADA,
						mapaProbabilidade[rodada], funcaoUltimaCamada, funcaoDemaisCamadas, quadInscrito, 
						inicializacaoPesosConfig, randPesos, randEstrutura, normaliza, camadaSaida, crossEntropy,
						ativaInibicaoLateral,normalizaSaidaInibitoria,pesoInibitorio,raioInibitorio);
				}catch(Exception e){
					e.printStackTrace();
					//try again
					spnn = new SPNN(Constantes.QUANTIDADE_INICIAL_MAXIMA_DE_PONTOS, Constantes.QUANTIDADE_NEURONIOS_POR_CAMADA,
							mapaProbabilidade[rodada], funcaoUltimaCamada, funcaoDemaisCamadas, quadInscrito, 
							inicializacaoPesosConfig, randPesos, randEstrutura, normaliza, camadaSaida, crossEntropy,
							ativaInibicaoLateral,normalizaSaidaInibitoria,pesoInibitorio,raioInibitorio);		
				}
								
				comite[i] = spnn;
				
				escritor.writeMapaCamposEntrada(spnn.getMapaDeCampos(), id, true);
				
				System.out.println("Treinando "+id);
				
				//double[][] conv = new double[Constantes.QUANTIDADE_EPOCAS][3];
				TreinaSPNN.treina(spnn, tipoProblema, Constantes.QUANTIDADE_EPOCAS, bancoTreinoPos, bancoTreinoNeg,
					Constantes.qtImagensTreinoPorEpoca, rodada);	
				/*
				TreinaSPNN.treina(spnn, tipoProblema, Constantes.QUANTIDADE_EPOCAS, bancoTreinoPos, bancoTreinoNeg,
							bancoValidPos, bancoValidNeg, bancoTestePos, bancoTesteNeg,
							Constantes.qtImagensTreinoPorEpoca, rodada, conv, false);
				*/
				ResultadoAUC resTreino = TreinaSPNN.avalia(spnn, bancoTreinoPos, bancoTreinoNeg, tipoProblema);
				ResultadoAUC resValid = TreinaSPNN.avalia(spnn, bancoValidPos, bancoValidNeg, tipoProblema);
				ResultadoAUC resTeste = TreinaSPNN.avalia(spnn, bancoTestePos, bancoTesteNeg, tipoProblema);
				
				double aucTreino = resTreino.auc;
				double aucValid = resValid.auc;
				double aucTeste = resTeste.auc;
				
				System.out.print("("+id+") TR = "+aucTreino);
				System.out.print("\tVA = "+aucValid);
				System.out.println("\tTE = "+aucTeste);		
			
			}
			
			ResultadoAUC resTreino = TreinaSPNN.avaliaComiteComMedia(comite, bancoTreinoPos, bancoTreinoNeg, tipoProblema, false, caminhoSPNNRoc, simulacao+"");
			ResultadoAUC resValid = TreinaSPNN.avaliaComiteComMedia(comite, bancoValidPos, bancoValidNeg, tipoProblema, false, caminhoSPNNRoc, simulacao+"");
			ResultadoAUC resTeste = TreinaSPNN.avaliaComiteComMedia(comite, bancoTestePos, bancoTesteNeg, tipoProblema, true, caminhoSPNNRoc, simulacao+"");
			
			double aucTreino = resTreino.auc;
			double aucValid = resValid.auc;
			double aucTeste = resTeste.auc;
			
			
			double aucTreinoVote = TreinaSPNN.avaliaComiteComVotacaoMedia(comite, bancoTreinoPos, bancoTreinoNeg);
			double aucValidVote = TreinaSPNN.avaliaComiteComVotacaoMedia(comite, bancoValidPos, bancoValidNeg);
			double aucTesteVote = TreinaSPNN.avaliaComiteComVotacaoMedia(comite, bancoTestePos, bancoTesteNeg);
			

			double aucTreinoVoteMax = TreinaSPNN.avaliaComiteComVotacaoProbabilidadeMax(comite, bancoTreinoPos, bancoTreinoNeg);
			double aucValidVoteMax = TreinaSPNN.avaliaComiteComVotacaoProbabilidadeMax(comite, bancoValidPos, bancoValidNeg);
			double aucTesteVoteMax = TreinaSPNN.avaliaComiteComVotacaoProbabilidadeMax(comite, bancoTestePos, bancoTesteNeg);
			
			ResultadoAUC resTreinoProd = TreinaSPNN.avaliaComiteComProduto(comite, bancoTreinoPos, bancoTreinoNeg, tipoProblema, false, caminhoSPNNRoc, simulacao+"-prod");
			ResultadoAUC resValidProd = TreinaSPNN.avaliaComiteComProduto(comite, bancoValidPos, bancoValidNeg, tipoProblema, false, caminhoSPNNRoc, simulacao+"-prod");
			ResultadoAUC resTesteProd = TreinaSPNN.avaliaComiteComProduto(comite, bancoTestePos, bancoTesteNeg, tipoProblema, false, caminhoSPNNRoc, simulacao+"-prod");
			
			double aucTreinoProd = resTreinoProd.auc;
			double aucValidProd = resValidProd.auc;
			double aucTesteProd = resTesteProd.auc;
			
			
			System.out.println("******* Simulação "+simulacao+" *******");
			System.out.println("Treino = "+aucTreino+" - "+aucTreinoVote+" - "+aucTreinoVoteMax+" - "+aucTreinoProd);
			System.out.println("Valid = "+aucValid+" - "+aucValidVote+" - "+aucValidVoteMax+" - "+aucValidProd);
			System.out.println("Teste = "+aucTeste+" - "+aucTesteVote+" - "+aucTesteVoteMax+" - "+aucTesteProd);	
			
			escritor.writeBoxplot(aucTeste+"\t"+aucTesteVote+"\t"+aucTesteVoteMax+"\t"+aucTesteProd);
			
			this.resultado = aucTeste;
			
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
