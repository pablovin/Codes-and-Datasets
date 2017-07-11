package util.banco.base;

import java.util.ArrayList;

public class Base {

	private ArrayList<Integer> indices;
	
	private ArrayList<double[][]> imagens;

	public Base(ArrayList<Integer> indices, ArrayList<double[][]> imagens) {
		super();
		this.setIndices(indices);
		this.setImagens(imagens);		
	}

	public ArrayList<double[][]> getImagens() {
		return imagens;
	}

	private void setImagens(ArrayList<double[][]> imagens) {
		this.imagens = imagens;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}

	private void setIndices(ArrayList<Integer> indices) {
		this.indices = indices;
	}
	
}
