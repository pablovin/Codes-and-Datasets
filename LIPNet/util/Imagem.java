public class Imagem {

	private int id;
	
	private String identificador;
	
	private String nome;
	
	private String classe;
	
	private double[] saida;
	
	private double[][] imagem;
	
	private double[][][] imagemMC;
	
	private int posSaida;
	
	private int cv;
	
	public Imagem(String identificador, double[][] imagem) {
		super();
		this.identificador = identificador;
		this.imagem = imagem;
	}
	
	public Imagem(String identificador, double[][] imagem, double saida[], int posSaida) {
		super();
		this.identificador = identificador;
		this.saida = saida;
		this.imagem = imagem;
		this.posSaida = posSaida;
	}

	public Imagem(String nome, String classe, double[][] imagem, double saida[], int posSaida) {
		super();
		this.nome = nome;
		this.classe = classe;
		this.saida = saida;
		this.imagem = imagem;
		this.posSaida = posSaida;
	}
	
	public Imagem(double[][] imagem, double saida[], int posSaida) {
		super();
		this.saida = saida;
		this.imagem = imagem;
		this.posSaida = posSaida;
	}
	
	public Imagem(String nome, String classe, double[][][] imagemMC, double saida[], int posSaida) {
		super();
		this.nome = nome;
		this.classe = classe;
		this.saida = saida;
		this.imagemMC = imagemMC;
		this.posSaida = posSaida;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public double[] getSaida() {
		return saida;
	}

	public void setSaida(double[] saida) {
		this.saida = saida;
	}

	public double[][] getImagem() {
		return imagem;
	}

	public void setImagem(double[][] imagem) {
		this.imagem = imagem;
	}

	public int getPosSaida() {
		return posSaida;
	}

	public void setPosSaida(int posSaida) {
		this.posSaida = posSaida;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public int getCv() {
		return cv;
	}

	public void setCv(int cv) {
		this.cv = cv;
	}
	
	public double[][][] getImagemMC() {
		return imagemMC;
	}

	public void setImagemMC(double[][][] imagemMC) {
		this.imagemMC = imagemMC;
	}

	public double[] getImagemArray() {
		double[] retorno = new double[imagem.length*imagem[0].length];
		int contador = 0;
		for (int i = 0; i < imagem.length; i++) {
			for (int j = 0; j < imagem[0].length; j++) {
				retorno[contador] = imagem[i][j];
				contador++;
			}
		}
		return retorno;
	}
	
}
