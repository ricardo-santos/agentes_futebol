package gui;

import jade.gui.GuiAgent;
import jade.lang.acl.ACLMessage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import agents.Jogador;

public class AgentGui extends JFrame {

	
	private JPanel contentPane;
	private GuiAgent[] agents = new GuiAgent[4];  //[0]-BD, [1]-Treinador, [2]-Director, [3]-Presidente;
	
	private int agentPos = 0;
	private JLabel lblTreinador;
	private JLabel lblDirector;
	private JLabel lblPresidente;
	private JTextArea txtDirector;
	private JScrollPane scrollPane_1;
	private JTextArea txtBD;
	private JScrollPane scrollPane_2;
	private JTextArea txtTreinador;
	private JScrollPane scrollPane_3;
	private JTextArea txtPresidente;
	private JTextArea txtOlheiro;
	private JLabel lblOlheiro;
	private JTextArea txtJogadores;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AgentGui frame = new AgentGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public AgentGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1005, 767);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblMessages = new JLabel("Base de dados");
		lblMessages.setBounds(688, 0, 214, 16);
		contentPane.add(lblMessages);
		
		lblTreinador = new JLabel("Treinador");
		lblTreinador.setBounds(311, 10, 300, 16);
		contentPane.add(lblTreinador);
		
		lblDirector = new JLabel("Director");
		lblDirector.setBounds(311, 242, 232, 16);
		contentPane.add(lblDirector);
		
		lblPresidente = new JLabel("Presidente");
		lblPresidente.setBounds(311, 482, 263, 16);
		contentPane.add(lblPresidente);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(311, 259, 365, 205);
		contentPane.add(scrollPane);
		
		txtDirector = new JTextArea();
		scrollPane.setViewportView(txtDirector);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(688, 27, 296, 210);
		contentPane.add(scrollPane_1);
		
		txtBD = new JTextArea();
		scrollPane_1.setViewportView(txtBD);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(311, 27, 365, 210);
		contentPane.add(scrollPane_2);
		
		txtTreinador = new JTextArea();
		scrollPane_2.setViewportView(txtTreinador);
		
		scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(311, 496, 365, 222);
		contentPane.add(scrollPane_3);
		
		txtPresidente = new JTextArea();
		scrollPane_3.setViewportView(txtPresidente);
		
		JLabel lblJogadores = new JLabel("Jogadores");
		lblJogadores.setBounds(688, 242, 296, 16);
		contentPane.add(lblJogadores);
		
		lblOlheiro = new JLabel("Olheiro");
		lblOlheiro.setBounds(16, 10, 83, 16);
		contentPane.add(lblOlheiro);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(16, 27, 283, 205);
		contentPane.add(scrollPane_4);
		
		txtOlheiro = new JTextArea();
		scrollPane_4.setViewportView(txtOlheiro);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(688, 259, 296, 205);
		contentPane.add(scrollPane_5);
		
		txtJogadores = new JTextArea();
		scrollPane_5.setViewportView(txtJogadores);
		
	}
	
	public void addAgent(GuiAgent ag) {
		agents[agentPos] = ag;
	}
	
	public void mostrarMsgBD(ACLMessage msg) {
		String txt = txtBD.getText();
		txtBD.setText(txt + msg.getSender().getLocalName() + " -> Mensagem : "+msg.getContent() + "\n"); 
	}

	public void mostrarMsgDirector(ACLMessage msg) {
		String txt = txtDirector.getText();
		txtDirector.setText(txt + msg.getSender().getLocalName() + " -> Mensagem : "+msg.getContent() + "\n");
	}

	public void mostrarMsgPresidente(ACLMessage msg) {
		String txt = txtPresidente.getText();
		txtPresidente.setText(txt + msg.getSender().getLocalName() + " -> Mensagem : "+msg.getContent() + "\n");
	}
	
	public void mostrarStrPresidente(String msg) {
		String txt = txtPresidente.getText();
		txtPresidente.setText(txt + msg);
	}

	public void mostrarMsgTreinador(ACLMessage msg) {
		String txt = txtTreinador.getText();
		txtTreinador.setText(txt + msg.getSender().getLocalName() + " -> Mensagem : "+msg.getContent() + "\n");
	}
	
	public void mostrarStrTreinador(String msg) {
		String txt = txtTreinador.getText();
		txtTreinador.setText(txt + msg);
	}

	public void relatorioGerado() {
		String txt = txtDirector.getText();
		txtDirector.setText(txt + "\nRelatorio Gerado" + "\n");
	}

	public void vender() {
		String txt = txtPresidente.getText();
		txtPresidente.setText(txt + "\nVender");
	}

	public void mostraJogadores(TreeMap<String, Jogador> jogadores) {
		StringBuilder sb = new StringBuilder();
		for (Jogador j: jogadores.values())
			sb.append(j.toString() + "\n");
		txtJogadores.setText(sb.toString());
	}

	public void mostrarStrOlheiro(String conteudo) {
		String txt = txtOlheiro.getText();
		txtOlheiro.setText(txt + "\n" + conteudo);		
	}
}
