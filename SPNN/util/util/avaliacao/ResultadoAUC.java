package util.avaliacao;

public class ResultadoAUC extends ResultadoTaxas{

	public final double auc;
	
	public ResultadoAUC(double auc, double tp, double tn, double fp, double fn){
		super(tp,tn,fp,fn);
		this.auc = auc;
	}

	@Override
	public String getResultado() {
		return auc+";"+super.getResultado();
	}
	
	
}
