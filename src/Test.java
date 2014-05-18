import java.awt.font.NumericShaper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dados.Registro;

public class Test {
	private static List<Registro> dadosDeTreinamento;
	private static List<Registro> dadosDeTeste;

	private static void lerArquivo() throws IOException {
		dadosDeTreinamento = new ArrayList<Registro>();
		dadosDeTeste = new ArrayList<Registro>();

		String path = "C://Users//Daniel//workspace//MLP//src//";
		String file;

		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				file = "iris.txt";
			} else {
				file = "test.txt";
			}

			File arq = new File(path + file);
			InputStream is = new FileInputStream(arq);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String linha = br.readLine();
			int cont = 0;
			while (linha != null) {
				carregarUnidadesDeEntrada(linha, i == 0 ? true : false);
				linha = br.readLine();
				cont++;

			}
		}
	}

	private static void carregarUnidadesDeEntrada(String linha, boolean treino) {
		int inicio = 0;
		int fim = 0;
		boolean first = true;
		linha.trim();

		char[] linhaArray = linha.toCharArray();
		String linhaFinal = "";

		for (int i = 0; i < linha.length(); i++) {
			if (linhaArray[i] != ' ') {
				linhaFinal = linhaFinal.concat(String.valueOf(linhaArray[i]));
			}
		}

		linha = linhaFinal;

		Registro novoRegistro = new Registro();

		for (int i = 0; i < linha.length(); i++) {

			if (linha.charAt(i) == ',' || i == linha.length() - 1) {
				if (i != linha.length() - 1) {
					fim = i;
				} else {
					fim = i + 1;
				}
				if (first) {
					novoRegistro.getTupla().add(
							Double.valueOf(linha.substring(inicio, fim)));
					first = false;
				} else {
					novoRegistro.getTupla().add(
							Double.valueOf(linha.substring(inicio + 1, fim)));
				}

				inicio = fim;
			}
		}
		if (treino) {
			dadosDeTreinamento.add(novoRegistro);
		} else {
			dadosDeTeste.add(novoRegistro);
		}
	}

	public static List<Registro> normalizarDados(List<Registro> dados){
		int numeroAtributos = dados.get(0).getTupla().size() - 1;
		
		for (int i = 0; i < numeroAtributos; i++) {
			double min = 0, max = 0;			
			for(int j = 0; j < dados.size(); j++){
				double valor = dados.get(j).getTupla().get(i);
				if(j == 0){
					min = valor;
					max = valor;
				}else{
					if(valor < min){
						min = valor;
					}
					if(valor > max){
						max = valor;
					}
				}				
			}
			for(int j = 0; j < dados.size(); j++){
				double valor = dados.get(j).getTupla().get(i);
				double normal = (valor - min) / (max - min);
				dados.get(j).getTupla().set(i, normal);
			}
		}
		
		return dados;
	}
	
	public static void main(String[] args) throws IOException {
		MLP mlp = new MLP(4, 3, 2);
		mlp.setLearningRate(0.1);

		lerArquivo();
		dadosDeTreinamento = normalizarDados(dadosDeTreinamento);
		dadosDeTeste = normalizarDados(dadosDeTeste);
		
		int numeroAtributos = dadosDeTreinamento.get(0).getTupla().size() - 1;

		double[] entradas = new double[numeroAtributos];
		double[] saidas = new double[1];
		double[] result = new double[2];

		for (int epoca = 0; epoca < 100; epoca++) { // Numero de Epocas

			for (int i = 0; i < dadosDeTreinamento.size(); i++) { // Para cada dado
				for (int j = 0; j < numeroAtributos; j++) { // Para cada tupla
					entradas[j] = dadosDeTreinamento.get(i).getTupla().get(j); // Pegar os atributos
				}
				saidas[0] = dadosDeTreinamento.get(i).getTupla().get(numeroAtributos); // Pegar o alvo
				result = mlp.treinar(entradas, saidas);
				// System.out.println(result[1]);
			}

			mlp.ds.addValue(mlp.erroTreinamentoDaEpoca * 100/ dadosDeTreinamento.size() ,
					"Erro de Treinamento", "" + epoca);
			//System.out.println(mlp.erroTreinamentoDaEpoca / dadosDeTreinamento.size());
			mlp.erroTreinamentoDaEpoca = 0;
			
			
			
			for (int i = 0; i < dadosDeTeste.size(); i++) { // Para cada dado
				for (int j = 0; j < numeroAtributos; j++) { // Para cada tupla
					entradas[j] = dadosDeTeste.get(i).getTupla().get(j); // Pegar os atributos
				}
				saidas[0] = dadosDeTeste.get(i).getTupla().get(numeroAtributos); // Pegar o alvo
				result = mlp.validar(entradas, saidas);
							
				for(int j = 1; j <= mlp.nOutput; j++){
					System.out.println("Neurônio" + j + ": " + result[j]);
				}
				
			}
			
			mlp.ds.addValue(mlp.erroValidacaoDaEpoca * 100/ dadosDeTeste.size(),
					"Erro Validação", "" + epoca);
			
			mlp.erroValidacaoDaEpoca = 0;
		}
		
		
		
	}
}