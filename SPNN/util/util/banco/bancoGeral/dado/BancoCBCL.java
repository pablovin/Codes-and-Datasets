package util.banco.bancoGeral.dado;

import java.util.ArrayList;
import java.util.List;

import constantes.ConstantesUtil;
import util.banco.bancoGeral.base.Banco;

public class BancoCBCL extends Banco{

	private static final int FACE = 0;
	private static final int NAO_FACE = 1;	
	
	private static final String treino = "train";
	private static final String teste = "test";
	
	private static final String face = "face";
	private static final String naoFace = "non-face";


	private static final int qtTotalTreinoPositivas = 2429;
	private static final int qtTotalTreinoNegativas = 4544;
	
	private static final int qtTotalTestePositivas = 472;
	private static final int qtTotalTesteNegativas = 23573;
	
	public BancoCBCL(String diretorio) {
		super(19, 19, 2, diretorio, 2);
	}

	@Override
	public List<String> getCaminhoBanco(int classe, boolean ehTreino, boolean valid) {
		if(classe != FACE && classe != NAO_FACE){
			return null;
		}
		
		String caminhoBase = "";
		int qt = 0;
		
		if(classe == FACE){
			if(ehTreino){
				caminhoBase = this.getDiretorioBase() + treino + "/" + face + "/" + "histeq/";
				qt = getQuantidadeTotalImagensTreino(classe);
				
			}else{
				caminhoBase = this.getDiretorioBase() + teste + "/" + face + "/" + "histeq/";
				qt = getQuantidadeTotalImagensTeste(classe);
				
			}
		}
		else{
			if(ehTreino){
				caminhoBase = this.getDiretorioBase() + treino + "/" + naoFace + "/" + "histeq/";
				qt = getQuantidadeTotalImagensTreino(classe);
				
			}else{
				caminhoBase = this.getDiretorioBase() + teste + "/" + naoFace + "/" + "histeq/";
				qt = getQuantidadeTotalImagensTeste(classe);
				
			}			
		}
		
		return gerarDiretorios(caminhoBase, qt);
	}

	private List<String> gerarDiretorios(String base, int qt){
		
		List<String> lista = new ArrayList<String>();
		
		for (int i = 1; i <= qt; i++) {
			lista.add(base + i + ".jpg");
		}
		
		return lista;
		
	}
	
	
	@Override
	public int getQuantidadeTotalImagensTreino(int classe) {
		if(classe == FACE){
			return qtTotalTreinoPositivas;
		}
		else if(classe == NAO_FACE){
			return qtTotalTreinoNegativas;
		}
		else
			return 0;
	}
	
	@Override
	public int getQuantidadeTotalImagensValidacao(int classe) {
		return -1;
	}
	
	@Override
	public int getQuantidadeTotalImagensTeste(int classe) {
		if(classe == FACE){
			return qtTotalTestePositivas;
		}
		else if(classe == NAO_FACE){
			return qtTotalTesteNegativas;
		}
		else
			return 0;
	}

	public String toString(){
		return "CBCL";
	}
	
	/*
	public static void main(String[] args) {
		
		Banco BANCO = new BancoCBCL("C:/Alessandra/FACU/Mestrado/CBCL-histeq/");
		
		List<String> caminhos = BANCO.getCaminhoBanco(FACE, false);
		
		for (String string : caminhos) {
			
			System.out.println(string);
			
		}
		
	}
	*/
}
