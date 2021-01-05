public class Candidate {
    private Point point;
    private Link involvedLink;
    private double tp;
    private double ep;
    private double tpep;
    private double ep_median;
    private double tp_median;

    public Candidate(){
        this.point = null;
        this.involvedLink = null;
        this.tp= 0.0;
        this.ep=0.0;
        this.tpep=0.0;
        this.ep_median=0.0;
        this.tp_median=0.0;
    }

    public Candidate (Point point, Link involvedLink){
        this.point = point;
        this.involvedLink = involvedLink;
        this.tp= 0.0;
        this.ep=0.0;
        this.tpep=0.0;
        this.ep_median=0.0;
        this.tp_median=0.0;
    }

    public void setPoint(Point point){this.point=point;}

    public void setInvolvedLink(Link link){this.involvedLink=link;}

    public Point getPoint() {
        return point;
    }

    public Link getInvolvedLink(){
        return involvedLink;
    }
    //유림 혹시 몰라 push

    public void setTp(double tp) {
        this.tp = tp;
    }

    public void setEp(double ep) {
        this.ep = ep;
    }

    public double getTp() {
        return tp;
    }

    public double getEp() {
        return ep;
    }

    public void setTpep(double tpep){
        this.tpep=tpep;
    }

    public double getTpep(){return tpep;}

    public void setEp_median(double ep_median) {this.ep_median = ep_median;}

    public void setTp_median(double tp_median){this.tp_median = tp_median;}

    public double getEp_median() {
        return ep_median;
    }

    public double getTp_median() {
        return tp_median;
    }

    @Override
    public String toString() {
        return "point: "+ point + "  involvedLink: " + involvedLink + " tp/ep/tpep: "+tp+"/"+ep+"/"+tpep;//+"\n";
    }

    public String toStringOnlyPoint() {
        return point.toString();
    }
}
