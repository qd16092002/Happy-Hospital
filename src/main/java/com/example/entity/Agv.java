package com.example.entity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.example.Scene;

public class Agv extends Actor {
    public Graph graph;
    public Node curNode;
    public Node nextNode;
    public boolean keyW, keyA, keyS, keyD;

    public Agv(Scene scene, double x, double y, double endX, double endY, Graph graph, int id) {

        super(scene, x, y, endX, endY, id, true);

        this.graph = graph;

        this.curNode = this.nextNode = graph.nodes[(int) x / 32][(int) y / 32];
        this.curNode.setState(StateOfNode2D.BUSY);

    }

    public Agv clone() throws CloneNotSupportedException {
        return (Agv) super.clone();
    }

    public void preUpdate(String mes) {

        try {
            var parser = new JSONParser();
            var jsonObject = (JSONObject) parser.parse(mes);
            this.keyW = (boolean) jsonObject.get("w");
            this.keyA = (boolean) jsonObject.get("a");
            this.keyS = (boolean) jsonObject.get("s");
            this.keyD = (boolean) jsonObject.get("d");
            if (keyW && keyS)
                keyW = keyS = false;
            if (keyA && keyD)
                keyA = keyD = false;

            if ((this.nextNode.x * 32 == this.x && this.nextNode.y * 32 == this.y) || (this.curNode == this.nextNode)) {
                boolean w = this.keyW && (this.nextNode.nodeN != null);
                boolean a = this.keyA && (this.nextNode.nodeW != null);
                boolean s = this.keyS && (this.nextNode.nodeS != null);
                boolean d = this.keyD && (this.nextNode.nodeE != null);

                if (w) {
                    if (this.curNode.nodeN != this.nextNode || (!a && !d)) {
                        this.curNode = this.nextNode;
                        this.nextNode = this.nextNode.nodeN;
                    }
                } else if (a) {
                    if (this.curNode.nodeW != this.nextNode || (!w && !s)) {
                        this.curNode = this.nextNode;
                        this.nextNode = this.nextNode.nodeW;
                    }
                } else if (s) {
                    if (this.curNode.nodeS != this.nextNode || (!a && !d)) {
                        this.curNode = this.nextNode;
                        this.nextNode = this.nextNode.nodeS;
                    }
                } else if (d) {
                    if (this.curNode.nodeE != this.nextNode || (!w && !s)) {
                        this.curNode = this.nextNode;
                        this.nextNode = this.nextNode.nodeE;

                    }
                }
            }
            boolean w = this.keyW && (this.curNode.nodeN == this.nextNode);
            boolean a = this.keyA && (this.curNode.nodeW == this.nextNode);
            boolean s = this.keyS && (this.curNode.nodeS == this.nextNode);
            boolean d = this.keyD && (this.curNode.nodeE == this.nextNode);
            if (w || a || s || d) {

                this.moveTo(nextNode.x * 32, nextNode.y * 32, 1);
            }
            if (this.x == this.endX && this.y == this.endY) {
                this.endX = 50 * 32;
                this.endY = 14 * 32;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }
}
