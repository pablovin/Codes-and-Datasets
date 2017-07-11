package util.banco.bancoGeral.base;

import java.util.ArrayList;
import java.util.List;

public class FirstOrderedOthersRandomBaseSplitter extends Splitter {

	@Override
	public List<String>[] splitBaseDeDados(List<String> caminhoDados,
			int[] qtDadosPorBase) {
		
		int qtBases = qtDadosPorBase == null? 0: qtDadosPorBase.length;
		
		if(qtBases == 0){
			return new List[]{caminhoDados};
		}
		
		List<String> copy = new ArrayList<String>();
		for (String string : caminhoDados) {
			copy.add(string);
		}
		
		List<String>[] retorno = new List[qtDadosPorBase.length];
		
		//First Ordered
		int qt = qtDadosPorBase[0];		
		List<String> dados = new ArrayList<String>();
		
		for (int j = 0; j < qt; j++) {			
			String caminho = copy.remove(0);
			dados.add(caminho);
		}
		retorno[0] = dados;		
			
		
		//Others Random
		for (int i = 1; i < qtBases; i++) {
			qt = qtDadosPorBase[i];			
			dados = new ArrayList<String>();
			
			for (int j = 0; j < qt; j++) {				
				int indice = (int) (copy.size() * Math.random());
				String caminho = copy.remove(indice);
				dados.add(caminho);			
			}
			
			retorno[i] = dados;			
		}
		
		return retorno;
	}

	public static void main(String[] args) {
		
		List<String> caminhos = new ArrayList<String>();
		caminhos.add("A");
		caminhos.add("B");
		caminhos.add("C");
		caminhos.add("D");
		caminhos.add("E");
		caminhos.add("F");
		caminhos.add("G");
		caminhos.add("H");
		caminhos.add("I");
		caminhos.add("J");
		caminhos.add("K");
		
		Splitter split = new FirstOrderedOthersRandomBaseSplitter();
		List<String>[] splits = split.splitBaseDeDados(caminhos, new int[]{3,6});
	
		for (List<String> s:splits) {
			
			for (String string : s) {
				System.out.println(string);
			}
			
			System.out.println();
		}
	
	}
	
	public String toString(){
		return "FirstOrderedOthersRandomBaseSplitter";
	}
	
}
