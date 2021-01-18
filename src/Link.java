import java.util.ArrayList;

public class Link {
    private int linkID; // Link ID
    private int startNodeID; // Link의 Start Node
    private int endNodeID; // Link의 End Node
    private Double weight; // Link의 weight (길이)
    // Link가 포함하고 있는 node List
    //private ArrayList<Point> involvingPointList = new ArrayList<>();

    //////생성자, getter, setter, toString//////
    // int로 ID 파라미터 받음
    public Link (int linkID, int startNodeID, int endNodeID, Double weight){
        this.linkID = linkID;
        this.startNodeID = startNodeID;
        this.endNodeID = endNodeID;
        this.weight = weight;
    }

    // String으로 ID 파라미터 받음
    public Link(String linkID, String startNodeID, String endNodeID, Double weight) {
        this.linkID = Integer.parseInt(linkID);
        this.startNodeID = Integer.parseInt(startNodeID);
        this.endNodeID = Integer.parseInt(endNodeID);
        this.weight = weight;
    }

    public String toString() {
        return "[" + linkID + "]\t" + "(" + startNodeID +", "
                + endNodeID+")" + "\t" + "weight: " + weight;
    }

    public int getLinkID() {
        return linkID;
    }

    public void setLinkID(int linkID) {
        this.linkID = linkID;
    }

    public int getStartNodeID() {
        return startNodeID;
    }

    public void setStartNodeID(int startNodeID) {
        this.startNodeID = startNodeID;
    }

    public int getEndNodeID() {
        return endNodeID;
    }

    public void setEndNodeID(int endNodeID) {
        this.endNodeID = endNodeID;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /*public ArrayList<Point> getInvolvingPointList() {
        return involvingPointList;
    }

    public void setInvolvingPointList(ArrayList<Point> involvingPointList) {
        this.involvingPointList = involvingPointList;
    }*/
    //////////////////////////////////////

    //  [VERIFIED] 이 링크의 startNode(이 아이)와 이웃하는(이 아이를 startNode혹은 endNode로 가지는) links 출력
    public ArrayList<Link> linksNeighborOnStartNode (RoadNetwork roadNetwork) {
        ArrayList<Link> result = roadNetwork.getNode(startNodeID).includingLinks(roadNetwork.linkArrayList);
        for (int i=0;i<result.size();i++) {
            Link l = result.get(i);
            if (l.getLinkID() == linkID) {
                result.remove(l);
                i--;
            }
        }
        return result;
    }

    //  [VERIFIED] 이 링크의 endNode(이 아이)와 이웃하는(이 아이를 startNode혹은 endNode로 가지는) links 출력
    public ArrayList<Link> linksNeighborOnEndNode (RoadNetwork roadNetwork) {
        ArrayList<Link> result =  roadNetwork.getNode(endNodeID).includingLinks(roadNetwork.linkArrayList);
        for (int i=0;i<result.size();i++) {
            Link l = result.get(i);
            if (l.getLinkID() == linkID) {
                result.remove(l);
                i--;
            }
        }
        return result;
    }

    //  [VERIFIED] 이 링크의 startNode endNode(이 아이)와 이웃하는(이 아이를 startNode혹은 endNode로 가지는) links 출력
    public ArrayList<Link> linksNeighborOnStartOrEndNode (RoadNetwork roadNetwork) {
        ArrayList<Link> resultLinks = new ArrayList<>();
        resultLinks.addAll(linksNeighborOnStartNode(roadNetwork));
        resultLinks.addAll(linksNeighborOnEndNode(roadNetwork));

        return resultLinks;
    }

    // 11/20에 만듦: param으로 받은 link와 이 링크가 이웃하는지 여부 출력
    public boolean isLinkNextTo(RoadNetwork rn, int _linkID) {
        if (this.linksNeighborOnStartOrEndNode(rn) != null) {
            for (Link l : this.linksNeighborOnStartOrEndNode(rn)) {
                if (l.getLinkID() == _linkID)
                    return true;
            }
        } return false;
    }

    //두 링크가 연결되어있을때 연결되어있는 노드
    public Point isLinkNextToPoint(RoadNetwork rn, Link _linkID){
        Point linked_point = new Point(0.0, 0.0);

        if (this.getStartNodeID() == _linkID.getStartNodeID()) {
            linked_point = rn.getNode(this.getStartNodeID()).getCoordinate(); //point 반환
        }
        else if(this.getStartNodeID() == _linkID.getEndNodeID()){
            linked_point = rn.getNode(this.getStartNodeID()).getCoordinate(); //point 반환
        }
        else if(this.getEndNodeID() == _linkID.getStartNodeID()){
            linked_point = rn.getNode(this.getEndNodeID()).getCoordinate(); //point 반환
        }
        else if(this.getEndNodeID() == _linkID.getEndNodeID()){
            linked_point = rn.getNode(this.getEndNodeID()).getCoordinate(); //point 반환
        }

        return linked_point;
    }

    // 11/20에 만듦: 이 링크와 이웃한 링크 개수 출력
    public int nextLinksNum(RoadNetwork rn) {
        int n = 0;
        if (this.linksNeighborOnStartOrEndNode(rn) != null) {
            for (Link l : this.linksNeighborOnStartOrEndNode(rn)) {
                n++;
            }
        } return n;
    }
}
