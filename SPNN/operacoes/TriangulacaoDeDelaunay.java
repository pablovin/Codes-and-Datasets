package operacoes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import operacoes.geometria.Circulo;
import operacoes.geometria.Triangulo;
import util.Util;


public class TriangulacaoDeDelaunay {

	public synchronized static List<Triangulo> triangular(double[][] posicoes){
		
		List<Triangulo> triangulos = new ArrayList<Triangulo>();
		Map<Circulo,Integer> mapaCirculoPontoReferencia = new HashMap<Circulo, Integer>();
		
		for (int i = 0; i < posicoes.length; i++) {
			System.out.print(i+",");
			for (int j = i+1; j < posicoes.length; j++) {
				
				for (int k = j+1; k < posicoes.length; k++) {
					
					double[] centro = Util.getCentroTriangulo(new double[][]{posicoes[i],posicoes[j],posicoes[k]});
					
					double raio = Util.calculaDistanciaEuclidiana(centro, posicoes[i]);
					
					boolean ehTriangulo = true, maisDeUmTriangulo = false;
					
					if(!Double.isNaN(raio) && !Double.isInfinite(raio) && raio < 100000000){ //limite de raio para evitar erros de arredondamento
						
						Map<Integer,double[]> pontosEmUmCirculo = new HashMap<Integer, double[]>();
												
						for (int l = 0; l < posicoes.length; l++) {
							if(l != i && l != j && l != k){					
								double dist = Util.calculaDistanciaEuclidiana(centro, posicoes[l]);
								if(dist < raio){
									ehTriangulo = false;
									break;
								}	
								else if(dist == raio){
									maisDeUmTriangulo = true;
									pontosEmUmCirculo.put(l, posicoes[l]);
								}					
							}
						}
						
						if(ehTriangulo){
							if(maisDeUmTriangulo){
								Circulo circ = new Circulo(centro,raio);
								Integer indice = mapaCirculoPontoReferencia.get(circ);
								if(indice == null){
									
									pontosEmUmCirculo.put(i, posicoes[i]);
									pontosEmUmCirculo.put(j, posicoes[j]);
									pontosEmUmCirculo.put(k, posicoes[k]);
									
									int[] ordem = ordenarNoSentidoHorario(pontosEmUmCirculo,centro,raio);
									
									for (int l = 1; l < ordem.length-1; l++) {
										
										
										triangulos.add(new Triangulo(new int[]{ordem[0],ordem[l],ordem[l+1]}, circ));
									}

									mapaCirculoPontoReferencia.put(circ, ordem[0]);
									
								}
							}else{
								triangulos.add(new Triangulo(new int[]{i,j,k},new Circulo(centro,raio)));
							}
							
						}
					}
					
				}
				
			}
			
			
		}
		
				
		return triangulos;
		
	}
	
	private static int[] ordenarNoSentidoHorario(Map<Integer,double[]> pontos, double[] centro, double raio){
				
		int[] novaOrdem = new int[pontos.size()];
		
		List<Entry<Integer,double[]>> pts = new ArrayList<Entry<Integer,double[]>>(pontos.entrySet());
		ArrayList<Integer> quadrantes = getQuadrantes(pts, centro);		
		
		int i = 0;
		Entry<Integer,double[]> corrente = pts.remove(i);
		int quadranteAtual = quadrantes.remove(i);
		int sentidoAtual = getSentidoPorQuadrante(quadranteAtual);
		novaOrdem[i] = corrente.getKey();
		double[] posicaoCorrente = corrente.getValue();
		i++;
		int cont = 0;
		while(pts.size() > 0){

			
			List<Integer> indices = getIndicesNoSentidoHorario(posicaoCorrente, pts, quadrantes, quadranteAtual, sentidoAtual);
			ArrayList<Entry<Integer,double[]>> ptsCopy =  new ArrayList<Entry<Integer,double[]>>(pts);
			for (int indice : indices) {

				corrente = pts.get(indice);
				int i2 = ptsCopy.indexOf(corrente);
				ptsCopy.remove(i2);
				quadrantes.remove(i2);
				novaOrdem[i] = corrente.getKey();
				i++;
			}
			pts = ptsCopy;
			quadranteAtual = (quadranteAtual+1)%5 == 0? 1:(quadranteAtual+1)%5;
			sentidoAtual = getSentidoPorQuadrante(quadranteAtual);
			posicaoCorrente = getReferenciaQuandrante(quadranteAtual, centro, raio);
			cont++;
		}	
		
		if(cont>5){
			System.out.println("OOPS cont>5!!!");
		}
		
		return novaOrdem;
	}
	
