package util.redeneural.funcaoteste;

import util.redeneural.base.FuncaoAtivacao;

public class TangenteHiperbolicaFuncaoAtivacao implements FuncaoAtivacao {

	@Override
	public double funcaoAtivacao(Double somatorio) {
		return Math.tanh(somatorio);
	}

	@Override
	public double funcaoAtivacaoDerivada(Double somatorio) {
		return 1 - Math.pow(Math.tanh(somatorio),2);
	}
	public String toString()
	{
		return "TangHip";
	}

	@Override
	public double getValorMedioSaida() {
		return 0;
	}
	
}
