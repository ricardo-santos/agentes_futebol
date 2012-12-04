package agents;

import java.util.HashMap;
import java.util.TreeMap;

/*
 * Fuo custo:
 * 	- Informacao incompleta:
 * 		- custo = estado+performance
 * 	- Informacao completa
 * 		- custo = somar todos
 */

public class Jogador {
	
	private String nome;
	private String posicao;			//{ defesa(1),		medio(2),		avannçado(3)	}
	
	private String estado;			//{ apto(1),		condicionado(0), lesionado(-1)	}
	private String influencia;		//{ dispensavel(-5),util(1),		importante(5)	}
	private String performance;		//{ Baixo(-5),		Medio(1) 		alto(5)			}
	private String preco;			//{ Barato(5)		Normal(1)		caro(5-)  		}
	//Custo calculado pelo director
	private double custo;				//{ 1 - positivo,		-1 - negativo }
	private int salario;			//{ valor inteiro 	}
	
	//private String gravidade;		//{ baixa(-5), 		normal(1),		alta(5) 		}
	
	private boolean relatorioCompleto;
	
	
	public Jogador(String n, String pos, String e, int s, String p,String pr) {
		nome = n;
		posicao = pos;
		estado = e;
		influencia = null;
		performance = p; 
		salario = s;
		preco = pr;
		custo = 0;
		
		relatorioCompleto = false;
	}
	
	public boolean eUm(String p) {
		return posicao.equals(p);
	}
	
	public String toString() {
		return nome + "," + posicao + "," + estado + "," + salario + "," + performance; 
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getInfluencia() {
		return influencia;
	}

	public void setInfluencia(String influencia) {
		this.influencia = influencia;
	}

	public String getPerformance() {
		return performance;
	}

	public void setPerformance(String performance) {
		this.performance = performance;
	}

	public String getPreco() {
		return preco;
	}

	public void setPreco(String preco) {
		this.preco = preco;
	}

	public double getCusto() {
		return custo;
	}

	public void setCusto(double custo) {
		this.custo = custo;
	}

	public int getSalario() {
		return salario;
	}

	public void setSalario(int salario) {
		this.salario = salario;
	}

	public boolean isRelatorioCompleto() {
		return relatorioCompleto;
	}

	public void setRelatorioCompleto(boolean relatorioCompleto) {
		this.relatorioCompleto = relatorioCompleto;
	}

	public String getPosicao() {
		return posicao;
	}
	
	public String getNome() {
		return nome;
	}
}
