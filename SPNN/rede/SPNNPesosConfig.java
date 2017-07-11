package rede;

public class SPNNPesosConfig {

	public final double maxPesoCamada2D;
	public final double minPesoCamada2D;
	public final boolean mudaSinalPesoCamada2D;
	
	public final double maxBiasCamada2D;
	public final double minBiasCamada2D;
	public final boolean mudaSinalBiasCamada2D;
	
	
	public final double maxPesoCamada1D;
	public final double minPesoCamada1D;
	public final boolean mudaSinalPesoCamada1D;
	
	public final double maxBiasCamada1D;
	public final double minBiasCamada1D;
	public final boolean mudaSinalBiasCamada1D;
	
	
	public SPNNPesosConfig(double maxPesoCamada2D,
			double minPesoCamada2D, boolean mudaSinalPesoCamada2D,
			double maxBiasCamada2D, double minBiasCamada2D,
			boolean mudaSinalBiasCamada2D, double maxPesoCamada1D,
			double minPesoCamada1D, boolean mudaSinalPesoCamada1D,
			double maxBiasCamada1D, double minBiasCamada1D,
			boolean mudaSinalBiasCamada1D) {
		super();
		this.maxPesoCamada2D = maxPesoCamada2D;
		this.minPesoCamada2D = minPesoCamada2D;
		this.mudaSinalPesoCamada2D = mudaSinalPesoCamada2D;
		this.maxBiasCamada2D = maxBiasCamada2D;
		this.minBiasCamada2D = minBiasCamada2D;
		this.mudaSinalBiasCamada2D = mudaSinalBiasCamada2D;
		this.maxPesoCamada1D = maxPesoCamada1D;
		this.minPesoCamada1D = minPesoCamada1D;
		this.mudaSinalPesoCamada1D = mudaSinalPesoCamada1D;
		this.maxBiasCamada1D = maxBiasCamada1D;
		this.minBiasCamada1D = minBiasCamada1D;
		this.mudaSinalBiasCamada1D = mudaSinalBiasCamada1D;
	}
	
	@Override
	public String toString() {
		
		String str = "Camada2D (Peso) = "+maxPesoCamada2D+","+minPesoCamada2D+","+mudaSinalPesoCamada2D+";\n";
		str += "Camada2D (Bias) = "+maxBiasCamada2D+","+minBiasCamada2D+","+mudaSinalBiasCamada2D+";\n";
		str += "Camada1D (Peso) = "+maxPesoCamada1D+","+minPesoCamada1D+","+mudaSinalPesoCamada1D+";\n";
		str += "Camada1D (Bias) = "+maxBiasCamada1D+","+minBiasCamada1D+","+mudaSinalBiasCamada1D+";\n";
		
		return str;
	
	}
	
public String toStringCamada2D() {
		
		String str = maxBiasCamada2D+"\t"+minBiasCamada2D+"\t"+maxPesoCamada2D+"\t"+minPesoCamada2D+"\t"+mudaSinalPesoCamada2D;
		
		return str;
	
	}
	

public String toStringCamada1D() {
	
	String str = maxBiasCamada1D+"\t"+minBiasCamada1D+"\t"+maxPesoCamada1D+"\t"+minPesoCamada1D+"\t"+mudaSinalPesoCamada1D;
	
	return str;

}

}
