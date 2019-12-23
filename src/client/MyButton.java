package client;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class MyButton extends JButton{
	Color normalColor=new Color(52,152,219);
//	Color hoverColor=new Color(200,200,255);
//	Color selectedColor=new Color(255,200,200);
	
	Color hoverColor=normalColor.brighter();
	Color selectedColor=normalColor.darker();
	
	boolean isSelect=false;
	
	void setColor(Color c) {
		normalColor=c;
		hoverColor=normalColor.brighter();
		selectedColor=normalColor.darker();
		setBackground(normalColor);
	}
	
	void setNormalColor(Color c) {
		normalColor=c;
		setBackground(normalColor);
	}
	void setHoverColor(Color c) {
		hoverColor=c;
	}
	void setSelectedColor(Color c) {
		selectedColor=c;
	}
	
	void setSelect(boolean b) {
		isSelect=b;
		updateColor();
	}
	
	void updateColor() {
		if(isSelect) {
			setBackground(selectedColor);
		}
		else {
			setBackground(normalColor);
		}
	}
	//构造函数
	public MyButton(String s) {
		super(s);
		init();
	}
	public MyButton() {
		init();
	}
	public MyButton(String s,ImageIcon ic) {
		super(s,ic);
		
		init();
		setImageIcon(ic,40);
	}
	void setImageIcon(ImageIcon ic,int size) {
		Image img = ic.getImage();  
		Image newimg = img.getScaledInstance(size,size,Image.SCALE_DEFAULT);  
		ic = new ImageIcon(newimg);
		setIcon(ic);
	}
	//按钮初始化
	void init() {
		setFocusPainted(false);
		setBorder(null);
		setFont(new Font("微软雅黑", 0, 15));
		setBackground(normalColor);
		
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				setBackground(hoverColor);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				setBackground(selectedColor);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				updateColor();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(hoverColor);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}
}
