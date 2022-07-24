package com.example.entity.stateOfAutoAgv;

import com.example.entity.AutoAgv;
import com.example.entity.Constant;
import com.example.entity.StateOfNode2D;

public class RunningState extends HybridState {
    public boolean _isLastMoving;
    private boolean _agvIsDestroyed;

    public RunningState(boolean _isLastMoving) {

        this._isLastMoving = _isLastMoving;
        this._agvIsDestroyed = false;
    }

    public void move(AutoAgv agv) {
        if (this._agvIsDestroyed) // || this._isEliminated)
            return;
        // nếu không có đường đi đến đích thì không làm gì
        if (agv.path == null) {
            return;
        }
        // nếu đã đến đích thì không làm gì
        if (agv.cur == agv.path.length - 1) {
            agv.setVelocity(0, 0);
            if (this._isLastMoving) {
                // agv.scene.forcasting.rememberDoneAutoAgv(agv.getAgvID());
                agv.curNode.setState(StateOfNode2D.EMPTY);
                this._agvIsDestroyed = true;
                agv.scene.remove(agv);
                return;
            } else {
                agv.hybridState = new IdleState(Constant.getTime());
            }
            return;
        }
        // nodeNext: nút tiếp theo cần đến
        if (agv.cur + 1 >= agv.path.length) {
            System.out.println("Loi roi do: " + (agv.cur + 1));
        }
        var nodeNext = agv.graph.nodes[agv.path[agv.cur + 1].x][agv.path[agv.cur + 1].y];
        // Khoảng cách của autoAgv với các actors khác đã va chạm
        var shortestDistance = Constant.minDistance(agv, agv.collidedActors);

        /**
         * nếu nút tiếp theo đang ở trạng thái bận
         * thì Agv chuyển sang trạng thái chờ
         */
        if (nodeNext.state == StateOfNode2D.BUSY || shortestDistance < Constant.SAFE_DISTANCE()) {
            agv.setVelocity(0, 0);
            if (agv.waitT > 0)
                return;
            agv.waitT = Constant.getTime();
            /*
             * agv.scene.forcasting.
             * addDuration(agv.getAgvID(), new WaitingDuration(Math.floor(agv.waitT/1000)));
             */
        } else {
            /*
             * Nếu tất cả các actor đều cách autoAgv một khoảng cách an toàn
             */
            if (shortestDistance >= Constant.SAFE_DISTANCE()) {
                // Thì gỡ hết các actors trong danh sách đã gây ra va chạm
                agv.collidedActors.clear();
            }
            /**
             * nếu Agv từ trạng thái chờ -> di chuyển
             * thì cập nhật u cho node hiện tại
             */
            if (agv.waitT > 0) {
                agv.curNode.setU((Constant.getTime() - agv.waitT) / 1000);
                /*
                 * agv.scene.forcasting.
                 * updateDuration(agv.getAgvID(), Math.floor(agv.waitT/1000),
                 * Math.floor(currentTimeMillis()/1000));
                 */
                agv.waitT = 0;
            }
            // di chuyển đến nút tiếp theo
            if (agv.x != nodeNext.x * 32 || agv.y != nodeNext.y * 32) {
                agv.moveTo(nodeNext.x * 32, nodeNext.y * 32, 1);
            } else {
                /**
                 * Khi đã đến nút tiếp theo thì cập nhật trạng thái
                 * cho nút trước đó, nút hiện tại và Agv
                 */
                agv.curNode.setState(StateOfNode2D.EMPTY);
                agv.curNode = nodeNext;
                agv.curNode.setState(StateOfNode2D.BUSY);
                agv.cur++;
                agv.setVelocity(0, 0);
                agv.sobuocdichuyen++;
                // cap nhat lai duong di Agv moi 10 buoc di chuyen;
                // hoac sau 10s di chuyen
                if (agv.sobuocdichuyen % 10 == 0 || Constant.getTime() - agv.thoigiandichuyen > 10000) {
                    agv.thoigiandichuyen = Constant.getTime();
                    agv.cur = 0;
                    agv.path = agv.calPathAStar(agv.curNode, agv.endNode);
                }
            }
        }

    }
}
