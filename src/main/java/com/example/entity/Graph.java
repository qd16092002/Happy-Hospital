package com.example.entity;

import java.util.HashSet;
import java.util.Vector;

public class Graph {
    public Node[][] nodes;
    public int width;
    public int height;
    public int[][] busy;
    public Vector<Position> pathPos;
    private HashSet<AutoAgv> autoAgvs;
    private HashSet<Agent> agents;

    public Graph(
            int width,
            int height,
            Vector<Position>[][] danhsachke,
            Vector<Position> pathPos
    // scene: MainScene
    ) {
        this.width = width;
        this.height = height;
        this.nodes = new Node[width][height];
        this.pathPos = pathPos;
        this.agents = new HashSet<Agent>();
        this.autoAgvs = new HashSet<AutoAgv>();
        for (var i = 0; i < width; i++) {
            for (var j = 0; j < height; j++) {
                this.nodes[i][j] = new Node(i, j);
            }
        }
        for (var i = 0; i < width; i++) {
            for (var j = 0; j < height; j++) {
                for (var k = 0; k < danhsachke[i][j].size(); k++) {
                    var nutke = danhsachke[i][j].get(k);
                    this.nodes[i][j].setNeighbor(this.nodes[(int) nutke.x][(int) nutke.y]);
                }
            }
        }
        for (var p : pathPos) {
            this.nodes[(int) p.x][(int) p.y].setState(StateOfNode2D.EMPTY);
        }
        // console.log(this.nodes);

        this.busy = new int[52][28];
        for (var i = 0; i < 52; i++) {
            for (var j = 0; j < 28; j++) {
                if (this.nodes[i][j].state == StateOfNode2D.EMPTY) {
                    this.busy[i][j] = 0;
                } else {
                    this.busy[i][j] = 2;
                }
            }
        }
    }

    public void setAutoAgvs(HashSet<AutoAgv> agvs) {
        this.autoAgvs = agvs;
    }

    public HashSet<AutoAgv> getAutoAgvs() {
        return this.autoAgvs;
    }

    public void setMAgv(Agv agv) {
    }

    public void setAgents(HashSet<Agent> agents) {
        for (var p : this.pathPos) {
            this.nodes[(int) p.x][(int) p.y].setState(StateOfNode2D.EMPTY);
        }
        this.busy = new int[52][28];
        for (var i = 0; i < 52; i++) {
            for (var j = 0; j < 28; j++) {
                if (this.nodes[i][j].state == StateOfNode2D.EMPTY) {
                    this.busy[i][j] = 0;
                } else {
                    this.busy[i][j] = 2;
                }
            }
        }
        this.agents = agents;
    }

    public void updateState() {
        var cur = new int[52][28];
        for (var i = 0; i < 52; i++) {
            for (var j = 0; j < 28; j++) {
                cur[i][j] = 0;
            }
        }

        for (var agent : agents) {
            if (agent.active == true) {
                var xl = (int) Math.floor(agent.x / 32);
                var xr = (int) Math.floor((agent.x + 31) / 32);
                var yt = (int) Math.floor(agent.y / 32);
                var yb = (int) Math.floor((agent.y + 31) / 32);
                cur[xl][yt] = 1;
                cur[xl][yb] = 1;
                cur[xr][yt] = 1;
                cur[xr][yb] = 1;
            }
        }

        for (var i = 0; i < 52; i++) {
            for (var j = 0; j < 28; j++) {
                if (this.busy[i][j] == 2) {
                    continue;
                } else if (this.busy[i][j] == 0) {
                    if ((cur[i][j] == 0))
                        continue;
                    this.nodes[i][j].setState(StateOfNode2D.BUSY);
                    this.busy[i][j] = 1;
                } else {
                    if (cur[i][j] == 1)
                        continue;
                    this.nodes[i][j].setState(StateOfNode2D.EMPTY);
                    this.busy[i][j] = 0;
                }
            }
        }
    }

    public void removeAgent(Agent agent) {
        var i = (int) agent.x / 32;
        var j = (int) agent.y / 32;
        this.nodes[i][j].setState(StateOfNode2D.EMPTY);
        this.busy[i][j] = 0;
    }

