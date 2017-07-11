package operacoes.geometria;

public class Triangulo{
		
		private int[] indicesPontos;
		private Circulo circulo;
		
		public Triangulo(int[] indicesPontos, Circulo circulo) {
			super();
			this.indicesPontos = indicesPontos;
			this.circulo = circulo;
		}

		public int[] getIndicesPontos() {
			return indicesPontos;
		}

		public Circulo getCirculo() {
			return circulo;
		}

		
	}

	