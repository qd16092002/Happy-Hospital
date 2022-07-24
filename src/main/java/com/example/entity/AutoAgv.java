package com.example.entity;

import com.example.entity.stateOfAutoAgv.HybridState;
import com.example.entity.stateOfAutoAgv.RunningState;
import com.example.Scene;

public class AutoAgv extends Actor {
    public Graph graph;
    public Node[] path;
    public Node curNode;
    public Node endNode;
    public int cur;
    public double waitT;
    public int sobuocdichuyen;
    public double thoigiandichuyen;
    public HybridState hybridState;

    public double startX;
    public double startY;

    public AutoAgv(
            Scene scene,
            double x,
            double y,
            double endX,
            double endY,
            Graph graph,
            int id) {
        super(scene, x, y, endX, endY, id, true);
        this.startX = x;
        this.startY = y;

        this.graph = graph;

        this.cur = 0;
        this.waitT = 0;
        this.curNode = this.graph.nodes[(int) x / 32][(int) y / 32];
        this.curNode.setState(StateOfNode2D.BUSY);
        this.endNode = this.graph.nodes[(int) endX / 32][(int) endY / 32];

        this.path = this.calPathAStar(this.curNode, this.endNode);
        this.sobuocdichuyen = 0;
        this.thoigiandichuyen = Constant.getTime();
        this.hybridState = new RunningState(false);
    }

    public AutoAgv clone() throws CloneNotSupportedException {
        return (AutoAgv) super.clone();
    }

    public void preUpdate() {
        // this.move();
        // console.log(this.x, this.y);
        this.hybridState.move(this);
    }

    public Node[] calPathAStar(Node start, Node end) {
        return this.graph.calPathAStar(start, end);
    }

    public void changeTarget() {

        var agvsToGate1 = scene.mapOfExits.get("Gate1");
        var agvsToGate2 = scene.mapOfExits.get("Gate2");
        var choosenGate = agvsToGate1[2] < agvsToGate2[2] ? "Gate1" : "Gate2";
        var newArray = scene.mapOfExits.get(choosenGate);
        newArray[2]++;
        scene.mapOfExits.replace(choosenGate, newArray);

        this.startX = this.endX;
        this.startY = this.endY;

        var xEnd = newArray[0];
        var yEnd = newArray[1];

        this.endX = xEnd * 32;
        this.endY = yEnd * 32;

        this.endNode = this.graph.nodes[xEnd][yEnd];

        this.path = this.calPathAStar(this.curNode, this.endNode);
        this.cur = 0;
        this.sobuocdichuyen = 0;
        this.thoigiandichuyen = Constant.getTime();
        this.estimateArrivalTime(
                this.startX,
                this.startY,
                this.endX,
                this.endY);
    }
}
