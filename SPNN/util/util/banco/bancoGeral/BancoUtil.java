package util.banco.bancoGeral;
import java.util.ArrayList;
import java.util.List;

import util.ArquivoImagemUtil;
import util.banco.bancoGeral.base.Banco;
import util.banco.bancoGeral.base.BancoConfig;
import util.banco.bancoGeral.base.BancoDeImagens;
import util.banco.bancoGeral.base.BancoDeImagens.TIPO_BANCO;
import util.banco.bancoGeral.base.Splitter;
import util.banco.base.Base;

public class BancoUtil {

	public static BancoDeImagens ler(Banco banco, BancoConfig config, boolean comTeste) throws Exception
	{
		int qtClasses = banco.getQtClasses();
		int divisao = banco.divisao();
		
		BancoDeImagens bancoImagens = new BancoDeImagens(qtClasses);
		Splitter split = config.getSplitTreino();
		
		double normalizacaoImagemInicio = config.getNormalizacaoImagemInicio();
		double normalizacaoImagemFim = config.getNormalizacaoImagemFim();
		
		if(divisao == 2 || divisao == 3){
		
			for (int classe = 0; classe < qtClasses; classe++) {
				
				List<String> caminhos = banco.getCaminhoBanco(classe, true, false);
					
				List<String>[] caminhoPorBanco;
				if(divisao == 2){
					caminhoPorBanco = split.splitBaseDeDados(caminhos, new int[]{config.getTreino(classe), config.getValidacao(classe)});			
				}else{
					caminhoPorBanco = new List[2];
					
					caminhoPorBanco[0] = split.splitBaseDeDados(caminhos, new int[]{config.getTreino(classe)})[0];
					
					List<String> caminhosValid = banco.getCaminhoBanco(classe, false, true);
					caminhoPorBanco[1] = split.splitBaseDeDados(caminhosValid, new int[]{config.getValidacao(classe)})[0];					
					
				}
				//System.out.println("Treino - Classe="+classe);
				ArrayList<double[][]> imagensTreino = lerBanco(caminhoPorBanco[0], normalizacaoImagemInicio, normalizacaoImagemFim);
				
				//System.out.println("Validação - Classe="+classe);
				ArrayList<double[][]> imagensValidacao = lerBanco(caminhoPorBanco[1], normalizacaoImagemInicio, normalizacaoImagemFim);
				
				Base treino = new Base(null, imagensTreino);
				Base validacao = new Base(null, imagensValidacao);
				
				bancoImagens.set(treino, TIPO_BANCO.TREINO, classe);
				bancoImagens.set(validacao, TIPO_BANCO.VALIDACAO, classe);
			}
			
			split = config.getSplitTeste();
			
			if(comTeste)
			{
				for (int classe = 0; classe < qtClasses; classe++) {
					
					List<String> caminhos = banco.getCaminhoBanco(classe, false, false);
				
					List<String>[] caminhoPorBanco = split.splitBaseDeDados(caminhos, new int[]{config.getTeste(classe)});	
					
					//System.out.println("Teste - Classe="+classe);
					ArrayList<double[][]> imagensTeste = lerBanco(caminhoPorBanco[0], normalizacaoImagemInicio, normalizacaoImagemFim);
					
					Base teste = new Base(null, imagensTeste);
					bancoImagens.set(teste, TIPO_BANCO.TESTE, classe);
					
				}
				
			}
			
		}else{
			
			for (int classe = 0; classe < qtClasses; classe++) {
				
				List<String> caminhos = banco.getCaminhoBanco(classe, true, false);
					
				List<String>[] caminhoPorBanco = split.splitBaseDeDados(caminhos, new int[]{config.getTreino(classe), config.getValidacao(classe), config.getTeste(classe)});			
				
				//System.out.println("Treino - Classe="+classe);
				ArrayList<double[][]> imagensTreino = lerBanco(caminhoPorBanco[0], normalizacaoImagemInicio, normalizacaoImagemFim);
				
				//System.out.println("Validação - Classe="+classe);
				ArrayList<double[][]> imagensValidacao = lerBanco(caminhoPorBanco[1], normalizacaoImagemInicio, normalizacaoImagemFim);
				
				//System.out.println("Teste - Classe="+classe);
				ArrayList<double[][]> imagensTeste = lerBanco(caminhoPorBanco[2], normalizacaoImagemInicio, normalizacaoImagemFim);
				
				
				Base treino = new Base(null, imagensTreino);
				Base validacao = new Base(null, imagensValidacao);
				Base teste = new Base(null, imagensTeste);
				
				bancoImagens.set(treino, TIPO_BANCO.TREINO, classe);
				bancoImagens.set(validacao, TIPO_BANCO.VALIDACAO, classe);
				bancoImagens.set(teste, TIPO_BANCO.TESTE, classe);
			}
			
			
		}
		
		return bancoImagens;
		
	}
/*
	
	public static BancoDeImagens lerTreinoTeste(Banco banco, BancoConfig config) throws Exception
	{
		int qtClasses = banco.getQtClasses();
		
		BancoDeImagens bancoImagens = new BancoDeImagens(qtClasses);
		Splitter split = config.getSplitTreino();
		
		double normalizacaoImagemInicio = config.getNormalizacaoImagemInicio();
		double normalizacaoImagemFim = config.getNormalizacaoImagemFim();
		
		for (int classe = 0; classe < qtClasses; classe++) {
			
			List<String> caminhos = banco.getCaminhoBanco(classe, true, false);
				
			List<String>[] caminhoPorBanco = split.splitBaseDeDados(caminhos, new int[]{config.getTreino(classe), config.getTeste(classe)});			
			
			//System.out.println("Treino - Classe="+classe);
			ArrayList<double[][]> imagensTreino = lerBanco(caminhoPorBanco[0], normalizacaoImagemInicio, normalizacaoImagemFim);
			
			//System.out.println("Validação - Classe="+classe);
			ArrayList<double[][]> imagensTeste = lerBanco(caminhoPorBanco[1], normalizacaoImagemInicio, normalizacaoImagemFim);
			
			Base treino = new Base(null, imagensTreino);
			Base teste = new Base(null, imagensTeste);
			
			bancoImagens.set(treino, TIPO_BANCO.TREINO, classe);
			bancoImagens.set(teste, TIPO_BANCO.TESTE, classe);
		}		
		
		return bancoImagens;
		
	}
	*/
	static boolean print = false;
	
	private static ArrayList<double[][]> lerBanco(List<String> caminhos, double normalizacaoImagemInicio, double normalizacaoImagemFim) throws Exception{
		
		ArrayList<double[][]> bancoImagens = new ArrayList<double[][]>();
		for (String caminho: caminhos) {
			if(print) {
				System.out.println(caminho);
			}
			bancoImagens.add(ArquivoImagemUtil.lerImagem(caminho, normalizacaoImagemInicio, normalizacaoImagemFim));
		}
		
		return bancoImagens;
		
	}
	
	/*
	public static void main(String[] args) {
		
		Banco banco = new util.banco.bancoGeral.dado.BancoCBCL("C:/Alessandra/FACU/Mestrado/CBCL-histeq/");
		
		BancoConfig config = new BancoConfig(new int[]{10,3}, new int[]{5,4}, new int[]{15,16}, 
				new OrderedBaseSplitter(), new RandomBaseSplitter(),
				-0.0430769, 0.0430769);
		
		try {
			BancoDeImagens bancoImagens = BancoUtil.ler(banco, config, true);
			bancoImagens.getBase(TIPO_BANCO.TREINO, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		
	}
	*/
	
	
}
