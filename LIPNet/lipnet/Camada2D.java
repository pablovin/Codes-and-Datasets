package lipnet;
import java.util.HashMap;

public class Camada2D {

	private Neuronio2D[][] neuronios;

	private int tamanhoX;

	private int tamanhoY;

	private int receptivo;

	private int overlap;

	private int campoInibicao;

	private int posicaoCamada;

	private boolean primeiraCamada;
	
	private double pesoInibitorio;
	
	public Camada2D(int x, int y, int posicaoCamada, int receptivo,
			int overlap, int campoInibicao, double pesoInibitorio, boolean primeiraCamada) {
		neuronios = new Neuronio2D[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				neuronios[i][j] = new Neuronio2D(i, j, posicaoCamada,
						primeiraCamada);
			}
		}
		this.posicaoCamada = posicaoCamada;
		this.tamanhoX = x;
		this.tamanhoY = y;
		this.receptivo = receptivo;
		this.overlap = overlap;
		this.campoInibicao = campoInibicao;
		this.primeiraCamada = primeiraCamada;
		this.pesoInibitorio = pesoInibitorio/(Math.pow(this.campoInibicao*2+1,2));
	}

	public Camada2D clone() {
		Camada2D retorno = new Camada2D(tamanhoX, tamanhoY, posicaoCamada,
				receptivo, overlap, campoInibicao, pesoInibitorio, primeiraCamada);
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				retorno.neuronios[i][j] = this.neuronios[i][j].clone();
			}
		}
		return retorno;
	}

	public Neuronio2D[][] getNeuronios() {
		return neuronios;
	}

	public void setNeuronios(Neuronio2D[][] neuronios) {
		this.neuronios = neuronios;
	}

	public int getPosicaoCamada() {
		return posicaoCamada;
	}

	public void setPosicaoCamada(int posicaoCamada) {
		this.posicaoCamada = posicaoCamada;
	}

	public int getOverlap() {
		return overlap;
	}

	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}

	public int getReceptivo() {
		return receptivo;
	}

	public void setReceptivo(int receptivo) {
		this.receptivo = receptivo;
	}

	public int getCampoInibicao() {
		return campoInibicao;
	}

	public void setCampoInibicao(int campoInibicao) {
		this.campoInibicao = campoInibicao;
	}

	public Neuronio1D[] rearrumaSaida() {

		Neuronio1D[] retorno = new Neuronio1D[tamanhoX * tamanhoY];

		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				retorno[((tamanhoY) * i) + j] = new Neuronio1D(
						((tamanhoY) * i) + j, -1, 0);
				retorno[((tamanhoY) * i) + j]
						.setSaida(neuronios[i][j].getSaida());
			}
		}

		return retorno;

	}

	public static boolean imprime = false;

	public void atualizaSaidaNeuronios(Neuronio2D[][] entradas) {
		// Passo 1: Excitatório
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				double somatorio = 0;
				if (primeiraCamada) {
					somatorio = this.calculaSomatorioNeuronio(neuronio,
							entradas);
				} else {
					somatorio = neuronio.getPesoBias()
							+ this.calculaSomatorioNeuronio(neuronio, entradas);
				}
				neuronio.funcaoAtivacao(somatorio);
			}
		}
		// Passo 2: Inibitório
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				double somatorio = 0;
				if (!primeiraCamada) {
					somatorio = calculaSomatorioInibitorioNeuronio(neuronio, entradas);
				}
				neuronio.setSomatorioEntrada(neuronio.getSomatorioEntrada()-somatorio);
			}
		}

		//System.out.println("Calculando saídas para camada "+this.getPosicaoCamada());
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.funcaoAtivacao(neuronio.getSomatorioEntrada());
				//System.out.print(neuronio.getSomatorioEntrada()+"|"+neuronio.getSaida()+" ");
			}
			//System.out.println();
		}
	}

	public void atualizaSaidaNeuronios(double[][] entradas) {
		// Passo 1: Excitatório
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.setSaida(entradas[i][j]);
				neuronio.setSomatorioEntrada(entradas[i][j]);
				/*double somatorio = 0;
				if (primeiraCamada) {
					somatorio = this.calculaSomatorioNeuronio(neuronio,
							entradas);
				} else {
					somatorio = neuronio.getPesoBias()
							+ this.calculaSomatorioNeuronio(neuronio, entradas);
				}
				neuronio.funcaoAtivacao(somatorio);*/
			}
		}
		// Nesse caso não uso inibitório porque essa parte é só para converter
		// da imagem para neurônios
	}

	public int getTamanhoX() {
		return tamanhoX;
	}

	public void setTamanhoX(int tamanhoX) {
		this.tamanhoX = tamanhoX;
	}

	public int getTamanhoY() {
		return tamanhoY;
	}

	public void setTamanhoY(int tamanhoY) {
		this.tamanhoY = tamanhoY;
	}

	public void atualizaSensibilidadeSaida(Neuronio2D[][] entradas,
			Neuronio1D[] saidas, int soma) {
		for (int i = 0; i < this.tamanhoX; i++) {
			for (int j = 0; j < this.tamanhoY; j++) {
				double somatorioErro = 0;
				Neuronio1D tmp = new Neuronio1D(soma + ((tamanhoY) * i) + j, -1,
						0);
				for (Neuronio1D neuronio : saidas) {
					somatorioErro += neuronio.getSensibilidade()
							* neuronio.getPesos()[tmp.getPosicao()];
				}
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.setSomatorioErro(somatorioErro);
				//neuronio.funcaoAtivacaoDerivadaSaida(neuronio.getSomatorioEntrada(), somatorioErro);
			}
		}
		
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				double somatorioErro = 0;
				if (!primeiraCamada || posicaoCamada > 0) {
					somatorioErro = calculaErroInibitorioNeuronio(neuronio);
				}
				neuronio.setSomatorioErro(neuronio.getSomatorioErro()-somatorioErro);
			}
		}
		
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.funcaoAtivacaoDerivada(neuronio.getSomatorioEntrada(), neuronio.getSomatorioErro());
			}
		}
		
	}

	public void atualizaSensibilidadeSaida(double[][] entradas,
			Neuronio1D[] saidas, int soma) {
		for (int i = 0; i < this.tamanhoX; i++) {
			for (int j = 0; j < this.tamanhoY; j++) {
				double somatorioErro = 0;
				Neuronio1D tmp = new Neuronio1D(soma + (tamanhoY * i) + j, -1,
						0);
				for (Neuronio1D neuronio : saidas) {
					somatorioErro += neuronio.getSensibilidade()
							* neuronio.getPesos()[tmp.getPosicao()];
				}
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.setSomatorioErro(somatorioErro);
				//neuronio.funcaoAtivacaoDerivadaSaida(neuronio.getSomatorioEntrada(), somatorioErro);
			}
		}
		
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				double somatorioErro = 0;
				if (!primeiraCamada || posicaoCamada > 0) {
					somatorioErro = calculaErroInibitorioNeuronio(neuronio);
				}
				neuronio.setSomatorioErro(neuronio.getSomatorioErro()-somatorioErro);
			}
		}
		
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.funcaoAtivacaoDerivada(neuronio.getSomatorioEntrada(), neuronio.getSomatorioErro());
			}
		}
		
	}

	private double calculaSomatorioNeuronio(Neuronio2D neuronio,
			Neuronio2D[][] entradas) {
		double somatorio = 0;
		int inicioX = (receptivo - overlap) * neuronio.getPosX();
		int inicioY = (receptivo - overlap) * neuronio.getPosY();
		for (int m = inicioX; m < inicioX + receptivo; m++) {
			for (int n = inicioY; n < inicioY + receptivo; n++) {
				if (m >= 0 && m < entradas.length && n >= 0
						&& n < entradas.length) {
					somatorio += entradas[m][n].getSaida()
							* entradas[m][n].getPesoAssociado();
				}
			}
		}
		return somatorio;
	}

	private double calculaSomatorioNeuronio(Neuronio2D neuronio,
			double[][] entradas) {
		double somatorio = 0;
		int inicioX = (receptivo - overlap) * neuronio.getPosX();
		int inicioY = (receptivo - overlap) * neuronio.getPosY();
		for (int m = inicioX; m < inicioX + receptivo; m++) {
			for (int n = inicioY; n < inicioY + receptivo; n++) {
				if (m >= 0 && m < entradas.length && n >= 0
						&& n < entradas.length) {
					somatorio += entradas[m][n];
				}
			}
		}

		return somatorio;
	}

	private double calculaSomatorioNeuronioTmp(Neuronio2D neuronio,
			Neuronio2D[][] entradas) {
		double somatorio = 0;
		int inicioX = (receptivo - overlap) * neuronio.getPosX();
		int inicioY = (receptivo - overlap) * neuronio.getPosY();
		for (int m = inicioX; m < inicioX + receptivo; m++) {
			for (int n = inicioY; n < inicioY + receptivo; n++) {
				if (m >= 0 && m < entradas.length && n >= 0
						&& n < entradas.length) {
					somatorio += entradas[m][n].getSaida()
							* pesoInibitorio;
				}
			}
		}
		return somatorio;
	}
	
	private double calculaSomatorioInibitorioNeuronio(Neuronio2D neuronio, Neuronio2D[][] entradas) {
		double somatorio = 0;
		int inicioX = neuronio.getPosX() - campoInibicao;
		int inicioY = neuronio.getPosY() - campoInibicao;
		double conta = 0;
		for (int m = inicioX; m <= neuronio.getPosX() + campoInibicao; m++) {
			for (int n = inicioY; n <= neuronio.getPosY() + campoInibicao; n++) {
				if (m >= 0 && m < neuronios.length && n >= 0
						&& n < neuronios[0].length && (m != neuronio.getPosX() || n != neuronio.getPosY())) {
					somatorio += neuronios[m][n].getSaida()
							//* neuronios[m][n].getPesoAssociado();
							* pesoInibitorio;
					conta++;
				}
			}
		}
		if(conta > 0) {
			return somatorio;
		} else {
			return 0;
		}
	}

	public void atualizaSensibilidadesOcultas(Neuronio2D[][] saidas, int receptivoSaida, int overlapSaida,
			int campoInibitorioSaida, int tamanhoXSaida, int tamanhoYSaida) {
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.setSomatorioErro(calculaErroPonderado(saidas, neuronio, receptivoSaida,
								overlapSaida, campoInibitorioSaida,
								tamanhoXSaida, tamanhoYSaida));
			}
		}
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				double somatorioErro = 0;
				if (!primeiraCamada || posicaoCamada > 0) {
					somatorioErro = calculaErroInibitorioNeuronio(neuronio);
				}
				neuronio.setSomatorioErro(neuronio.getSomatorioErro()-somatorioErro);
			}
		}
		
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.funcaoAtivacaoDerivada(neuronio.getSomatorioEntrada(), neuronio.getSomatorioErro());
			}
		}
	}

	private double calculaErroInibitorioNeuronio(Neuronio2D neuronio) {
		double somatorio = 0;
		int inicioX = neuronio.getPosX() - campoInibicao;
		int inicioY = neuronio.getPosY() - campoInibicao;
		double conta = 0;
		for (int m = inicioX; m <= neuronio.getPosX() + campoInibicao; m++) {
			for (int n = inicioY; n <= neuronio.getPosY() + campoInibicao; n++) {
				if (m >= 0 && m < neuronios.length && n >= 0
						&& n < neuronios[0].length && (m != neuronio.getPosX() || n != neuronio.getPosY())) {
					//somatorio += neuronios[m][n].getSensibilidade()*neuronio.getPesoAssociado();
					somatorio += neuronios[m][n].getSomatorioErro()*pesoInibitorio;
					conta++;
				}
			}
		}
		if(conta > 0) {
			return somatorio;
		} else {
			return 0;
		}
		//return somatorio;
	}
	
	private double calculaErroPonderado(Neuronio2D[][] saidas,
			Neuronio2D neuronio, int receptivoSaida, int overlapSaida,
			int campoInibitorioSaida, int tamanhoXSaida, int tamanhoYSaida) {
		double retorno = 0;
		double tmp = (neuronio.getPosX() - receptivoSaida) + 1;
		tmp = tmp / (receptivoSaida - overlapSaida);
		long iMin = Math.round(Math.ceil(tmp) + 1) - 1;
		tmp = (neuronio.getPosX());
		tmp = tmp / (receptivoSaida - overlapSaida);
		long iMax = Math.round(Math.floor(tmp) + 1) - 1;
		tmp = (neuronio.getPosY() - receptivoSaida) + 1;
		tmp = tmp / (receptivoSaida - overlapSaida);
		long jMin = Math.round(Math.ceil(tmp) + 1) - 1;
		tmp = (neuronio.getPosY());
		tmp = tmp / (receptivoSaida - overlapSaida);
		long jMax = Math.round(Math.floor(tmp) + 1) - 1;
		for (long i = iMin; i <= iMax; i++) {
			for (long j = jMin; j <= jMax; j++) {
				if (i >= 0 && i < tamanhoXSaida && j >= 0 && j < tamanhoYSaida) {
					retorno += saidas[new Long(i).intValue()][new Long(j)
							.intValue()].getSensibilidade();
				}
			}
		}
		return retorno;
	}

	public void calculaGradientesPesos(Neuronio2D[][] saidas,
			int tamanhoXSaida, int tamanhoYSaida, int receptivoSaida,
			int overlapSaida, int campoInibitorioSaida) {
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				double somatorio = this.calculaErroPonderado(saidas, neuronio,
						receptivoSaida, overlapSaida, campoInibitorioSaida,
						tamanhoXSaida, tamanhoYSaida);
				neuronio.setGradiente(neuronio.getGradiente()
						+ (neuronio.getSaida() * somatorio));
			}
		}
	}

	// Nao precisa rodar
	/*
	 * public void calculaGradientesPesos(double[][] entradas, double[][] pesos,
	 * double[][] gradiente, Neuronio2D[][] saidas, int tamanhoXSaida, int
	 * tamanhoYSaida, int tamanhoXEntrada, int tamanhoYEntrada, int
	 * receptivoSaida, int overlapSaida) { for (int i = 0; i < tamanhoXEntrada;
	 * i++) { for (int j = 0; j < tamanhoYEntrada; j++) { double somatorio = 0;
	 * double tmp = (i - receptivoSaida) + 1; tmp = tmp / (receptivoSaida -
	 * overlapSaida); int inicioX = new Long(Math.round(Math.ceil(tmp) + 1))
	 * .intValue() - 1; tmp = (i); tmp = tmp / (receptivoSaida - overlapSaida);
	 * int fimX = new Long(Math.round(Math.floor(tmp) + 1)).intValue() - 1; tmp =
	 * (j - receptivoSaida) + 1; tmp = tmp / (receptivoSaida - overlapSaida);
	 * int inicioY = new Long(Math.round(Math.ceil(tmp) + 1)) .intValue() - 1;
	 * tmp = (j); tmp = tmp / (receptivoSaida - overlapSaida); int fimY = new
	 * Long(Math.round(Math.floor(tmp) + 1)).intValue() - 1; for (int m =
	 * inicioX; m <= fimX; m++) { for (int n = inicioY; n <= fimY; n++) { if (m >=
	 * 0 && m < tamanhoXSaida && n >= 0 && n < tamanhoYSaida) { somatorio +=
	 * saidas[m][n].getSensibilidade(); } } } // Inicio Inibitorio for (int m =
	 * inicioX - campoInibicao; m < inicioX; m++) { for (int n = inicioY -
	 * campoInibicao; n < inicioY; n++) { if (m >= 0 && m < tamanhoXSaida && n >=
	 * 0 && n < tamanhoYSaida) { somatorio -= saidas[m][n].getSensibilidade(); } } }
	 * for (int m = fimX + 1; m <= fimX + campoInibicao; m++) { for (int n =
	 * inicioY - campoInibicao; n < inicioY; n++) { if (m >= 0 && m <
	 * tamanhoXSaida && n >= 0 && n < tamanhoYSaida) { somatorio -=
	 * saidas[m][n].getSensibilidade(); } } } for (int m = inicioX -
	 * campoInibicao; m < inicioX; m++) { for (int n = fimY + 1; n <= fimY +
	 * campoInibicao; n++) { if (m >= 0 && m < tamanhoXSaida && n >= 0 && n <
	 * tamanhoYSaida) { somatorio -= saidas[m][n].getSensibilidade(); } } } for
	 * (int m = fimX + 1; m <= fimX + campoInibicao; m++) { for (int n = fimY +
	 * 1; n <= fimY + campoInibicao; n++) { if (m >= 0 && m < tamanhoXSaida && n >=
	 * 0 && n < tamanhoYSaida) { somatorio -= saidas[m][n].getSensibilidade(); } } } //
	 * Fim Inibitorio gradiente[i][j] = gradiente[i][j] + (entradas[i][j] *
	 * pesos[i][j] * somatorio); } } }
	 */

	public void calculaGradientesBias() {
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.setGradienteBias(neuronio.getGradienteBias()
						+ neuronio.getSensibilidade());
			}
		}
	}

	private void zeraGradienteBias() {
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.setGradienteBias(0.0);
			}
		}
	}

	private void zeraGradientePesos() {
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.setGradiente(0.0);
			}
		}
	}

	public void zeraGradientes() {
		this.zeraGradienteBias();
		this.zeraGradientePesos();
	}

	public void atualizaPesos(double max, double min, double nPos, double nNeg) {
		// System.out.println("Peso-Camada 2-D "+this.getPosicaoCamada());
		for (int i = 0; i < tamanhoX; i++) {
			// System.out.println(i);
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				// System.out.print(j+": "+neuronio.getPesoAssociado());
				double sinal = Math.signum(neuronio.getGradiente()
						* neuronio.getGradienteAnterior());
				double sinalGrad = Math.signum(neuronio.getGradiente());
				if (sinal > 0) {
					double delta = Math.min(neuronio.getDeltaAnterior() * nPos,
							max);
					neuronio.setDeltaAnterior(delta);
					double atualiza = (-sinalGrad) * delta;
					neuronio.setPesoAssociado(neuronio.getPesoAssociado()
							+ atualiza);
					// System.out.println("2D "+i+"|"+j+" - Sinal positivo:
					// ajustou com "+atualiza+" novo peso =
					// "+neuronio.getPesoAssociado());
					neuronio.setGradienteAnterior(neuronio.getGradiente());
					neuronio.setAtualizaAnterior(atualiza);
				} else if (sinal < 0) {
					double delta = Math.max(neuronio.getDeltaAnterior() * nNeg,
							min);
					neuronio.setDeltaAnterior(delta);
					// neuronio.setPesoAssociado(neuronio.getPesoAssociado()-neuronio.getAtualizaAnterior());
					// System.out.println("2D "+i+"|"+j+" - Sinal negativo:
					// ajustou com "+-neuronio.getAtualizaAnterior()+" novo peso
					// = "+neuronio.getPesoAssociado());
					neuronio.setGradienteAnterior(0.0);
					neuronio.setAtualizaAnterior(null);
				} else {
					double delta = neuronio.getDeltaAnterior();
					double atualiza = (-sinalGrad) * delta;
					neuronio.setPesoAssociado(neuronio.getPesoAssociado()
							+ atualiza);
					// System.out.println("2D "+i+"|"+j+" - Sinal zero: ajustou
					// com "+atualiza+" novo peso =
					// "+neuronio.getPesoAssociado());
					neuronio.setGradienteAnterior(neuronio.getGradiente());
					neuronio.setAtualizaAnterior(atualiza);
				}
				// System.out.println(" | "+neuronio.getPesoAssociado());
			}
		}
	}

	public void atualizaBias(double max, double min, double nPos, double nNeg) {
		// System.out.println("Bias-Camada 2-D "+this.getPosicaoCamada());
		for (int i = 0; i < tamanhoX; i++) {
			// System.out.println(i);
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				// System.out.print(j+": "+neuronio.getPesoBias());
				double sinal = Math.signum(neuronio.getGradienteBias()
						* neuronio.getGradienteAnteriorBias());
				double sinalGrad = Math.signum(neuronio.getGradienteBias());
				if (sinal > 0) {
					double delta = Math.min(neuronio.getDeltaAnteriorBias()
							* nPos, max);
					neuronio.setDeltaAnteriorBias(delta);
					double atualiza = (-sinalGrad) * delta;
					neuronio.setPesoBias(neuronio.getPesoBias() + atualiza);
					// System.out.println("Bias2D "+i+"|"+j+" - Sinal positivo:
					// ajustou com "+atualiza+" novo bias =
					// "+neuronio.getPesoBias());
					neuronio.setGradienteAnteriorBias(neuronio
							.getGradienteBias());
					neuronio.setAtualizaAnteriorBias(atualiza);
				} else if (sinal < 0) {
					double delta = Math.max(neuronio.getDeltaAnteriorBias()
							* nNeg, min);
					neuronio.setDeltaAnteriorBias(delta);
					// neuronio.setPesoBias(neuronio.getPesoBias()-neuronio.getAtualizaAnteriorBias());
					// System.out.println("Bias2D "+i+"|"+j+" - Sinal negativo:
					// ajustou com "+-neuronio.getAtualizaAnteriorBias()+" novo
					// bias = "+neuronio.getPesoBias());
					neuronio.setGradienteAnteriorBias(0.0);
					neuronio.setAtualizaAnteriorBias(null);
				} else {
					double delta = neuronio.getDeltaAnteriorBias();
					double atualiza = (-sinalGrad) * delta;
					neuronio.setPesoBias(neuronio.getPesoBias() + atualiza);
					// System.out.println("Bias2D "+i+"|"+j+" - Sinal zero:
					// ajustou com "+atualiza+" novo bias =
					// "+neuronio.getPesoBias());
					neuronio.setGradienteAnteriorBias(neuronio
							.getGradienteBias());
					neuronio.setAtualizaAnteriorBias(atualiza);
				}
				// System.out.println(" | "+neuronio.getPesoBias());
			}
		}
	}

	public void print() {
		System.out.println("Pesos para camada 2-D " + posicaoCamada);
		for (int i = 0; i < neuronios.length; i++) {
			for (int j = 0; j < neuronios[0].length; j++) {
				System.out.println("Neuronio na posição " + i + "," + j
						+ " peso = " + neuronios[i][j].getPesoAssociado()
						+ " |  bias = " + neuronios[i][j].getPesoBias());
			}
		}
		System.out.println();
	}
	
	public void printSaida() {
		System.out.println("Saidas para camada 2-D " + posicaoCamada);
		for (int i = 0; i < neuronios.length; i++) {
			for (int j = 0; j < neuronios[0].length; j++) {
				System.out.println("Neuronio na posição " + i + "," + j
						+ " saida = " + neuronios[i][j].getSaida());
			}
		}
		System.out.println();
	}
	
	public boolean[][] atualizaSaidaNeuronios(double[][] entradas,boolean[][] neuroniosConsiderados) {
		// Passo 1: Excitatório
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				if(neuroniosConsiderados[i][j]) {
				Neuronio2D neuronio = neuronios[i][j];
				neuronio.setSaida(entradas[i][j]);
				neuronio.setSomatorioEntrada(entradas[i][j]);
				/*double somatorio = 0;
				if (primeiraCamada) {
					somatorio = this.calculaSomatorioNeuronio(neuronio,
							entradas);
				} else {
					somatorio = neuronio.getPesoBias()
							+ this.calculaSomatorioNeuronio(neuronio, entradas);
				}
				neuronio.funcaoAtivacao(somatorio);*/
				}
			}
		}
		
		return neuroniosConsiderados;
	}
	
	public boolean[][] atualizaSaidaNeuronios(Neuronio2D[][] entradas, boolean[][] neuroniosConsiderados) {
		boolean[][] retorno = new boolean[tamanhoX][tamanhoY];
		// Passo 1: Excitatório
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				double somatorio = 0;
				if (primeiraCamada) {
					somatorio = this.calculaSomatorioNeuronio(neuronio, entradas, neuroniosConsiderados, retorno);
				} else {
					somatorio = //neuronio.getPesoBias() +
							this.calculaSomatorioNeuronio(neuronio, entradas, neuroniosConsiderados, retorno);
				}
				neuronio.funcaoAtivacao(somatorio);
			}
		}
		// Passo 2: Inibitório
