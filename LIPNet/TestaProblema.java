import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;
import util.ArquivoImagemUtil;
import lipnet.LIPNet;

public class TestaProblema {

	static int dividePixels = 650;

	int subtraiPixels = 128;

	public void preencheImagem(String caminho, ArrayList<double[][]> entrada) throws Exception {
		try {
			File f = new File(caminho);
			BufferedImage image = ImageIO.read(f);
			double[][] imagem = new double[image.getHeight()][image.getWidth()];
			for (int y = 0; y < image.getHeight(); ++y) {
				for (int x = 0; x < image.getWidth(); ++x) {
					double[] tmp = new double[1];
					image.getRaster().getPixel(x, y, tmp);
					imagem[y][x] = (tmp[0] - subtraiPixels) / dividePixels;
				}
			}
			entrada.add(imagem);
		} catch (Exception e) {
			System.out.println(caminho);
			e.printStackTrace();
		}
	}

	public double[][] recuperaImagem(String caminho)
			throws Exception {
		try {
			File f = new File(caminho);
			BufferedImage image = ImageIO.read(f);
			double[][] imagem = new double[image.getHeight()][image.getWidth()];
			for (int y = 0; y < image.getHeight(); ++y) {
				for (int x = 0; x < image.getWidth(); ++x) {
					double[] tmp = new double[3];
					image.getRaster().getPixel(x, y, tmp);
					imagem[y][x] = (tmp[0] - subtraiPixels) / dividePixels;
				}
			}
			return imagem;
		} catch (Exception e) {
			System.out.println(caminho);
			e.printStackTrace();
			return null;
		}
	}

	public void preencheDados(ArrayList<double[][]> entradaA,
			ArrayList<double[][]> entradaB) throws Exception {

		// Classe 1
		for (int h = 1; h <= 1000; h++) {
			preencheImagem(
					"caminho"+ h + ".jpg", entradaA);

		}

		// Classe 2
			for (int h = 1; h <= 1000; h++) {
				preencheImagem(
						"caminho"+ h + ".jpg", entradaB);

			}
		
		}
		
	public static void main(String[] args) {
		int r1 = 4;
		int r2 = 3;
		int o1 = 1;
		int o2 = 0;
		int c1 = new Double(Math.floor((19.0 - new Integer(o1).doubleValue())
				/ (new Integer(r1 - o1).doubleValue()))).intValue();
		int c2 = new Double(Math.floor((c1 - new Integer(o2).doubleValue())
				/ (new Integer(r2 - o2).doubleValue()))).intValue();
		int i1 = 2;
		int i2 = 0;		
		double v1 = 1.6;
		double v2 = 0;

		System.out.println("Calculando resultados para:");
		System.out.println("- Camadas = " + c1 + " | " + c2);
		System.out.println("- Receptivo = " + r1 + " | " + r2);
		System.out.println("- Overlap = " + o1 + " | " + o2);
		System.out.println("- Inibitório = " + i1 + " | " + i2);
		System.out.println("- Peso inibitório = " + v1 + " | " + v2);

		dividePixels = 650;

		double area = 0;
		double[][] areas = new double[21][2];
		int repeticoes = 10;
		for (int z = 0; z < repeticoes; z++) {
			System.out.println("Teste " + z);
			area += treinaBase(0,500, new int[] { i1, i2 }, new double[] { v1, v2 },
					new int[] { c1, c2 }, new int[] { r1, r2 }, new int[] { o1,
							o2 }, areas);
		}
		System.out.println("Média geral = " + (area / repeticoes));

	}

	public static double treinaBase(int escondida, int epocas, int[] inib,
			double[] pesosInib, int[] camadas, int[] receptivo, int[] overlap,
			double[][] areas) {

		double retorno = 0;

		double[] limiares2 = new double[] { 0.0, 0.05, 0.1, 0.15, 0.2, 0.25,
				0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8,
				0.85, 0.9, 0.95 };

		try {

			ArrayList<double[][]> entradaA = new ArrayList<double[][]>();
			ArrayList<double[][]> entradaB = new ArrayList<double[][]>();

			TestaProblema testa = new TestaProblema();

			testa.preencheDados(entradaA, entradaB);
			

			LIPNet rede = null;
			int[] mlp = null;
			if (escondida > 0) {
				mlp = new int[] { escondida, 2 };
			} else {
				mlp = new int[] { 2 };
			}
			rede = new LIPNet(mlp,
					new int[] { 19, camadas[0], camadas[1] }, new int[] { 1,
							receptivo[0], receptivo[1] }, new int[] { 0,
							overlap[0], overlap[1] }, new int[] { 0, inib[0],
							inib[1] }, new double[] { 0, pesosInib[0],
							pesosInib[1] }, 0);

			rede.treinaDicotomia(epocas, entradaA, entradaB, 100);

			retorno = testa(rede, testa, limiares2, areas);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return retorno;

	}

	public static double testa(LIPNet rede, TestaFace testa,
			double[] limiares2, double[][] areaTotal) throws Exception {
		ArrayList<Double> positivos = new ArrayList<Double>();
		ArrayList<Double> falsosPositivos = new ArrayList<Double>();
		
		ArrayList<double[][]> imagensPositivas = new ArrayList<double[][]>();
		ArrayList<double[][]> imagensNegativas = new ArrayList<double[][]>();

		int totalImagensF = 472;
		int totalImagensNF = 23573;

		for (int i = 1; i <= totalImagensF; i++) {
			double[][] entrada = testa.recuperaImagem("caminho"+ i + ".jpg");
			imagensPositivas.add(entrada);
			double[] saida = rede.calculaSaidaRede(entrada);
			positivos.add(saida[0]);
		}
		for (int i = 1; i <= totalImagensNF; i++) {
			double[][] entrada = testa.recuperaImagem("caminho"+ i + ".jpg");
			imagensNegativas.add(entrada);
			double[] saida = rede.calculaSaidaRede(entrada);
			falsosPositivos.add(saida[0]);
		}

		Collections.sort(positivos);
		Collections.reverse(positivos);
		Collections.sort(falsosPositivos);
		Collections.reverse(falsosPositivos);

		double[][] roc = new double[21][2];

		for (int i = 0; i < limiares2.length; i++) {
			int indice = new Double(Math.round(totalImagensNF * limiares2[i]))
					.intValue();
			double valor = falsosPositivos.get(indice);
			int indicePos = 0;
			while (indicePos < totalImagensF
					&& positivos.get(indicePos) > valor) {
				indicePos++;
			}
			int indice2 = indice;
			boolean continua = true;
			double percentualTruePositive = new Integer(indicePos)
					.doubleValue()
					/ totalImagensF;
			double percentualFalsePositive = new Integer(indice2).doubleValue()
					/ totalImagensNF;
			roc[i][0] = percentualTruePositive;
			roc[i][1] = percentualFalsePositive;
			areaTotal[i][0] +=  percentualTruePositive;
			areaTotal[i][1] += percentualFalsePositive;
		}

		roc[20][0] = 1;
		roc[20][1] = 1;
		double area = calculaArea(roc);
		System.out.println("Área da curva = " + area);
		
		return area;
	}

}
