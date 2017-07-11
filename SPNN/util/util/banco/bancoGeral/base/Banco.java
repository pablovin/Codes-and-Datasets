package util.banco.bancoGeral.base;

import java.util.List;

public abstract class Banco {
	
	private int alturaImagem;
	private int larguraImagem;
	
	private int qtClasses;
	private String diretorioBase;
	private int divisao;//1 - apenas 1 conjunto/2 - treino e teste/3-treino,valid,teste
	
	public Banco(int alturaImagem, int larguraImagem, int qtClasses, String diretorioBase, int divisao) {
		super();
		this.alturaImagem = alturaImagem;
		this.larguraImagem = larguraImagem;
		this.qtClasses = qtClasses;
		this.diretorioBase = diretorioBase;
		this.divisao = divisao;
	}

	public int getAlturaImagem() {
		return alturaImagem;
	}

	public int getLarguraImagem() {
		return larguraImagem;
	}

	public int getQtClasses() {
		return qtClasses;
	}

	public String getDiretorioBase() {
		return diretorioBase;
	}

	public abstract List<String> getCaminhoBanco(int classe, boolean ehTreino, boolean ehValid);
	
	public abstract int getQuantidadeTotalImagensTreino(int classe);
	public abstract int getQuantidadeTotalImagensValidacao(int classe);//retorna -1 se não tiver base definida
	public abstract int getQuantidadeTotalImagensTeste(int classe);
	public abstract String toString();
	
	public int divisao() {
		return divisao;
	}
	
	
	
}