//		for (int i = 0; i < tamanhoX; i++) {
//			for (int j = 0; j < tamanhoY; j++) {
//				Neuronio2D neuronio = neuronios[i][j];
//				double somatorio = 0;
//				if (!primeiraCamada) {
//					somatorio = calculaSomatorioInibitorioNeuronio(neuronio, entradas);
//				}
//				neuronio.setSomatorioEntrada(neuronio.getSomatorioEntrada()-somatorio);
//			}
//		}

		//System.out.println("Calculando saídas para camada "+this.getPosicaoCamada());
		for (int i = 0; i < tamanhoX; i++) {
			for (int j = 0; j < tamanhoY; j++) {
				Neuronio2D neuronio = neuronios[i][j];
				//neuronio.funcaoAtivacao(neuronio.getSomatorioEntrada());
				neuronio.setSaida(neuronio.getSomatorioEntrada()); //AQUI MILLA!!!!!
				//System.out.print(neuronio.getSomatorioEntrada()+"|"+neuronio.getSaida()+" ");
			}
			//System.out.println();
		}
		return retorno;
	}
	
	private double calculaSomatorioNeuronio(Neuronio2D neuronio,
			Neuronio2D[][] entradas, boolean[][] neuroniosConsiderados, boolean[][] retorno) {
		double somatorio = 0;
		int inicioX = (receptivo - overlap) * neuronio.getPosX();
		int inicioY = (receptivo - overlap) * neuronio.getPosY();
		
		//System.out.print(inicioX+"|"+(inicioX+receptivo-1)+"|"+inicioY+"|"+(inicioY+receptivo-1));
		for (int m = inicioX; m < inicioX + receptivo; m++) {
			for (int n = inicioY; n < inicioY + receptivo; n++) {
				if (m >= 0 && m < entradas.length && n >= 0
						&& n < entradas[0].length) {
					if(neuroniosConsiderados[m][n]) {
						somatorio += entradas[m][n].getSaida()
							* entradas[m][n].getPesoAssociado();
						retorno[neuronio.getPosX()][neuronio.getPosY()] = true;
					}
				}
			}
		}
		//System.out.print(" "+somatorio+" ");
		return somatorio;
	}


}
