import javafx.util.Pair;
import sun.management.AgentConfigurationError;

import java.util.ArrayList;
import java.util.List;

public class RoadNetwork {

    // 데이터를 보관할 ArrayList들을 담는 class..
    // 필요 없을 수도 있지만 혹시의 상황을 대비해서 만들었음
    // 일단 이런 구조로 ArrayList로 선언해보고 이 클래스 정 필요 없겠다 싶으면 다음 테스트에서 다시 파기 예정
    // 만약 필요하게 되면 private으로 만들어주고 getter setter만들 예정
    protected ArrayList<Node> nodeArrayList = new ArrayList<>();
    protected ArrayList<Link> linkArrayList = new ArrayList<>();

    // _nodeID를 nodeID로 가지는 node반환
    public Node getNode (int _nodeID) {
        for (Node currNode : nodeArrayList) {
            if (currNode.getNodeID() == _nodeID) {
                return currNode;
            }
        }
        // 탐색에 실패한 경우 nodeId가 -1인 Node반환
        return new Node(-1, new Point((double)-99,(double)-99));
    }

    public Node getNode1 (Point nodePoint){
        for(Node currNode : nodeArrayList){
            if(currNode.getCoordinate()==nodePoint)
                return currNode;
        }
        // 탐색에 실패한 경우 nodeId가 -1인 Node반환
        return new Node(-1, new Point((double)-99,(double)-99));
    }

    // _linkID를 linkID로 가지는 link반환
    public Link getLink (int _linkID) {
        for (Link currLink : linkArrayList) {
            if (currLink.getLinkID() == _linkID) {
                return currLink;
            }
        }
        // 탐색에 실패한 경우 nodeId가 -1인 Link반환
        return new Link(-1,-1,-1,(double)-1);
    }
    // nodeID_s를 start node ID로, nodeID_e를 end node id로 가지는 가지는 link반환
    // 혹은 nodeID_e를 start node ID로, nodeID_s를 end node id로 가지는 가지는 link반환
    public Link getLink (int nodeID_s, int nodeID_e) {
        for (Link currLink : linkArrayList) {
            if ((currLink.getStartNodeID() == nodeID_s) && (currLink.getEndNodeID() == nodeID_e)
                    || (currLink.getStartNodeID() == nodeID_e) && (currLink.getEndNodeID() == nodeID_s)) {
                return currLink;
            }
        }
        // 탐색에 실패한 경우 nodeId가 -1인 Link반환
        return new Link(-1,-1,-1,(double)-1);
    }
    List<Pair<Link,Integer>> getLink1 (int nodeID) {
        List<Pair<Link,Integer>> pairs = new ArrayList<>();
        for (Link currLink : linkArrayList) {
            if (currLink.getStartNodeID() == nodeID) {
                pairs.add(new Pair<Link,Integer>(currLink,currLink.getEndNodeID()));
            }
            else if(currLink.getEndNodeID() == nodeID){
                pairs.add(new Pair<Link,Integer>(currLink,currLink.getStartNodeID()));
            }
        }
        return pairs;
    }

