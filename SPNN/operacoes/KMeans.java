package operacoes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import util.Util;


public class KMeans {

	
	private Random rand;
	private double[][] centroides;
	
	public KMeans(int r){
		if(r != 0)
			this.rand = new Random(r);
		else
			this.rand = new Random();
	}
	
	public int[] kmeans(int k, double[][] posicoes){
		
		int nPontos = posicoes.length;
		int dimensoes = posicoes[0].length;
		
		if(k > posicoes.length){
			k=posicoes.length;
		}
		
		centroides = new double[k][dimensoes];
		
		List<Integer> centroidesInicio = new ArrayList<Integer>();
		
		//seleciona centróides aleatoriamente		
		for (int cluster = 0; cluster < k; cluster++) {
		
			int c = 0;
			do{
				c = (int)(rand.nextDouble()*nPontos);
			}while(centroidesInicio.contains(c));
			centroidesInicio.add(c);
			
			for (int d = 0; d < dimensoes; d++) {
			
				centroides[cluster][d] = posicoes[c][d];
	
			}
			
		}
		
		int[] clustersAnt;	
		int[] clusters  = new int[nPontos];		
		
		int cont = 0;
		
		do{
			
			clustersAnt = Arrays.copyOf(clusters, nPontos);	
		
			//atribui clusters		
			for (int pt = 0; pt < nPontos; pt++) {
				
				int clusterMin = 0;
				double distMinima = Util.calculaDistanciaEuclidiana(posicoes[pt], centroides[clusterMin]);
				
				for (int cluster = 1; cluster < k; cluster++) {
					
					double dist = Util.calculaDistanciaEuclidiana(posicoes[pt], centroides[cluster]);
					if(dist < distMinima){
						distMinima = dist;
						clusterMin = cluster;
					}
					
				}
				
				clusters[pt] = clusterMin;			
						
			}
			
			//recalcula centróides
			for (int cluster = 0; cluster < k; cluster++) {
				
				double[] media = new double[dimensoes];
				int qt = 0;
				
				for (int pt = 0; pt < nPontos; pt++) {
					
					if(clusters[pt] == cluster){
					
						for (int d = 0; d < dimensoes; d++) {
						
							media[d] += posicoes[pt][d];
							
						}
						qt++;
					}				
				
				}
				
				for (int d = 0; d < dimensoes; d++) {
					
					media[d] = media[d]/qt;
					centroides[cluster][d] = media[d];
					
				}
				
			}
			cont++;
		
		}while(!comparaClusters(clusters, clustersAnt) && cont < 100000);
		
		if(cont >= 100000){
			System.out.println("Kmeans Finalizada por contador");
		}
		
		/*
		for (int i = 0; i < centroides.length; i++) {
			for (int j = 0; j < centroides[i].length; j++) {
				System.out.print(centroides[i][j]+" ,");
			}
			System.out.println();
		}
		*/
		
		return clusters;
		
		
	}
	
	public double[][] getCentroides(){
		return centroides;
	}
	
	private static boolean comparaClusters(int[] cluster1, int[] cluster2){
		
		int dif = 0;
		
		for (int i = 0; i < cluster1.length; i++) {
			if(cluster1[i] != cluster2[i]){
				dif++;
				break;
			}
		}
		
		if(dif > 0)
			return false;
		
		return true;
	
	}
	
	public static void main(String[] args) {
		
		double[][] posicoes = new double[][]{{1,1},{2,2},{3,3},{0,1},{1,3},{5,4}};
		KMeans kmeans = new KMeans(0);
		int[] clusters = kmeans.kmeans(2, posicoes);
		
		for (int i = 0; i < clusters.length; i++) {
			System.out.println(clusters[i]);
		}
		
	}
	
}
