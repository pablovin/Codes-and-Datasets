package rede;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import operacoes.GeradorAleatorio;
import operacoes.KMeans;
import operacoes.TriangulacaoDeDelaunay;
import operacoes.geometria.Ponto;
import operacoes.geometria.Triangulo;
import util.Util;
import util.redeneural.base.FuncaoAtivacao;


public class SPNN extends SPNNBase{

	
	public Random rand;
	
	private Neuronio2D[][] camadaEntrada;
	private List<Camada2D> camadas2D;
	private Camada1D camadaSaida;
	
	private boolean temCamadaSaida1D;
	
	public static boolean ativaInibicaoLateral = false;
	public static boolean normalizaSaidaInibitoria = false;
	public static double pesoInibitorio = 0;
	private static double raioInibitorio = 0;
	
	public SPNN(int qtPontosInicial, int[] qtNeuronios, double[][] mapaProbabilidade, FuncaoAtivacao funcaoUltimaCamada, FuncaoAtivacao funcaoDemaisCamadas, boolean quadradoInscrito, 
			SPNNPesosConfig inicializacaoPesosConfig, int randPesos, int randEstrutura, boolean normaliza,
			 boolean camadaSaida, boolean crossEntropy, boolean ativaInibicaoLateral, boolean normalizaSaidaInibitoria, double pesoInibitorio, double raioInibitorio){		
		if(randPesos == 0){
			this.rand = new Random();
		}else{
			this.rand = new Random(randPesos);
		}		
		
		this.temCamadaSaida1D = camadaSaida;
		
		SPNN.ativaInibicaoLateral = ativaInibicaoLateral;
		SPNN.normalizaSaidaInibitoria = normalizaSaidaInibitoria;
		SPNN.pesoInibitorio = pesoInibitorio;
		SPNN.raioInibitorio = raioInibitorio;
		
		estruturaRede(qtPontosInicial, qtNeuronios, mapaProbabilidade, funcaoUltimaCamada, funcaoDemaisCamadas, 
				quadradoInscrito, inicializacaoPesosConfig, randEstrutura, normaliza, crossEntropy);
	}

	public SPNN(Neuronio2D[][] camadaEntrada, List<Camada2D> camadas, Camada1D camadaSaida, 
			boolean ativaInibicaoLateral, boolean normalizaSaidaInibitoria, double pesoInibitorio, double raioInibitorio){
		this.camadaEntrada = camadaEntrada;
		this.camadas2D = camadas;
		this.temCamadaSaida1D = camadaSaida != null;
		this.camadaSaida = camadaSaida;
		
		SPNN.ativaInibicaoLateral = ativaInibicaoLateral;
		SPNN.normalizaSaidaInibitoria = normalizaSaidaInibitoria;
		SPNN.pesoInibitorio = pesoInibitorio;
		SPNN.raioInibitorio = raioInibitorio;
		
		if(ativaInibicaoLateral){
			criaCampoInibitorio();
		}
		
	}
	
