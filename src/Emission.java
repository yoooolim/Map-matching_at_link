import com.sun.javafx.UnmodifiableArrayList;

import java.util.ArrayList;

public class Emission {

    private static ArrayList<Double> emission_median = new ArrayList<Double>();

    public static Double coordDistanceofPoints(Point a, Point b){
        return Math.sqrt(Math.pow(a.getX()-b.getX(),2)+Math.pow(a.getY()-b.getY(),2));
    }//유클리드 거리 구하기

    //emission probability 구하는 함수
    public double Emission_pro(GPSPoint gps, Point candidate, int size) {
        double ep_distance = 0;

        Point gpspoint = new Point(0.0, 0.0);
        gpspoint.setX(gps.getX());
        gpspoint.setY(gps.getY());

        ep_distance = coordDistanceofPoints(candidate, gpspoint); //후보point와 gps point의 유클리드 직선 거리

        if(size==1 || size == 2) {
            return ep_distance;
        } //size: gps배열 사이즈

        double ep = 0;
        double sigma=0;
        sigma = (1.4826) * emission_median.get((emission_median.size() / 2));


        ep = Math.exp(Math.pow(Math.abs(ep_distance) / sigma, 2) * (-0.5)) / (Math.sqrt(2 * Math.PI) * sigma);

        //System.out.println(candidate);
        //System.out.println("ep : "+ ep);
        return ep;

    } //GPS와 후보의 거리 구하기, 중앙값 배열에 저장

    //median 저장하는 함수
    public void Emission_Median(GPSPoint gps, Point matching){
        double ep_distance = 0;

        Point gpspoint = new Point(0.0, 0.0);
        gpspoint.setX(gps.getX());
        gpspoint.setY(gps.getY());

        ep_distance = coordDistanceofPoints(matching, gpspoint); //매칭된 포인트와 gps point의 유클리드 직선거리

        if(emission_median.size() == 0)
            emission_median.add(ep_distance);

        else {
            for (int i = 0; i < emission_median.size(); i++) {
                if (emission_median.get(i) > ep_distance) {
                    emission_median.add(i, ep_distance);
                    break;
                }
                if(i == emission_median.size()-1){
                    emission_median.add(ep_distance);
                    break;
                }
            }//위치 찾고 삽입
        }
    }//중앙값 저장


}
