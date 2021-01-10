import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class FileIO {
    // 파일이 저장된 path name
    // 세정: 1, 윤혜: 2, 유림: 3
    String directoryName = "data";
    public FileIO (int testNo) {
        directoryName += testNo;
    }
    RoadNetwork generateRoadNetwork () throws IOException {

        RoadNetwork roadNetwork = new RoadNetwork();

        /*=======Node.txt 파일읽어오기 작업========*/
        //파일 객체 생성
        File file1 = new File("./" + directoryName + "/Node.txt");
        //입력 스트림 생성
        FileReader fileReader1 = new FileReader(file1);
        //BufferedReader 클래스 이용하여 파일 읽어오기
        BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
        //System.out.println("======== Node 정보 =======");
        while (bufferedReader1.ready()) {
            String line = bufferedReader1.readLine();
            String[] lineArray = line.split("\t");
            Point coordinate = new Point(lineArray[1], lineArray[2]);
            Node node = new Node(lineArray[0], coordinate); // 노드생성
            roadNetwork.nodeArrayList.add(node); // nodeArrayList에 생성한 노드 추가
            //System.out.println(node);
        }
        // close the bufferedReader
        bufferedReader1.close();

        /*=======Link.txt 파일읽어오기 작업========*/
        //파일 객체 생성
        File file2 = new File("./" + directoryName + "/Link.txt");
        //입력 스트림 생성
        FileReader fileReader2 = new FileReader(file2);
        //BufferedReader 클래스 이용하여 파일 읽어오기
        BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
        //System.out.println("======== Link 정보 =======");
        while (bufferedReader2.ready()) {
            String line = bufferedReader2.readLine();
            String[] lineArray = line.split("\t");
            //Point coordinate = new Point (lineArray[1], lineArray[2]);
            // weight 구하기 - 피타고라스법칙 적용
            // a=밑변 b=높이 weight=(a제곱+b제곱)의 제곱근의 반올림값
            Double a = roadNetwork.nodeArrayList.get(Integer.parseInt(lineArray[1])).getCoordinate().getX()
                    - roadNetwork.nodeArrayList.get(Integer.parseInt(lineArray[2])).getCoordinate().getX();
            Double b = roadNetwork.nodeArrayList.get(Integer.parseInt(lineArray[1])).getCoordinate().getY()
                    - roadNetwork.nodeArrayList.get(Integer.parseInt(lineArray[2])).getCoordinate().getY();
            Double weight = (double) Math.round(Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));

            // link 생성
            Link link = new Link(lineArray[0], lineArray[1], lineArray[2], weight);

            /*
            //involving points 구하기

            // start point와 end point 좌표 지정
            double xs = roadNetwork.nodeArrayList.get(link.getStartNodeID()).getCoordinate().getX();
            double ys = roadNetwork.nodeArrayList.get(link.getStartNodeID()).getCoordinate().getY();
            double xe = roadNetwork.nodeArrayList.get(link.getEndNodeID()).getCoordinate().getX();
            double ye = roadNetwork.nodeArrayList.get(link.getEndNodeID()).getCoordinate().getY();

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
            link.setInvolvingPointList(involvingPointList);
*/
            /* 아직 필요없어보여서 안짬 - 필요시 추가 예정
            // 기울기가 무한아닌 양수인 경우 : /

            // 기울기가 음수인 경우 : \
            */

            roadNetwork.linkArrayList.add(link); // linkArrayList에 생성한 노드 추가
            // System.out.println(link);
//            System.out.print("involving points:");
//            System.out.println(link.getInvolvingPointList());
        }
        // close the bufferedReader
        bufferedReader2.close();

        return roadNetwork;
    }
//유림 혹시 몰라 push
}