	private void estruturaRede(int qtPontosInicial, int[] qtNeuronios, double[][] mapaProbabilidade,
			FuncaoAtivacao funcaoUltimaCamada, FuncaoAtivacao funcaoDemaisCamadas, boolean quadradoInscrito,
			SPNNPesosConfig inicializacaoPesosConfig, int rand, boolean normaliza, boolean crossEntropy) {
		
		//inicialização da camada de entrada
		this.camadaEntrada = new Neuronio2D[mapaProbabilidade.length][mapaProbabilidade[0].length];
		for (int i = 0; i < camadaEntrada.length; i++) {
			for (int j = 0; j < camadaEntrada[0].length; j++) {
				this.camadaEntrada[i][j] = new Neuronio2D(i, j, 0, null, true, funcaoDemaisCamadas, normaliza);
			}
		}
		
		this.camadas2D = new ArrayList<Camada2D>();
	
		int qtPontos = qtPontosInicial;
		int icamada = 1;
		GeradorAleatorio gerador = new GeradorAleatorio(rand);
		double[][] pontos = gerador.gerarPontosAleatorios(qtPontos, mapaProbabilidade);
		System.out.println("Pts aleatórios");
		KMeans kmeans = new KMeans(rand);
		
		do{
			List<Triangulo> triangulos = TriangulacaoDeDelaunay.triangular(pontos);
			System.out.println("Triangulos");
			triangulos = selecionaTriangulos(triangulos);
			
			//int[][] tri = getTriangulos(triangulos);
			double[][] centros = getCentros(triangulos);
			//double[] raios = getRaios(triangulos);
			
			/*
			int qtNeur = qtPontos/taxaDeReducao;
			if(qtNeur <= 2){
				qtNeur = 1;
			}*/
			
			int[] cluster = kmeans.kmeans(qtNeuronios[icamada-1], centros);
			System.out.println("Kmeans");
			double[][] centroides = kmeans.getCentroides();
			
			FuncaoAtivacao funcao = icamada == qtNeuronios.length && !temCamadaSaida1D? funcaoUltimaCamada: funcaoDemaisCamadas;
			
			List<Neuronio2D> neuronios = null;
			if(icamada == 1){
				neuronios = criarNeuroniosDeSegundaCamada(camadaEntrada, pontos, triangulos, cluster, centroides, funcao, icamada, quadradoInscrito, normaliza);
				
			}else{
				neuronios = criarNeuroniosDeCamadaSuperior(camadas2D.get(camadas2D.size()-1).getNeuronios(), pontos, triangulos, cluster, centroides, funcao, icamada, normaliza);
			}
			
			Camada2D camada = new Camada2D(neuronios, icamada, false);
			this.camadas2D.add(camada);
			System.out.println("Camadas");
			icamada++;
			pontos = centroides;
			qtPontos = centroides.length;
		
		}while(qtPontos > 2);
		
		if(qtPontos == 2){
			
			FuncaoAtivacao funcao = !temCamadaSaida1D? funcaoUltimaCamada: funcaoDemaisCamadas;
			
			double[] pontoMedio = Util.mediaDePontos(pontos);
			Camada2D penultimaCamada = this.camadas2D.get(this.camadas2D.size()-1);
			List<Neuronio2D> neuroniosCampos = new ArrayList<Neuronio2D>(penultimaCamada.getNeuronios());
			
			Neuronio2D neuronioUltimaCamada = new Neuronio2D(pontoMedio[0], pontoMedio[1], icamada,
					neuroniosCampos, false, funcao, normaliza);
			List<Neuronio2D> neuroniosUltimaCamada = new ArrayList<Neuronio2D>();
			neuroniosUltimaCamada.add(neuronioUltimaCamada);
			
			Camada2D ultimaCamada = new Camada2D(neuroniosUltimaCamada,icamada, false);
			this.camadas2D.add(ultimaCamada);
			
			icamada++;
			
		}
		
		//Criar camada de saída com 2 neurônios
		if(temCamadaSaida1D){
			
			camadaSaida = new Camada1D(2, 0, qtNeuronios[qtNeuronios.length-1], funcaoUltimaCamada, crossEntropy);
			
		}
		
		if(ativaInibicaoLateral){
			criaCampoInibitorio();
		}
		/*
		for (Camada2D camada : camadas2D) {
			String str = "Camada "+camada.getCamada()+": ";
			for (Neuronio2D neuronio : camada.getNeuronios()) {
				str += neuronio.getCampoReceptivo().size()+"; ";
			}
			System.out.println(str);
		}
		if(temCamadaSaida1D) 
			System.out.println(camadaSaida.getNeuronios().length);
		*/
		this.geraPesosAleatorios(inicializacaoPesosConfig);
	
	}	

