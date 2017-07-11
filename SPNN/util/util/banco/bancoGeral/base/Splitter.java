package util.banco.bancoGeral.base;

import java.util.List;

public abstract class Splitter {
	
	public abstract List<String>[] splitBaseDeDados(List<String> caminhoDados, int[] qtDadosPorBase);
	
}
