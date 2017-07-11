package operacoes;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import operacoes.geometria.Ponto;


public class GeradorAleatorio {

	private Random random;

	public GeradorAleatorio(int rand){
		if(rand != 0)
			this.random = new Random(rand);
		else
			this.random = new Random();
	}
	
	
	public double[][] gerarPontosAleatorios(int qtPontos, double[][] mapaProbabilidade) {
		
		//encontra o mínimo para normalizar
		double min = mapaProbabilidade[0][0];
		for (int i = 0; i < mapaProbabilidade.length; i++) {
			for (int j = 0; j < mapaProbabilidade[i].length; j++) {
				
				if(mapaProbabilidade[i][j] < min){
					min = mapaProbabilidade[i][j];
				}
				
			}
		}
		
		//normaliza com mapa de probabilidade a partir de 0
		int sizeX = mapaProbabilidade.length;
		int sizeY = mapaProbabilidade[0].length;
		double somatorio = 0;
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				
				if(min < 0)
					mapaProbabilidade[i][j] = mapaProbabilidade[i][j] + min;
				else if(min > 0)
					mapaProbabilidade[i][j] = mapaProbabilidade[i][j] - min;
			
				somatorio += mapaProbabilidade[i][j];
			}
		}
		
		//atualiza mapa de probabilidade acumulado
		double[] acc = new double[sizeX * sizeY];
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				
				int qt = i*sizeY + j;
				if(qt == 0){
					acc[0] = mapaProbabilidade[i][j]/somatorio;
				}else{
					acc[qt] = acc[qt-1] + (mapaProbabilidade[i][j]/somatorio);
				}
				
			}
		}
		
		Set<Ponto> pts = new HashSet<Ponto>();
		
		for (int n = 0; n < qtPontos; n++) { //Permite quantidade de pontos menor que qtPontos
		//while(pts.size() < qtPontos){	//Força a quantidade de pontos ser igual a qtPontos
		
			double r = random.nextDouble();
			
			label:
			for (int i = 0; i < sizeX; i++) {
				for (int j = 0; j < sizeY; j++) {
			
					int qt = i*sizeY + j;
					if(r < acc[qt]){
						pts.add(new Ponto(i,j));
						break label;
					}					
				}
			}		
			
		}	
		
		double[][] pontos = new double[pts.size()][];
		int i = 0;
		for (Ponto pt : pts) {
			pontos[i] = new double[]{pt.getX(), pt.getY()};
			i++;
		}
		
		return pontos;
		
	}
	
}