	private void criaCampoInibitorio() {
		
		for (Camada2D camada : camadas2D) {			
			if(camada.getNeuronios().size() > 1){
				for (int i = 0; i < camada.getNeuronios().size(); i++) {
					
					Neuronio2D neu1 = camada.getNeuronios().get(i);
					HashMap<Neuronio2D, Integer> map1 = neu1.getCampoInibitorio();
					if(map1 == null){
						map1 = new HashMap<Neuronio2D, Integer>();
						neu1.setCampoInibitorio(map1);
					}
					
					for (int j = i+1; j < camada.getNeuronios().size(); j++) {
					
						Neuronio2D neu2 = camada.getNeuronios().get(j);						
						
						double dist = Util.calculaDistanciaEuclidiana(
								new double[]{neu1.getPosX(),neu1.getPosY()}, new double[]{neu2.getPosX(),neu2.getPosY()});
						
						if(dist <= SPNN.raioInibitorio){
							
							int qtNeuCampoReceptivo = 0;
							
							for (Neuronio2D neuCampo : neu1.getCampoReceptivo()) {								
								if(neu2.getCampoReceptivo().contains(neuCampo)){
									qtNeuCampoReceptivo++;
								}
							}						
							
							map1.put(neu2, qtNeuCampoReceptivo);
							
							HashMap<Neuronio2D, Integer> map2 = neu2.getCampoInibitorio();
							if(map2 == null){
								map2 = new HashMap<Neuronio2D, Integer>();
								neu2.setCampoInibitorio(map2);
							}
							map2.put(neu1, qtNeuCampoReceptivo);
							
						}
						
					}
					
				}
			}
		}
		/*
		System.out.println();
		for (Camada2D camada : camadas2D) {			
			if(camada.getNeuronios().size() > 1){
				for (Neuronio2D neuronio : camada.getNeuronios()) {
					System.out.print(neuronio.getCampoInibitorio().size()+" , ");
				}
				System.out.println();
			}
			
		}*/
		
	}

	private List<Neuronio2D> criarNeuroniosDeSegundaCamada(
			Neuronio2D[][] camadaEntrada2, double[][] pontos, List<Triangulo> triangulos,
			int[] cluster, double[][] centroides, FuncaoAtivacao funcao, int icamada, boolean quadradoInscrito, 
			boolean normaliza) {
		
		List<Neuronio2D> neuroniosSegundaCamada = new ArrayList<Neuronio2D>();
		
		for (int icluster = 0; icluster < centroides.length; icluster++) {
			
			Set<Neuronio2D> campoReceptivoClusterI = new HashSet<Neuronio2D>();
			
			double posX = centroides[icluster][0];
			double posY = centroides[icluster][1];
			
			for (int i = 0; i < cluster.length; i++) {
				
				if(cluster[i] == icluster){
					
					Triangulo tri = triangulos.get(i);
					double cx = tri.getCirculo().getCentro().getX();					
					double cy = tri.getCirculo().getCentro().getY();
					double raio;
					if(quadradoInscrito){
						raio = tri.getCirculo().getRaio()/Math.sqrt(2);//quadrado inscrito
					}else{
						raio = tri.getCirculo().getRaio();//quadrado circunscrito
					}
					
					for (int j = (int)(cx-raio); j <= (int)(cx+raio); j++) {
						
						if(j >= 0 && j < camadaEntrada2.length){
							
							for (int j2 = (int)(cy-raio); j2 <= (int)(cy+raio); j2++) {
								
								if(j2 >= 0 && j2 < camadaEntrada2[j].length){
									
									campoReceptivoClusterI.add(camadaEntrada2[j][j2]);
									
								}							
							}
						}
					}
					
				}
			
			}
			
			List<Neuronio2D> list = new ArrayList<Neuronio2D>(campoReceptivoClusterI);
			
			if(list.size() > 0){
				Neuronio2D neu = new Neuronio2D(posX, posY, icamada, list, false, funcao, normaliza);
				neuroniosSegundaCamada.add(neu);			
			}
		}		
		
		return neuroniosSegundaCamada;
	}

