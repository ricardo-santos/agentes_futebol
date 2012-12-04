package agents;

import gui.AgentGui;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


/*
 * Tarefas:
 * 	- Garantir que o treinador actualiza a BD e so depois comunicar ao director
 *  
 */
public class Treinador extends GuiAgent {
	protected AgentGui gui;
	protected Agent myAgent;
	
	public void setup() {
		myAgent = this;
		gui = (AgentGui) getArguments()[0];
		
		/*
		 * RELATORIOS
		 */
		TickerBehaviour tickRelatorios = new TickerBehaviour(this,100000){
			protected void onTick(){
				Random pr = new Random();
				int perf;
				String performance = null;
				for (int i = 0;i<Database.NUM_JOGADORES;i++){
					perf = pr.nextInt(3);
					switch(perf){
						case 0: performance = "baixa";
								break;
						case 1: performance = "media";
								break;
						case 2: performance = "alta";
								break;
					}
					actualizarBD("Relatorio",i,"performance",performance);
				}
				enviarConfRelat();
			}
			
			private void enviarConfRelat(){
				AID receiver = new AID();
				receiver.setLocalName("director");
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("Relatorio");
				msg.setConversationId(""+System.currentTimeMillis());
				msg.addReceiver(receiver);
				myAgent.send(msg);
			}
		};
		
		/*
		 * GERAR LESOES
		 */
		TickerBehaviour tickLesaoBehav = new TickerBehaviour(this, 1000) {
			protected void onTick() {
				int jogador = gerarLesao(Database.NUM_JOGADORES);  //-> e preciso saber o numero de jogadores na base de dados
				int nJogador = jogador;
				//comunica suposta lesao ou recuperacao
				if (jogador<0){
					nJogador = Math.abs(jogador);
					Random r = new Random();
					int prob = r.nextInt(100);
					if (prob>=50){
						actualizarBD("Recuperacao",nJogador-1,"estado","apto");
					} else {
						actualizarBD("Recuperacao",nJogador-1,"estado","condicionado");
					}
				} else {
					actualizarBD("Lesao",nJogador-1,"estado","lesionado");
				}
			}
			
		};
		
	
		/*
		 * LER MENAGENS DA BD
		 */
		CyclicBehaviour msgBehav = new CyclicBehaviour() {
			
			public void action() {
			
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					String conteudo = msg.getContent();
					String parametros[] = conteudo.split(",");
					
					if (msg.getPerformative() == ACLMessage.CONFIRM) {
						if (conteudo.contains("Lesao")){
							comunicarLesaoDirector(Integer.valueOf(parametros[1]));
							gui.mostrarStrTreinador("Lesao confirmada no jogador " + parametros[1] + "\n");
							//gui.mostrarMsgTreinador(msg);
						} else {
							gui.mostrarStrTreinador("Jogador " + parametros[1] + " recuperado da lesao");
						}
					} else if (msg.getPerformative() == ACLMessage.REQUEST) {
						/*
						 * REMOVER
						 */
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						

						gui.mostrarMsgTreinador(msg);
						String influencia = gerarInfluencia();
						
						System.out.println(msg.getSender().toString());
						
						int nJogador = Integer.parseInt(parametros[1]);
					
						actualizarBD("Influencia",nJogador,"influencia",influencia);	
					}
				} else {
					block();
				}
			}
		};
		
		ParallelBehaviour pb = new ParallelBehaviour(myAgent,ParallelBehaviour.WHEN_ANY); 
		pb.addSubBehaviour(tickLesaoBehav);
		pb.addSubBehaviour(msgBehav);
		//pb.addSubBehaviour(tickRelatorios);
		this.addBehaviour(pb);
	}
	
	
	private void comunicarLesaoDirector(int jogador) { //passar como parametro tb a performance
		String conteudo = "Lesao,"+jogador;
		enviarMensagem("director", ACLMessage.INFORM, conteudo);
	}
	
	private void actualizarBD(String info,int numJog, String atrib, String valor){
		String conteudo = info + "," + atrib + "," + String.valueOf(numJog) + "," + valor;
		enviarMensagem("database", ACLMessage.PROPOSE, conteudo);
	}
	
	public String getJogador(int jogador){
		String conteudo = "Dados,"+jogador;
		return enviarMensagem("database", ACLMessage.REQUEST, conteudo); 
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
	
	private String gerarInfluencia() {
		String influencia;
		Random r = new Random();
		int prob = r.nextInt(3);
		switch(prob) {
		case 0: 
				influencia = "dispensavel";
				break;
		case 1: 
				influencia = "util";
				break;
		default:
				influencia = "importante";
				break;
		}
		return influencia;
	}
	
	
	public int gerarLesao(int numJogadores) {
		Random r = new Random();
		int prob = r.nextInt(100);
		
		if (prob >= 50) {
			int jogador = r.nextInt(numJogadores)+1;
			return jogador;
		} else {
			int jogador = -(r.nextInt(numJogadores)+1);
			return jogador;
		}
	}
	
}
