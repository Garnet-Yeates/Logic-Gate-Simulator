package wit.edu.yeatesg.simulator.objects.other;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class Frame extends JFrame
{
	private static final long serialVersionUID = 7190077168079570705L;

	public static void main(String[] args)
	{
		EventQueue.invokeLater(() ->
		{
			new Frame();
		});
	}
	
	public Frame()
	{
		EditorPanel editorPanel = new EditorPanel(this);
		addMouseListener(editorPanel);
		addKeyListener(editorPanel);
		addMouseMotionListener(editorPanel);
		this.getContentPane().add(editorPanel);
		this.setPreferredSize(new Dimension(editorPanel.getWidth(), editorPanel.getHeight()));
		this.pack();
		setVisible(true);
	}
	
	@Override
	public void setPreferredSize(Dimension preferredSize)
	{
		super.setPreferredSize(new Dimension(preferredSize.width + 16, preferredSize.height + 39));
	}
}
