package lipnet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import util.Imagem;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

public class LIPNet implements Comparable<LIPNet> {

	public static final double a = 2;
	
	private static volatile long seedUniquifier = 8682522807148012L;

	public Camada1D[] camadas1D;

	public Camada2D[] multiCamadas2D;

	private boolean crossEntropy = false;

	private Double compara;
	
	private Random rand;
	
	private boolean randFixo;

	public LIPNet() {

	}
	
	public LIPNet(int[] tamanhoCamadas1D,
			int[] tamanhoCamadas2D, int[] tamanhoCampos2D,
			int[] tamanhoOverlap2D, int[] tamanhoInibitorios2D, double[] pesosInibitorio,
			int rand) throws Exception {
		if (tamanhoCamadas2D.length != tamanhoCampos2D.length
				|| tamanhoCamadas2D.length != tamanhoOverlap2D.length) {
			throw new Exception(
					"Dados de entrada incompatíveis para as camadas 2D");
		}

			this.multiCamadas2D = new Camada2D[tamanhoCamadas2D.length];

		// 2 camadas 2D e 1 1D
		this.camadas1D = new Camada1D[tamanhoCamadas1D.length];

			for (int i = 0; i < tamanhoCamadas2D.length; i++) {

				this.multiCamadas2D[i] = new Camada2D(
						tamanhoCamadas2D[i], tamanhoCamadas2D[i], i,
						tamanhoCampos2D[i], tamanhoOverlap2D[i],
						tamanhoInibitorios2D[i], pesosInibitorio[i], i == 0);
			}

		for (int i = 0; i < tamanhoCamadas1D.length; i++) {

			if (i == 0) {
				int t = tamanhoCamadas2D[tamanhoCamadas2D.length - 1];
				t = t * t;

				this.camadas1D[i] = new Camada1D(tamanhoCamadas1D[i], i, t);
			} else {
				this.camadas1D[i] = new Camada1D(tamanhoCamadas1D[i], i,
						tamanhoCamadas1D[i - 1]);
			}
		}

		if(rand == 0) {
			this.rand = new Random();
			this.randFixo = false;
		} else {
			this.rand = new Random(rand);
			this.randFixo = true;
		}
		this.geraPesosAleatorios();
	}

	public double[] calculaSaidaRede(double[][] entrada) {
		// Fase Forward
		// Camadas 2D, considerando que a imagem já foi convertida para camada
		// inicial da rede neural
			for (int i = 0; i < multiCamadas2D.length; i++) {
				Camada2D camada2D = multiCamadas2D[i];
				if (i == 0) {
					camada2D.atualizaSaidaNeuronios(entrada);
				} else {
					camada2D
							.atualizaSaidaNeuronios(multiCamadas2D[i - 1]
									.getNeuronios());
				}
			}
		// Transformar saida neuronios 2D para camadas 1D
		Neuronio1D[] entradas1D = rearrumaSaida();

		// Camadas 1D
		for (int i = 0; i < camadas1D.length; i++) {
			camadas1D[i].atualizaSaidaNeuronios(entradas1D, crossEntropy
					&& i == camadas1D.length - 1);
			entradas1D = camadas1D[i].getNeuronios();
		}
		// Passando saidas da rede para um array de retorno
		double[] retorno = new double[camadas1D[camadas1D.length - 1]
				.getNeuronios().length];
		for (int i = 0; i < camadas1D[camadas1D.length - 1].getNeuronios().length; i++) {
			retorno[i] = camadas1D[camadas1D.length - 1].getNeuronios()[i]
					.getSaida();
		}

		return retorno;
	}
	
	public double[] extraiVetorCaracteristicas(double[][] entrada) {
		// Fase Forward
		// Camadas 2D, considerando que a imagem já foi convertida para camada
		// inicial da rede neural
			for (int i = 0; i < multiCamadas2D.length; i++) {
				Camada2D camada2D = multiCamadas2D[i];
				if (i == 0) {
					camada2D.atualizaSaidaNeuronios(entrada);
				} else {
					camada2D
							.atualizaSaidaNeuronios(multiCamadas2D[i - 1]
									.getNeuronios());
				}
			}
		// Transformar saida neuronios 2D para camadas 1D
		Neuronio1D[] neuronioSaidas = rearrumaSaida();

		double[] retorno = new double[neuronioSaidas.length];
		for (int i = 0; i < retorno.length; i++) {
			retorno[i] = neuronioSaidas[i].getSaida();
		}

		return retorno;
	}