	private List<Neuronio2D> criarNeuroniosDeCamadaSuperior(
			List<Neuronio2D> neuronios, double[][] pontos, List<Triangulo> triangulos, int[] cluster,
			double[][] centroides, FuncaoAtivacao funcao, int icamada, boolean normaliza) {
		
		List<Neuronio2D> neuroniosCamadaSuperior = new ArrayList<Neuronio2D>();
		
		for (int icluster = 0; icluster < centroides.length; icluster++) {
			
			Set<Neuronio2D> campoReceptivoClusterI = new HashSet<Neuronio2D>();
			
			double posX = centroides[icluster][0];
			double posY = centroides[icluster][1];
			
			for (int i = 0; i < cluster.length; i++) {
				
				if(cluster[i] == icluster){
					
					Triangulo tri = triangulos.get(i);
					
					int[] ipts = tri.getIndicesPontos();
					
					for (int j = 0; j < ipts.length; j++) {
						
						double posnX = pontos[ipts[j]][0];
						double poxnY = pontos[ipts[j]][1];
						Neuronio2D neu = null;
						for (Neuronio2D neuron : neuronios) {
							if(neuron.getPosX() == posnX && neuron.getPosY() == poxnY){
								neu = neuron;
								break;
							}
						}
						
						if(neu == null){
							System.out.println("OOPS!!"+icamada);							
						}else{
							campoReceptivoClusterI.add(neu);
						}
					}
					
				}
				
				
			}
			
			List<Neuronio2D> list = new ArrayList<Neuronio2D>(campoReceptivoClusterI);
			
			if(list.size() > 0){
				Neuronio2D neu = new Neuronio2D(posX, posY, icamada, list, false, funcao, normaliza);
				neuroniosCamadaSuperior.add(neu);			
			}
		}		
		
		return neuroniosCamadaSuperior;
	}


	private double[][] getCentros(List<Triangulo> triangulos) {
		
		double[][] centros = new double[triangulos.size()][];
		int i = 0;
		
		for (Triangulo tri : triangulos) {
			Ponto centro = tri.getCirculo().getCentro();
			centros[i] = new double[]{centro.getX(), centro.getY()};
			i++;
		}
		
		return centros;
	}

	private double[] getRaios(List<Triangulo> triangulos) {
		
		double[] raios = new double[triangulos.size()];
		int i = 0;
		
		for (Triangulo tri : triangulos) {
			raios[i] = tri.getCirculo().getRaio();
			i++;
		}
		
		return raios;
	}


	private int[][] getTriangulos(List<Triangulo> triangulos) {
		
		int[][] tris = new int[triangulos.size()][];
		int i = 0;
		
		for (Triangulo tri : triangulos) {
			
			tris[i] = tri.getIndicesPontos();
			i++;
		}
		
		return tris;
	}
	
