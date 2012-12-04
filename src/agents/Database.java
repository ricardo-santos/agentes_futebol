package agents;


import gui.AgentGui;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Random;



public class Database extends Agent {
	
	public static int NUM_JOGADORES = 5;
	private TreeMap<String,Jogador> jogadores;
	private TreeMap<String,Integer> custos = new TreeMap<String,Integer>();
	private TreeMap<String,Jogador> vendidos = new TreeMap<String,Jogador>();
	
	protected AgentGui gui;
	private Agent myAgent;

	/* 
	 * Performance : 0 - baixa
	 * 				 1 - media
	 * 				 2 - alta
	 * Estado: 		 0 - apto
	 * 				 1 - condicionado
	 * 
	 * Salario: entre 500 a 10000 (?)
	 * 			  	 
	 * */
	public void registarJogadores() {
		String[] nomes = new String[]{"Bino","Albino","Jaquelino","Antonino","Miquelino"};
		String[] posicoes = new String[]{"Defesa", "Médio", "Avançado"};
		
		jogadores = new TreeMap<String,Jogador>();
		custos.put("apto", 1);			//0.5  -> estado
		custos.put("condicionado", 0);
		custos.put("lesionado", -1);
		custos.put("dispensavel", -5);	//0.8  -> influencia
		custos.put("util", 1);			
		custos.put("importante", 5);
		custos.put("baixa", -5);		//0.9  -> performance
		custos.put("media", 1);
		custos.put("alta", 5);
		custos.put("barato", 5);		//0.6  -> preco
		custos.put("normal", 1);
		custos.put("caro", -5);
		
		Random pr = new Random();
		Random er = new Random();
		Random sr = new Random();
		Random precor = new Random();

		int perf,esta,salario,pre,pos;
		String performance, estado,preco;
		performance = estado = preco = null;
		for (int i = 0; i<NUM_JOGADORES; i++){
			pos = pr.nextInt(3);
			perf = pr.nextInt(3);
			esta = er.nextInt(2);
			pre = precor.nextInt(3);
			salario = (sr.nextInt(9501)) + 500;
			
			switch(esta){
				case 0: estado = "apto";
						break;
				case 1: estado = "condicionado";
						break;
			}
			switch(perf){
				case 0: performance = "baixa";
						break;
				case 1: performance = "media";
						break;
				case 2: performance = "alta";
						break;
			}
			switch(pre){
				case 0: preco = "barato";
						break;
				case 1: preco = "normal";
						break;
				case 2: preco = "caro";
						break;
			}
			jogadores.put(String.valueOf(i), new Jogador(nomes[i],posicoes[pos],estado,salario,performance,preco));
			
		}
		System.out.println(jogadores);

	}

	public void setup() {
		myAgent = this;
		gui = (AgentGui) getArguments()[0];
		registarJogadores();
		
		CyclicBehaviour receberMsg = new CyclicBehaviour() {
			public void action() {

				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					gui.mostrarMsgBD(msg);
					
					String remetente = msg.getSender().getLocalName();
					String conteudo = msg.getContent();
					String parametros[] = conteudo.split(",");
					String numJogador = parametros[1];
					
					if (jogadores.get(numJogador) != null){
						String estado = jogadores.get(numJogador).getEstado();
						boolean relatorio = Boolean.valueOf(parametros[2]);
						if (conteudo.contains("Custo")){
							if(relatorio && estado.equals("lesionado") ){
								;
							} else {
								double custo = calCusto(numJogador);
								jogadores.get(numJogador).setCusto(custo);
								enviarCusto(remetente,numJogador,custo);
							}
						} else {
							if (conteudo.equals("Dados")){
								enviarDadosJogador(remetente, parametros[1]);
							}
						}
					}
				} else {
					block();
				}
			}
			
			public double calCusto(String jogador){
				String perfor = jogadores.get(jogador).getPerformance();
				int preco = custos.get(jogadores.get(jogador).getPreco());
				int estado = custos.get(jogadores.get(jogador).getEstado());
				double custo;
				int perf = custos.get(jogadores.get(jogador).getPerformance());
				custo = (perf*0.9)+(preco*0.6)+(estado*0.8);
				return custo;
			}

			private void enviarDadosJogador(String remetente, String jogador) {
				String conteudo = "Dados," + /*informacao completa,*/jogadores.get(jogador).toString();
				enviarMensagem(remetente, ACLMessage.INFORM, conteudo);
			}

			public void enviarCusto(String dest, String jogador, double custo){
				String conteudo = "Custo," + jogador + ","+custo;
				enviarMensagem(dest, ACLMessage.INFORM, conteudo);
			}
			
		};
		
