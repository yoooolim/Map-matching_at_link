//import com.sun.deploy.util.SyncAccess;
import javafx.util.Pair;
/*import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import javax.swing.SwingWorker;*/

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Emission emission = new Emission();
    private static Transition transition = new Transition();
    //MySwingWorker mySwingWorker;
    /*그림 그려 주는 tool
    private static SwingWrapper<XYChart> sw;
    private static XYChart chart;*/

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("===== [YSY] Map-matching PilotTest 1-2 =====");
        int testNo = 2; // 여기만 바꿔주면 됨 (1-세정, 2-유네, 3-유림)
        FileIO fileIO = new FileIO(testNo);
        // 파일에서 읽어와 도로네트워크 생성
        RoadNetwork roadNetwork = fileIO.generateRoadNetwork();

        // Link와 Node를 바탕으로 Adjacent List 구축
        ArrayList<AdjacentNode> heads = new ArrayList<>();
        for (int i = 0; i < roadNetwork.nodeArrayList.size(); i++) {
            AdjacentNode headNode = new AdjacentNode(roadNetwork.nodeArrayList.get(i));
            heads.add(headNode);

            List<Pair<Link, Integer>> adjacentLink = roadNetwork.getLink1(headNode.getNode().getNodeID());
            if (adjacentLink.size() == 0) continue;
            AdjacentNode ptr = headNode;
            for (int j = 0; j < adjacentLink.size(); j++) {
                AdjacentNode addNode = new AdjacentNode(roadNetwork.getNode(adjacentLink.get(j).getValue()), adjacentLink.get(j).getKey());
                ptr.setNextNode(addNode);
                ptr = ptr.getNextNode();
            }
        }

        /* 여기부터 dijkstra~ */
        /*
        int node_num = roadNetwork.nodeArrayList.size();
        double INF = 1000000.0;
        double[][] a = new double[node_num][node_num]; //전체 거리 그래프

        for(int i=0;i<node_num;i++){
            for(int j=0;j<node_num;j++){
                a[i][j]=INF;
            }
        }

        for(int i=0;i<node_num;i++){
            AdjacentNode head = heads.get(i);
            AdjacentNode ptr = head;
            while(ptr!=null){
                double weight = coordDistanceofPoints(head.getNode().getCoordinate(),ptr.getNode().getCoordinate());
                a[i][ptr.getNode().getNodeID()]=weight;
                ptr=ptr.getNextNode();
            }
        }

        boolean[] v = new boolean[node_num]; //방문한 노드
        double[] d = new double[node_num]; //거리

        //시작 노드는 여기에서 변경
        int start = 0;
        AdjacentNode start_adj_node = heads.get(start);
        for(int i=0;i<node_num;i++){
            d[i]=a[start][i];
        }
        v[start]=true;
        for(int i=0;i<node_num-2;i++){
            int current = 0;
            double min=INF;
            for(int j=0;j<node_num;j++){
                if(d[j]<min&&!v[j]){
                    min=d[j];
                    current =j;
                }
            }
            v[current]=true;
            for(int j=0;j<node_num;j++){
                if(!v[j]){
                    if(d[current]+a[current][j]<d[j]) d[j]=d[current]+a[current][j];
                }
            }
        }

        for(int i=0;i<node_num;i++){
            System.out.println("end ("+i+") : "+d[i]);
        }*/

        // Adjacency List 구조 바탕으로 출력 test
        /*
        for (AdjacentNode adjacentNode : heads) {
            System.out.print( " [ " + adjacentNode.getNode().getNodeID() + " ] ");
            while (adjacentNode.getNextNode() != null) {
                System.out.print(adjacentNode);
                adjacentNode = adjacentNode.getNextNode();
            }
            System.out.println();
        }*/

        // GPS points와 routePoints를 저장할 ArrayList생성
        ArrayList<GPSPoint> gpsPointArrayList = new ArrayList<>();
        ArrayList<Point> routePointArrayList; // 실제 경로의 points!
        ArrayList<Candidate> matchingCandiArrayList = new ArrayList<>();
        ArrayList<Candidate> matched = new ArrayList<>();

        // test 번호에 맞는 routePoints생성
        routePointArrayList = roadNetwork.routePoints(testNo);

        /*
        for(int i=0; i<gpsPointArrayList.size(); i++){
            emission.Emission_Median(gpsPointArrayList.get(i), routePointArrayList.get(i));
            if(i>0){
                transition.Transition_Median(gpsPointArrayList.get(i-1), gpsPointArrayList.get(i),routePointArrayList.get(i-1), routePointArrayList.get(i));
            }//매칭된 point로 해야하나.. 실제 point로 해야하나.. 의문?
            //중앙값 저장
        }
        */

        // window size만큼의 t-window, ... , t-1, t에서의 candidates의 arrayList
        ArrayList<ArrayList<Candidate>> arrOfCandidates = new ArrayList<>();

        // GPSPoints 생성
        int timestamp = 0;
        //System.out.println("여기부터 생성된 gps point~~");
        for (Point point : routePointArrayList) {
            GPSPoint gpsPoint = new GPSPoint(timestamp, point);
            gpsPointArrayList.add(gpsPoint);
            timestamp++;
            //System.out.println(gpsPoint); //gps point 제대로 생성 되는지 확인차 넣음
            ArrayList<Candidate> candidates = new ArrayList<>();
            candidates.addAll(findRadiusCandidate(gpsPointArrayList, matchingCandiArrayList, gpsPoint.getPoint(), 20, roadNetwork, timestamp));
            arrOfCandidates.add(candidates);

            /////////matching print/////////////
            //System.out.println("매칭완료 " + matchingPointArrayList.get(timestamp-1));

            //System.out.println();

            emission.Emission_Median(matchingCandiArrayList.get(timestamp-1));
            if(timestamp > 1){
                transition.Transition_Median(matchingCandiArrayList.get(timestamp-1));
            }
            //median값 저장
        }
        ///////////// FSW VITERBI /////////////
        // window size 입력받기
        System.out.print("Fixed Sliding Window Viterbi. Window size: \n");
        //Scanner scanner = new Scanner(System.in);
        int wSize = 3; // window size;

        ArrayList<Candidate[]> subpaths = new ArrayList<>();
        // arrOfCandidates를 순회하며 찾은 path의 마지막을 matching_success에 추가하는 loop
        // t는 timestamp를 의미
        for (int t = wSize-1; t < arrOfCandidates.size(); t+=wSize-1) {
            Candidate matching;
            double maximum_prob = 0;
            Candidate[] subpath = new Candidate[wSize-1]; // path의 길이를 t로 설정

            // 현재 candidates와 다음 candidates로 가는 t.p와 e.p곱 중 최대 값을 가지는 curr와 그 index를 maximum_tpep[현재]에 저장
            for (int i = t - wSize + 1; i < t; i++) { // i moves in window
                ArrayList<Candidate> curr_candidates = arrOfCandidates.get(i);
                ArrayList<Candidate> next_candidates = arrOfCandidates.get(i+1);
                System.out.println("☆origin point:" + routePointArrayList.get(i));
                System.out.println("☆GPS point: " + gpsPointArrayList.get(i));
                // 다음 candidate를 하나씩 순회
                for (Candidate nc : next_candidates) {
                    maximum_prob = 0;

                    System.out.println("  nc: "+nc.getPoint()+"/ ep: "+nc.getEp());
                    // 현재 candidate를 하나씩 순회하며
                    for (Candidate cc : curr_candidates) {
                        double tp = transition.Transition_pro(gpsPointArrayList.get(t-2).getPoint(), gpsPointArrayList.get(t).getPoint(), cc, nc, roadNetwork);
                        double prob = tp * nc.getEp(); /*cc->nc로의 tp구해야하고 */

                        System.out.println("    cc: "+cc.getPoint()+"/ ep: "+cc.getEp()+ "/ tp: " + tp + "/ prob: "+nc.getEp()*tp);

                        if (i == t - wSize + 1) { // window내 window의 시작 부분
                            if(maximum_prob < prob * cc.getEp()) { // 최대의 acc_prob를 갱신하며 이전전
                                maximum_prob = prob * cc.getEp();// window의 시작부분이므로 현재의 ep * 다음의 ep * 현재->다음의tp를 Acc_prob에 축적한다
                                nc.setPrev_index(curr_candidates.indexOf(cc));
                                nc.setAcc_prob(maximum_prob);
                                //System.out.println("    MAX!");
                            }
                        }
                        else { // window 내 그 외의 부분
                            if(maximum_prob < prob * cc.getAcc_prob()) {
                                maximum_prob = prob * cc.getAcc_prob(); // 현재의 acc_prob * 다음의 ep * 현재->다음의 tp를 Acc_prob에 축적한다
                                nc.setPrev_index(curr_candidates.indexOf(cc));
                                nc.setAcc_prob(maximum_prob);
                                //System.out.println("    MAX!");
                            }
                        }
                    }
                }
                if (t > arrOfCandidates.size() - wSize + 1) {
                    //wSize = arrOfCandidates.size() - t + 1;
                    break;
                }
            }

            // 마지막 candidates 중 acc_prob가 가장 높은 것 max_last_candi에 저장
            Candidate max_last_candi = new Candidate();
            double max_prob = 0;
            for(Candidate candidate : arrOfCandidates.get(t)) {
                if (max_prob < candidate.getAcc_prob()) {
                    max_prob = candidate.getAcc_prob();
                    max_last_candi = candidate;
                }
            }
            // max_last_candi를 시작으로 back tracing하여 subpath구하기
            Candidate tempCandi = arrOfCandidates.get(t-1).get(max_last_candi.getPrev_index());
            subpath[wSize-2] = tempCandi;
            int _t = t-2;
            for (int j = wSize-3; j>=0; j--) {
                tempCandi = arrOfCandidates.get(_t--).get(tempCandi.getPrev_index());
                subpath[j] = tempCandi;
            }

            // 생성된 subpath를 subpaths에 추가가
            subpaths.add(subpath);
            ArrayList<Candidate> subpathArrayList = new ArrayList<Candidate>(Arrays.asList(subpath));
            // subpath를 모두 매칭!!
            matched.addAll(subpathArrayList);
            if (t > arrOfCandidates.size() - wSize + 1) {
                break;
            }
        }

        // subpath 출력
        int t = wSize-2;
        for (Candidate[] subpath : subpaths) {
            System.out.print(t + "] ");
            for (int  i=0;i<subpath.length;i++) {
                System.out.print("["+subpath[i].getInvolvedLink().getLinkID() + "]");
                if (i!=subpath.length-1)
                    System.out.print(" ㅡ ");
            }
            System.out.println(); t++;
        }

        // origin->생성 gps->matched 출력*
        double success_sum= 0;
        System.out.println("[Origin]\t->\t[GPS]\t->\t[Matched]");
        System.out.println("HERE!!:" + matched.size());
        for(int i = 0; i< matched.size() ; i++){
            System.out.println(i +" [" + routePointArrayList.get(i) + "] -> ["
                    + gpsPointArrayList.get(i).getPoint() + "] -> ["
                    + matched.get(i).getPoint() + ", id: "
                    + matched.get(i).getInvolvedLink().getLinkID()+ "]");

            if (i >=0 && i <= 19 && matched.get(i).getInvolvedLink().getLinkID() == 0){
                success_sum ++;
            } else if (i >= 20 && i <=40 && matched.get(i).getInvolvedLink().getLinkID() == 3) {
                success_sum ++;
            } else if (i >= 41 && i <= 61 && matched.get(i).getInvolvedLink().getLinkID() == 13) {
                success_sum ++;
            } else if (i >= 62 && i <= 82 && matched.get(i).getInvolvedLink().getLinkID() == 25) {
                success_sum ++;
            } else if (i >= 83 && i <= 103 && matched.get(i).getInvolvedLink().getLinkID() == 46) {
                success_sum ++;
            } else if (i >= 104 && i <= 124 && matched.get(i).getInvolvedLink().getLinkID() == 48) {
                success_sum ++;
            } else if (i >= 125 && i <= 145 && matched.get(i).getInvolvedLink().getLinkID() == 52) {
                success_sum ++;
            } else if (i >= 146 && i <= 165 && matched.get(i).getInvolvedLink().getLinkID() == 58) {
                success_sum ++;
            }

        }
        System.out.println("Success prob = "+(100*(success_sum/(double)matchingCandiArrayList.size()))/* + "%"*/);
        System.out.println(" Total: "+ matchingCandiArrayList.size() +"\n Succeed: "+success_sum+ "\n Failed: "+(matchingCandiArrayList.size()-success_sum));

    }

    public static Double coordDistanceofPoints(Point a, Point b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }//유클리드 거리 구하기

    public static ArrayList<Candidate> findRadiusCandidate(ArrayList<GPSPoint> gpsPointArrayList, ArrayList<Candidate> matchingPointArrayList, Point center, Integer Radius, RoadNetwork roadNetwork, int timestamp) {
        ArrayList<Candidate> resultCandidate = new ArrayList<>();
        for (int i = 0; i < roadNetwork.linkArrayList.size(); i++) {
            double startX = roadNetwork.nodeArrayList.get(roadNetwork.linkArrayList.get(i).getStartNodeID()).getCoordinate().getX();
            double startY = roadNetwork.nodeArrayList.get(roadNetwork.linkArrayList.get(i).getStartNodeID()).getCoordinate().getY();
            double endX = roadNetwork.nodeArrayList.get(roadNetwork.linkArrayList.get(i).getEndNodeID()).getCoordinate().getX();
            double endY = roadNetwork.nodeArrayList.get(roadNetwork.linkArrayList.get(i).getEndNodeID()).getCoordinate().getY();

            Vector2D vectorFromStartToCenter = new Vector2D(center.getX() - startX, center.getY() - startY);
            Vector2D vectorFromEndToCenter = new Vector2D(center.getX() - endX, center.getY() - endY);
            Vector2D vectorFromEndToStart = new Vector2D(startX - endX, startY - endY);

            double dotProduct1 = vectorFromStartToCenter.dot(vectorFromEndToStart);
            double dotProduct2 = vectorFromEndToCenter.dot(vectorFromEndToStart);

            if (dotProduct1 * dotProduct2 <= 0) {
                //System.out.println("어허");
                //System.out.println("dot Product : "+dotProduct1+", "+dotProduct2);
                Candidate candidate = new Candidate();
                candidate.setInvolvedLink(roadNetwork.linkArrayList.get(i));
                Vector2D vectorStart = new Vector2D(startX, startY);
                Vector2D vectorC = new Vector2D(center.getX(), center.getY()); //원점에서 시작해 center로의 vector
                Vector2D vectorH = vectorStart.getAdded(vectorFromEndToStart.getMultiplied(
                        (vectorC.getSubtracted(vectorStart).dot(vectorFromEndToStart))
                                / Math.pow(vectorFromEndToStart.getLength(), 2))); //원점에서 시작해 수선의 발로의 vector
                candidate.setPoint(new Point(vectorH.getX(), vectorH.getY())); //수선의 발 vector의 x와 y값을 candidate의 point로 대입
                if (coordDistanceofPoints(center, candidate.getPoint()) > Radius) continue;
                resultCandidate.add(candidate);
//////////////////////////////////////////
                //candidate마다 ep, tp 구하기
                calculationEP(candidate, center, timestamp);
                calculationTP(candidate, matchingPointArrayList, center, gpsPointArrayList, timestamp, roadNetwork);

                for (Candidate c: matchingPointArrayList) {
                    emission.Emission_Median(c);
                    transition.Transition_Median(c);
                }

            }
        }
        calculationEPTP(resultCandidate, matchingPointArrayList, timestamp);

        return resultCandidate;
    }

    // EP클래스 가서 캔디데이트 마다 값 구하고 저장
    public static void calculationEP(Candidate cand, Point center, int timestamp) {
        cand.setEp(emission.Emission_pro(cand, center, cand.getPoint(), timestamp)); //ep 구하기
        return;
    }

    // TP클래스 가서 캔디데이트 마다 값 구하고 저장
    public static void calculationTP(Candidate cand, ArrayList<Candidate> matchingPointArrayList, Point center, ArrayList<GPSPoint> gpsPointArrayList, int timestamp,  RoadNetwork roadNetwork) {
        if (timestamp == 1 || timestamp == 2) {
            cand.setTp(0);
            return;
        }
        Candidate matching_pre = matchingPointArrayList.get(timestamp - 2);
        cand.setTp(transition.Transition_pro(gpsPointArrayList.get(timestamp - 2).getPoint(), center, matching_pre, cand, roadNetwork)); //tp 구하기
        return;

    }

    // 곱해진 eptp저장하고 후보들 중 가장 높은 eptp를 가지는 후보를 matchingPointArrayList에 저장하고
    // tp median과 ep median을 저장
    public static Candidate calculationEPTP(ArrayList<Candidate> resultCandidate, ArrayList<Candidate> matchingPointArrayList, int timestamp) {
        Candidate matchingCandidate = new Candidate();

        if (timestamp == 2 || timestamp == 1) {
            double min_ep = 0;
            for (int i = 0; i < resultCandidate.size(); i++) {
                if (i == 0) {
                    min_ep = resultCandidate.get(i).getEp();
                    matchingCandidate = resultCandidate.get(i);
                } else if (min_ep > resultCandidate.get(i).getEp()) {
                    min_ep = resultCandidate.get(i).getEp();
                    matchingCandidate = resultCandidate.get(i);
                }
            }
            matchingPointArrayList.add(matchingCandidate);

            return matchingCandidate;
        }

        double maximum_tpep = 0;

        for(int i=0; i < resultCandidate.size(); i++){
            double tpep=0;
            tpep = resultCandidate.get(i).getEp() * resultCandidate.get(i).getTp();
            resultCandidate.get(i).setTpep(tpep);

            if(maximum_tpep < tpep){
                maximum_tpep = tpep;
                matchingCandidate = resultCandidate.get(i);

            }
        }
        matchingPointArrayList.add(matchingCandidate);

        return matchingCandidate;

    }

    public static ArrayList<Link> AdjacentLink(Link mainLink,RoadNetwork roadNetwork,ArrayList<AdjacentNode> heads){
        int startNode=mainLink.getStartNodeID();
        int endNode = mainLink.getEndNodeID();
        ArrayList<Link> secondLink = new ArrayList<>();
        //ArrayList<Node> startAdjacentNode = new ArrayList<>();
        //ArrayList<Node> endAdjacentNode = new ArrayList<>();
        AdjacentNode pointer = heads.get(roadNetwork.nodeArrayList.get(startNode).getNodeID()).getNextNode();
        while(true){
            if(pointer==null) break;
            secondLink.add(roadNetwork.getLink(pointer.getNode().getNodeID(),startNode));
            pointer=pointer.getNextNode();
        }
        pointer = heads.get(roadNetwork.nodeArrayList.get(endNode).getNodeID()).getNextNode();
        while(true){
            if(pointer==null) break;
            secondLink.add(roadNetwork.getLink(pointer.getNode().getNodeID(),endNode));
            pointer=pointer.getNextNode();
        }
        return secondLink;
    }

}