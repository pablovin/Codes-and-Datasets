package util;

public class Util {


	public static double calculaDistanciaEuclidiana(double[] pos1, double[] pos2){
		
		double acc = 0;
		for (int i = 0; i < pos2.length; i++) {
			acc += Math.pow(pos1[i]- pos2[i], 2);
		}
		
		return Math.sqrt(acc);
	}
	
	public static double[] getCentroTriangulo(double[][] pontos){
		
		
		double[][] numX = new double[][]{{Math.pow(pontos[0][0],2)+Math.pow(pontos[0][1],2), pontos[0][1], 1},
										 {Math.pow(pontos[1][0],2)+Math.pow(pontos[1][1],2), pontos[1][1], 1},
										 {Math.pow(pontos[2][0],2)+Math.pow(pontos[2][1],2), pontos[2][1], 1}};

		
		double[][] numY = new double[][]{{pontos[0][0], Math.pow(pontos[0][0],2)+Math.pow(pontos[0][1],2), 1},
										 {pontos[1][0], Math.pow(pontos[1][0],2)+Math.pow(pontos[1][1],2), 1},
										 {pontos[2][0], Math.pow(pontos[2][0],2)+Math.pow(pontos[2][1],2), 1}};
		
		
		double[][] den = new double[][]{{pontos[0][0], pontos[0][1], 1},
										{pontos[1][0], pontos[1][1], 1},
										{pontos[2][0], pontos[2][1], 1}};
		
		double detDen = 2 * determinante(den);
		
		double cx = determinante(numX)/detDen;
		double cy = determinante(numY)/detDen;
		
		return new double[]{cx,cy};
		
	}
	
	public static double determinante(double[][] matriz){
		
		double accPos = 0, accNeg = 0;
		int tamanho = matriz.length;

		for (int k = 0; k < tamanho; k++) {
				
				double mulPos = 1;
				
				for (int i = 0; i < tamanho; i++) {
					
						int iPos = (i+k)%tamanho;						
						mulPos = mulPos * matriz[i][iPos];		
					
				}
				
				accPos += mulPos;
			
		}
		
		for (int k = 0; k < tamanho; k++) {
			
			double mulNeg = 1;
			
			for (int i = tamanho - 1,j = 0; i >= 0 && j < tamanho; i--, j++) {
				
					int iNeg = i-k < 0? tamanho + (i- k) : i-k ;						
					mulNeg = mulNeg * matriz[j][iNeg];		
			
			}
			accNeg += mulNeg;
		
	}
		
		return accPos - accNeg;
		
	}

	
	public static double[] mediaDePontos(double[][] pontos){
		
		int qt = pontos.length;
		int dim = pontos[0].length;
		double[] media = new double[dim];
		
		for (int i = 0; i < dim; i++) {
			double total = 0;
			for (int j = 0; j < qt; j++) {
				total += pontos[j][i];
			}
			media[i] = total/qt;
		}
		
		return media;
	}
	
	public static void main(String[] args) {
		
		double[][] pontos = new double[][]{{1,1},{3,1},{2,3}};
		double[] centro = getCentroTriangulo(pontos);
		System.out.println(centro[0]+" , "+centro[1]);
		
	}
	
	
}
