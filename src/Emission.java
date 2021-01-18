import com.sun.javafx.UnmodifiableArrayList;

import java.util.ArrayList;

public class Emission {

    private static ArrayList<Double> emission_median = new ArrayList<Double>();

    public static Double coordDistanceofPoints(Point a, Point b){
        return Math.sqrt(Math.pow(a.getX()-b.getX(),2)+Math.pow(a.getY()-b.getY(),2));
    }//유클리드 거리 구하기

    //emission probability 구하는 함수
    public double Emission_pro(Candidate cand, Point gps, Point candidate, int size) {
        double ep_distance = 0;

        ep_distance = coordDistanceofPoints(candidate, gps); //후보point와 gps point의 유클리드 직선 거리

        cand.setEp_median(ep_distance); //median 값 저장

        if(size==1 || size == 2) {
            return ep_distance;
        } //size: gps배열 사이즈

        double ep = 0;
        double sigma=0;
        //System.out.println("ep median 확인" +emission_median.get((emission_median.size() / 2)));

        sigma = (1.4826) * emission_median.get((emission_median.size() / 2));

        ep = Math.exp(Math.pow(Math.abs(ep_distance) / sigma, 2) * (-0.5)) / (Math.sqrt(2 * Math.PI) * sigma);

        return ep;

    } //GPS와 후보의 거리 구하기, 중앙값 배열에 저장

    //중앙값 저장하는 함수, emission에 필요한 중앙값
    public void Emission_Median(Candidate matching){
        if(emission_median.size() == 0)
            emission_median.add(matching.getEp_median());
        else{
            for(int i=0; i<emission_median.size(); i++){
                if(emission_median.get(i) > matching.getEp_median()){
                    emission_median.add(i, matching.getEp_median());
                    break;
                }
                if(i == emission_median.size()-1){
                    emission_median.add(matching.getEp_median());
                    break;
                }
            }//위치 찾고 삽입하는 과정, 오름차순으로 나열
        }
        /*
        for(int i=0; i<emission_median.size(); i++){
            System.out.println("ep" + emission_median.get(i));
        }
         */
    }
    /*
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
*/

}
