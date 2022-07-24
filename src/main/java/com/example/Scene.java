package com.example;

import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.example.gameAlgorithm.AStarSearch;
import com.example.entity.Actor;

import com.example.entity.Agent;
import com.example.entity.Agv;
import com.example.entity.AutoAgv;
import com.example.entity.Constant;
import com.example.entity.Graph;
import com.example.entity.Position;

public class Scene {
    public Map<String, int[]> mapOfExits;
    public int harmfullness;
    public Vector<Position> groundPos;
    public Vector<Position> pathPos;
    public Vector<Position> doorPos;
    public Position[] ground;
    public Vector<Position>[][] danhsachke;
    public Vector<Actor> RemoveActors;
    public HashSet<Agent> agents;
    public HashSet<AutoAgv> autoAgvs;
    public Agv mAgv;
    public int count;
    public Graph graph;
    
    public String timeTable;
    public int maxAgent = 2;
    public int countAgv = 0;
    public Scene()  {
        initGraph();
        count = 0;
        this.RemoveActors = new Vector<Actor>();
        this.mapOfExits = new HashMap<String, int[]>();
        this.mapOfExits.put("Gate1", new int[] { 50, 13, 0 });
        this.mapOfExits.put("Gate2", new int[] { 50, 14, 0 });
        this.graph = new Graph(52, 28, this.danhsachke, this.pathPos);
        this.autoAgvs = new HashSet<AutoAgv>();
        this.agents = new HashSet<Agent>();
        this.timeTable = new String();
        Constant.setTime();
        AStarSearch.init(52, 28, this.ground);

        var r = (int) Math.floor(Math.random() * this.pathPos.size());
        while (!Constant.validDestination((int) this.pathPos.get(r).x, (int) this.pathPos.get(r).y, 1, 14)) {
            r = (int) Math.floor(Math.random() * this.pathPos.size());
        }
        this.mAgv = new Agv(this, 1 * 32, 14 * 32, this.pathPos.get(r).x * 32, this.pathPos.get(r).y * 32, this.graph,++count);
        
        // createRandomAutogAgv();
    }

    public void createRandomAutogAgv() {
        var r = (int) Math.floor(Math.random() * this.pathPos.size());
        while (!Constant.validDestination((int) this.pathPos.get(r).x , (int) this.pathPos.get(r).y, 1, 13)) {
            r = (int) Math.floor(Math.random() * this.pathPos.size());
        }
        if (this.graph != null) {
            ++count;
            var tempAgv = new AutoAgv(this, 1 * 32, 13 * 32, this.pathPos.get(r).x * 32, this.pathPos.get(r).y * 32, this.graph, count);
            
            this.autoAgvs.add(tempAgv);
        }
        this.graph.setAutoAgvs(autoAgvs);
    }

    public void createAgent() {
        ++count;
        var r1 = (int) Math.floor(Math.random() * this.doorPos.size());
        var r2 = (int) Math.floor(Math.random() * this.doorPos.size());
        var agent = new Agent(
                this,
                this.doorPos.get(r1).x * 32,
                this.doorPos.get(r1).y * 32,
                this.doorPos.get(r2).x * 32,
                this.doorPos.get(r2).y * 32,
                this.ground,
                count);
        
        this.agents.add(agent);
        this.graph.setAgents(agents);
    }

    public void setMaxAgent(int num){
       
        this.maxAgent = num;
    }

    public String update(String mes, JSONArray jsonArray) {
        var time = Constant.getTime();

        if (time > count * 2000 ) {
            count++;
            if (this.agents.size() < this.maxAgent) {
                this.createAgent();
                this.countAgv++;
            }
            if (this.countAgv == 2){
                this.createRandomAutogAgv();
                this.countAgv = 0;
            }
        }
        
        if (mes.length()>0) mAgv.preUpdate(mes);
        
        addJsonObject(jsonArray, mAgv);
        for (var actor : agents) {
           
            actor.preUpdate();
            addJsonObject(jsonArray, actor);
        }
       
        graph.updateState();
    
        for (var actor : autoAgvs) {
          
            actor.preUpdate();
            addJsonObject(jsonArray, actor);
        }
        for (var item : RemoveActors) {
            var jsonObject = new JSONObject();
            jsonObject.put("ID",item.id);
            jsonObject.put("active", false);
            jsonArray.add(jsonObject);
            if (item.isAgv)
                autoAgvs.remove(item);
            else {
                agents.remove(item);
                graph.removeAgent((Agent) item);
            }
        }
     
        RemoveActors.clear();
        return jsonArray.toJSONString();
    }
    public void addJsonObject(JSONArray jsonArray, Actor actor) {
        var jsonObject = new JSONObject();
            jsonObject.put("ID", actor.id);
            jsonObject.put("active", true);
            jsonObject.put("isAgv", actor.isAgv);
            jsonObject.put("X", actor.x);
            jsonObject.put("Y", actor.y);
            jsonObject.put("endX", actor.endX);
            jsonObject.put("endY", actor.endY);
            jsonArray.add(jsonObject);
    }
    public void add(Actor actor) {

    }

    public void remove(Actor actor) {
        this.RemoveActors.add(actor);
    }