		CyclicBehaviour actualizarDados = new CyclicBehaviour() {
			public void action(){
				
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				ACLMessage msg = myAgent.receive(mt);
				
				if (msg != null) {
					gui.mostrarMsgBD(msg);
					
					String conteudo = msg.getContent();
					String parametros[] = conteudo.split(",");
					String info = parametros[0];
					String numJogador = parametros[2];
					String atributo = parametros[1];
					String valor = parametros[3];
					
					
					if (jogadores.get(numJogador)!=null){
						String posicao = jogadores.get(numJogador).getPosicao();
						String estadoJog = jogadores.get(numJogador).getEstado();
						if (info.equals("Lesao")){
							if (!estadoJog.equals("lesionado")){
						  		alterarAtributo(atributo,numJogador,valor);
						  		enviarConfirmacao(msg.getSender().getLocalName(),"Lesao",numJogador);
							}
						}
						else if (info.equals("Recuperacao")){
							if (estadoJog.equals("lesionado")){
								alterarAtributo(atributo,numJogador,valor);
								enviarConfirmacao(msg.getSender().getLocalName(),"Recuperado",numJogador);
							}
						}
						else if (info.equals("Influencia")){
							alterarAtributo(atributo,numJogador,valor);
							confirmarInfluencia(numJogador,valor,posicao);
						}
						else if (info.equals("Relatorio")){
							if (estadoJog.equals("lesionado")){
								alterarAtributo(atributo,numJogador,valor);
							}
						}
						else if (info.equals("Vender")){
							Jogador j = jogadores.get(numJogador);
							jogadores.remove(numJogador);
							vendidos.put(numJogador,j);
							gui.mostrarStrPresidente("Jogador: " + numJogador + "-> VENDIDO\n");
						}
					}
				} else {
					block();
				}
			}
			
			private void confirmarInfluencia(String numJogador, String valor, String posicao) {
				AID receptor = new AID();
				receptor.setLocalName("presidente");
				ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM); 
				msg.setContent("Influencia" + ","+numJogador + ","+valor + ","+posicao); 
				msg.setConversationId(""+System.currentTimeMillis()); 
				msg.addReceiver(receptor); 

				myAgent.send(msg);
			}

			public void alterarAtributo(String atributo, String numJogador,String valor){
				synchronized (jogadores.get(numJogador)){
					if(atributo.equals("estado"))
						jogadores.get(numJogador).setEstado(valor);
					else if (atributo.equals("performance"))
						jogadores.get(numJogador).setPerformance(valor);
					else if (atributo.equals("salario"))
						jogadores.get(numJogador).setSalario(Integer.valueOf(valor));
					else if (atributo.equals("infocomp"))
						jogadores.get(numJogador).setRelatorioCompleto(Boolean.valueOf(valor));
					else if (atributo.equals("influencia"))
						jogadores.get(numJogador).setInfluencia(valor);
				}
				gui.mostraJogadores(jogadores);
			}
			
			public void enviarConfirmacao(String dest,String conteudo,String nJogador){
				AID receptor = new AID();
				receptor.setLocalName(dest);
				ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM); 
				msg.setContent(conteudo + ","+nJogador); 
				msg.setConversationId(""+System.currentTimeMillis()); 
				msg.addReceiver(receptor); 

				myAgent.send(msg);
			}
		};
		
		ParallelBehaviour pb = new ParallelBehaviour(myAgent,ParallelBehaviour.WHEN_ANY); 
		pb.addSubBehaviour(receberMsg);
		pb.addSubBehaviour(actualizarDados);
		
		
		CyclicBehaviour behav = new CyclicBehaviour() {
			public void action(){
				
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
				ACLMessage msg = myAgent.receive(mt);
				
				if (msg != null) {
					gui.mostrarMsgBD(msg);
					
					String conteudo = msg.getContent();
					String parametros[] = conteudo.split(",");
					
					int salario = Integer.parseInt(parametros[4]);
					
					Jogador j = new Jogador(parametros[1],parametros[2],parametros[3],salario,parametros[5],parametros[6]);
					jogadores.put(Integer.toString(NUM_JOGADORES),j);
					
				} else {
					block();
				}
			}
		};
		
		this.addBehaviour(pb);
		this.addBehaviour(behav);
	}
	
	
	public String enviarMensagem(String destino, int tipoMensagem, String conteudo) {
		AID receiver = new AID();
		receiver.setLocalName(destino);
		ACLMessage msg = new ACLMessage(tipoMensagem);
		msg.setContent(conteudo);
		msg.setConversationId(""+System.currentTimeMillis());
		msg.addReceiver(receiver);
		myAgent.send(msg);
		
		return msg.getConversationId();
	}
	
}
