package dados;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import dados.Registro;

public class ResourceManager {
	public List<Registro> dadosDeTreinamento;
	public List<Registro> dadosDeTeste;
	
	public void lerArquivos() throws IOException {
		dadosDeTreinamento = new ArrayList<Registro>();
		dadosDeTeste = new ArrayList<Registro>();

		String path = "resources/";
		String file;

		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				//file = "dados_model_rep.txt";
				//file = "DATA_MODELO_MOD.txt";
				file = "treinamento.txt";
				file = "DADOS_MODELO_REP2.txt";
			} else {
				//file = "dados_validacao_rep.txt";
				//file = "DATA_VALIDACAO_MOD.txt";
				file = "teste.txt";
				file = "DADOS_VALIDACAO_REP2.txt";
			}

			File arq = new File(path + file);
			InputStream is = new FileInputStream(arq);
			InputStreamReader isr = new InputStreamReader(is);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(isr);
			String linha = br.readLine();
			linha = br.readLine();	
			while (linha != null) {
				carregarUnidadesDeEntrada(linha, i == 0 ? true : false);
				linha = br.readLine();	
			}
		}
		
		normalizarDados(dadosDeTreinamento);
		normalizarDados(dadosDeTeste);
	}

	private void carregarUnidadesDeEntrada(String linha, boolean treino) {
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

	private List<Registro> normalizarDados(List<Registro> dados){
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
}