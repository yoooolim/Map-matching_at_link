import java.util.ArrayList;

public class TransitionProbability {
    public void calculationTP(ArrayList<Candidate> cand,Candidate lastMatch,RoadNetwork roadNetwork,ArrayList<AdjacentNode> heads){
        Link mainLink = lastMatch.getInvolvedLink();
        ArrayList<Link> secondLink = AdjacentLink(mainLink,roadNetwork,heads);
        ArrayList<Link> thirdLink = new ArrayList<>();
        for(int i=0;i<secondLink.size();i++){
            thirdLink.addAll(AdjacentLink(secondLink.get(i),roadNetwork,heads));
        }
        for(int i=0;i<cand.size();i++){
            if(cand.get(i).getInvolvedLink()==mainLink) cand.get(i).setTp(3/5);
            else{
                for(int j=0;j<secondLink.size();j++){
                    if(secondLink.get(j)==cand.get(i).getInvolvedLink()) cand.get(i).setTp(2/5);
                }
                for(int j=0;j<thirdLink.size();j++){
                    if(thirdLink.get(j)==cand.get(i).getInvolvedLink()) cand.get(i).setTp(1/5);
                }
            }
        }
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
