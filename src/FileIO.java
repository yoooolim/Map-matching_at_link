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
        System.out.println("======== Node 정보 =======");
        while (bufferedReader1.ready()) {
            String line = bufferedReader1.readLine();
            String[] lineArray = line.split("\t");
            Point coordinate = new Point(lineArray[1], lineArray[2]);
            Node node = new Node(lineArray[0], coordinate); // 노드생성
            roadNetwork.nodeArrayList.add(node); // nodeArrayList에 생성한 노드 추가
            System.out.println(node);
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
        System.out.println("======== Link 정보 =======");
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
            Link link = new Link(lineArray[0], lineArray[1], lineArray[2], weight, lineArray[3]);

            roadNetwork.linkArrayList.add(link); // linkArrayList에 생성한 노드 추가
            System.out.println(link);
        }
        // close the bufferedReader
        bufferedReader2.close();

        return roadNetwork;
    }
}