	private static List<Integer> getIndicesNoSentidoHorario(
			double[] posicaoCorrente, List<Entry<Integer, double[]>> pts,
			List<Integer> quadrantes, int quadranteAtual, int sentidoAtual) {
		
		List<Integer> indices = new ArrayList<Integer>();
		List<Double> distancias = new ArrayList<Double>();
		
 		for (int i = 0; i < pts.size(); i++) {
			
			int sentido = getSentido(posicaoCorrente, pts.get(i).getValue());
			if(quadrantes.get(i) == quadranteAtual && 
					(sentido == sentidoAtual || sentido == 8)){
				
				indices.add(i);
				distancias.add(Util.calculaDistanciaEuclidiana(posicaoCorrente, pts.get(i).getValue()));
			}
			
		}
		
 		List<Integer> indicesOrd = new ArrayList<Integer>();
		
 		while(indices.size() > 0){
 		
 			int indMin = 0;
 			double distMin = distancias.get(indMin);
 			
			for (int i = 1; i< indices.size(); i++) {
				
				if(distancias.get(i) < distMin){
					indMin = i;
					distMin = distancias.get(i);
				}
			}
			
			indicesOrd.add(indices.remove(indMin));
			distancias.remove(indMin);
 		}
		
		return indicesOrd;
	}

	private static int getSentidoPorQuadrante(int quadrante){
		switch (quadrante) {
		case 1:
			return 1;
		case 2:
			return 3;			
		case 3:
			return 5;		
		case 4:
			return 7;
		default:
			return -1;
		}
	}
	
	private static double[] getReferenciaQuandrante(int quadrante, double[] centro, double raio){
		
		switch (quadrante) {
		case 1:
			return new double[]{centro[0],centro[1]-raio};
		case 2:
			return new double[]{centro[0]-raio, centro[1]};			
		case 3:
			return new double[]{centro[0],centro[1]+raio};		
		case 4:
			return new double[]{centro[0]+raio, centro[1]};
		default:
			return null;
		}
		
	}
	
	private static int getQuadrante(double[] ponto, double[] centro){
		
		double px = ponto[0];
		double py = ponto[1];
		
		double cx = centro[0];
		double cy = centro[1];
		
		if(px < cx){
			if(py < cy){
				return 1;
			}else{// py >= cy
				return 2;
			}
		}else if(px > cx){
			if(py <= cy){
				return 4;
			}else{// py > cy
				return 3;
			}
		}else{//px == cx
			if(py < cy){
				return 1;
			}else if(py > cy){
				return 3;
			}
		}
		return -1;
	}
	
	
	private static ArrayList<Integer> getQuadrantes(List<Entry<Integer, double[]>> pts, double[] centro){
		
		ArrayList<Integer> sentidos = new ArrayList<Integer>();
		
		for (Entry<Integer, double[]> entry : pts) {
			int sentido = getQuadrante(entry.getValue(), centro);
			sentidos.add(sentido);
		}
		
		return sentidos;		
		
	}
	
	private static int getSentido(double[] posOrig, double[] posDest){
		
		if(posOrig[0] > posDest[0] && posOrig[1] == posDest[1]){
			return 0;
		}else if(posOrig[0] > posDest[0] && posOrig[1] < posDest[1]){
			return 1;
		}else if(posOrig[0] == posDest[0] && posOrig[1] < posDest[1]){
			return 2;
		}else if(posOrig[0] < posDest[0] && posOrig[1] < posDest[1]){
			return 3;
		}else if(posOrig[0] < posDest[0] && posOrig[1] == posDest[1]){
			return 4;
		}else if(posOrig[0] < posDest[0] && posOrig[1] > posDest[1]){
			return 5;
		}else if(posOrig[0] == posDest[0] && posOrig[1] > posDest[1]){
			return 6;
		}else if(posOrig[0] > posDest[0] && posOrig[1] > posDest[1]){
			return 7;
		}else
			return 8;
		
	}
	
	
	public static void main(String[] args) {
		
		/*
		double[][] matriz = new double[][]{{2,5,6},{1,6,7},{-1,2,3}};
		double det = Util.determinante(matriz);
		System.out.println(det);
		
		double[] centro = Util.getCentroTriangulo(new double[][]{{4,1},{-3,7},{5,-2}});
		System.out.println(centro[0]+", "+centro[1]);
		
		System.out.println("Triângulos");
		List<Triangulo> triangulos = triangular(new double[][]{{1,1},{2,2},{3,3},{3,4}});
		for (Triangulo tri : triangulos) {
			int[] is = tri.getIndicesPontos();
			for (int i : is) {
				System.out.print(i+" ");
			}
			System.out.println();
		}
		*/
		
		Map<Integer,double[]> pts = new HashMap<Integer, double[]>();	

		pts.put(22,new double[]{5,8});
		pts.put(8,new double[]{2,8});
		pts.put(14, new double[]{5,7});
		pts.put(2,new double[]{2,7});
		pts.put(6, new double[]{4,6});
		
		double[] centro = Util.getCentroTriangulo(new double[][]{pts.get(8),pts.get(2),pts.get(14)});
		double raio = Util.calculaDistanciaEuclidiana(pts.get(14), centro);
		
		int[] ordem = TriangulacaoDeDelaunay.ordenarNoSentidoHorario(pts, centro, raio);
		
		for (int i : ordem) {
			System.out.println(i);
		}
	}
	
	
}
