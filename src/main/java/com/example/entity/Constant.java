package com.example.entity;

import java.lang.Math;
import java.util.HashSet;

enum ModeOfPathPlanning {
    FRANSEN,
    PROPOSE
}

public class Constant {
    public static long startTime;

    public static void setTime() {
        startTime = java.lang.System.currentTimeMillis();
    }

    public static long getTime() {
        return java.lang.System.currentTimeMillis() - startTime;
    }

    public static int DURATION() {
        return 4;
    } // thời gian AutoAgv đợi để nhận/dỡ hàng khi đến đích

    public static int Lateness(int x) {
        return 5 * x;
    } // hàm tính chi phí thiệt hại nếu đến quá sớm hoặc quá trễ

    public static int SAFE_DISTANCE() {
        return 46;
    }

    public static int DELTA_T() {
        return 10;
    }

    public static ModeOfPathPlanning MODE() {
        return ModeOfPathPlanning.FRANSEN;
    }

    public static String secondsToHMS(long seconds) {
        var h = (int) Math.floor((double) (seconds % (3600 * 24)) / 3600);
        var m = (int) Math.floor((double) (seconds % 3600) / 60);
        var s = (int) Math.floor((double) (seconds % 60));

        var hDisplay = h >= 10 ? h : ("0" + h);
        var mDisplay = m >= 10 ? m : ("0" + m);
        var sDisplay = s >= 10 ? s : ("0" + s);
        return hDisplay + ":" + mDisplay + ":" + sDisplay;
    }

    public static boolean validDestination(int destX, int destY, int x, int y) {
        if ((destY == 14 || destY == 13) && ((destX >= 0 && destX <= 5) || (destX >= 45 && destX <= 50)))
            return false;
        var d = Math.sqrt(Math.pow((destX - x), 2) + Math.pow((destY - y), 2));
        if (d * 32 < 10)
            return false;
        return true;
    }

    public static double minDistance(Actor actor, HashSet<Actor> otherActors) {
        double dist = Double.MAX_VALUE;
        for (Actor element : otherActors) {
            double smaller = Math.sqrt((Math.pow(element.x - actor.x, 2) + Math.pow(element.y - actor.y, 2)));
            if (dist > smaller)
                dist = smaller;
        }
        return dist;
    }

    public static int numberOfEdges(int width, int height, Node[][] nodes) {
        int count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count += (nodes[i][j].nodeE != null) ? 1 : 0;
                count += (nodes[i][j].nodeS != null) ? 1 : 0;
                count += (nodes[i][j].nodeW != null) ? 1 : 0;
                count += (nodes[i][j].nodeN != null) ? 1 : 0;
                count += (nodes[i][j].nodeVE != null) ? 1 : 0;
                count += (nodes[i][j].nodeVS != null) ? 1 : 0;
                count += (nodes[i][j].nodeVW != null) ? 1 : 0;
                count += (nodes[i][j].nodeVN != null) ? 1 : 0;
            }
        }
        return count;
    }
}
