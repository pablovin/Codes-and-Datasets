package util.banco.bancoGeral.base;

import util.banco.base.Base;

public class BancoDeImagens {

	private final int qtClasses;
	
	private Base[] treino;
	private Base[] validacao;
	private Base[] teste;
	
	public BancoDeImagens(int qtClasses){
		this.qtClasses = qtClasses;
		this.treino = new Base[qtClasses];
		this.validacao = new Base[qtClasses];
		this.teste = new Base[qtClasses];
		
	}
		
	private Base[] getTreino() {
		return treino;
	}
	private void setTreino(Base treino, int classe) {
		this.treino[classe] = treino;
	}
	private Base[] getValidacao() {
		return validacao;
	}
	private void setValidacao(Base validacao, int classe) {
		this.validacao[classe] = validacao;
	}
	private Base[] getTeste() {
		return teste;
	}
	private void setTeste(Base teste, int classe) {
		this.teste[classe] = teste;
	}
	
	public static enum TIPO_BANCO
	{
		TREINO,
		VALIDACAO,
		TESTE
	}
	
	public void set(Base banco, TIPO_BANCO tipo, int classe)
	{
		switch (tipo) {
		case TREINO:
			setTreino(banco, classe);
			break;

		case VALIDACAO:
			setValidacao(banco, classe);
			break;
		
		case TESTE:
			setTeste(banco, classe);
			break;
		}
	}
	
	public Base getBase(TIPO_BANCO banco, int classe)
	{
		Base base = null;
		switch (banco) {
		case TREINO:
			base = getTreino()[classe];
			break;

		case VALIDACAO:
			base = getValidacao()[classe];
			break;
		
		case TESTE:
			base = getTeste()[classe];
			break;
		}
		return base;
	}

	public int getQtClasses() {
		return qtClasses;
	}
}
