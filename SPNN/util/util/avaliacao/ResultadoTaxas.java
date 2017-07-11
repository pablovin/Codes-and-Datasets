package util.avaliacao;

public class ResultadoTaxas extends Resultado{

	public final double tp,tn,fp,fn;
	public final double especificidade,sensibilidade,erroClassificao;
	
	public ResultadoTaxas(double tp, double tn, double fp, double fn) {
		
		double somap = tp + fn;
		double soman = tn + fp;
		
		this.tp = tp/somap;
		this.tn = tn/soman;
		this.fp = fp/soman;
		this.fn = fn/somap;
		
		this.sensibilidade = this.tp/(this.tp + this.fn);
		this.especificidade = this.tn/(this.tn + this.fp);
		this.erroClassificao = this.fp + this.fn;
		
	}
	
	@Override
	public String getResultado() {
		return tp+";"+tn+";"+fp+";"+fn+";"+especificidade+";"+sensibilidade+";"+erroClassificao;
	}

}
