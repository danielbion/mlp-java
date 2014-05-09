package dados;

import java.util.ArrayList;
import java.util.List;

public class Registro {
	

	private List<Double> tupla;
	private double saida;
	
	public Registro(){
		this.tupla = new ArrayList<Double>();
	}

	public List<Double> getTupla() {
		return tupla;
	}

	public void setTupla(List<Double> tupla) {
		this.tupla = tupla;
	}
	
	public void setSaida(double saida){
		this.saida = saida;
	}

	public double getSaida() {
		return saida;
	}
	
	

}
