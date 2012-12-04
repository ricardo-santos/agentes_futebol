package agents;

import gui.AgentGui;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Presidente extends Agent {
	
	protected AgentGui gui;
	private Agent myAgent;
	
	public void setup() {
		myAgent = this;
		gui = (AgentGui) getArguments()[0];
		
		CyclicBehaviour behav = new CyclicBehaviour() {	
			public void action() {
				ACLMessage msg = myAgent.receive();
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
					
					gui.mostrarMsgPresidente(msg);
					String conteudo = msg.getContent();
					if (conteudo.contains("Custo")) {
						String parametros[] = conteudo.split(",");
						pedirInfluenciaTreinador(parametros[1]);
					} else if (conteudo.contains("Influencia")) {
						String parametros[] = conteudo.split(",");
						regularizarSituacaoJogador(parametros);
						//gui.vender();
					} else if (conteudo.contains("Jogador")) {
						String parametros[] = conteudo.split(",");
						
						//verificar se preco e performace est‹o abaixo do limite
						if (matchRequisitos(parametros[3],parametros[4])) { 
							//se sim adicionar ˆ base de dados
							conteudo = "Comprar," + parametros[1];
							enviarMensagem("olheiro", ACLMessage.ACCEPT_PROPOSAL, conteudo);
							
						} else {
							//se n‹o pedir outro jogador ao olheiro
							conteudo = "Jogador," + parametros[1] + ", " + parametros[2];
							enviarMensagem("olheiro", ACLMessage.PROPOSE, conteudo);
						}
							
						
					}
				}
			}
				
			private boolean matchRequisitos(String custo, String preformance) {
				int preco = Integer.parseInt(custo);
				
				if (preco > 6000 || preformance.equals("baixo") || preformance.equals("medio"))
					return false;
					
				return true;
			}

			private void regularizarSituacaoJogador(String[] parametros) {
				String numJogador = parametros[1];
				String influencia = parametros[2];
				String posicao = parametros[3];
				if (influencia.equals("dispensavel")){
					venderJogador(numJogador);
					pedirJogadorOlheiro(posicao);
				} 
			}
			
			private void pedirJogadorOlheiro(String posicao) {
				String conteudo = "Contratar," + posicao;
				enviarMensagem("olheiro", ACLMessage.REQUEST, conteudo);
			}

			private void venderJogador(String numJogador){
				String conteudo = "Vender," + " ,"+numJogador + ", ";
				enviarMensagem("database", ACLMessage.PROPOSE, conteudo);
			}

			private void pedirInfluenciaTreinador(String jogador) {
				String conteudo = "Influencia," + jogador;
				enviarMensagem("treinador", ACLMessage.REQUEST, conteudo);
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
}
