package rede;

public class MLP {

	public int nInputs, nHidden, nOutput; // Neurônios por camada
	public int nCamadasEscondidas;

	private double[/* i */] input, output; // Saidas da camada de entrada, e da
											// de saída
	private double[/* j */][/* i */] hidden; // Saida do neuronio i da camada j

	private double[/* j */][/* i */] pesosCamada1; // Pesos das conexões entre o
													// neurônio j da camada
													// escondida
	// e o i da camada de entrada
	private double[/* j */][/* i */] pesosCamada2;

	private double[/* h */][/* j */][/* i */] pesosCamadasEscondidas;

	// private double[/* j */][/* i */] pesosCamada1_Tm1;
	// private double[/* j */][/* i */] pesosCamada2_Tm1;

	// private boolean primeiraVez;
	private double learningRate = 0.1;
	private double momentumRate = 0.01;

	public double erroTreinamentoDaEpoca;
	public double erroValidacaoDaEpoca;

	public MLP(int nInput, int nHidden, int nOutput, int nCamadasEscondidas) {

		this.nInputs = nInput;
		this.nHidden = nHidden;
		this.nOutput = nOutput;
		this.nCamadasEscondidas = nCamadasEscondidas;
		// Bias
		input = new double[nInput + 1];
		hidden = new double[nCamadasEscondidas][nHidden + 1];
		output = new double[nOutput + 1];

		pesosCamada1 = new double[nHidden + 1][nInput + 1];
		pesosCamada2 = new double[nOutput + 1][nHidden + 1];

		pesosCamadasEscondidas = new double[nCamadasEscondidas - 1][nHidden + 1][nHidden + 1];

		// pesosCamada1_Tm1 = new double[nHidden + 1][nInput + 1];
		// pesosCamada2_Tm1 = new double[nOutput + 1][nHidden + 1];

		// Inicializar os Pesos
		gerarPesosAleatorios();

		// primeiraVez = true;
	}

	private void gerarPesosAleatorios() {

		for (int j = 1; j <= nHidden; j++) {
			for (int i = 0; i <= nInputs; i++) {
				pesosCamada1[j][i] = Math.random() - 0.5;
			}
		}

		for (int j = 1; j <= nOutput; j++) {
			for (int i = 0; i <= nHidden; i++) {
				pesosCamada2[j][i] = Math.random() - 0.5;
			}
		}

		for (int h = 0; h < nCamadasEscondidas - 1; h++) {
			for (int j = 1; j <= nHidden; j++) {
				for (int i = 0; i <= nHidden; i++) {
					pesosCamadasEscondidas[h][j][i] = Math.random() - 0.5;
				}
			}
		}
	}

	private double calcularErro(double[] desiredOutput) {
		double erroTotal = 0;
		double[] erroCamadaSaida = new double[nOutput + 1];

		for (int i = 1; i <= nOutput; i++) {
			erroCamadaSaida[i] = desiredOutput[i - 1] - output[i];// * output[i]
																	// * (1.0 -
																	// output[i]);//
																	// Erro da
																	// camada de
																	// saída
			erroTotal += Math.pow(erroCamadaSaida[i], 2);
		}
		erroTotal = erroTotal / 2; // Calculando erro da rede para o padrão
		// 1/2 E e^2
		return erroTotal;
	}

	public double[] treinar(double[] pattern, double[] desiredOutput) {
		double[] output = null;

		output = forward(pattern);
		backward(desiredOutput);

		this.erroTreinamentoDaEpoca += calcularErro(desiredOutput);

		return output;
	}

	public double[] validar(double[] pattern, double[] desiredOutput) {
		double[] output = null;

		output = forward(pattern);

		this.erroValidacaoDaEpoca += calcularErro(desiredOutput);

		return output;
	}

