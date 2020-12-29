import java.util.ArrayList;

public class Transition {
    private static ArrayList<Double> transition_median = new ArrayList<Double>();

    public static Double coordDistanceofPoints(Point a, Point b){
        return Math.sqrt(Math.pow(a.getX()-b.getX(),2)+Math.pow(a.getY()-b.getY(),2));
    }//유클리드 거리 구하기

    public static double Transition_pro(GPSPoint gps_pre, GPSPoint gps, Point matching_pre, Point candidate) {

        double tp_gps_distance, tp_route_distance, tp_gps_test_distance;
        double dt=0;

        Point gpspoint_pre = new Point(0.0, 0.0);
        gpspoint_pre.setX(gps_pre.getX());
        gpspoint_pre.setY(gps_pre.getY());

        Point gpspoint = new Point(0.0, 0.0);
        gpspoint.setX(gps.getX());
        gpspoint.setY(gps.getY());
        tp_gps_distance = coordDistanceofPoints(gpspoint_pre, gpspoint); //이전gps_point 와 gps_point의 유클리드 직선거리

        tp_route_distance = coordDistanceofPoints(matching_pre, candidate);
        //이전 매칭된point와 후보의 유클리드 직선거리
        //실제 tp는 직선거리가 아니고 경로상의 거리여야함!!

        //test
        tp_gps_test_distance = coordDistanceofPoints(matching_pre, gpspoint); //이전matching_point 와 gps_point의 유클리드 직선거리

        //tp_gps_distance -> tp_gps_test_distance 변경해봄 // 달라지는것이 많이 없음
        dt = Math.abs(tp_gps_test_distance-tp_route_distance); //gps와 경로 거리 차이 절대값
        //System.out.println("dt: "+dt);

        double tp=0;
        double beta=0;

        beta = transition_median.get(transition_median.size()/2) / (Math.log(2));
        //System.out.println("beta: "+beta);
        tp = Math.exp((dt * (-1)) / beta) / beta;

        //System.out.print("tp : ");
        //System.out.println(tp);
        return tp;
    }



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
}
