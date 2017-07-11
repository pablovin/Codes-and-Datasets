package util.avaliacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ROCCompress {

	
	private static double[][] reduzRoc(double[][] roc, double[] niveis){
		
		double[][] nroc = new double[niveis.length][2];
		int idnivel = 0;
		for (int i = 0; i < roc.length-1 && idnivel < niveis.length; i++) {
			
			if(Math.abs(niveis[idnivel] - roc[i][1]) < Math.abs(niveis[idnivel] - roc[i+1][1])){
				
				nroc[idnivel][0] = roc[i][0];
				nroc[idnivel][1] = roc[i][1];
				idnivel++;
			}
			
		}
		nroc[idnivel][0] = 1;
		nroc[idnivel][1] = 1;
		
		return nroc;
		
	}
	
	private static double[][] lerRoc(String caminho) throws IOException{
		
		File file = new File(caminho);
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		ArrayList<double[]> list = new ArrayList<double[]>();
		
		while(reader.ready()){
			
			String line = reader.readLine();
			String[] splits = line.split("\t");
			double tp = Double.parseDouble(splits[0]);
			double fp = Double.parseDouble(splits[1]);
			
			list.add(new double[]{tp, fp});
			
		}
		reader.close();
		
		double[][] roc = new double[list.size()][2];
		int i = 0;
		for (double[] ds : list) {
			roc[i] = ds;
			i++;
		}
		
		return roc;
	}
	
	public static void main(String[] args) {
		
		String caminhobase = "C:/Alessandra/FACU/MESTRADO/Theano e Matlab/Resultados CNN/AllIdb2/";
		String base = "allidb2_cnn_resultado_roc";
		
		double[] niveis = new double[]{0,0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5,0.55,0.6,0.65,0.7,0.75,0.8,0.85,0.9,0.95,1};
		
		int total = 30;//8*30;
		int cont = 0;
		double aucMedio = 0;
		double[][] curvaaucmedia = new double[niveis.length][2];
		
		double[] aucs = new double[total];
		
	//	for (int i = 1; i <= 8; i++) {
			for (int j = 0; j < 30; j++) {
				

				String id = j+"";//i+"_"+j;
				String caminhoEntrada = caminhobase+base+"_"+id+".txt";
				String caminhoSaida = caminhobase+base;
				String idSaida = "reduzida_"+id+".txt";
				
				try {	
					
					double[][] roc = lerRoc(caminhoEntrada);
					double[][] nroc = reduzRoc(roc, niveis);
					
					double auc = AvaliacaoUtilDuasClasses.calculaAreaSobCurvaRoc(nroc);
					
					aucs[cont++] = auc;
					aucMedio += auc;
					soma(curvaaucmedia, nroc);
					
					AvaliacaoUtilDuasClasses.printRoc(caminhoSaida, true, idSaida, nroc);
					
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		//}
		
		media(curvaaucmedia, total);
		AvaliacaoUtilDuasClasses.printRoc(caminhobase+"_RESULTADO_"+base, true, "curva_media_reduzida", curvaaucmedia);
		File file = new File(caminhobase+"_RESULTADO_"+base+"_reduzida.txt");
		try {
			
			PrintWriter writer = new PrintWriter(file);
			for (double d : aucs) {
				writer.println(d);
			}
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(aucMedio/total);
	}
	
	private static double[][] soma(double[][] v1, double[][] v2){
		
		for (int i = 0; i < v2.length; i++) {
			for (int j = 0; j < v2[i].length; j++) {
				
				v1[i][j] += v2[i][j];
				
			}
		}
		
		return v1;
		
	}
	
	private static double[][] media(double[][] v1, int n){
		
		for (int i = 0; i < v1.length; i++) {
			for (int j = 0; j < v1[i].length; j++) {
				
				v1[i][j] = v1[i][j]/n;
				
			}
		}
		
		return v1;
		
	}
	
}
