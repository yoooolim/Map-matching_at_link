import java.util.ArrayList;

public class Transition {
    private static ArrayList<Double> transition_median = new ArrayList<Double>();

    public static Double coordDistanceofPoints(Point a, Point b){
        return Math.sqrt(Math.pow(a.getX()-b.getX(),2)+Math.pow(a.getY()-b.getY(),2));
    }//유클리드 거리 구하기

    public static Double routeDistanceofPoints(Candidate pre_matching, Candidate cand,  RoadNetwork roadNetwork){
        double routeDistance;
        //Point a,b는 링크 안에서의 point

        //같은 링크인지 어떻게 판단하지? -> 링크로 매칭할때 문제 없음

        //a,b가 같은 링크일 때 유클리드 거리
        if(pre_matching.getInvolvedLink() == cand.getInvolvedLink())
            routeDistance = coordDistanceofPoints(pre_matching.getPoint(), cand.getPoint());

        //a,b가 다른 링크일 때
        else {
            //case 1: a,b가 다른 링크이고 두 링크가 맞닿아 있을때
            if(pre_matching.getInvolvedLink().isLinkNextTo(roadNetwork, cand.getInvolvedLink().getLinkID()) == true){
                Point linked_point = new Point(0.0, 0.0); //두 링크가 만나는 점
                linked_point = pre_matching.getInvolvedLink().isLinkNextToPoint(roadNetwork, cand.getInvolvedLink());
                routeDistance = coordDistanceofPoints(pre_matching.getPoint(), linked_point) + coordDistanceofPoints(cand.getPoint(), linked_point);
                //a와 두 링크가 만나는 점까지 거리 + b와 두 링크가 만나는 점까지 거리
            }
            //case 2: a,b가 다른 링크이고 두 링크가 맞닿아 있지 않을때
            else{
                routeDistance = -1;// false 갈 수 없음, 후보 탈락
            }
        }
        return routeDistance;
    }//경로상의 거리 구하기

    public static double Transition_pro(Point gps_pre, Point gps, Candidate matching_pre, Candidate candidate,  RoadNetwork roadNetwork) {

        double tp_gps_distance, tp_candidate_distance;
        double dt=0;

        //case 1 : 이전 gps_point 와 gps_point의 유클리드 직선거리
        tp_gps_distance = coordDistanceofPoints(gps_pre, gps); //이전gps_point 와 gps_point의 유클리드 직선거리
        //case 2 : 이전 매칭 point와 gps point의 유클리드 직선거리
        //tp_gps_distance = coordDistanceofPoints(matching_pre.getPoint(), gps); //gps와 이전 매칭된 point

        //case 1 : 유클리드 거리
        //tp_candidate_distance = coordDistanceofPoints(matching_pre.getPoint(), candidate.getPoint()); //유클리드 거리
        //case 2 : 경로상의 거리
        tp_candidate_distance = routeDistanceofPoints(matching_pre, candidate, roadNetwork); //경로상의 거리
        //이전 매칭된point와 후보의 유클리드 직선거리
        //실제 tp는 직선거리가 아니고 경로상의 거리여야함!!

        double tp=0;

        if(tp_candidate_distance <0){
            tp = -1;
            candidate.setTp_median(0);
            return tp;
        } //거리가 0보다 작으면 후보 탈락

        dt = Math.abs(tp_gps_distance-tp_candidate_distance); //gps와 경로 거리 차이 절대값
        candidate.setTp_median(dt); //median 값 저장

        double beta=0;

        //System.out.println("tp median 확인" +transition_median.get((transition_median.size() / 2)));
        beta = transition_median.get(transition_median.size()/2) / (Math.log(2));
        tp = Math.exp((dt * (-1)) / beta) / beta;
        //tp 구하는 공식

        return tp;
    }

    //중앙값 저장하는 함수, beta의 median값
    public void Transition_Median(Candidate matching){

        if(transition_median.size() == 0)
            transition_median.add(matching.getEp_median());

        else{
            for(int i=0; i<transition_median.size(); i++){
                if(transition_median.get(i) > matching.getEp_median()){
                    transition_median.add(i, matching.getEp_median());
                    break;
                }
                if(i == transition_median.size()-1){
                    transition_median.add(matching.getEp_median());
                    break;
                }
            }//위치 찾고 삽입하는 과정, 오름차순으로 나열
        }
        /*
        for(int i=0; i<transition_median.size(); i++){
            System.out.println("제발tp" + transition_median.get(i));
        }
         */
    }
/*
    public static void Transition_Median(GPSPoint gps_pre, GPSPoint gps, Point matching_pre, Point matching){

        double tp_gps_distance, tp_route_distance;
        double dt=0;

        Point gpspoint_pre = new Point(0.0, 0.0);
        gpspoint_pre.setX(gps_pre.getX());
        gpspoint_pre.setY(gps_pre.getY());

        Point gpspoint = new Point(0.0, 0.0);
        gpspoint.setX(gps.getX());
        gpspoint.setY(gps.getY());
        tp_gps_distance = coordDistanceofPoints(gpspoint_pre, gpspoint); //이전gps_point 와 gps_point의 유클리드 직선거리

        tp_route_distance = coordDistanceofPoints(matching_pre, matching); //이전 실제 point와 실제 point의 유클리드 직선거리
        //실제 tp는 직선거리가 아니고 경로상의 거리여야함!!

        dt = Math.abs(tp_gps_distance-tp_route_distance); //gps와 경로 거리 차이 절대값

        if(transition_median.size() == 0)
            transition_median.add(dt);

        else {
            for (int i = 0; i < transition_median.size(); i++) {
                if (transition_median.get(i) > dt) {
                    transition_median.add(i, dt);
                    break;
                }
                if(i == transition_median.size()-1){
                    transition_median.add(dt);
                    break;
                }
            }//위치 찾고 삽입
        }
    }//중앙값 저장
 */

}