    // testNo에 맞게 경로 Point로 생성하는 작업
    // 아직  startNode가 닿는지 endNode가 닿는지에 따라 순서대로/역순으로 나오는 로직은 추가 안함
    /*왼쪽에서 오른쪽으로 가는 방향만 고려함 (왼, 오를 따질 수 없는 경우는 아래에서 위로 가는 방향만 고려)
     *되는 루트 →, ↑, ↗,↘
     *안되는 루트: ←, ↓, ↙, ↖
     * */
    public ArrayList<Point> routePoints (int testNo) {
        ArrayList<Point> routePoints = new ArrayList<>();
        if (testNo == 1) { // 세정이 데이터
            int[] routeNodes = {0, 1, 9, 10, 11, 19, 28, 36, 44, 45, 46, 47, 55};
            for (int i=0; i<routeNodes.length-1; i++) {
                Link routelink = getLink(routeNodes[i], routeNodes[i+1]);
                routePoints.addAll(getInvolvingPointList(getNode(routelink.getStartNodeID()).getCoordinate(),
                        getNode(routelink.getEndNodeID()).getCoordinate()));
            }
        } else if (testNo == 2) { // 유네 데이터
            // node0 에서 node 55로 가는 경로
            int[] routeNodes = {0,1, 6, 12, 25, 26, 27, 33, 34};
            for (int i=0; i<routeNodes.length-1; i++) {
                Link routelink = getLink(routeNodes[i], routeNodes[i+1]);
                routePoints.addAll(getInvolvingPointList(getNode(routelink.getStartNodeID()).getCoordinate(),
                        getNode(routelink.getEndNodeID()).getCoordinate()));
            }
        } else if (testNo == 3) { // 유림이 데이터
            int[] routeNodes= {0,20,};
            for (int i=0; i<routeNodes.length-1; i++) {
                Link routelink = getLink(routeNodes[i], routeNodes[i+1]);
                routePoints.addAll(getInvolvingPointList(getNode(routelink.getStartNodeID()).getCoordinate(),
                        getNode(routelink.getEndNodeID()).getCoordinate()));
            }
        } return routePoints;
    }

    // link개수 출력하기
    int getLinksSize () {
        return linkArrayList.size();
    }

    //우리 route node만 입력 해도 실제 경로 쭈르륵 떠야 해서 이 부분에 involving point list 살짝 변경해서 넣음
    public ArrayList<Point> getInvolvingPointList(Point start, Point end){

        //involving points 구하기

        // start point와 end point 좌표 지정
        double xs = start.getX();
        double ys = start.getY();
        double xe = end.getX();
        double ye = end.getY();

        ArrayList<Point> involvingPointList = new ArrayList<>();

        // link 기울기가 0인 경우 : ㅡ
        if (ys == ye) {
            // y값이 정수인 경우만 involvingPoint에 추가 (int의 ++연산)
            for (int x_cord = (int) xs; x_cord <= (int) xe; x_cord++) {
                // xs가 5.1등과 같이 (int)5.1 즉 5보다 큰 경우 (int)5.1 즉 5는 involvingPoint가 될수없음
                // 5.0등인 case는 else이하 로직을 수행할 수 있도록 함
                if (x_cord < xs) continue;

                    // involvingPointList에 Point 추가
                else {
                    involvingPointList.add(new Point((double) x_cord, ys));
                }
            }

        }
        // link 기울기가 무한인 경우 : |
        else if (xs == xe) {
            // y값이 정수인 경우만 involvingPoint에 추가 (int의 ++연산)
            for (int y_cord = (int) ys; y_cord <= (int) ye; y_cord++) {
                // ys가 5.1등과 같이 (int)5.1 즉 5보다 큰 경우 (int)5.1 즉 5는 involvingPoint가 될수없음
                // 5.0등인 case는 else이하 로직을 수행할 수 있도록 함
                if (y_cord < ys) continue;

                    // involvingPointList에 Point 추가
                else {
                    involvingPointList.add(new Point(xs, (double) y_cord));
                }
            }
        }
        // 기울기가 양수 혹은 음수인 경우 (/,\)
        else {
            double slope = (ye-ys)/(xe-xs);
            double y_intercept = ((xe*ys)-(xs*ye))/(xe-xs);
            for (int x_cord = (int) xs; x_cord <= (int) xe; x_cord++) {
                // xs가 5.1등과 같이 (int)5.1 즉 5보다 큰 경우 (int)5.1 즉 5는 involvingPoint가 될수없음
                // 5.0등인 case는 else이하 로직을 수행할 수 있도록 함
                if (x_cord < xs) continue;

                    // involvingPointList에 Point 추가
                else {
                    double y = (slope * x_cord) + y_intercept;
                    if (y % 1.0 == 0.0) {
                        involvingPointList.add(new Point((double) x_cord, y));
                    }
                }
            }
        }
        return involvingPointList;
    }
}