	private List<Triangulo> selecionaTriangulos(List<Triangulo> triangulos) {
		
		for (int i = 0; i < triangulos.size(); i++) {
			Ponto centro = triangulos.get(i).getCirculo().getCentro();
			if(centro.getX() >= camadaEntrada.length || centro.getX() < 0 ||
					centro.getY() >= camadaEntrada[0].length || centro.getY() < 0){
				triangulos.remove(i);
				i--;
			}
			
		}
		
		
		return triangulos;
	}
	
	
	
	
	public double[] calculaSaidaRede(double[][] entrada) {
		
		// Fase Forward
		//Convertendo para camada de entrada
		for (int i = 0; i < entrada.length; i++) {
			for (int j = 0; j < entrada[i].length; j++) {
				
				Neuronio2D neuronio = camadaEntrada[i][j];
				neuronio.setSaida(entrada[i][j]);
				neuronio.setSomatorioEntrada(entrada[i][j]);
			}
		}
		//Convertendo para as demais camadas
		for (Camada2D camada: camadas2D) {
			
			camada.atualizaSaidaNeuronios();
		}

		double[] retorno = null;
		
		if(temCamadaSaida1D){
			
			camadaSaida.atualizaSaidaNeuronios(camadas2D.get(camadas2D.size()-1).getNeuronios());
			
			// Passando saidas da rede para um array de retorno// Passando saidas da rede para um array de retorno
			int qtNeuroniosUltimaCamada = camadaSaida.getNeuronios().length;
			retorno = new double[qtNeuroniosUltimaCamada];
			for (int i = 0; i < qtNeuroniosUltimaCamada; i++)
			{
				retorno[i] = camadaSaida.getNeuronios()[i].getSaida();
			}
			
		}else{
			// Passando saidas da rede para um array de retorno
			List<Neuronio2D> neuroniosUltimaCamada = camadas2D.get(camadas2D.size()-1).getNeuronios();
			int qtNeuroniosUltimaCamada = neuroniosUltimaCamada.size();
			retorno = new double[qtNeuroniosUltimaCamada];
			for (int i = 0; i < qtNeuroniosUltimaCamada; i++)
			{
				retorno[i] = neuroniosUltimaCamada.get(i).getSaida();
			}
		}

		return retorno;
	}
	
	
	public void treina(double[][] entrada, double[] saidaEsperada) {
		
		// Fase Forward
		double[] saidaRede = calculaSaidaRede(entrada);		
		
		int tamanhoSaida = saidaEsperada.length;		
			
		//Fase Backward
		//Calcula erro da camada da saída
		double[] erro = new double[tamanhoSaida];
		for (int i = 0; i < saidaEsperada.length; i++) {
			erro[i] = saidaRede[i] - saidaEsperada[i];
			//System.out.print(erro[i]+" ");
		}
		

		//Calcula sensibilidade da última camada
		if(temCamadaSaida1D){
			camadaSaida.atualizaSensibilidadeSaida(erro, camadas2D.get(camadas2D.size()-1).getNeuronios());
			camadas2D.get(camadas2D.size()-1).atualizaSensibilidadesDeTransicao(camadaSaida.getNeuronios());
		}else{
			camadas2D.get(camadas2D.size()-1).atualizaSensibilidadeSaida(erro);
		}
		
		//Calcula sensibilidade das demais camadas
		for (int i = camadas2D.size()-2; i >= 0; i--) {
			camadas2D.get(i).atualizaSensibilidadesOcultas();
		}
		
		//Calcula sensibilidade camada de entrada
		for (int i = 0; i < camadaEntrada.length; i++) {
			for (int j = 0; j < camadaEntrada[i].length; j++) {
				Neuronio2D neuronio = camadaEntrada[i][j];
				neuronio.setSomatorioErro(neuronio.calculaErroPonderado());
				neuronio.funcaoAtivacaoDerivada(neuronio.getSomatorioEntrada(), neuronio.getSomatorioErro());
			}
		}
		

		//Calcula gradientes
		if(temCamadaSaida1D){
			camadaSaida.calculaGradientes(camadas2D.get(camadas2D.size()-1).getNeuronios());
		}
		
		for (Camada2D camada: camadas2D) {
			camada.calculaGradientesPesos();
			camada.calculaGradientesBias();
		}
		
		//Calcula gradiente da camada de entrada
		for (int i = 0; i < camadaEntrada.length; i++) {
			for (int j = 0; j < camadaEntrada[i].length; j++) {
				Neuronio2D neuronio = camadaEntrada[i][j];
				double somatorio = neuronio.calculaErroPonderado();
				neuronio.setGradiente(neuronio.getGradiente() + (neuronio.getSaida() * somatorio));
				neuronio.setGradienteBias(neuronio.getGradienteBias() + neuronio.getSensibilidade());
			}
		}
					
		
		
	}
	
	
	void atualizaPesos() {

		double max = 50;
		double min = 0.000001;
		double nPos = 1.2;
		double nNeg = 0.5;

		//Atualizando pesos e bias 1D
		if(temCamadaSaida1D){
			camadaSaida.atualizaPesos(max, min, nPos, nNeg);
		}
		
		// Atualizando pesos 2D
		for (Camada2D camada: camadas2D) {
			camada.atualizaPesos(max, min, nPos, nNeg);
			camada.atualizaBias(max, min, nPos, nNeg);
		}
		
		for (int i = 0; i < camadaEntrada.length; i++) {
			for (int j = 0; j < camadaEntrada[i].length; j++) {
				Neuronio2D neuronio = camadaEntrada[i][j];
				neuronio.atualizaPesos(max, min, nPos, nNeg);
				neuronio.atualizaBias(max, min, nPos, nNeg);
			}
		}
		
		
	}

