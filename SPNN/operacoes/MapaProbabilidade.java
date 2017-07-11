package operacoes;
import java.util.ArrayList;

import util.banco.bancoGeral.TipoProblema;
import util.banco.bancoGeral.base.BancoDeImagens;
import util.banco.bancoGeral.base.BancoDeImagens.TIPO_BANCO;
import util.banco.base.Base;


public class MapaProbabilidade {

	public static double[][][] criarMapaProbabilidadeUniforme(BancoDeImagens bancoImagens, TipoProblema tipoProblema){

		int qtRodadas = tipoProblema.getQuantidadeTotalRodadas();
		
		double[][][] mapaProbabilidade = new double[qtRodadas][][];
		Base baseTreino = bancoImagens.getBase(TIPO_BANCO.TREINO, 0);
		double[][] imagem = baseTreino.getImagens().get(0);
		int tamanhoX = imagem.length;
		int tamanhoY = imagem[0].length;		
		
		double fator = 1/((double)(tamanhoX * tamanhoY));
		
		for (int rodada = 0; rodada < qtRodadas; rodada++) {	
			
			mapaProbabilidade[rodada] = new double[tamanhoX][tamanhoY];
			
			for (int i = 0; i < tamanhoX; i++) {
				for (int j = 0; j < tamanhoY; j++) {

					mapaProbabilidade[rodada][i][j] = fator;
							
				}
			}			
		}
		
		return mapaProbabilidade;
		
	}
	
	public static double[][][] criarMapaProbabilidade(BancoDeImagens bancoImagens, TipoProblema tipoProblema) throws Exception{
		
		int qtClasses = bancoImagens.getQtClasses();
		
		int qtRodadas = tipoProblema.getQuantidadeTotalRodadas();
		int qtSaidas = tipoProblema.getQuantidadeSaidas();
		
		double[][][] mapaProbabilidade = new double[qtRodadas][][];
		
		for (int rodada = 0; rodada < qtRodadas; rodada++) {
			
			ArrayList<double[][]>[] imagensPorSaida = new ArrayList[qtSaidas];
			
			for (int classe = 0; classe < qtClasses; classe++) {
				
				Base baseTreino = bancoImagens.getBase(TIPO_BANCO.TREINO, classe);
				int tipoSaida = tipoProblema.geraTipoSaida(classe, rodada);
				
				if(tipoSaida >= 0){
					if(imagensPorSaida[tipoSaida] == null){
						imagensPorSaida[tipoSaida] = new ArrayList<double[][]>();				
					}
					imagensPorSaida[tipoSaida].addAll(baseTreino.getImagens());
				}
			}

			double[][][] medias = new double[qtSaidas][][];
			for (int saida = 0; saida < qtSaidas; saida++) {
				medias[saida] = media(imagensPorSaida[saida]);
			}			
			
			double[][] varianciaFace = extraiVarianciaIntraClasse(medias[0], bancoImagens.getBase(TIPO_BANCO.TREINO, 0).getImagens());
			double[][] varianciaNaoFace = extraiVarianciaIntraClasse(medias[1], bancoImagens.getBase(TIPO_BANCO.TREINO, 1).getImagens());
			double[][] varianciaInter = extraiVarianciaInterClasse(medias);
			
			mapaProbabilidade[rodada] =  extraiMapa(varianciaInter, new double[][][]{varianciaFace, varianciaNaoFace}, 2);
				
			//mapaProbabilidade[rodada] = extraiMapaComPotencia(mapaProbabilidade[rodada], 1);
		}
		return mapaProbabilidade;
	}
	

	public static double[][] media(ArrayList<double[][]> entradas) {
		
		int larg = entradas.get(0).length;
		int alt = entradas.get(0)[0].length;
		
		double[][] media = new double[larg][alt];
		for (double[][] ent : entradas) {
			for (int i = 0; i < ent.length; i++) {
				for (int j = 0; j < ent[0].length; j++) {
					media[i][j] += ent[i][j]/entradas.size();
				}
			}
		}
		return media;
	}
	

	
	public static double[][] extraiVarianciaIntraClasse(double[][] media, ArrayList<double[][]> elementos) {
		double[][] retorno = new double[media.length][media[0].length];
		for (int i = 0; i < retorno.length; i++) {
			for (int j = 0; j < retorno[0].length; j++) {
				double mediaTotal = 0;
				for (int k = 0; k < elementos.size(); k++) {
					mediaTotal += elementos.get(k)[i][j];
				}
				mediaTotal = mediaTotal/elementos.size();
				double var = 0;
				for (int k = 0; k < elementos.size(); k++) {
					var += Math.pow(elementos.get(k)[i][j] - mediaTotal, 2);
				}
				retorno[i][j] = Math.pow(var/elementos.size(), 2);
			}
		}
		return retorno;
	}
	
	public static double[][] extraiVarianciaInterClasse(double[][][] elementos) {
		double[][] retorno = new double[elementos[0].length][elementos[0][0].length];
		for (int i = 0; i < retorno.length; i++) {
			for (int j = 0; j < retorno[0].length; j++) {
				double mediaTotal = 0;
				for (int k = 0; k < elementos.length; k++) {
					mediaTotal += elementos[k][i][j];
				}
				mediaTotal = mediaTotal/elementos.length;
				double var = 0;
				for (int k = 0; k < elementos.length; k++) {
					var += Math.pow(elementos[k][i][j] - mediaTotal, 2);
				}
				retorno[i][j] = Math.pow(var/elementos.length, 2);
			}
		}
		return retorno;
	}
	
	public static double[][] extraiMapa(double[][] varianciaInterClasse, double[][][] varianciaIntraClasse, double p) {
		double[][] retorno = new double[varianciaInterClasse.length][varianciaInterClasse[0].length];
		double total = 0;
		for (int i = 0; i < retorno.length; i++) {
			for (int j = 0; j < retorno.length; j++) {
				if(varianciaIntraClasse == null) {
					retorno[i][j] = varianciaInterClasse[i][j];
				}else{
					double somaVariancias = 0;
					for (int k = 0; k < varianciaIntraClasse.length; k++) {
						somaVariancias += varianciaIntraClasse[k][i][j];
					}
					retorno[i][j] = varianciaInterClasse[i][j]/Math.pow(somaVariancias, p);
				}
				total += retorno[i][j];
			}
		}
		
		for (int i = 0; i < retorno.length; i++) {
			for (int j = 0; j < retorno[0].length; j++) {
				retorno[i][j] = (retorno[i][j]/total);
			}
		}
		return retorno;
	}
	
	public static double[][] extraiMapaComPotencia(double[][] mapa, int potencia){
		
		double[][] retorno = new double[mapa.length][mapa[0].length];
		double total = 0; 
		for (int i = 0; i < mapa.length; i++) {
			for (int j = 0; j < mapa.length; j++) {
				retorno[i][j] = Math.pow(mapa[i][j],potencia);
				total += retorno[i][j];
			}
		}
		
		for (int i = 0; i < retorno.length; i++) {
			for (int j = 0; j < retorno[0].length; j++) {
				retorno[i][j] = (retorno[i][j]/total);
			}
		}
		return retorno;
		
	}
	
}
