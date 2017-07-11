package rede;
import java.util.ArrayList;
import java.util.Collections;

import util.avaliacao.AvaliacaoDuasClasses;
import util.avaliacao.Resultado;
import util.avaliacao.ResultadoAUC;
import util.banco.bancoGeral.TipoProblema;


public class TreinaSPNN {

	public static void treina(SPNNBase spnn, int quantidadeEpocas,
			ArrayList<double[][]> entradas, ArrayList<double[]> saidas)
			throws Exception {
		
		for (int i = 0; i < quantidadeEpocas; i++) {
			System.out.println("Época "+i);
			for (int j = 0; j < entradas.size(); j++) {
				//System.out.println("Entrada "+j);
				spnn.treina(entradas.get(j), saidas.get(j));
			}
			spnn.atualizaPesos();
			spnn.zeraGradientes();
		}
	}
	
	public static void treina(SPNNBase spnn, TipoProblema tipoProblema, int quantidadeEpocas,
			ArrayList<double[][]> positivos, ArrayList<double[][]> negativos, int numElementos, int rodada)
			throws Exception {
		
		for (int i = 0; i < quantidadeEpocas; i++){	
			
			treinaDicotomia(spnn, tipoProblema, positivos, negativos, numElementos, rodada);
			//double area = avalia(spnn, positivos, negativos);
			//System.out.println("Treino em época ("+i+") = "+area);
		}
	}
	
	public static void treina(SPNNBase spnn, TipoProblema tipoProblema, int quantidadeEpocas,
			ArrayList<double[][]> posTreino, ArrayList<double[][]> negTreino,
			ArrayList<double[][]> posValid, ArrayList<double[][]> negValid,
			ArrayList<double[][]> posTeste, ArrayList<double[][]> negTeste, int numElementos, 
			int rodada,	double[][] conv, boolean registraVaTe)
			throws Exception {
		
		for (int i = 0; i < quantidadeEpocas; i++){	
			
			treinaDicotomia(spnn, tipoProblema, posTreino, negTreino, numElementos, rodada);
			
			
			double areaTr = avalia(spnn, posTreino, negTreino, tipoProblema).auc;
			
			if(conv != null){
				conv[i][0] = areaTr;
			}
			
			if(registraVaTe){
				//double areaVa = avalia(spnn, posValid, negValid);
				double areaTe = avalia(spnn, posTeste, negTeste, tipoProblema).auc;
				if(conv != null){
					//conv[i][1] = areaVa;
					conv[i][2] = areaTe;
				}
				System.out.println("Época "+Thread.currentThread().getName()+" ("+i+") = TR:"+areaTr+/*"\t VA:"+areaVa+*/"\t TE:"+areaTe);
				
			}else{
				System.out.println("Época "+Thread.currentThread().getName()+" ("+i+") = TR:"+areaTr);
			}
			
		}
	}
	
	private static void treinaDicotomia(SPNNBase spnn, TipoProblema tipoProblema, ArrayList<double[][]> positivos, ArrayList<double[][]> negativos,
			int numElementos, int rodada)
	{
		Collections.shuffle(negativos);
		Collections.shuffle(positivos);
		
		int qtNeg = negativos.size();
		int qtPos = positivos.size();
		
		if(numElementos == 0) {
			for (int j = 0; j < qtNeg; j++) {
				spnn.treina(positivos.get(j % qtPos), tipoProblema.geraSaida(0, rodada));
				spnn.treina(negativos.get(j), tipoProblema.geraSaida(1, rodada));

			}
		} else {
			for (int j = 0; j < numElementos; j++) {
				spnn.treina(positivos.get(j), tipoProblema.geraSaida(0, rodada));
				spnn.treina(negativos.get(j), tipoProblema.geraSaida(1, rodada));
			}
		}

		spnn.atualizaPesos();
		spnn.zeraGradientes();
		
	}
	
