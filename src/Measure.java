import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Measure implements Serializable {

    private String version;

    private boolean region = false;

    private double course;
    private int delivery;
    private int other;

    private List<String> prodList = new ArrayList<>();
    private List<String> itemInfo = new ArrayList<>();
    private List<Double> prodItemPrice = new ArrayList<>();
    private List<Integer> prodInterest = new ArrayList<>();
    private List<Integer> prodMounting = new ArrayList<>();
    private List<Integer> prodSlopes = new ArrayList<>();

    public List<String> getItemInfo(){
        return itemInfo;
    }

    public Boolean getRegion() {
        return this.region;
    }

    public int getProdInterest() {
        int itemInterest = 0;
        for(int i : prodInterest) {
            itemInterest += i;
        }
        return  itemInterest;
    }

    public double getProdItemPrice() {
        double itemPrice = 0;

        for(int i = 0;i < prodList.size();i++) {

            if( !prodList.get(i).contains("Брус деревянный") && !prodList.get(i).contains("Нащельник ПВХ")) {
                itemPrice += prodItemPrice.get(i);
            }
        }
        return itemPrice;
    }

    public double getProdItemPriceDop() {
        double itemPrice = 0;

        for(int i = 0;i < prodList.size();i++) {
            if( prodList.get(i).contains("Брус деревянный") || prodList.get(i).contains("Нащельник ПВХ")) {
                itemPrice += prodItemPrice.get(i);
            }
        }
        return itemPrice;
    }

    public double getCourse() {
        return course;
    }

    public List<Double> getProdItemPriceLst() {

        return prodItemPrice;
    }

    public int getDelivery() {
        return delivery;
    }

    public int getMounting() {
        int mounting = 0;
        for(int i : prodMounting) {
            mounting += i;
        }
        return mounting;
    }

    public List<Integer> getProdMounting() {

        return this.prodMounting;
    }

    public int getOther() {
        return other;
    }

    public int getSlopes() {
        int slopes = 0;
        for(int i : prodSlopes) {
            slopes += i;
        }
        return slopes;
    }

    public List<Integer> getProdSlopes() {

        return this.prodSlopes;
    }

    public String getVersion() {
        return this.version;
    }

    public List<String> getListItem() {
        List<String> tmpList = new ArrayList<>();
        for (String s : prodList) {

            if(s.equals("0")) {
                tmpList.add("***********БАЛКОННЫЙ БЛОК***********");
            }
            else if(s.equals("1")) {
                tmpList.add("***********БАЛК.РАМА(ИЗ НЕСК. ЧАСТЕЙ)***********");
            }
            else if(s.equals("2")) {
                tmpList.add("***********ПОЛУКРУГЛАЯ РАМА***********");
            }
            else if(s.equals("3")){
                tmpList.add("***********КОНЕЦ***********");
            }
            else {
                tmpList.add(s);
            }
        }
        return tmpList;
    }

}
