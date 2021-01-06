import java.util.Random;

public class GPSPoint {
    private Point coordinate;
    private int timeStamp; // 일단 쉽게 짜려고 int형으로 생성. 필요시 수정 가능 (String형 혹은 다른 Time관련 클래스로)

    GPSPoint (int timeStamp, Point orgCoordinate) { // org means origin
        this.timeStamp = timeStamp;
        double gps_x, gps_y;
        Random random = new Random();
        while(true) {
            //10m 안쪽의 오차가 약 95% 가량 나도록 표준편차 4로 설정
            gps_x = 4 * random.nextGaussian() + orgCoordinate.getX();
            //if(gps_x<0) gps_x=orgCoordinate.getX();
            gps_y = 4 * random.nextGaussian() + orgCoordinate.getY();
            //if(gps_y<0) gps_y=orgCoordinate.getY();
            if (gps_x<0 && gps_y<0)
                continue;
            else
                break;
        }
        coordinate = new Point (gps_x, gps_y);
    }

    public String toString() {
        return "[" + timeStamp+ "] " + coordinate;
    }
    public Point getPoint() {
        return coordinate;
    }

    public Double getX(){return coordinate.getX();}
    public Double getY(){return coordinate.getY();}

}