	public static ResultadoAUC avalia(SPNNBase spnn, ArrayList<double[][]> positivas, ArrayList<double[][]> negativas, TipoProblema tipoProblema,String path, String id)
	{		
		
		ArrayList<double[]> saidaPositivas = new ArrayList<double[]>();
		
		double tp = 0,fp = 0,tn = 0,fn = 0;
		
		for (double[][] ds : positivas) {
			
			double[] saida = spnn.calculaSaidaRede(ds);
			int classe = tipoProblema.classifica(new double[][]{saida});
			
			if(classe == 0){
				tp++;
			}else{
				fn++;
			}
			
			saidaPositivas.add(saida);
			
		}
		
		ArrayList<double[]> saidaNegativas = new ArrayList<double[]>();
		
		for (double[][] ds : negativas) {
			double[] saida = spnn.calculaSaidaRede(ds);
			
			int classe = tipoProblema.classifica(new double[][]{saida});
			
			if(classe == 1){
				tn++;
			}else{
				fp++;
			}
			
			saidaNegativas.add(saida);
			
		}
		
		
		double auc = AvaliacaoDuasClasses.avaliaErroPorCurvaRocPorPosNeg(saidaPositivas, saidaNegativas, 0.05, true, path, id);
	

		return new ResultadoAUC(auc, tp, tn, fp, fn);
	}
	
	public static ResultadoAUC avalia(SPNNBase spnn, ArrayList<double[][]> positivas, ArrayList<double[][]> negativas, TipoProblema tipoProblema)
	{		
		
		ArrayList<double[]> saidaPositivas = new ArrayList<double[]>();
		
		double tp = 0,fp = 0,tn = 0,fn = 0;
		
		for (double[][] ds : positivas) {
			
			double[] saida = spnn.calculaSaidaRede(ds);
			int classe = tipoProblema.classifica(new double[][]{saida});
			
			if(classe == 0){
				tp++;
			}else{
				fn++;
			}
			
			saidaPositivas.add(saida);
			
		}
		
		ArrayList<double[]> saidaNegativas = new ArrayList<double[]>();
		
		for (double[][] ds : negativas) {
			double[] saida = spnn.calculaSaidaRede(ds);
			
			int classe = tipoProblema.classifica(new double[][]{saida});
			
			if(classe == 1){
				tn++;
			}else{
				fp++;
			}
			
			saidaNegativas.add(saida);
			
		}
		
		double auc = AvaliacaoDuasClasses.avaliaErroPorCurvaRocPorPosNeg(saidaPositivas, saidaNegativas, 0.05);
		
		return new ResultadoAUC(auc, tp, tn, fp, fn);
		
	}
	
	public static ResultadoAUC avaliaComiteComMedia(SPNNBase[] spnns, ArrayList<double[][]> positivas, ArrayList<double[][]> negativas, TipoProblema tipoProblema, boolean imprime, String path, String id){
		
		int tamanhoComite = spnns.length;

		double tp = 0,fp = 0,tn = 0,fn = 0;
		
		ArrayList<double[]> saidaPositivas = new ArrayList<double[]>();
		
		for (double[][] ds : positivas) {
			
			double[] saidaMedia = null;
			
			for(SPNNBase spnn: spnns){
				saidaMedia = adiciona(saidaMedia, spnn.calculaSaidaRede(ds));
			}
			
			double[] saida = media(saidaMedia, tamanhoComite);
			
			int classe = tipoProblema.classifica(new double[][]{saida});
			
			if(classe == 0){
				tp++;
			}else{
				fn++;
			}			
			
			saidaPositivas.add(saida);
			
		}
		
		ArrayList<double[]> saidaNegativas = new ArrayList<double[]>();
		
		for (double[][] ds : negativas) {
			
			double[] saidaMedia = null;
			
			for(SPNNBase spnn: spnns){
				saidaMedia = adiciona(saidaMedia, spnn.calculaSaidaRede(ds));
			}
			
			double[] saida = media(saidaMedia, tamanhoComite);
			
			int classe = tipoProblema.classifica(new double[][]{saida});
			
			if(classe == 1){
				tn++;
			}else{
				fp++;
			}
			
			saidaNegativas.add(saida);
			
		}
		
		double auc = AvaliacaoDuasClasses.avaliaErroPorCurvaRocPorPosNeg(saidaPositivas, saidaNegativas, 0.05, imprime, path, id);
		return new ResultadoAUC(auc, tp, tn, fp, fn);
		
	}