	public Neuronio1D[] rearrumaSaida() {
		Neuronio1D[] entradas1D = multiCamadas2D[multiCamadas2D.length - 1]
				.rearrumaSaida();

		return entradas1D;
	}

	public void treina(double[][] entrada, double[] saida,
			int quantidadeEntradas, boolean ajustaSomenteErro) {
		// Fase Forward
		// Camadas 2D, considerando que a imagem já foi convertida para camada
		// inicial da rede neural
			for (int i = 0; i < multiCamadas2D.length; i++) {
				Camada2D camada2D = multiCamadas2D[i];
				if (i == 0) {
					camada2D.atualizaSaidaNeuronios(entrada);
				} else {
					camada2D
							.atualizaSaidaNeuronios(multiCamadas2D[i - 1]
									.getNeuronios());
				}
			}

		// Transformar saida neuronios 2D para camadas 1D
		Neuronio1D[] entradas1D = rearrumaSaida();
		// Camadas 1D
		for (int i = 0; i < camadas1D.length; i++) {
			camadas1D[i].atualizaSaidaNeuronios(entradas1D, crossEntropy
					&& i == camadas1D.length - 1);
			entradas1D = camadas1D[i].getNeuronios();
		}

		double saidaTmp = camadas1D[camadas1D.length - 1].getNeuronios()[0]
				.getSaida();
		if (!ajustaSomenteErro
				|| ((saidaTmp >= 0.5 && saida[0] < 0.5) || (saidaTmp < 0.5 && saida[0] >= 0.5))) {
			// Fase backward
			// Sensibilidade erro da camada da saída
			double[] erro = new double[saida.length];
			double[] erroTmp = new double[saida.length];
			//System.out.println("Erro");
			for (int i = 0; i < saida.length; i++) {
				erro[i] = camadas1D[camadas1D.length - 1].getNeuronios()[i]
						.getSaida()
						- saida[i];
				erroTmp[i] = erro[i];
				//System.out.print(erro[i]+" ");
				//erro[i] += erroAnterior[i];
			}
			//System.out.println();
			// erroAnterior = erroTmp;
			if (camadas1D.length - 2 >= 0) {
				camadas1D[camadas1D.length - 1].atualizaSensibilidadeSaida(
						quantidadeEntradas, erro,
						camadas1D[camadas1D.length - 2].getNeuronios());
			} else {
				camadas1D[camadas1D.length - 1].atualizaSensibilidadeSaida(
						quantidadeEntradas, erro, rearrumaSaida());
			}
			// Demais camadas 1D
			for (int i = camadas1D.length - 2; i >= 0; i--) {
				if (i == 0) {
					camadas1D[i].atualizaSensibilidadesOcultas(rearrumaSaida(),
							camadas1D[i + 1].getNeuronios());
				} else {
					camadas1D[i].atualizaSensibilidadesOcultas(camadas1D[i - 1]
							.getNeuronios(), camadas1D[i + 1].getNeuronios());
				}

			}
			// Rearruma sensibilidade para última camada piramidal
			int acumulada = 0;
				if (multiCamadas2D.length - 2 >= 0) {
					multiCamadas2D[multiCamadas2D.length - 1].atualizaSensibilidadeSaida(
							multiCamadas2D[multiCamadas2D.length - 2].getNeuronios(),
							camadas1D[0].getNeuronios(), acumulada);

					acumulada += multiCamadas2D[multiCamadas2D.length - 1].getTamanhoX()
							* multiCamadas2D[multiCamadas2D.length - 1].getTamanhoY();

				} else {
					multiCamadas2D[multiCamadas2D.length - 1].atualizaSensibilidadeSaida(
							entrada, camadas1D[0].getNeuronios(),
							acumulada);

					acumulada += multiCamadas2D[multiCamadas2D.length - 1].getTamanhoX()
							* multiCamadas2D[multiCamadas2D.length - 1].getTamanhoY();
				}


			// Calcula sensibilidade nas demais camadas piramidais
				for (int i = multiCamadas2D.length - 2; i >= 0; i--) {
					if (i == 0) {
						multiCamadas2D[i].atualizaSensibilidadesOcultas(
								multiCamadas2D[i + 1].getNeuronios(),
								multiCamadas2D[i + 1].getReceptivo(),
								multiCamadas2D[i + 1].getOverlap(), multiCamadas2D[i + 1]
										.getCampoInibicao(), multiCamadas2D[i + 1]
										.getTamanhoX(), multiCamadas2D[i + 1]
										.getTamanhoY());
					} else {
						multiCamadas2D[i].atualizaSensibilidadesOcultas(
								multiCamadas2D[i + 1].getNeuronios(),
								multiCamadas2D[i + 1].getReceptivo(),
								multiCamadas2D[i + 1].getOverlap(), multiCamadas2D[i + 1]
										.getCampoInibicao(), multiCamadas2D[i + 1]
										.getTamanhoX(), multiCamadas2D[i + 1]
										.getTamanhoY());
					}
				}
			// Calculando gradientes 1D
			for (int i = 0; i < camadas1D.length; i++) {
				if (i == 0) {
					camadas1D[i].calculaGradientes(rearrumaSaida());
				} else {
					camadas1D[i].calculaGradientes(camadas1D[i - 1]
							.getNeuronios());
				}
			}

			// Calculando gradientes 2D

				for (int i = 0; i < multiCamadas2D.length - 1; i++) {
					multiCamadas2D[i].calculaGradientesPesos(multiCamadas2D[i + 1]
							.getNeuronios(), multiCamadas2D[i + 1].getTamanhoX(),
							multiCamadas2D[i + 1].getTamanhoY(), multiCamadas2D[i + 1]
									.getReceptivo(), multiCamadas2D[i + 1]
									.getOverlap(), multiCamadas2D[i + 1]
									.getCampoInibicao());
				}

			// Calculando bias 2D
				for (int i = 0; i < multiCamadas2D.length; i++) {
					multiCamadas2D[i].calculaGradientesBias();
				}
		}
	}