    public void initGraph() {
        try {
            this.groundPos = new Vector<Position>();
            this.pathPos = new Vector<Position>();
            this.doorPos = new Vector<Position>();
            this.danhsachke = new Vector[52][28];
            for (int i = 0; i < 52; i++)
                for (int j = 0; j < 28; j++)
                    danhsachke[i][j] = new Vector<Position>();
            var ground = new int[52][28];
            var path = new int[52][28];
            var door = new int[52][28];
            var parser = new JSONParser();
            var jsonObject = (JSONObject) parser
                    .parse(new FileReader(System.getProperty("user.dir") + "/src/assets/tilemaps/json/hospital.json"));
            var layers = (JSONArray) jsonObject.get("layers");

            for (var layer : layers) {

                var item = (JSONObject) layer;
                String name = (String) item.get("name");
                if (name.equals("ground")) {
                    int i = 0;
                    int j = 0;
                    var data = (JSONArray) item.get("data");

                    for (int k = 0; k < (int) data.size(); k++) {

                        i += j / 52;
                        j %= 52;
                        ground[j][i] = ((Long) data.get(k)).intValue();
                        j++;
                    }
                }
                if (name.equals("path")) {
                    int i = 0;
                    int j = 0;
                    var data = (JSONArray) item.get("data");

                    for (int k = 0; k < (int) data.size(); k++) {
                        i += j / 52;
                        j %= 52;
                        path[j][i] = ((Long) data.get(k)).intValue();
                        j++;

                    }
                }
                if (name.equals("door")) {
                    int i = 0;
                    int j = 0;
                    var data = (JSONArray) item.get("data");

                    for (int k = 0; k < (int) data.size(); k++) {
                        i += j / 52;
                        j %= 52;
                        door[j][i] = ((Long) data.get(k)).intValue();
                        j++;

                    }
                }
            }

            for (int i = 0; i < 52; i++) {
                for (int j = 0; j < 28; j++) {

                    if (ground[i][j] > 0)
                        this.groundPos.add(new Position(i, j));
                    if (path[i][j] > 0)
                        this.pathPos.add(new Position(i, j));
                    if (door[i][j] > 0)
                        this.doorPos.add(new Position(i, j));
                }
            }
            this.ground = this.groundPos.toArray(new Position[this.groundPos.size()]);
            for (int i = 0; i < this.pathPos.size(); i++) {

                for (int j = 0; j < this.pathPos.size(); j++)

                    if (checkNeibor(i, j, path)) {
                        this.danhsachke[(int) this.pathPos.get(i).x][(int) this.pathPos.get(i).y]
                                .add(new Position(this.pathPos.get(j).x, this.pathPos.get(j).y));
                    }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public boolean checkNeibor(int i, int j, int[][] path) {
        var x1 = (int) this.pathPos.get(i).x;
        var y1 = (int) this.pathPos.get(i).y;
        var x2 = (int) this.pathPos.get(j).x;
        var y2 = (int) this.pathPos.get(j).y;

        if (Math.abs(x1 - x2) + Math.abs(y1 - y2) != 1)
            return false;
        if (path[x1][y1] == 12 && x1 == x2 && y1 - 1 == y2)
            return true;
        if (path[x1][y1] == 20 && x1 == x2 && y1 + 1 == y2)
            return true;
        if (path[x1][y1] == 28 && x1 + 1 == x2 && y1 == y2)
            return true;
        if (path[x1][y1] == 36 && x1 - 1 == x2 && y1 == y2)
            return true;
        if (path[x1][y1] != 12 && path[x1][y1] != 20 && path[x1][y1] != 28 && path[x1][y1] != 36) {
            if (path[x2][y2] != 20 && path[x2][y2] != 28 && path[x2][y2] != 36 && x1 == x2 && y1 - 1 == y2)
                return true;
            if (path[x2][y2] != 12 && path[x2][y2] != 28 && path[x2][y2] != 36 && x1 == x2 && y1 + 1 == y2)
                return true;
            if (path[x2][y2] != 12 && path[x2][y2] != 20 && path[x2][y2] != 36 && x1 + 1 == x2 && y1 == y2)
                return true;
            if (path[x2][y2] != 12 && path[x2][y2] != 20 && path[x2][y2] != 28 && x1 - 1 == x2 && y1 == y2)
                return true;
            // return true;
        }
        return false;
    }

  
    public void handleClickSaveButton() {
        
    }
    public void handleClickLoadButton(String mes) {
        
        try {
            var parser = new JSONParser();
            var jsonArray = (JSONArray) parser.parse(mes);
            this.autoAgvs = new HashSet<AutoAgv>();
            this.agents = new HashSet<Agent>();
            for (int i = 4; i < jsonArray.size(); i++) {
                var item = (JSONObject) jsonArray.get(i);
                if (((Number)item.get("ID")).intValue() == 1) {
                    this.mAgv = new Agv(this, ((Number)item.get("X")).doubleValue(), ((Number)item.get("Y")).doubleValue(), 
                                        ((Number)item.get("endX")).doubleValue(), ((Number)item.get("endY")).doubleValue(), 
                                        graph, ((Number)item.get("ID")).intValue());
                                        
                }
                else {
                    if ((boolean)item.get("isAgv")) {
                        this.autoAgvs.add(new AutoAgv(this, ((Number)item.get("X")).doubleValue(), ((Number)item.get("Y")).doubleValue(), 
                                                ((Number)item.get("endX")).doubleValue(), ((Number)item.get("endY")).doubleValue(), 
                                                graph, ((Number)item.get("ID")).intValue()));
                    
                    }
                    else {
                        this.agents.add(new Agent(this, ((Number)item.get("X")).doubleValue(), ((Number)item.get("Y")).doubleValue(), 
                                            ((Number)item.get("endX")).doubleValue(), ((Number)item.get("endY")).doubleValue(), 
                                            ground, ((Number)item.get("ID")).intValue()));
                        
                    }
                }
            }
            this.graph.setAgents(agents);
            this.graph.setAutoAgvs(autoAgvs);
            this.timeTable = new String();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
   
}