    public Node[] calPathAStar(Node start, Node end) {
        /**
         * Khoi tao cac bien trong A*
         */
        var openSet = new Vector<Node>();
        var closeSet = new Vector<Node>();
        var path = new Vector<Node>();
        var astar_f = new int[this.width][this.height];
        var astar_g = new int[this.width][this.height];
        var astar_h = new int[this.width][this.height];
        var previous = new Node[this.width][this.height];
        for (var i = 0; i < this.width; i++) {
            for (var j = 0; j < this.height; j++) {
                astar_f[i][j] = 0;
                astar_g[i][j] = 0;
                astar_h[i][j] = 0;
            }
        }
        /**
         * Thuat toan
         */
        openSet.add(this.nodes[(int) start.x][(int) start.y]);
        while (openSet.size() > 0) {
            var winner = 0;
            for (var i = 0; i < openSet.size(); i++) {
                if (astar_f[openSet.get(i).x][openSet.get(i).y] < astar_f[openSet.get(winner).x][openSet
                        .get(winner).y]) {
                    winner = i;
                }
            }
            var current = openSet.get(winner);
            if (openSet.get(winner).equal(end)) {
                var cur = this.nodes[(int) end.x][(int) end.y];
                path.add(cur);
                while (previous[(int) cur.x][(int) cur.y] != null) {
                    path.add(previous[(int) cur.x][(int) cur.y]);
                    cur = previous[(int) cur.x][(int) cur.y];
                }
                var result = new Node[path.size()];
                for (var k = path.size() - 1; k >= 0; k--) {
                    result[path.size() - 1 - k] = new Node(path.get(k).x, path.get(k).y);
                }
                return result;
                // console.assert(lengthOfPath == path.length, "path has length: " + path.length
                // + " instead of " + lengthOfPath);
            }
            openSet.remove(winner);
            closeSet.add(current);
            Node[] neighbors = { current.nodeN, current.nodeE, current.nodeS, current.nodeW,
                    current.nodeVN, current.nodeVE, current.nodeVS, current.nodeVW };

            for (var i = 0; i < neighbors.length; i++) {
                var neighbor = neighbors[i];
                if (neighbor != null) {
                    var timexoay = 0;
                    if (previous[(int) current.x][(int) current.y] != null &&
                            neighbor.x != previous[(int) current.x][(int) current.y].x &&
                            neighbor.y != previous[(int) current.x][(int) current.y].y) {
                        timexoay = 1;
                    }
                    var tempG = astar_g[current.x][current.y] + 1 + current.getW() + timexoay;

                    if (!this.isInclude(neighbor, closeSet)) {
                        if (this.isInclude(neighbor, openSet)) {
                            if (tempG < astar_g[neighbor.x][neighbor.y]) {
                                astar_g[neighbor.x][neighbor.y] = tempG;
                            }
                        } else {
                            astar_g[neighbor.x][neighbor.y] = tempG;
                            openSet.add(neighbor);
                        }
                        astar_h[neighbor.x][neighbor.y] = this.heuristic(neighbor, end);
                        astar_f[neighbor.x][neighbor.y] = astar_h[neighbor.x][neighbor.y]
                                + astar_g[neighbor.x][neighbor.y];
                        previous[neighbor.x][neighbor.y] = current;
                    } else {
                        if (tempG < astar_g[neighbor.x][neighbor.y]) {
                            openSet.add(neighbor);
                            var index = closeSet.indexOf(neighbor);
                            if (index > -1) {
                                closeSet.remove(index);
                            }
                        }
                    }
                }
            }
        } // end of while (openSet.length > 0)
        System.out.println("Path not found!" + start.x + " " + start.y + " " + end.x + " " + end.y);
        return null;
    }

    public boolean isInclude(Node node, Vector<Node> nodes) {
        for (var i = 0; i < nodes.size(); i++) {
            if (node.equal(nodes.get(i)))
                return true;
        }
        return false;
    }

    public int heuristic(Node node1, Node node2) {
        return Math.abs(node1.x - node2.x) + Math.abs(node1.y - node2.y);
    }
}
