package com.example.entity;

public class Node {
    final double lambda = 0.4;

    public int x; // 0 <= x <= 52
    public int y; // 0 <= y <= 28
    public Node nodeW = null;
    public Node nodeN = null;
    public Node nodeS = null;
    public Node nodeE = null;
    public int w_edge_W = Integer.MAX_VALUE; // trong so canh
    public int w_edge_N = Integer.MAX_VALUE; // trong so canh
    public int w_edge_S = Integer.MAX_VALUE;// trong so canh
    public int w_edge_E = Integer.MAX_VALUE; // trong so canh
    private int w = 0; // thời gian dự đoán dừng (ms)
    private int u = 0; // thời gian dừng thực tế (ms)
    public StateOfNode2D state; // trạng thái nút
    public double p_random; // xác xuất nút chuyển sang trạng thái Busy
    public int t_min; // thời gian tối thiểu nút ở trạng thái busy (ms)
    public int t_max; // thời gian tối đa nút ở trạng thái busy (ms)

    public Node nodeVW = null;
    public Node nodeVN = null;
    public Node nodeVS = null;
    public Node nodeVE = null;
    public int w_edge_VW = Integer.MAX_VALUE; // trong so canh
    public int w_edge_VN = Integer.MAX_VALUE; // trong so canh
    public int w_edge_VS = Integer.MAX_VALUE;// trong so canh
    public int w_edge_VE = Integer.MAX_VALUE; // trong so canh

    public boolean isVirtualNode = false;
    private int _weight = 0;

    Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.isVirtualNode = false;
        this.state = StateOfNode2D.NOT_ALLOW;
        this.p_random = 0.05;
        this.t_min = 2000;
        this.t_max = 3000;
    }

    public int getW() {
        if (Constant.MODE() == ModeOfPathPlanning.FRANSEN)
            return this.w;
        else
            return this.getweight();
    }

    public int getweight() {
        return this._weight;
    }

    public void setweight(int value) {
        this._weight = value;
    }

    public void setNeighbor(Node node) {
        if (node == null)
            return;
        if (node.isVirtualNode) {
            if (this.x + 1 == node.x && this.y == node.y) {
                this.nodeVE = node;
                this.w_edge_VE = 1;
            } else if (this.x == node.x && this.y + 1 == node.y) {
                this.nodeVS = node;
                this.w_edge_VS = 1;
            } else if (this.x - 1 == node.x && this.y == node.y) {
                this.nodeVW = node;
                this.w_edge_VW = 1;
            } else if (this.x == node.x && this.y - 1 == node.y) {
                this.nodeVN = node;
                this.w_edge_VN = 1;
            }
            return;
        }
        this.setRealNeighbor(node);
        return;
    }

    private void setRealNeighbor(Node node) {
        if (this.x + 1 == node.x && this.y == node.y) {
            this.nodeE = node;
            this.w_edge_E = 1;
        } else if (this.x == node.x && this.y + 1 == node.y) {
            this.nodeS = node;
            this.w_edge_S = 1;
        } else if (this.x - 1 == node.x && this.y == node.y) {
            this.nodeW = node;
            this.w_edge_W = 1;
        } else if (this.x == node.x && this.y - 1 == node.y) {
            this.nodeN = node;
            this.w_edge_N = 1;
        }
    }

    public void setState(StateOfNode2D state) {
        this.state = state;
    }

    public boolean equal(Node node) {
        if (node.isVirtualNode != this.isVirtualNode)
            return false;
        return this.x == node.x && this.y == node.y;
    }

    public boolean madeOf(Node node) {
        return this.equal(node);
    }

    public void setU(double u) {
        this.u = (int) Math.floor(u);
        this.updateW();
    }

    public void updateW() {
        this.w = (int) ((1 - lambda) * this.w + lambda * this.u);
    }

}