	void zeraGradientes() {
		// Zerando gradientes
		if(temCamadaSaida1D){
			camadaSaida.zeraGradientes();
		}
		
		for (Camada2D camada : camadas2D) {
			camada.zeraGradientes();
		}
		
		for (int i = 0; i < camadaEntrada.length; i++) {
			for (int j = 0; j < camadaEntrada[i].length; j++) {
				Neuronio2D neuronio = camadaEntrada[i][j];
				neuronio.setGradiente(0.0);
				neuronio.setGradienteBias(0.0);	
			}
		}
	}
	
	
	
	public void geraPesosAleatorios(SPNNPesosConfig conf) {
		
		if(temCamadaSaida1D){
			
			for (Neuronio1D neuronio : camadaSaida.getNeuronios()) {
				for (int j = 0; j < neuronio.getPesos().length; j++) {
					neuronio.getPesos()[j] = random(conf.maxPesoCamada1D,conf.minPesoCamada1D,conf.mudaSinalPesoCamada1D);
				}
				neuronio.setPesoBias(random(conf.maxBiasCamada1D,conf.minBiasCamada1D,conf.mudaSinalBiasCamada1D));
			}
			
		}
		
		
		// Gerando pesos entrada
		for (int i = 0; i < camadaEntrada.length; i++) {		
			for (int j = 0; j < camadaEntrada[i].length; j++) {		
				
				Neuronio2D neuron = camadaEntrada[i][j];
				neuron.setPesoAssociado(random(conf.maxPesoCamada2D,conf.minPesoCamada2D,conf.mudaSinalPesoCamada2D));
				neuron.setPesoBias(random(conf.maxBiasCamada2D,conf.minBiasCamada2D,conf.mudaSinalBiasCamada2D));		
		
			}
		}		
		
		for (int c = 0; c < camadas2D.size()-1; c++) {
			Camada2D camada = camadas2D.get(c);	
			List<Neuronio2D> neuronios = camada.getNeuronios();				
			for (Neuronio2D neuron: neuronios){
				
				//neuron.setPesoAssociado(random(conf.maxPesoCamada1D,conf.minPesoCamada1D,conf.mudaSinalPesoCamada1D));
				//neuron.setPesoBias(random(conf.maxBiasCamada1D,conf.minBiasCamada1D,conf.mudaSinalBiasCamada1D));
				
				neuron.setPesoAssociado(random(conf.maxPesoCamada2D,conf.minPesoCamada2D,conf.mudaSinalPesoCamada2D));
				neuron.setPesoBias(random(conf.maxBiasCamada2D,conf.minBiasCamada2D,conf.mudaSinalBiasCamada2D));
				
				//System.out.print(neuronios[i][j].getPesoAssociado()+" "+neuronios[i][j].getPesoBias()+" ");
			}
		}
		//System.out.println("_--------- ");
	}
	
