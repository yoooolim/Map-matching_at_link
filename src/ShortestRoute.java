import java.util.ArrayList;

public class ShortestRoute {

    public ShortestRoute(){;}

    public ArrayList<Integer> dijkstra(RoadNetwork roadNetwork,ArrayList<AdjacentNode> heads){
        ArrayList<Integer> route = new ArrayList<>();
        //시작 노드는 여기에서 변경
        int start = 0;
        int end = 34;

        int node_num = roadNetwork.nodeArrayList.size();
        double INF = 1000000.0;
        double[][] a = new double[node_num][node_num]; //전체 거리 그래프
        int path[] = new int[node_num];

        /* 전체 거리 그래프 초기화 */

        for(int i=0;i<node_num;i++){ // 전체 거리 그래프 전체 INF로 초기화
            path[i]=-1;
            for(int j=0;j<node_num;j++){
                a[i][j]=INF;
            }
        }
        AdjacentNode head = heads.get(0);
        while(head.getNextNode()!=null){
            head = head.getNextNode();
            path[head.getNode().getNodeID()]=start;
        }

        for(int i=0;i<node_num;i++){ //실제 이어져 있는 LINK는 WEIGHT로 전체 거리 그래프 재 초기화
            head = heads.get(i);
            AdjacentNode ptr = head;
            double weight = 0.0;
            while(ptr!=null){
                if(ptr!=head) weight = ptr.getAdjacentLink().getWeight();
                a[i][ptr.getNode().getNodeID()]=weight;
                ptr=ptr.getNextNode();
            }
        }

        boolean[] v = new boolean[node_num]; //방문한 노드
        double[] d = new double[node_num]; //거리

        AdjacentNode start_adj_node = heads.get(start);

        for(int i=0;i<node_num;i++){ //시작점이 start일 때 거리 그래프 초기화
            d[i]=a[start][i];
        }
        v[start]=true;

        for(int i=0;i<node_num-2;i++){
            int current = 0; //현재 방문 중인 node
            double min=INF;
            for(int j=0;j<node_num;j++){
                if(d[j]<min&&!v[j]){ //방문하지 않고 거리가 가장 짧은 node를 current로 설정
                    min=d[j];
                    current = j;
                }
            }
            v[current]=true; //방문 기록
            for(int j=0;j<node_num;j++){
                if(!v[j]){
                    if(d[current]+a[current][j]<d[j]) { // 거치는 방법이 바로 가는 것보다 최단일 경우
                        d[j]=d[current]+a[current][j]; //최단 거리 갱신
                        path[j] = current;
                    }
                }
            }
        }

        int findroute = path[end];
        ArrayList<Integer> trace = new ArrayList<>();
        trace.add(end);
        while(path[findroute]!=-1){
            trace.add(findroute);
            findroute = path[findroute];
        }
        trace.add(start);
        for(int i=0;i<trace.size();i++){
            System.out.println(i+" : "+trace.get(i));
        }
        System.out.println();
        for(int i=trace.size()-1;i>=0;i--){
            System.out.print(i+" : ");
            System.out.println(trace.get(i));
            route.add(trace.get(i));
        }
        return route;
    }
}
