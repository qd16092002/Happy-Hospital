package com.example.entity.stateOfAutoAgv;

import com.example.entity.AutoAgv;
import com.example.entity.Constant;

public class IdleState extends HybridState {
    private double _start;
    private boolean _calculated;

    IdleState(double start) {
        super();
        this._start = start;
        this._calculated = false;
    }

    public void move(AutoAgv agv) {
        if (Constant.getTime() - this._start < Constant.DURATION() * 1000) {
            if (!this._calculated) {
                this._calculated = true;
                var finish = this._start / 1000;
                var mainScene = agv.scene;
                var expectedTime = agv.getExpectedTime();
                if (finish >= expectedTime - Constant.DURATION()
                        && finish <= expectedTime + Constant.DURATION()) {
                    return;
                } else {
                    var diff = Math.max(expectedTime - Constant.DURATION() - finish,
                            finish - expectedTime - Constant.DURATION());
                    var lateness = Constant.Lateness((int) diff);
                    mainScene.harmfullness = mainScene.harmfullness + lateness;
                }
            }
            return;
        } else {
            agv.hybridState = new RunningState(true);
            agv.eraseDeadline(agv.scene.timeTable);
            // console.log((agv.hybridState as RunningState)._isLastMoving);
            agv.changeTarget();

        }
    }
}
