package com.example.entity;

import java.util.Vector;

import com.example.gameAlgorithm.AStarSearch;
import com.example.Scene;

public class Agent extends Actor {
    private Position startPos;
    private Position endPos;
    private Position[] groundPos;
    private Position[] path;
    private Vector<Position> vertexs;
    private AStarSearch astar;
    private int next = 1;
    public boolean isOverlap = false;
    public double speed = 0.5;
    public boolean active = true;

    public Agent(
            Scene scene,
            double x,
            double y,
            double endX,
            double endY,
            Position[] groundPos,
            int id) {
        super(scene, x, y, endX, endY, id, false);

        this.startPos = new Position(x / 32, y / 32);
        this.endPos = new Position(endX / 32, endY / 32);
        this.groundPos = groundPos;
        this.astar = new AStarSearch(52, 28, startPos, endPos, groundPos);
        this.path = this.astar.cal();
        this.vertexs = new Vector<Position>();
        this.initVertexs();
        // PHYSICS

    }

    public void goToDestinationByVertexs() {
        if (this.next == this.vertexs.size()) {

            this.x = this.vertexs.get(this.vertexs.size() - 1).x * 32;
            this.y = this.vertexs.get(this.vertexs.size() - 1).y * 32;
            this.setVelocity(0, 0);
            this.eliminate();
            return;
        }
        if (this.vertexs.get(this.next).x * 32 != this.x ||
                this.vertexs.get(this.next).y * 32 != this.y) {
            this.moveTo(
                    this.vertexs.get(this.next).x * 32,
                    this.vertexs.get(this.next).y * 32,
                    this.speed);
        } else {
            this.next++;
        }
    }

    public Agent clone() throws CloneNotSupportedException {
        return (Agent) super.clone();
    }

    public void addRandomVertexs(Position start, Position end) {
        var dis = Math.sqrt(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2));
        var num = Math.ceil((dis * 32) / 50);
        for (int i = 1; i < num; i++) {
            while (true) {
                var rV = new Position(
                        ((end.x - start.x) / num) * i + start.x + (Math.random() - 0.5),
                        ((end.y - start.y) / num) * i + start.y + (Math.random() - 0.5));
                Position _1, _2, _3, _4;
                boolean b_1, b_2, b_3, b_4;
                _1 = new Position(rV.x, rV.y);
                _2 = new Position(rV.x + 1, rV.y);
                _3 = new Position(rV.x + 1, rV.y + 1);
                _4 = new Position(rV.x, rV.y + 1);
                b_1 = b_2 = b_3 = b_4 = false;
                for (int j = 0; j < this.groundPos.length; j++) {
                    var p = this.groundPos[j];
                    if (_1.x < p.x + 1 && _1.y < p.y + 1 && _1.x >= p.x && _1.y >= p.y) {
                        b_1 = true;
                    }
                    if (_2.x < p.x + 1 && _2.y < p.y + 1 && _2.x >= p.x && _2.y >= p.y) {
                        b_2 = true;
                    }
                    if (_3.x < p.x + 1 && _3.y < p.y + 1 && _3.x >= p.x && _3.y >= p.y) {
                        b_3 = true;
                    }
                    if (_4.x < p.x + 1 && _4.y < p.y + 1 && _4.x >= p.x && _4.y >= p.y) {
                        b_4 = true;
                    }
                }
                if (b_1 && b_2 && b_3 && b_4) {
                    this.vertexs.add(rV);
                    break;
                }
            }
        }
    }

    public void initVertexs() {
        if (this.path != null) {
            this.vertexs.add(this.path[0]);
            for (int cur = 2; cur < this.path.length; cur++) {
                if ((this.path[cur].x == this.path[cur - 1].x &&
                        this.path[cur].x == this.path[cur - 2].x) ||
                        (this.path[cur].y == this.path[cur - 1].y &&
                                this.path[cur].y == this.path[cur - 2].y)) {
                    continue;
                }

                var curV = this.vertexs.get(this.vertexs.size() - 1);
                var nextV = this.path[cur - 1];
                this.addRandomVertexs(curV, nextV);
                this.vertexs.add(nextV);
            }
            this.addRandomVertexs(
                    this.vertexs.get(this.vertexs.size() - 1),
                    this.path[this.path.length - 1]);
            this.vertexs.add(this.path[this.path.length - 1]);
        }
    }

    public void preUpdate() {
        this.goToDestinationByVertexs();
    }

    public Position getStartPos() {
        return this.startPos;
    }

    public Position getEndPos() {
        return this.endPos;
    }

    public int getId() {
        return this.id;
    }

    public void eliminate() {
        this.scene.remove(this);
    }

    public void pause() {
        this.setVelocity(0, 0);
        this.setActive(false);
    }

    public void restart() {
        this.setActive(true);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