	public void atualizaPesos() {

		double max = 50;
		double min = 0.000001;
		double nPos = 1.2;
		double nNeg = 0.5;

		// Atualizando pesos 1D
		for (int i = 0; i < camadas1D.length; i++) {
			camadas1D[i].atualizaPesos(max, min, nPos, nNeg);
		}
		// Atualizando pesos 2D
			for (int i = 0; i < multiCamadas2D.length - 1; i++) {
				multiCamadas2D[i].atualizaPesos(max, min, nPos, nNeg);
			}
		// Calculando bias 2D
			for (int i = 0; i < multiCamadas2D.length; i++) {
				multiCamadas2D[i].atualizaBias(max, min, nPos, nNeg);
			}
	}

	public void zeraGradientes() {
		// Zerando gradientes
		for (Camada1D camada : camadas1D) {
			camada.zeraGradientes();
		}
			for (Camada2D camada2D : multiCamadas2D) {
				camada2D.zeraGradientes();
			}
		// for (int i = 0; i < erroAnterior.length; i++) {
		// erroAnterior[i] = 0;
		// }
	}

	public double random(double geraMax, double geraMin, boolean mudaSinal) {
		double retorno = rand.nextDouble();
		// while (retorno > quantidade) {
		retorno = rand.nextDouble() * (geraMax - geraMin);
		// }
		retorno += geraMin;
		double sinal = rand.nextDouble();
		if (mudaSinal && sinal < 0.5) {
			retorno = retorno * (-1);
		}
		return retorno;
	}
	

	//public static double geraMin = 0.0;

	//public static double geraMax = 1.0;
	
