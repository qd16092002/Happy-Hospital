package com.example.gameAlgorithm;

import java.util.Vector;

import com.example.entity.Position;

class Spot {
    public double i,j,f,g,h;
    public Vector<Spot> neighbors = new Vector<Spot>();
    Spot previous;
    Spot(double i, double j){
        this.i = i;
        this.j = j;
        this.f = 0;
        this.g = 0;
        this.h = 0;
    }

    public void addNeighbors(Vector<Spot> ableSpot){
        for (var k = 0; k < ableSpot.toArray().length ; k++) {
            if (this.i + 1 == ableSpot.get(k).i && this.j == ableSpot.get(k).j) {
                this.neighbors.add(ableSpot.get(k));
            } else if (this.i == ableSpot.get(k).i && this.j + 1 == ableSpot.get(k).j) {
                this.neighbors.add(ableSpot.get(k));
            } else if (this.i - 1 == ableSpot.get(k).i && this.j == ableSpot.get(k).j) {
                this.neighbors.add(ableSpot.get(k));
            } else if (this.i == ableSpot.get(k).i && this.j - 1 == ableSpot.get(k).j) {
                this.neighbors.add(ableSpot.get(k));
            }
        }
    }

    public boolean equal(Spot spot) {
        if (this.i == spot.i && this.j == spot.j) return true;
        return false;
    }
}
public class AStarSearch {
    public static double width, height;
    public Spot start;
    public Spot end;
    public static Vector<Spot> ableSpot;
    public static int count = 0 ;
    //public Vector<Vector<Spot>> grid;
    public static Spot[][] grid;
    public Vector<Spot> path = new Vector<Spot>();

    public AStarSearch(double width, double height, Position startPos, Position endPos, Position[] ablePos){
        
        this.start = new Spot(startPos.x , startPos.y );
        this.end = new Spot(endPos.x , endPos.y );
        //this.grid = new Vector<Vector<Spot>>();
     
        
        for (var i = 0 ; i< width; i++) {
            for (var j = 0;j < height; j++) {
                    AStarSearch.grid[i][j].previous = null;
                }
        }
        count++;
        
    }
    public static void init(double width, double height,Position[] ablePos) {
        AStarSearch.width = width;
        AStarSearch.height = height;  
        AStarSearch.grid = new Spot[52][28];
        for (var i = 0 ; i< width; i++) {
            for (var j = 0;j < height; j++) {
                AStarSearch.grid[i][j] = new Spot(i,j);
            }
        }

        AStarSearch.ableSpot = new Vector<Spot>();
        for (var i = 0; i < ablePos.length; i++) {
            AStarSearch.ableSpot.add( AStarSearch.grid[(int) ablePos[i].x ][(int) ablePos[i].y ]);
        }

        for (var i = 0; i < width; i++) {
            for (var j = 0; j < height; j++) {
                AStarSearch.grid[i][j].addNeighbors(AStarSearch.ableSpot);
            }
        }
    }
    
    private double heuristic2(Spot spot1, Spot spot2) {
        return Math.sqrt(  Math.pow(spot1.i - spot2.i, 2) + Math.pow(spot1.j - spot2.j, 2) );
    }

    private boolean isInclude(Spot spot,Vector<Spot> spots) {
        for (var i = 0; i < spots.toArray().length; i++) {
            if (spot.i == spots.get(i).i && spot.j == spots.get(i).j) return true;
        }
        return false;
    }

    public Position[] cal() {
        var openSet = new Vector<Spot>();
        var closeSet = new Vector<Spot>();
        openSet.add(AStarSearch.grid[(int) this.start.i][(int) this.start.j]);

        while (openSet.toArray().length > 0) {
            var winner = 0;
            for (var i = 0; i < openSet.toArray().length ; i++) {
                if (openSet.get(i).f < openSet.get(winner).f) {
                    winner = i;
                }
            }

            var current = openSet.get(winner);
            if (openSet.get(winner).equal(this.end)) {

                var cur = AStarSearch.grid[(int) this.end.i][(int) this.end.j];
                this.path.add(cur);
                while (cur.previous != null) {
                    this.path.add(cur.previous);
                    cur = cur.previous;
                }

                var result = new Position[this.path.size()];
                for (var k = this.path.size() -1; k>=0 ; k--) {
                    result[this.path.size() - 1 - k] = new Position(this.path.get(k).i, this.path.get(k).j) ;
                }
                return result;
            }

            openSet.remove(winner);
            closeSet.add(current);

            Vector<Spot> neighbors = current.neighbors;

            for (var i = 0; i < neighbors.toArray().length ; i++) {
                var neighbor = neighbors.get(i);
                if (!this.isInclude(neighbor, closeSet)) {
                    var tempG = current.g + 1;
                    if (this.isInclude(neighbor, openSet)) {
                        if (tempG < neighbor.g) {
                            neighbor.g = tempG;
                        }
                    } else {
                        neighbor.g = tempG;
                        openSet.add(neighbor);
                    }

                    neighbor.h = this.heuristic2(neighbor, this.end);
                    neighbor.f = neighbor.h + neighbor.g;
                    neighbor.previous = current;
                } else {
                    var tempG = current.g + 1;
                    if (tempG < neighbor.g) {
                        openSet.add(neighbor);
                        var index = closeSet.indexOf(neighbor);
                        if (index > -1) {
                            closeSet.remove(index);
                        }
                    }
                }
            }
        }
        System.out.println("Path not found!");
        //result.add( new Position(-1,-1));
        return null;
    }
}





































