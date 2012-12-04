package agents;

import java.util.ArrayList;
import java.util.TreeMap;

import gui.AgentGui;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/*
 * Tarefas:
 * 	- Definir a funcao custo 
 */


public class Olheiro extends GuiAgent {
	
	protected AgentGui gui;
	private Agent myAgent;
	
	private int numJogadores;
	private String[][] jogadores = {{"Mickey","defesa","apto","2000","medio","barato"},
									{"Pateta","medio","apto","3000","medio","normal"},
									{"Neymar","avançado","apto","100000","medio","alto"}};
	
	private ArrayList<String> rejeitados;


	public void setup() {
		myAgent = this;
		gui = (AgentGui) getArguments()[0];
		
		CyclicBehaviour behav = new CyclicBehaviour() {
			public void action() {
				
				/*
				 * Fornecer jogadores
				 */
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					
					String conteudo = msg.getContent();
					String parametros[] = conteudo.split(",");
					
					gui.mostrarStrOlheiro(conteudo);
					
					if(msg.getPerformative() == ACLMessage.REQUEST) {
						String posicao = parametros[1];
						
						//Procurar jogador para a posicao
						proporJogador(posicao);
						
					} else if (msg.getPerformative() == ACLMessage.PROPOSE) {
						String posicao = parametros[2];
						rejeitados.add(parametros[1]);
						proporJogador(posicao);
						
					} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
						int i=0;
						
						for(i=0; i<numJogadores; i++) {
							if(jogadores[i][0].equals(parametros[1])) break;
						}
						
						String resposta = "Jogador,"+ jogadores[i][0] + "," 
													+ jogadores[i][1] + "," 
													+ jogadores[i][2] + ","
													+ jogadores[i][3] + ","
													+ jogadores[i][4];
													
						enviarMensagem("database",ACLMessage.PROPAGATE,resposta);
					}
					
				} else {
					block();
				}
				
				/*
				 * Fornecer jogadores
				 */
			}

			private void proporJogador(String posicao) {
				boolean found = false;
				
				for(int i = 0; i < numJogadores; i++) {
					if (!rejeitados.contains(jogadores[i][0]) && jogadores[i][1].equals(posicao)) {  //Se não está nos rejeitados e é da mesma posicao
						
						String resposta = "Jogador,"+ jogadores[i][0] + "," + jogadores[i][1]+ "," + jogadores[i][3] + "," +jogadores[i][4];
						enviarMensagem("presidente",ACLMessage.PROPOSE,resposta); //Enviar dados do jogador
						found = true;
					}	
				}
				if (!found) enviarMensagem("presidente",ACLMessage.REFUSE,"Jogador"); //Se não encontrou nehum jodador para a posicao
			}
		};
		
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

	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