	public static ResultadoAUC avaliaComiteComProduto(SPNNBase[] spnns, ArrayList<double[][]> positivas, ArrayList<double[][]> negativas, TipoProblema tipoProblema, boolean imprime, String path, String id){
		
		int tamanhoComite = spnns.length;

		double tp = 0,fp = 0,tn = 0,fn = 0;
		
		ArrayList<double[]> saidaPositivas = new ArrayList<double[]>();
		
		for (double[][] ds : positivas) {
			
			double[] saidaMedia = null;
			
			for(SPNNBase spnn: spnns){
				saidaMedia = multiplica(saidaMedia, spnn.calculaSaidaRede(ds));
			}
			
			double[] saida = media(saidaMedia, tamanhoComite);
			
			//int classe = tipoProblema.classifica(new double[][]{saida});
			
			if(saida[0] >= saida[1]){
				tp++;
			}else{
				fn++;
			}			
			
			saidaPositivas.add(saida);
			
		}
		
		ArrayList<double[]> saidaNegativas = new ArrayList<double[]>();
		
		for (double[][] ds : negativas) {
			
			double[] saidaMedia = null;
			
			for(SPNNBase spnn: spnns){
				saidaMedia = multiplica(saidaMedia, spnn.calculaSaidaRede(ds));
			}
			
			double[] saida = media(saidaMedia, tamanhoComite);
			
			//int classe = tipoProblema.classifica(new double[][]{saida});
			
			if(saida[0] < saida[1]){
				tn++;
			}else{
				fp++;
			}
			
			saidaNegativas.add(saida);
			
		}
		
		double auc = AvaliacaoDuasClasses.avaliaErroPorCurvaRocPorPosNeg(saidaPositivas, saidaNegativas, 0.05, imprime, path, id);
		return new ResultadoAUC(auc, tp, tn, fp, fn);
	}

	
	
	public static double avaliaComiteComVotacaoMedia(SPNNBase[] spnns, ArrayList<double[][]> positivas, ArrayList<double[][]> negativas){
				
		ArrayList<double[]> saidaPositivas = new ArrayList<double[]>();
		
		for (double[][] ds : positivas) {
			
			double[] saidaPosMedia = new double[2];
			double[] saidaNegMedia = new double[2];
			int placarPos = 0, placarNeg = 0;
			
			for(SPNNBase spnn: spnns){
				double[] saida = spnn.calculaSaidaRede(ds);
				if(saida[0] >= saida[1]){
					saidaPosMedia = adiciona(saidaPosMedia, saida);
					placarPos++;
				}else{
					saidaNegMedia = adiciona(saidaNegMedia, saida);
					placarNeg++;
				}
			}
			
			double[] media = null;
			int qt = 0;
			if(placarPos >= placarNeg){
				media = saidaPosMedia;
				qt = placarPos;
			}else{
				media = saidaNegMedia;
				qt = placarNeg;
			}
			
			saidaPositivas.add(media(media, qt));
			
		}
		
		ArrayList<double[]> saidaNegativas = new ArrayList<double[]>();
		
		for (double[][] ds : negativas) {
			
			double[] saidaPosMedia = new double[2];
			double[] saidaNegMedia = new double[2];
			int placarPos = 0, placarNeg = 0;
			
			for(SPNNBase spnn: spnns){
				double[] saida = spnn.calculaSaidaRede(ds);
				if(saida[0] >= saida[1]){
					saidaPosMedia = adiciona(saidaPosMedia, saida);
					placarPos++;
				}else{
					saidaNegMedia = adiciona(saidaNegMedia, saida);
					placarNeg++;
				}
			}
			
			double[] media = null;
			int qt = 0;
			if(placarPos >= placarNeg){
				media = saidaPosMedia;
				qt = placarPos;
			}else{
				media = saidaNegMedia;
				qt = placarNeg;
			}
			
			saidaNegativas.add(media(media, qt));
			
		}
		
		return AvaliacaoDuasClasses.avaliaErroPorCurvaRocPorPosNeg(saidaPositivas, saidaNegativas, 0.05);		
		
	}
	
