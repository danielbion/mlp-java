import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import dados.ResourceManager;

import rede.MLP;

public class TesteRede {

	static DefaultCategoryDataset ds;
	static int intervalo_impressao_resultados = 1;
	
	public static void main(String[] args) throws IOException {
		// Gráfico
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(800, 600);

		ds = new DefaultCategoryDataset();

		JFreeChart chart = ChartFactory.createLineChart("Erros", "Nº Épocas",
				"Erro %", ds, PlotOrientation.VERTICAL, true, true, true);

		CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.YELLOW);

		ChartPanel panel = new ChartPanel(chart);
		jf.add(panel, BorderLayout.CENTER);
		jf.setVisible(true);

		
		ResourceManager rm = new ResourceManager();

		rm.lerArquivos();
	
		int numeroNosEntrada= rm.dadosDeTreinamento.get(0).getTupla().size() - 1;
		int numeroNosEscondida = 8;
		int numeroNosSaida = 2;
		int numeroCamadasEscondidas = 8;

		MLP mlp = new MLP(numeroNosEntrada, numeroNosEscondida, numeroNosSaida, numeroCamadasEscondidas);
		mlp.setLearningRate(0.1);
		mlp.setMomentumRate(0);

		double[] entradas = new double[numeroNosEntrada];
		double[] saidas = new double[numeroNosSaida];
		double[] result = new double[numeroNosSaida];

		for (int epoca = 0; epoca < 5000; epoca++) { // Numero de Epocas

			for (int i = 0; i < rm.dadosDeTreinamento.size(); i++) { // Para cada dado
				for (int j = 0; j < numeroNosEntrada; j++) { // Para cada tupla
					entradas[j] = rm.dadosDeTreinamento.get(i).getTupla().get(j); // Pegar os atributos
				}
				if (rm.dadosDeTreinamento.get(i).getTupla().get(numeroNosEntrada) == 1) { // Se o alvo é 1
					saidas[0] = 1;
					saidas[1] = 0;
				} else {
					saidas[0] = 0;
					saidas[1] = 1;
				}

				result = mlp.treinar(entradas, saidas);
			}

			if (epoca%intervalo_impressao_resultados==0) ds.addValue(
					mlp.erroTreinamentoDaEpoca * 100/ rm.dadosDeTreinamento.size(), "Erro de Treinamento",
					"" + epoca); 
			mlp.erroTreinamentoDaEpoca = 0;

			entradas = new double[numeroNosEntrada];
			saidas = new double[numeroNosSaida];
			result = new double[numeroNosSaida];

			for (int i = 0; i < rm.dadosDeTeste.size(); i++) { 
				for (int j = 0; j < numeroNosEntrada; j++) { 
					entradas[j] = rm.dadosDeTeste.get(i).getTupla().get(j); 																			
				}
				if (rm.dadosDeTeste.get(i).getTupla().get(numeroNosEntrada) == 1) { 
					saidas[0] = 1;
					saidas[1] = 0;
				} else {
					saidas[0] = 0;
					saidas[1] = 1;
				}
				result = mlp.validar(entradas, saidas);

//				for (int j = 1; j <= mlp.nOutput; j++) {
//					System.out.println("Neurônio" + j + ": " + result[j]);
//				}

			}
			if (epoca%intervalo_impressao_resultados==0){
				ds.addValue(mlp.erroValidacaoDaEpoca * 100 / rm.dadosDeTeste.size(),
					"Erro Validação", "" + epoca);
				System.out.println(mlp.erroValidacaoDaEpoca * 100 / rm.dadosDeTeste.size() + "Epoca " + epoca);
			}
			mlp.erroValidacaoDaEpoca = 0;
		}

	}
}
