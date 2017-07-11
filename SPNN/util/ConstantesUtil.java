package util;

import util.redeneural.base.FuncaoAtivacao;
import util.redeneural.funcaoteste.SigmoideLogisticaFuncaoAtivacao;
import util.redeneural.funcaoteste.TangenteHiperbolicaFuncaoAtivacao;

public interface ConstantesUtil {

	FuncaoAtivacao FUNCAO_ATIVACAO_TANGENTE_HIPERBOLICA = new TangenteHiperbolicaFuncaoAtivacao();
	FuncaoAtivacao FUNCAO_ATIVACAO_SIGMOIDE_LOGISTICA = new SigmoideLogisticaFuncaoAtivacao();
	
}
