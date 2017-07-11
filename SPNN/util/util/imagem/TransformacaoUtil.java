package util.imagem;

public class TransformacaoUtil {

	public static double media(double[][] imagem) {

		double media = 0;

		for (int i = 0; i < imagem.length; i++) {
			for (int j = 0; j < imagem[0].length; j++) {
				media += imagem[i][j];
			}
		}

		return media / (imagem.length * imagem[0].length);

	}

	public static double desvioPadrao(double[][] imagem) {

		double media = media(imagem);
		double desvio = 0;

		for (int i = 0; i < imagem.length; i++) {
			for (int j = 0; j < imagem[0].length; j++) {
				desvio += Math.pow(imagem[i][j] - media, 2);
			}
		}

		desvio = desvio / (imagem.length * imagem[0].length);

		return Math.sqrt(desvio);

	}

	public static double[][] convoluir(double[][] imagem, double[][] mascara) {

		int altura = imagem.length, largura = imagem[0].length;
		int raio = mascara.length / 2;// máscara quadrada
		double[][] imagemFinal = new double[altura][largura];

		for (int i = raio; i < altura - raio; i++) {
			for (int j = raio; j < largura - raio; j++) {

				imagemFinal[i][j] = 0;

				for (int k = -raio; k <= raio; k++) {
					for (int l = -raio; l <= raio; l++) {
						imagemFinal[i][j] += imagem[i + k][j + l]
								* mascara[raio + k][raio + l];
					}
				}

			}
		}

		return imagemFinal;

	}

	public static double[][] somarImagens(double[][] imagemA, double[][] imagemB) {

		double[][] soma = new double[imagemA.length][imagemA[0].length];

		for (int i = 0; i < imagemA.length; i++) {
			for (int j = 0; j < imagemA[0].length; j++) {
				soma[i][j] = imagemA[i][j] + imagemB[i][j];
			}
		}

		return soma;
	}

	public static double[][] subtrairImagens(double[][] imagemA,
			double[][] imagemB) {

		double[][] subtracao = new double[imagemA.length][imagemA[0].length];

		for (int i = 0; i < imagemA.length; i++) {
			for (int j = 0; j < imagemA[0].length; j++) {
				subtracao[i][j] = imagemA[i][j] - imagemB[i][j];
			}
		}

		return subtracao;
	}

	public static double[] pixelMinimoMaximo(double[][] imagem) {

		double minimo = imagem[0][0];
		double maximo = imagem[0][0];
		for (int i = 0; i < imagem.length; i++) {
			for (int j = 0; j < imagem[0].length; j++) {
				if (minimo > imagem[i][j])
					minimo = imagem[i][j];
				if (maximo < imagem[i][j])
					maximo = imagem[i][j];
			}
		}

		return new double[] { minimo, maximo };// 0-minimo; 1-maximo
	}

	public static double[][] normalizarImagem(double[][] imagem) 
	{	
		return normalizarImagem(imagem, 0, 1);
	}
	
	public static double[][] normalizarImagem(double[][] imagem, double inicio, double fim)
	{

		double[] minMax = pixelMinimoMaximo(imagem);
		
		double minimo = minMax[0];//0;
		double maximo = minMax[1];//255;

		//double[][] imgNormal = new double[imagem.length][imagem[0].length];
				
		//double fator = (fim-inicio) / (maximo - minimo + 1);
		double fator;
		if(maximo == minimo)
			fator = 0;
		else
			fator = (fim-inicio) / (maximo - minimo);
		
		//double fator = (fim-inicio) / (maximo - minimo);
		
		//System.out.println("Imprimindo normalizacao");
		for (int i = 0; i < imagem.length; i++) {
			for (int j = 0; j < imagem[0].length; j++) {
				//imgNormal[i][j] = (imagem[i][j] - minimo) * fator + inicio;
				imagem[i][j] = (imagem[i][j] - minimo) * fator + inicio;
				//System.out.println(imgNormal[i][j]+"	"+imagem[i][j]);
				//if(Double.isNaN(imgNormal[i][j]) || Double.isInfinite(imgNormal[i][j]))
				if(Double.isNaN(imagem[i][j]) || Double.isInfinite(imagem[i][j]))
				{
					System.out.println("NAN NA NORMALIZACAO!");
					System.out.println(imagem[i][j]);
					System.out.println(maximo);
					System.out.println(minimo);
					System.out.println(fator);
					System.out.println(inicio);
					System.out.println(fim);
					System.out.println(maximo-minimo);
					System.out.println(fim-inicio);
					System.out.println((fim-inicio) / (maximo - minimo));
				}
				
			}
		}

		return imagem;
		//return imgNormal;
		
		
	}

	public static double[][] clarearImagem(double[][] imagem, int fator) {
		double[][] imagemClara = new double[imagem.length][imagem[0].length];
		for (int i = 0; i < imagem.length; i++) {
			for (int j = 0; j < imagem[0].length; j++) {
				imagemClara[i][j] = imagem[i][j] + fator;
			}
		}

		return imagemClara;
	}

	public static double[][] moduloImagem(double[][] imagem) {
		double[][] mod = new double[imagem.length][imagem[0].length];
		for (int i = 0; i < imagem.length; i++) {
			for (int j = 0; j < imagem[0].length; j++) {
				mod[i][j] = Math.abs(imagem[i][j]);
			}
		}
		return mod;
	}

	public static double[][] multiplicarImagens(double[][] imagem1,
			double[][] imagem2) {

		double[][] mult = new double[imagem2.length][imagem2[0].length];

		for (int i = 0; i < imagem2.length; i++) {
			for (int j = 0; j < imagem2[0].length; j++) {

				mult[i][j] = imagem1[i][j] * imagem2[i][j];

			}
		}

		return mult;
	}


	public static double[][] cortarImagem(double[][] imagem, int x, int larguraX,
			int y, int larguraY) throws Exception {

		if (x + larguraX > imagem.length || y + larguraY > imagem[0].length)
			throw new Exception("Wrong Argumento for Image Cut");

		double[][] cut = new double[larguraX][larguraY];

		for (int i = 0; i < larguraX; i++) {

			for (int j = 0; j < larguraY; j++) {

				cut[i][j] = imagem[i+x][j+y];
				
			}

		}
		
		return cut;

	}
	

}
