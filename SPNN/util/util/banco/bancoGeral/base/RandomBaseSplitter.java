package util.banco.bancoGeral.base;

import java.util.ArrayList;
import java.util.List;

public class RandomBaseSplitter extends Splitter{

	public List<String>[] splitBaseDeDados(List<String> caminhoDados, int[] qtDadosPorBase){
		
		int qtBases = qtDadosPorBase == null? 0: qtDadosPorBase.length;
		
		if(qtBases == 0){
			return new List[]{caminhoDados};
		}
		
		List<String> copy = new ArrayList<String>();
		for (String string : caminhoDados) {
			copy.add(string);
		}
		
		List<String>[] retorno = new List[qtDadosPorBase.length];
		
		for (int i = 0; i < qtBases; i++) {
			
			int qt = qtDadosPorBase[i];
			
			List<String> dados = new ArrayList<String>();
			
			for (int j = 0; j < qt; j++) {
				
				int indice = (int) (copy.size() * Math.random());
				String caminho = copy.remove(indice);
				dados.add(caminho);
			
			}
			
			retorno[i] = dados;		
			
		}
		
		return retorno;
		
	}
	
	public String toString(){
		return "RandomBaseSplitter";
	}
	
}