	public void geraPesosAleatorios() {
		// Gerando os pesos 1D aleatoriamente

		double inicio = 1;
		for (int i = 0; i < camadas1D.length; i++) {
			Camada1D camada1D = camadas1D[i];
			for (Neuronio1D neuronio : camada1D.getNeuronios()) {
				for (int j = 0; j < neuronio.getPesos().length; j++) {
					neuronio.getPesos()[j] = random(1.0,0.0, true);
				}
				neuronio.setPesoBias(random(0.001,0.0, true));
				
			
			}
		}

		// Gerando pesos 2D
			for (int c = 0; c < multiCamadas2D.length-1; c++) {
				Camada2D camada2D = multiCamadas2D[c];
				for (int i = 0; i < camada2D.getTamanhoX(); i++) {
					for (int j = 0; j < camada2D.getTamanhoY(); j++) {
						
						camada2D.getNeuronios()[i][j]
								.setPesoAssociado(random(0.1,0.0,false));
						camada2D.getNeuronios()[i][j]
								.setPesoBias(random(0.0001,0.0,false));
						
				}
					
					
			}
	}



	public void resetaElementosPosTreino() {
		for (Camada1D camada : camadas1D) {
			camada.limpaMemoria();
		}
	}

	public void setCrossEntropy(boolean crossEntropy) {
		this.crossEntropy = crossEntropy;
	}

	public int compareTo(LIPNet o) {
		return this.compara.compareTo(o.compara);
	}

	public Double getCompara() {
		return compara;
	}

	public void setCompara(Double compara) {
		this.compara = compara;
	}

	
	public int maxIndice(double[] param) {
		double max = param[0];
		int retorno = 0;
		for (int i = 1; i < param.length; i++) {
			if(param[i] > max) {
				max = param[i];
				retorno = i;
			}
		}
		return retorno;
	}
	
	
	public void treinaDicotomia(int quantidadeMaximaEpocas,
			ArrayList<double[][]> positivos, ArrayList<double[][]> negativos, int numElementos)
			throws Exception {
		this.crossEntropy = true;
		double area = 0.0;
		int epoca = 0;
		int qtdRepeticoes = 0;
		while(area < 0.97) {
			if(epoca > quantidadeMaximaEpocas) {
				geraPesosAleatorios();
				epoca = 0;
				qtdRepeticoes++;
			}
			if(qtdRepeticoes == 5) {
				break;
			}
			Collections.shuffle(negativos);
			Collections.shuffle(positivos);
			if(numElementos == 0) {
				for (int j = 0; j < negativos.size(); j++) {
					this.treina(positivos.get(j % positivos.size()), new double[] {
							1.0, 0.0 }, 1, false);
					this.treina(negativos.get(j), new double[] { 0.0, 1.0 }, 1,
							false);
	
				}
			} else {
				for (int j = 0; j < numElementos; j++) {
					this.treina(positivos.get(j), new double[] {
							1.0, 0.0 }, 1, false);
					this.treina(negativos.get(j), new double[] { 0.0, 1.0 }, 1,
							false);
	
				}
			}
			Camada2D.imprime = false;

			this.atualizaPesos();
			this.zeraGradientes();
			area = avaliaRede(positivos, negativos);
			epoca++;
		}
	}
	
	private double avaliaRede(ArrayList<double[][]> positivos, ArrayList<double[][]> negativos) {
		
		ArrayList<Double> positivosRoc = new ArrayList<Double>();
		ArrayList<Double> falsosPositivosRoc = new ArrayList<Double>();
		double acertosPos = 0;
		for (double[][] ds : positivos) {
			double[] saida = this.calculaSaidaRede(ds);
			if (saida[0] > saida[1]) {
				acertosPos++;
			}
			positivosRoc.add(saida[0]);
		}
		double acertosNeg = 0;
		for (double[][] ds : negativos) {
			double[] saida = this.calculaSaidaRede(ds);
			if (saida[0] < saida[1]) {
				acertosNeg++;
			}
			falsosPositivosRoc.add(saida[0]);
		}
		
		Collections.sort(positivosRoc);
		Collections.reverse(positivosRoc);
		Collections.sort(falsosPositivosRoc);
		Collections.reverse(falsosPositivosRoc);
		
		double[][] roc = new double[21][2];

		for (int i = 0; i < 20; i++) {
			double limiar = 0.05*i;
			int indice = new Double(Math.floor(falsosPositivosRoc.size() * limiar))
					.intValue();
			double valor = falsosPositivosRoc.get(indice);
			int indicePos = 0;
			while (indicePos < positivosRoc.size()
					&& positivosRoc.get(indicePos) > valor) {
				indicePos++;
			}
			int indice2 = indice;
			boolean continua = true;
			//for (int j = indice + 1; j < totalImagensNF && continua; j++) {
				//if (falsosPositivos.get(j) == valor) {
					//indice2 = j;
				//} else {
					//continua = false;
				//}
			//}
			double percentualTruePositive = new Integer(indicePos)
					.doubleValue()
					/ positivosRoc.size();
			double percentualFalsePositive = new Integer(indice2).doubleValue()
					/ falsosPositivosRoc.size();
			roc[i][0] = percentualTruePositive;
			roc[i][1] = percentualFalsePositive;
		}

		roc[20][0] = 1;
		roc[20][1] = 1;
		double area = calculaArea(roc);
		return area;
	}
	

	public void print() {
		camadas1D[0].print();
	}

}