	private double random(double geraMin, double geraMax, boolean mudaSinal) {//Invertido
		double retorno = rand.nextDouble();
		retorno = rand.nextDouble() * (geraMax - geraMin);
		retorno += geraMin;
		double sinal = rand.nextDouble();
		if (mudaSinal && sinal < 0.5) {
			retorno = retorno * (-1);
		}
		return retorno;
	}
	
	
	
	
	public String toStringEstruturaArvore(){
		
		String str = "";
		
		if(temCamadaSaida1D){
			for (Neuronio1D neuronio : camadaSaida.getNeuronios()) {
				str += neuronio.toStringRede();
			}
		}
		
		List<Neuronio2D> neuronios = camadas2D.get(camadas2D.size()-1).getNeuronios();
		for (Neuronio2D neuronio : neuronios) {
			str += neuronio.toStringRede()+";";
		}
		return str;
	}
	
	public String toStringEstruturaCamadas(){
		
		this.sort();
		
		List<Neuronio2D> neuronios = camadas2D.get(camadas2D.size()-1).getNeuronios();
		String str = "";
		for (Neuronio2D neuronio : neuronios) {
			str += neuronio.toStringPosicao()+";";
		}
		str+="\n";
		for (int i = camadas2D.size()-1; i >= 0; i--) {
			
			neuronios = camadas2D.get(i).getNeuronios();
			for (Neuronio2D neuronio : neuronios) {
				str += neuronio.toStringCampos();
			}
			str+="\n";
			
		}
		
		
		List<Neuronio2D> segundaCamada = camadas2D.get(0).getNeuronios();
		Set<Neuronio2D> neuroniosCamadaEntrada = new HashSet<Neuronio2D>();
		for (Neuronio2D neuronio2d : segundaCamada) {
			neuroniosCamadaEntrada.addAll(neuronio2d.getCampoReceptivo());
		}
		str+="\nTotal de neurônios 2D = "+neuroniosCamadaEntrada.size()+",";
		for (Camada2D camada2d : camadas2D) {
			str+= camada2d.getNeuronios().size()+",";
		}
		str+="\n";
		
		return str;
	}
	
	public double[][] getMapaDeCampos(){
		
		double[][] mapa = new double[camadaEntrada.length][camadaEntrada[0].length];
		List<Neuronio2D> neuronios = camadas2D.get(0).getNeuronios();
		
		for (Neuronio2D neuronio : neuronios) {
			
			List<Neuronio2D> campoReceptivo = neuronio.getCampoReceptivo();
			for (Neuronio2D neuronio2 : campoReceptivo) {
				
				int posX = (int)neuronio2.getPosX();
				int posY = (int)neuronio2.getPosY();
				mapa[posX][posY]++;
				
			}
			
		}
		
		return mapa;
		
		
	}
	
	public void sort(){
		for (int i=camadas2D.size()-1;i>=0;i--) {
			
			Camada2D camada = camadas2D.get(i);
			Collections.sort(camada.getNeuronios(), new Comparator<Neuronio2D>() {

				@Override
				public int compare(Neuronio2D o1, Neuronio2D o2) {
					
					int v = Double.compare(o1.getPosX(), o2.getPosX());
					if(v == 0){
						return Double.compare(o1.getPosY(), o2.getPosY());
					}
					return v;
				}
				
				
			});
			
			
		}
	}
	
	public void limpaMemoria(int randPesos){
		
		this.zeraGradientes();
		
		// Zerando gradientes
		if(temCamadaSaida1D){
			camadaSaida.zeraMemoria();
		}
		
		for (Camada2D camada : camadas2D) {
			camada.zeraMemoria();
		}
		
		for (int i = 0; i < camadaEntrada.length; i++) {
			for (int j = 0; j < camadaEntrada[i].length; j++) {
				Neuronio2D neuronio = camadaEntrada[i][j];
				neuronio.zeraMemoria();	
			}
		}
		
		if(randPesos == 0){
			this.rand = new Random();
		}else{
			this.rand = new Random(randPesos);
		}	
	}
	
	
}
