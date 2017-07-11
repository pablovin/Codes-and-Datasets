package util.avaliacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import util.avaliacao.ProbabilidadeComparator.Ordem;
import util.banco.banco2classes.BancoUtil;
import util.banco.banco2classes.base.TipoImagem;

public class AvaliacaoUtilDuasClasses {

	private static int indicePositivo = BancoUtil.getIndice(TipoImagem.IMAGEM_POSITIVA);
	private static int indiceNegativo = BancoUtil.getIndice(TipoImagem.IMAGEM_NEGATIVA);
	
	private static Comparator<double[]> comparatorRoc = new ProbabilidadeComparator(
			indicePositivo, Ordem.DESCRESCENTE);
	
	public static double avaliarAUC(ArrayList<double[]> saidasEsperadas, 
			ArrayList<double[]> saidasCalculadas, double intervalo) 
	{
		return avaliarAUC(saidasEsperadas, saidasCalculadas, intervalo, false, null, 0+"");		
	}
	
	public static double avaliarAUC(ArrayList<double[]> saidasEsperadas, 
			ArrayList<double[]> saidasCalculadas, double intervalo, boolean imprime, String path, String id) 
	{
		
		ArrayList<double[]> positivas = new ArrayList<double[]>();
		ArrayList<double[]> negativas = new ArrayList<double[]>();
		
		Iterator<double[]> itSaidaCalculada = saidasCalculadas.iterator();

		int totalPos = 0;
		int totalNeg = 0;
		
		for (double[] saida : saidasEsperadas) {

			TipoImagem tipo = BancoUtil.getTipoImagem(saida);
			double[] saidaCalculada = itSaidaCalculada.next();

			//System.out.println(saidaCalculada[0]+"    "+saidaCalculada[1]);
			
			if(tipo == TipoImagem.IMAGEM_POSITIVA)
			{
				positivas.add(saidaCalculada);
				totalPos++;
			}
			else if( tipo == TipoImagem.IMAGEM_NEGATIVA)
			{
				negativas.add(saidaCalculada);
				totalNeg++;
			}
			else
			{
				System.out.println("ALGO MUITO ERRADO! ERRO POR CURVA ROC!");
			}
		}
		
		Collections.sort(positivas, comparatorRoc);
		/*
		System.out.println("Positivas-------");
		for (Iterator iterator = positivas.iterator(); iterator.hasNext();) {
			double[] ds = (double[]) iterator.next();
			System.out.println(ds[0]+"    "+ds[1]);
		}
		*/
		
		Collections.sort(negativas, comparatorRoc);		
		/*
		System.out.println("Negativas-------");
		for (Iterator iterator = negativas.iterator(); iterator.hasNext();) {
			double[] ds = (double[]) iterator.next();
			System.out.println(ds[0]+"    "+ds[1]);	
		}
		*/
		int qtPontos = (int)(1/intervalo + 1);
		
		double[][] roc = new double[qtPontos][2];

		int qtPontosMenosUm = qtPontos - 1;
		
		roc[0][0] = 0;
		roc[0][1] = 0;
		
		double taxaFalsoPositivo = intervalo;
		for (int i = 1; i < qtPontosMenosUm; taxaFalsoPositivo = taxaFalsoPositivo + intervalo, i++) 
		{
			int qt = (int) (taxaFalsoPositivo * totalNeg);
			
			double probPositivo = negativas.get(qt)[indicePositivo];
			//System.out.println("ProbPositivo="+probPositivo);
			double taxaVerdadeiroPositivo = 0;
			for (double[] probabilidadePos: positivas)
			{
				if(probabilidadePos[indicePositivo] <= probPositivo) //TODO < ou <= ??
				{
					break;
				}
				taxaVerdadeiroPositivo++;
			}
			
			taxaVerdadeiroPositivo = taxaVerdadeiroPositivo / totalPos;
			
			roc[i][0] = taxaVerdadeiroPositivo;
			roc[i][1] = taxaFalsoPositivo;
			
		}
		
		roc[qtPontosMenosUm][0] = 1;
		roc[qtPontosMenosUm][1] = 1;
		
		printRoc(path, imprime, id, roc);
		
		return calculaAreaSobCurvaRoc(roc);
		
	}
	

