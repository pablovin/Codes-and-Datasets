package configs;

import util.banco.bancoGeral.base.Banco;
import util.banco.bancoGeral.base.BancoConfig;
import util.banco.bancoGeral.base.OrderedBaseSplitter;
import util.banco.bancoGeral.base.Splitter;
import util.banco.bancoGeral.dado.BancoCBCL;

public interface CBCLConstantes extends ConstantesBase{
	
	int qtFolds = 0;
	
	String DIRETORIO_BANCO = BASE_FILE + "BASES-DE-IMAGENS/CBCL-histeq/";
	
	Banco BANCO = new BancoCBCL(DIRETORIO_BANCO);
	
	
	int qtTreinoPositivas = 1000;
	int qtTreinoNegativas = 1000;

	int qtValidacaoPositivas = BANCO.getQuantidadeTotalImagensTreino(0)-qtTreinoPositivas;
	int qtValidacaoNegativas = BANCO.getQuantidadeTotalImagensTreino(1)-qtTreinoNegativas;	

	int qtTestePositivas = BANCO.getQuantidadeTotalImagensTeste(0);
	int qtTesteNegativas = BANCO.getQuantidadeTotalImagensTeste(1);
	
	int[] treino = new int[]{qtTreinoPositivas, qtTreinoNegativas};
	
	int[] validacao = new int[]{qtValidacaoPositivas, qtValidacaoNegativas};	
	
	int[] teste = new int[]{qtTestePositivas, qtTesteNegativas};
		
	Splitter splitTreino = new OrderedBaseSplitter();//FirstOrderedOthersRandomBaseSplitter();
	Splitter splitTeste = new OrderedBaseSplitter();


	BancoConfig BANCO_CONFIG = new BancoConfig(treino, validacao, teste, splitTreino, splitTeste, 
			NORMALIZACAO_IMAGEM_INICIO, NORMALIZACAO_IMAGEM_FIM);
	
}
