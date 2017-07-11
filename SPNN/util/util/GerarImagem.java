package util;

public class GerarImagem {
	public static void geracao() throws Exception{
		int tamanho = 19;
		String caminho = "C:\\Users\\Milla\\Documents\\10º período\\TCC\\Experimentos\\Bases-de-imagens\\Formas-basicas\\experimento2\\";
		double[][] img = new double[tamanho][tamanho];
		
		for(int i=0; i<tamanho; i++){
			for(int j=0; j<tamanho; j++){
				//preto
				if(j-i==tamanho-1 || (i+j)%3==0){
					img[i][j] = 0; 
				}//branco
				else{
					img[i][j] = 255;
				}
				
			}
		}
		
		String caminho2 = caminho + "14" +".png";
		//img = util.imagem.TransformacaoUtil.normalizarImagem(img, 0, 255);
		ArquivoImagemUtil.salvaImagem(caminho2, img);
	}
	
	public static void main(String[] args) throws Exception {
		geracao();
	}
}