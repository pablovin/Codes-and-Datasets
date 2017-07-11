package util.redeneural.funcaoteste;

import util.redeneural.base.FuncaoAtivacao;

public class SigmoideLogisticaFuncaoAtivacao implements FuncaoAtivacao{

	@Override
	public double funcaoAtivacao(Double somatorio) {
		return 1/(1+Math.pow(Math.E, -1*somatorio));
	}

	@Override
	public double funcaoAtivacaoDerivada(Double somatorio) {
		return 1/(1+Math.pow(Math.E, -somatorio))*(1-1/(1+Math.pow(Math.E, -somatorio)));
	}

	public String toString()
	{
		return "SigmLog";
	}

	@Override
	public double getValorMedioSaida() {
		return 0.5;
	}
	
}