	public double[] forward(double[] pattern) {

		for (int i = 0; i < nInputs; i++) {
			input[i + 1] = pattern[i];
		}

		// Bias
		input[0] = 1.0;
		for (int i = 0; i < nCamadasEscondidas; i++) {
			hidden[i][0] = 1.0;
		}

		// 1 Camada escondida
		for (int j = 1; j <= nHidden; j++) {
			hidden[0][j] = 0.0;
			for (int i = 0; i <= nInputs; i++) {
				hidden[0][j] += pesosCamada1[j][i] * input[i]; // Calcular o net
			}
			hidden[0][j] = 1.0 / (1.0 + Math.exp(-hidden[0][j])); // Calcular
																	// saida
		}

		for (int h = 1; h < nCamadasEscondidas; h++) {
			for (int j = 1; j <= nHidden; j++) {
				hidden[h][j] = 0.0;
				for (int i = 0; i < nHidden; i++) {
					hidden[h][j] += pesosCamadasEscondidas[h - 1][j][i] * hidden[h - 1][i];
				}
				hidden[h][j] = 1.0 / (1.0 + Math.exp(-hidden[h][j]));
			}
		}
		// Camada de saída
		for (int j = 1; j <= nOutput; j++) {
			output[j] = 0.0;
			for (int i = 0; i <= nHidden; i++) {
				output[j] += pesosCamada2[j][i]
						* hidden[nCamadasEscondidas - 1][i]; // Calcular o net
			}
			output[j] = 1.0 / (1 + 0 + Math.exp(-output[j])); // Calcular saida
		}
		
		return output;
	}

	private void backward(double[] desiredOutput) {

		double[/* Neuronio */] erroCamadaSaida = new double[nOutput + 1];
		double[/* Camada */][/* Neuronio */] erroCamadaEscondida = new double[nCamadasEscondidas][nHidden + 1];
		double Esum = 0.0;
		
		// Erro da camada de saída
		for (int i = 1; i <= nOutput; i++) {
			erroCamadaSaida[i] = output[i] * (1.0 - output[i])
					* (desiredOutput[i - 1] - output[i]);
		}
		//Erro da ultima camada escondida 
		for (int i = 1; i <= nHidden; i++) {
			for (int j = 1; j <= nOutput; j++) {
				Esum += pesosCamada2[j][i] * erroCamadaSaida[j];
			}
			erroCamadaEscondida[nCamadasEscondidas - 1][i] = hidden[nCamadasEscondidas - 1][i]
					* (1.0 - hidden[nCamadasEscondidas - 1][i]) * Esum;
			Esum = 0.0;
		}

		//Erro de todas camadas escondidas
		for (int h = nCamadasEscondidas - 2; h >= 0; h--) {
			for (int i = 1; i <= nHidden; i++) {
				for (int j = 1; j <= nHidden; j++) {
					Esum += pesosCamadasEscondidas[h][j][i]
							* erroCamadaEscondida[h + 1][j];
				}
				erroCamadaEscondida[h][i] = hidden[h][i] * (1.0 - hidden[h][i])
						* Esum;
				Esum = 0.0;
			}
		}

		//Ajustando pesos da camada de saida
		for (int j = 1; j <= nOutput; j++) {
			for (int i = 0; i <= nHidden; i++) {
				pesosCamada2[j][i] += learningRate * erroCamadaSaida[j]
						* hidden[nCamadasEscondidas - 1][i];
			}
		}

		//Ajustando pesos das camadas escondidas
		for (int h = nCamadasEscondidas - 2; h >= 0; h--) {
			for (int j = 1; j <= nHidden; j++) {
				for (int i = 0; i <= nHidden; i++) {
					pesosCamadasEscondidas[h][j][i] += learningRate
							* erroCamadaEscondida[h + 1][j] * hidden[h][i];
				}
			}
		}

		//Ajustando pesos da camada de entrada
		for (int j = 1; j <= nHidden; j++) {
			for (int i = 0; i <= nInputs; i++) {
				pesosCamada1[j][i] += learningRate * erroCamadaEscondida[0][j]
						* input[i];
			}
		}
	}

	public void setLearningRate(double lr) {
		learningRate = lr;
	}

	public double getMomentumRate() {
		return momentumRate;
	}

	public void setMomentumRate(double momentumRate) {
		this.momentumRate = momentumRate;
	}

}