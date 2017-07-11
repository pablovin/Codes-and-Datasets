package util.banco.bancoGeral.base;

import util.banco.bancoGeral.TipoProblema;


public class BancoConfig {
	
	private final int[] qtTreino;
	private final int[] qtValidacao;
	private final int[] qtTeste;
	
	private Splitter splitTreino;
	private Splitter splitTeste;
	
	private double normalizacaoImagemInicio;
	private double normalizacaoImagemFim;
	
	private boolean porClasse;
	
	public BancoConfig(int[] treino, int[] validacao, int[] teste, Splitter splitTreino, Splitter splitTeste,
			double normalizacaoImagemInicio, double normalizacaoImagemFim) {
		super();
		this.qtTreino = treino;
		this.qtValidacao = validacao;
		this.qtTeste = teste;
		this.splitTreino = splitTreino;
		this.splitTeste = splitTeste;
		this.normalizacaoImagemInicio = normalizacaoImagemInicio;
		this.normalizacaoImagemFim = normalizacaoImagemFim;
		this.porClasse = false;
	}
	
	public BancoConfig(int treinoPorClasse, int validacaoPorClasse, int testePorClasse, Splitter splitTreino, Splitter splitTeste,
			double normalizacaoImagemInicio, double normalizacaoImagemFim) {
		super();
		this.qtTreino = new int[]{treinoPorClasse};
		this.qtValidacao = new int[]{validacaoPorClasse};
		this.qtTeste = new int[]{testePorClasse};
		this.splitTreino = splitTreino;
		this.splitTeste = splitTeste;
		this.normalizacaoImagemInicio = normalizacaoImagemInicio;
		this.normalizacaoImagemFim = normalizacaoImagemFim;
		this.porClasse = true;
	}

	public int getTreino(int classe) {
		if(porClasse)
			return qtTreino[0];
		else
			return qtTreino[classe];
	}

	public int getValidacao(int classe) {
		if(porClasse)
			return qtValidacao[0];
		else
			return qtValidacao[classe];
	}

	public int getTeste(int classe) {
		if(porClasse)
			return qtTeste[0];
		else
			return qtTeste[classe];
	}

	public Splitter getSplitTreino() {
		return splitTreino;
	}

	public Splitter getSplitTeste() {
		return splitTeste;
	}
	
	public double getNormalizacaoImagemInicio() {
		return normalizacaoImagemInicio;
	}

	public double getNormalizacaoImagemFim() {
		return normalizacaoImagemFim;
	}
	
}
