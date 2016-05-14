package main;

import java.awt.Point;

import tiles.Tile;

public class HexGrid {
	private double stepY;
	private double stepX;
	private Point center = new Point(11, 12);
	
	public HexGrid(){
		this(50);
	}
	public HexGrid(int side){
		this.stepY=1.5*side;
		this.stepX=side*Math.sqrt(3);
	}
	public Point nearest(Point p){
		Point nearest= new Point();
		int cellY=(int)(p.y/stepY);
		nearest.y=cellY;//(int) (stepY*cellY);
		int cellX=(int)(p.x/stepX+(cellY%2==0?0.5:0.));
		nearest.x=cellX;//(int)(stepX*(cellX+(cellY%2==0?0.5:0)));
		return nearest;
	}
	public Point grid(int cellX, int cellY){
		Point p=new Point();
		p.y=(int) ((cellY+.5)*stepY);
		p.x=(int) ((cellX+(cellY%2==0?0:0.5))*stepX);
		return p;
	}
	
	public Point[] neightbours(Point cell){
		Point[] n=new Point[6];
		int x=(int) cell.getX();
		int y=(int) cell.getY();
		if(y%2==0){
			n[0]=new Point(x,y-1);
			n[1]=new Point(x+1,y);
			n[2]=new Point(x,y+1);
			n[3]=new Point(x-1,y+1);
			n[4]=new Point(x-1,y);
			n[5]=new Point(x-1,y-1);
			}
		else{
			n[0]=new Point(x+1,y-1);
			n[1]=new Point(x+1,y);
			n[2]=new Point(x+1,y+1);
			n[3]=new Point(x,y+1);
			n[4]=new Point(x-1,y);
			n[5]=new Point(x,y-1);
		}
		return n;
	}
	
	public Point neightbour(Tile t, int side){
		Point p = neightbours(t.getCell())[side];
		return grid(p.x,p.y);
	}
	public Point neightbour(Point cell, int side){
		Point p = neightbours(cell)[side];
		return grid(p.x,p.y);
	}
	public Point neightbourCell(Point cell, int side){
		Point p = neightbours(cell)[side];
		return p;
	}
	public Point getCenter(){
		return center;
	}
	
	public Point getCenterNeightboursCoord(int side){
		Point c= neightbours(center)[side];
		return grid(c.x, c.y);
	}
}