	public static double avaliaComiteComVotacaoProbabilidadeMax(SPNNBase[] spnns, ArrayList<double[][]> positivas, ArrayList<double[][]> negativas){
		
		ArrayList<double[]> saidaPositivas = new ArrayList<double[]>();
		
		for (double[][] ds : positivas) {
			
			double[] saidaPosMax = null;
			double[] saidaNegMax = null;
			int placarPos = 0, placarNeg = 0;
			
			for(SPNNBase spnn: spnns){
				
				double[] saida = spnn.calculaSaidaRede(ds);
				if(saida[0] >= saida[1]){
					placarPos++;
					
					if(saidaPosMax == null || saidaPosMax[0] < saida[0]){
						saidaPosMax = saida;
					}
					
				}else{
					placarNeg++;
					//considera cross entropy
					if(saidaNegMax == null || saidaNegMax[1] < saida[1]){
						saidaNegMax = saida;
					}
					
					
				}
			}
			
			double[] minMax = null;
			if(placarPos >= placarNeg){
				minMax = saidaPosMax;
			}else{
				minMax = saidaNegMax;
			}
			
			saidaPositivas.add(minMax);
			
		}
		
		ArrayList<double[]> saidaNegativas = new ArrayList<double[]>();
		
		for (double[][] ds : negativas) {
			
			double[] saidaPosMax = null;
			double[] saidaNegMax = null;
			int placarPos = 0, placarNeg = 0;
			
			for(SPNNBase spnn: spnns){
				
				double[] saida = spnn.calculaSaidaRede(ds);
				if(saida[0] >= saida[1]){
					placarPos++;
					
					if(saidaPosMax == null || saidaPosMax[0] < saida[0]){
						saidaPosMax = saida;
					}
					
				}else{
					placarNeg++;
					//Considera cross entropy
					if(saidaNegMax == null || saidaNegMax[1] < saida[1]){
						saidaNegMax = saida;
					}
					
					
				}
			}
			
			double[] minMax = null;
			if(placarPos >= placarNeg){
				minMax = saidaPosMax;
			}else{
				minMax = saidaNegMax;
			}
			
			saidaNegativas.add(minMax);
			
		}
		
		return AvaliacaoDuasClasses.avaliaErroPorCurvaRocPorPosNeg(saidaPositivas, saidaNegativas, 0.05);		
		
	}

	
	private static double[] adiciona(double[] saidaMedia,
			double[] saidaRede) {
		if(saidaMedia == null)
			return saidaRede;
		
		for (int i = 0; i < saidaRede.length; i++) {
			saidaMedia[i] += saidaRede[i];
		}
		return saidaMedia;
		
	}
	
	private static double[] multiplica(double[] saidaMedia,
			double[] saidaRede) {
		if(saidaMedia == null)
			return saidaRede;
		
		for (int i = 0; i < saidaRede.length; i++) {
			saidaMedia[i] *= saidaRede[i];
		}
		return saidaMedia;
		
	}
	
	private static double[] media(double[] saidaMedia, int qt){
		for (int i = 0; i < saidaMedia.length; i++) {
			saidaMedia[i] = saidaMedia[i]/qt;
		}
		return saidaMedia;
	}
	
}
