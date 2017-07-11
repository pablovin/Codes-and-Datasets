package configs;
import java.io.BufferedWriter;
import java.io.IOException;

import util.ArquivoImagemUtil;
import util.imagem.TransformacaoUtil;
import util.otimizacao.io.Config;
import util.otimizacao.io.Escritor;


public class SPNNEscritor extends Escritor{


	private BufferedWriter convergenciaWriter;
	private int contConvergenciaWriter;
	private double[][] valorConvergencia;
	
	//private BufferedWriter mapaCamposWriter;
	private int contMapaWriter;
	private double[][] mapa;
	
	private final static String CONVERGENCIA_TRIPLA = "ConvergenciaTripla";
	private final static String ESTRUTURA_ARVORE = "EstruturaArvore";
	private final static String ESTRUTURA_CAMADAS = "EstruturaCamadas";
	private final static String MAPA_CAMPOS_ENTRADA = "MapaCamposEntrada";
	
	
	public SPNNEscritor(Config config, int qtIteracoes, int qtSimulacoes, int qtModeloComp) throws IOException {
		super(config,qtIteracoes, qtSimulacoes,qtModeloComp);
		
		String configPath = getConfigPath();

		convergenciaWriter = Escritor.generateWriter(configPath, getConfigName(),CONVERGENCIA_TRIPLA+".txt");
		//mapaCamposWriter = Escritor.generateWriter(configPath, getConfigName(),MAPA_CAMPOS_ENTRADA+".txt");
		
	}


	public synchronized String adicionarEstruturaArvore(String est, String id) throws IOException {

		BufferedWriter estruturaWriter = generateWriterTemp(getTempPath(), ESTRUTURA_ARVORE, id);
		write(estruturaWriter, est);
		close(estruturaWriter);	
		
		String fileName = getTempPath() + ESTRUTURA_ARVORE + id + ".txt";
		return fileName;
	}

	public synchronized void adicionarEstruturaCamadas(String est, String id) throws IOException {

		BufferedWriter estruturaWriter = generateWriterTemp(getTempPath(), ESTRUTURA_CAMADAS, id);
		write(estruturaWriter, est);
		close(estruturaWriter);	
		
	}
	
	public synchronized void writeMapaCamposEntrada(double[][] value, String id, boolean imprimeTemp) throws Exception
	{
		/*
		BufferedWriter tempWriter = generateWriterTemp(getTempPath(), MAPA_CAMPOS_ENTRADA, id);
		write(tempWriter, value);
		close(tempWriter);	
		*/
		if(mapa == null)
		{
			mapa = value;
		}
		else
		{
			adicionaMatriz(mapa, value);
		}
		

		if(imprimeTemp){
			value = TransformacaoUtil.normalizarImagem(value, 0, 255);
			String tempPath = getTempPath(MAPA_CAMPOS_ENTRADA);
			ArquivoImagemUtil.salvaImagem(tempPath+"_"+id+".png", value);
			
		}
		
		contMapaWriter++;
	}
	

	public synchronized void writeMapaCamposEntrada() throws Exception
	{
		mapa = TransformacaoUtil.normalizarImagem(mapa, 0, 255);
		ArquivoImagemUtil.salvaImagem(
				generatePath(getConfigPath(), getConfigName(), MAPA_CAMPOS_ENTRADA+".png"), mapa);
	}
	
	public synchronized void writeConvergenciaRede(double[][] value, String id) throws IOException
	{
		
		BufferedWriter tempWriter = generateWriterTemp(getTempPath(), CONVERGENCIA_TRIPLA, id);
		write(tempWriter, value);
		close(tempWriter);	
		
		if(valorConvergencia == null)
		{
			valorConvergencia = value;
		}
		else
		{
			adicionaMatriz(valorConvergencia, value);
		}
		contConvergenciaWriter++;
	}
	

	public synchronized void writeConvergenciaRede() throws IOException
	{
		geraMatrizFinal(valorConvergencia, contConvergenciaWriter);
		writeMatriz(convergenciaWriter, valorConvergencia);
	}
	
	private synchronized void adicionaMatriz(double[][] valorValidacaoCruzada, double[][] value) throws IOException
	{
		
		int qtX = valorValidacaoCruzada.length;
		int qtY = valorValidacaoCruzada[0].length;
		
		for (int i = 0; i < qtX; i++)
		{
			for (int j = 0; j < qtY; j++) 
			{	
				valorValidacaoCruzada[i][j] += value[i][j];	
			}
		}
	
	}
	
	private synchronized void geraMatrizFinal(double[][] valorEntrada, int qtd) throws IOException
	{
		int qtX = valorEntrada.length;
		int qtY = valorEntrada[0].length;
		
		for (int i = 0; i < qtX; i++) {
			for (int j = 0; j < qtY; j++) {
				
				valorEntrada[i][j] = valorEntrada[i][j] / qtd;
			}
		}
	}
	
	private synchronized void writeMatriz(BufferedWriter writer, double[][] valorEntrada) throws IOException
	{
		int qtX = valorEntrada.length;
		int qtY = valorEntrada[0].length;
		
		for (int i = 0; i < qtX; i++) {
			String valor = "";
			for (int j = 0; j < qtY; j++) {
				
				valor = valor + valorEntrada[i][j] + "\t";
			}
			Escritor.write(writer, valor);
		}
	}
	
	public void closeAll() throws IOException
	{
		Escritor.close(convergenciaWriter);
		super.closeAll();
	}

	
	
	public synchronized void writeMatriz(String id,double[][] valorEntrada) throws IOException
	{

		String configPath = getConfigPath();

		BufferedWriter writer = Escritor.generateWriter(configPath, getConfigName(),id+".txt");
		
		int qtX = valorEntrada.length;
		int qtY = valorEntrada[0].length;
		
		for (int i = 0; i < qtX; i++) {
			String valor = "";
			for (int j = 0; j < qtY; j++) {
				
				valor = valor + valorEntrada[i][j] + "\t";
			}
			writer.append(valor);
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}
	
	public synchronized void writeMatriz(String id, String valor) throws IOException
	{
		String configPath = getConfigPath();

		BufferedWriter writer = Escritor.generateWriter(configPath, getConfigName(),id+".txt");
	
		writer.write(valor);
		
		writer.flush();
		writer.close();
		
	}
	
	public BufferedWriter getWriter(String id) throws IOException{
		
		String configPath = getConfigPath();
		BufferedWriter writer = Escritor.generateWriter(configPath, getConfigName(),id+".txt");
		return writer;
	}
	
	
}

