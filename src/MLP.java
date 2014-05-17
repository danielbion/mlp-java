import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class MLP {

	int nInputs, nHidden, nOutput; // Neurônios por camada
	private double[/* i */] input, hidden, output; // Saidas da camada de entrada, da escondida e da de saída

	private double[/* j */][/* i */] pesosCamada1; // Pesos das conexões entre o neurônio j da camada escondida
											   // e o i da camada de entrada
												
	private double[/* j */][/* i */] pesosCamada2; // Pesos das conexões entre o neurônio j da camada de saída
											   // e o i da camada escondida					
	private double learningRate = 0.5;	
	
	double erroTreinamentoDaEpoca;
	double erroValidacaoDaEpoca;

	DefaultCategoryDataset ds;

	public MLP(int nInput, int nHidden, int nOutput) {

		this.nInputs = nInput;
		this.nHidden = nHidden;
		this.nOutput = nOutput;

		// Bias
		input = new double[nInput + 1]; 
		hidden = new double[nHidden + 1];
		output = new double[nOutput + 1];

		pesosCamada1 = new double[nHidden + 1][nInput + 1];
		pesosCamada2 = new double[nOutput + 1][nHidden + 1];

		// Inicializar os Pesos
		gerarPesosAleatorios();

		// Gráfico
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(800, 600);

		ds = new DefaultCategoryDataset();

		JFreeChart chart = ChartFactory.createLineChart("Erros",
				"Nº Épocas", "Erro %", ds, PlotOrientation.VERTICAL, true, true,
				true);

		CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.YELLOW);

		ChartPanel panel = new ChartPanel(chart);
		jf.add(panel, BorderLayout.CENTER);

		jf.setVisible(true);
	}

	public void setLearningRate(double lr) {
		learningRate = lr;
	}

	private void gerarPesosAleatorios() {

		for (int j = 1; j <= nHidden; j++)
			for (int i = 0; i <= nInputs; i++) {
				pesosCamada1[j][i] = Math.random() - 0.5;
			}

		for (int j = 1; j <= nOutput; j++)
			for (int i = 0; i <= nHidden; i++) {
				pesosCamada2[j][i] = Math.random() - 0.5;
			}
	}


	public double[] treinar(double[] pattern, double[] desiredOutput) {
		double[] output = null;

		output = forward(pattern);
		backward(desiredOutput);

		double erroTotal = 0;
		double[] erroCamadaSaida = new double[nOutput + 1];
		
		for (int i = 1; i <= nOutput; i++) { 
			erroCamadaSaida[i] = (desiredOutput[0] - output[i]);// * output[i] * (1.0 - output[i]);// Erro da camada de saída
			erroTotal += erroCamadaSaida[i] * erroCamadaSaida[i];
		}
		
		erroTotal = erroTotal / 2;  // Calculando erro da rede para o padrão
		this.erroTreinamentoDaEpoca += erroTotal;
		
		return output;
	}
	
	public double[] validar(double[] pattern, double[] desiredOutput) {
		double[] output = null;

		output = forward(pattern);
				
		double erroTotal = 0;
		double[] erroCamadaSaida = new double[nOutput + 1];
		
		for (int i = 1; i <= nOutput; i++) { 
			erroCamadaSaida[i] = (desiredOutput[0] - output[i]);// * output[i] * (1.0 - output[i]);// Erro da camada de saída
			erroTotal += erroCamadaSaida[i] * erroCamadaSaida[i];
		}
		
		erroTotal = erroTotal / 2;  // Calculando erro da rede para o padrão
		this.erroValidacaoDaEpoca += erroTotal;
		
		return output;
	}


	public double[] forward(double[] pattern) {

		for (int i = 0; i < nInputs; i++) {
			input[i + 1] = pattern[i];
		}

		// Bias
		input[0] = 1.0;
		hidden[0] = 1.0;

		// Camada escondida
		for (int j = 1; j <= nHidden; j++) {
			hidden[j] = 0.0;
			for (int i = 0; i <= nInputs; i++) {
				hidden[j] += pesosCamada1[j][i] * input[i]; // Calcular o net
			}
			hidden[j] = 1.0 / (1.0 + Math.exp(-hidden[j])); // Calcular saida
		}

		// Camada de saída
		for (int j = 1; j <= nOutput; j++) {
			output[j] = 0.0;
			for (int i = 0; i <= nHidden; i++) {
				output[j] += pesosCamada2[j][i] * hidden[i]; // Calcular o net
			}
			output[j] = 1.0 / (1 + 0 + Math.exp(-output[j])); // Calcular saida
		}

		return output;
	}
	
	private void backward(double[] desiredOutput) {

		double[] erroCamadaSaida = new double[nOutput + 1];
		double[] erroCamadaEscondida = new double[nHidden + 1];
		double Esum = 0.0;

		for (int i = 1; i <= nOutput; i++) { 
			erroCamadaSaida[i] = output[i] * (1.0 - output[i]) * (desiredOutput[0] - output[i]);// Erro da camada de saída
		}
		
		for (int i = 0; i <= nHidden; i++) { // Erro da camada escondida usando gradiente
			for (int j = 1; j <= nOutput; j++)
				Esum += pesosCamada2[j][i] * erroCamadaSaida[j]; // Calculando estimativa do erro

			erroCamadaEscondida[i] = hidden[i] * (1.0 - hidden[i]) * Esum;
			Esum = 0.0;
		}

		for (int j = 1; j <= nOutput; j++)
			for (int i = 0; i <= nHidden; i++)
				pesosCamada2[j][i] += learningRate * erroCamadaSaida[j] * hidden[i];

		for (int j = 1; j <= nHidden; j++)
			for (int i = 0; i <= nInputs; i++)
				pesosCamada1[j][i] += learningRate * erroCamadaEscondida[j] * input[i];
	}

}