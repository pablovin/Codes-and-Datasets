package util.avaliacao;

import java.util.ArrayList;

public class AvaliacaoDuasClasses {

	public static double avaliaErroPorCurvaRoc(ArrayList<double[]> saidaEsperada, ArrayList<double[]> saidaCalculada, double intervalo,
			boolean imprime, String path, String id)
	{
		return AvaliacaoUtilDuasClasses.avaliarAUC(saidaEsperada, saidaCalculada, intervalo, imprime, path, id);
	}
	
	public static double avaliaErroPorCurvaRoc(ArrayList<double[]> saidaEsperada, ArrayList<double[]> saidaCalculada, double intervalo)
	{
		return avaliaErroPorCurvaRoc(saidaEsperada, saidaCalculada, intervalo, false, null, 0+"");
	}
	
	
	
	public static double avaliaErroPorCurvaRocPorPosNeg(ArrayList<double[]> saidaPos, ArrayList<double[]> saidaNeg,
			double intervalo, boolean imprime, String path, String id)
	{
		return AvaliacaoUtilDuasClasses.avaliarAUCPosNeg(saidaPos, saidaNeg, intervalo, imprime, path, id);
	}

	public static double avaliaErroPorCurvaRocPorPosNeg(ArrayList<double[]> saidaPos, ArrayList<double[]> saidaNeg, double intervalo)
	{
		return avaliaErroPorCurvaRocPorPosNeg(saidaPos, saidaNeg, intervalo, false, null, 0+"");
	}
	
	
	
	
	//@compareFitness
	//@retorna true, se particula1.fitness for melhor que particula2.fitness; false, caso contrário.
	public static boolean compareFitness_ehMelhor(double fitnessParticula1, double fitnessParticula2)
	{
		return fitnessParticula1 > fitnessParticula2;
	}
	
	
	
	
}
