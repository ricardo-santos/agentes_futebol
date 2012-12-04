package agents;

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


public class Director extends GuiAgent {
	
	protected AgentGui gui;
	private Agent myAgent;

	public void setup() {
		myAgent = this;
		gui = (AgentGui) getArguments()[0];
		
		CyclicBehaviour receberMsgTreinador = new CyclicBehaviour() {
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					
					/*
					 * remover 
					 */
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/*
					 * Remover
					 */
					
					gui.mostrarMsgDirector(msg);
					String conteudo = msg.getContent();
					if (conteudo.contains("Lesao")) {
						String parametros[] = conteudo.split(",");
						String numJogador = parametros[1];
						calcularCustoJogador(numJogador,false);
						//obterInfoJogador(Integer.parseInt(parametros[1]));
					} else if (conteudo.contains("Custo")) {
						String dados[] = conteudo.split(",");
						String nJogadorCusto = dados[1];
						double custo = Double.valueOf(dados[2]);
						if (custo < 0){
						//if (calcularCusto(dados) < 0) {
						//dados[0]->jogador, dados[1]->custo
							informarPresidente(nJogadorCusto,custo);
						}
					} else if (conteudo.contains("Relatorio")){
						for (int i = 0; i<Database.NUM_JOGADORES;i++){
							calcularCustoJogador(String.valueOf(i),true);
						}
					}
				} else {
					block();
				}
			}
			
			public void calcularCustoJogador(String jogador,boolean relatorio){
				AID receiver = new AID();
				receiver.setLocalName("database");
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setContent("Custo,"+jogador+","+relatorio);
				msg.setConversationId(""+System.currentTimeMillis());
				msg.addReceiver(receiver);
				myAgent.send(msg);
			}

			private void informarPresidente(String jogador, double custo) {
				AID receptor = new AID();
				receptor.setLocalName("presidente");
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM); 
				msg.setContent("Custo,"+jogador+","+custo); 
				msg.setConversationId(""+System.currentTimeMillis()); 
				msg.addReceiver(receptor); 
				myAgent.send(msg);
			}

			private int calcularCusto(String[] dados) {
				//defenir a funcao para calcular o custo do jogador
				return -1;
			}


			private void obterInfoJogador(int jogador) {
				AID receptor = new AID();
				receptor.setLocalName("database");
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST); 
				msg.setContent("Dados,"+jogador); 
				msg.setConversationId(""+System.currentTimeMillis()); 
				msg.addReceiver(receptor); 
				myAgent.send(msg);
			}
		};
		
		TickerBehaviour gerarRelatorio = new TickerBehaviour(this, 4000 ) { //definir o tempo para uma semana
			protected void onTick() {
				gui.relatorioGerado();
			} 
		};
		
		this.addBehaviour(receberMsgTreinador);
	}

	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
