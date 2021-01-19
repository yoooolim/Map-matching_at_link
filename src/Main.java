import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Emission emission = new Emission();
    private static Transition transition = new Transition();

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("===== [YSY] Map-matching PilotTest 1-2 =====");
        int testNo = 4; // 여기만 바꿔주면 됨 (1-세정, 2-유네, 3-유림 4-폭 가중치 추가 데이터)
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

        // Adjacency List 구조 바탕으로 출력 test

        for (AdjacentNode adjacentNode : heads) {
            System.out.print( " [ " + adjacentNode.getNode().getNodeID() + " ] ");
            while (adjacentNode.getNextNode() != null) {
                System.out.print(adjacentNode);
                adjacentNode = adjacentNode.getNextNode();
            }
            System.out.println();
        }

        /* 여기부터 dijkstra~ ShortestRoute -> dijkstra */
        //before get map matching algorithm
        ShortestRoute shortestPath = new ShortestRoute();
        //시작, 끝 노드 ID!
        int start = 0;
        int end = 34;
        long startT=0, endT=0; //시간 계산 위해서 -> nano time으로 왜 dijk이 더 오래 걸리는지 파악할 것
        long sum =0;

        /* dijkstra */
        ArrayList<Integer> dijkstra_route = shortestPath.dijkstra(roadNetwork, heads, start, end);

        /* A_start */
        ArrayList<Integer> aStart_route = shortestPath.astar(roadNetwork, heads, start, end);

        /* longest leg first! (llf) */
        ArrayList<Integer> llf_route = shortestPath.longest_leg_first(roadNetwork, heads, start, end);

        /* fewest turn */
        ArrayList<Integer> ft_route = shortestPath.fewest_turn(roadNetwork, heads, start, end);

        ArrayList<Integer> result_route = dijkstra_route;
        for(int i=0;i<result_route.size();i++){
            System.out.println(result_route.get(i)+" ");
        }

        // GPS points와 routePoints를 저장할 ArrayList생성
        ArrayList<GPSPoint> gpsPointArrayList = new ArrayList<>();
        ArrayList<Point> routePointArrayList; // 실제 경로의 points!
        ArrayList<Candidate> matchingPointArrayList = new ArrayList<>();

        // test 번호에 맞는 routePoints생성
        routePointArrayList = roadNetwork.routePoints(testNo,result_route);

        // GPSPoints 생성 -> 이전 test1-1에서 generateGPSPoints메서드 삭제함
        int timestamp = 0;
        System.out.println("여기부터 생성된 gps point~~");
        for (Point point : routePointArrayList) {
            GPSPoint gpsPoint = new GPSPoint(timestamp, point);
            gpsPointArrayList.add(gpsPoint);
            timestamp++;
            System.out.println(gpsPoint); //gps point 제대로 생성 되는지 확인차 넣음
            ArrayList<Candidate> Candidates = new ArrayList<>();
            Candidates.addAll(findRadiusCandidate(gpsPointArrayList, matchingPointArrayList, gpsPoint.getPoint(), 20, roadNetwork, timestamp));

            System.out.println("\nCandidate : ");
            for (Candidate candidate : Candidates) {
                System.out.println(candidate);
            }

            /////////matching print/////////////
            System.out.println("매칭완료 " + matchingPointArrayList.get(timestamp-1));

            System.out.println();
        }
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

                //candidate마다 ep, tp 구하기
                calculationEP(candidate, center, timestamp);
                calculationTP(candidate, matchingPointArrayList, center, gpsPointArrayList, timestamp, roadNetwork);
            }
        }
        calculationEPTP(resultCandidate, matchingPointArrayList, timestamp);

        return resultCandidate;
    }

    public static void calculationEP(Candidate cand, Point center, int timestamp) {
        cand.setEp(emission.Emission_pro(cand, center, cand.getPoint(), timestamp)); //ep 구하기
        return;
    }

    public static void calculationTP(Candidate cand, ArrayList<Candidate> matchingPointArrayList, Point center, ArrayList<GPSPoint> gpsPointArrayList, int timestamp,  RoadNetwork roadNetwork) {
        if (timestamp == 1 || timestamp == 2) {
            cand.setTp(0);
            return;
        }
        Candidate matching_pre = matchingPointArrayList.get(timestamp - 2);
        cand.setTp(transition.Transition_pro(gpsPointArrayList.get(timestamp - 2).getPoint(), center, matching_pre, cand, roadNetwork)); //tp 구하기
        return;

    }

    public static Candidate calculationEPTP(ArrayList<Candidate> resultCandidate, ArrayList<Candidate> matchingPointArrayList, int timestamp) {
        //matchingPointArrayList.add();
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
            emission.Emission_Median(matchingCandidate);
            if(timestamp > 1){
                transition.Transition_Median(matchingCandidate);
            }

            return matchingCandidate;
        }

        double maximum_tpep = 0;

        for(int i=0; i< resultCandidate.size(); i++){
            double tpep=0;
            tpep = resultCandidate.get(i).getEp() * resultCandidate.get(i).getTp();
            resultCandidate.get(i).setTpep(tpep);

            if(maximum_tpep < tpep){
                maximum_tpep = tpep;
                matchingCandidate = resultCandidate.get(i);

            }
        }
        matchingPointArrayList.add(matchingCandidate);

        emission.Emission_Median(matchingCandidate);
        transition.Transition_Median(matchingCandidate);
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