	public static double avaliarAUCPosNeg(ArrayList<double[]> saidaPositivos, ArrayList<double[]> saidaNegativos,
			double intervalo, boolean imprime, String path, String id) {
		
		ArrayList<Double> positivosRoc = new ArrayList<Double>();
		ArrayList<Double> falsosPositivosRoc = new ArrayList<Double>();
		//double acertosPos = 0;
		for (double[] ds : saidaPositivos) {

			/*
			if (ds[indicePositivo] > ds[indiceNegativo]) {
				acertosPos++;
			}
			*/
			positivosRoc.add(ds[indicePositivo]);

		}
		//double acertosNeg = 0;
		for (double[] ds : saidaNegativos) {
			/*
			if (ds[indicePositivo] < ds[indiceNegativo]) {
				acertosNeg++;

			}*/
			falsosPositivosRoc.add(ds[indicePositivo]);

		}
		//System.out.println(acertosPos + "	" + acertosNeg);
		
		Collections.sort(positivosRoc);
		Collections.reverse(positivosRoc);
		Collections.sort(falsosPositivosRoc);
		Collections.reverse(falsosPositivosRoc);

		
		int qtPontos = (int)(1/intervalo + 1);
		
		double[][] roc = new double[qtPontos][2];

		int qtPontosMenosUm = qtPontos - 1;
		
		double totalFalsoPositivo = falsosPositivosRoc.size();
		double totalPositivo = positivosRoc.size();
		
		for (int i = 0; i < qtPontosMenosUm; i++) {
			
			double limiar = intervalo*i;
			int indice = new Double(Math.round(totalFalsoPositivo * limiar)).intValue();
			indice = indice >= totalFalsoPositivo? indice-1: indice;
			
			double valor = falsosPositivosRoc.get(indice);
			int indicePos = 0;
			/*
			while (indicePos < totalPositivo
					&& positivosRoc.get(indicePos) > valor) {
				indicePos++;
			}
			*/
			Iterator<Double> it = positivosRoc.iterator();
			while (it.hasNext() && it.next() > valor) {
					indicePos++;				
			}
			
			int indice2 = indice;
			
			double percentualTruePositive = new Integer(indicePos).doubleValue()/ totalPositivo;
			double percentualFalsePositive = new Integer(indice2).doubleValue()/ totalFalsoPositivo;
			
			roc[i][0] = percentualTruePositive;
			roc[i][1] = percentualFalsePositive;

		}

		roc[qtPontosMenosUm][0] = 1;
		roc[qtPontosMenosUm][1] = 1;
		
		printRoc(path, imprime, id, roc);
		
		double area = calculaAreaSobCurvaRoc(roc);
		//System.out.println("Resultado iteração = " + area);
		
		return area;
	}	

	/*
	 *  calculaAreaSobCurvaRoc:
	 *  els = double[qtd_pontos][2]
	 *  els[0] = [0, 0]
	 *  els[tamanho] = [1 1]
	 *  Primeira coluna armazena os valores de TruePositive e a segunda os de FalsePositive
	*/
	static double calculaAreaSobCurvaRoc(double[][] els) {
		double area = 0;
		//System.out.println("Curva Roc");
		for (int i = 1; i < els.length; i++) {
			//System.out.println(els[i][0]+" | "+els[i][1]);
			double base = els[i][1] - els[i - 1][1];
			double alturaTriangulo = els[i][0] - els[i - 1][0];
			double alturaRetangulo = els[i - 1][0];
			double areaTriangulo = (base * alturaTriangulo) / 2;
			double areaRetangulo = base * alturaRetangulo;
			area = area + areaTriangulo + areaRetangulo;
		}
		//System.out.println("Area="+area);
		return area;
	}
	

	static void printRoc(String path, boolean imprime, String id, double[][] roc)	{
		
		if(imprime)
		{
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(new File(path+"_"+id+".txt")));				
				
				for (int i = 0; i < roc.length; i++) {
					for (int j = 0; j < roc[i].length; j++) {
						writer.append(roc[i][j]+"\t");
					}
					writer.newLine();
				}

				writer.flush();
				writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}	
	
}
