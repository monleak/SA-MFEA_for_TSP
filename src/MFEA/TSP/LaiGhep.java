package MFEA.TSP;

import java.util.Random;

import static MFEA.TSP.main.*;
import static MFEA.TSP.DanhGiaQuanThe.*;
import static MFEA.TSP.KhoiTao.*;

public class LaiGhep {
    public static int LuaChonChaMe(){
        int c1 = genRandom(maxCaThe)-1;
        int c2 = genRandom(maxCaThe)-1;
        while (c1==c2) c2 = genRandom(maxCaThe)-1;
        if(dsNST[theHe-1][c1].scalar_fitness > dsNST[theHe-1][c2].scalar_fitness){
            return c1;
        } else
            return c2;
    }

    public static int[][] laiGhepOX(int[] cha,int[] me){
        //phương pháp lai ghép thứ tự
        int[][] child = new int[2][totalCities+5];
        //Chọn 2 điểm lai ngẫu nhiên
        int p1,p2;
        p1 = genRandom(totalCities-1);
        p2 = genRandom(totalCities);
        if(p1==totalCities-1) p2=totalCities;
        while (p1>=p2) p2=genRandom(totalCities);
        //Con 1:
        //Sao chép phần giữa 2 điểm lai của cha vào con 1
        for(int i=p1;i<=p2;i++){
            child[0][i]=cha[i];
        }
//        Các gen chưa được sao chép, tiến hành thêm vào con 1 bắt đầu từ điểm lai thứ 2, theo thứ tự xuất hiện ở mẹ
        int contro;
        if(p2==totalCities) contro = 1;
        else contro = p2+1;
        vonglap1:
        for(int i=1;i<=totalCities;i++){
            for(int j=1;j<=totalCities;j++){ //Sàng lọc những gen trong NST mẹ chưa có trong NST con để thêm vào NST con
                if(me[i]==child[0][j]) continue vonglap1;
            }
            child[0][contro] = me[i];
            contro++;
            if(contro>totalCities) contro = 1;
        }
        //Con 2 làm tương tự
        if(p2==totalCities) contro = 1;
        else contro = p2+1;
        for(int i=p1;i<=p2;i++){
            child[1][i]=me[i];
        }
//        Các gen chưa được sao chép, tiến hành thêm vào con 1 bắt đầu từ điểm lai thứ 2, theo thứ tự xuất hiện ở mẹ
        vonglap2:
        for(int i=1;i<=totalCities;i++){
            for(int j=1;j<=totalCities;j++){ //Sàng lọc những gen trong NST mẹ chưa có trong NST con để thêm vào NST con
                if(cha[i]==child[1][j]) continue vonglap2;
            }
            child[1][contro] = cha[i];
            contro++;
            if(contro>totalCities) contro = 1;
        }
//        for(int i=0;i<2;i++){
//            for(int j=1;j<=totalCities;j++){
//                System.out.print(child[i][j]+" ");
//            }
//            System.out.print("\n");
//        }
        return child;
    }

    public static double randomGauss(double µ, double σ){
        //Hàm lấy mẫu theo phân phối chuẩn
        Random output = new Random();
        double number = output.nextGaussian()*σ+µ;
        return number;
    }
    public static double tinhDelta(NST pA, NST pB, NST oA, NST oB){
        //Hàm tính giá trị Tỷ lệ phần trăm cải thiện tốt nhất
        //pA, pB là 2 cá thể cha mẹ
        //oA, oB là 2 cá thể con
        double Delta =0;
        if(oA.skill_factor==pA.skill_factor){
            double temp = (pA.f_cost[pA.skill_factor-1]-oA.f_cost[oA.skill_factor-1])/pA.f_cost[pA.skill_factor-1];
            if(Delta<temp) Delta = temp;
        }else {
            double temp = (pB.f_cost[pB.skill_factor-1]-oA.f_cost[oA.skill_factor-1])/pB.f_cost[pB.skill_factor-1];
            if(Delta<temp) Delta = temp;
        }
        if(oB.skill_factor==pA.skill_factor){
            double temp = (pA.f_cost[pA.skill_factor-1]-oB.f_cost[oB.skill_factor-1])/pA.f_cost[pA.skill_factor-1];
            if(Delta<temp) Delta = temp;
        }else {
            double temp = (pB.f_cost[pB.skill_factor-1]-oB.f_cost[oB.skill_factor-1])/pB.f_cost[pB.skill_factor-1];
            if(Delta<temp) Delta = temp;
        }
        return Delta;
    }
    public static void updateM(double[] S,double[] δ,int dem1){
        //Cập nhật bộ nhớ lịch sử thành công
        for(int i=0;i<2;i++){
            for (int j=i+1;j<2;j++){
                if(dem1>0){
                    double tuSo=0,mauSo=0;
                    for (int i1=0;i1<dem1;i1++){
                        tuSo+=δ[i1]*Math.pow(S[i1],2);
                    }
                    for (int i1=0;i1<dem1;i1++){
                        mauSo+=δ[i1]*S[i1];
                    }
                    M[p] = tuSo/mauSo;
                    p = p+1;
                    if(p==M.length) p=0;
                }
            }
        }
    }

    public static void TheHeTiepTheo(){
        NST[] child = new NST[maxCaThe*2];
        for(int i=0;i<maxCaThe*2;i++){
            child[i] = new NST();
            child[i].khoiTaoDoiTuong();
        }
        for(int i=0;i<maxCaThe;i++){
            System.arraycopy(dsNST[theHe-1][i].Gen, 0, child[i].Gen, 0, dsNST[theHe-1][i].Gen.length);
        }

        int contro = maxCaThe;
        int[][] tempChild = new int[2][totalCities+5];
        double[] S = new double[maxCaThe]; //Lưu giữ các giá trị rmp tốt
        double[] δ = new double[maxCaThe]; //Lưu giữ các giá trị Delta > 0
        int dem1=0; //Lưu giữ con trỏ
        while(contro<maxCaThe*2){
            int parentA, parentB;
            parentA = LuaChonChaMe();
            parentB = LuaChonChaMe();
            while (parentA==parentB) parentB = LuaChonChaMe();
            double random = genRandomDouble();
            if(dsNST[theHe-1][parentA].skill_factor == dsNST[theHe-1][parentB].skill_factor){
                //Lai ghép và đột biến các con (Đây là điểm khác biệt của LSA-MFEA)
                tempChild = laiGhepOX(dsNST[theHe-1][parentA].Gen,dsNST[theHe-1][parentB].Gen);

                int p1,p2;
                p1=genRandom(totalCities);
                p2=genRandom(totalCities);
                while (p1==p2) p2=genRandom(totalCities);

                int temp;
                temp = tempChild[0][p1];
                tempChild[0][p1] = tempChild[0][p2];
                tempChild[0][p2] = temp;
                temp = tempChild[1][p1];
                tempChild[1][p1] = tempChild[1][p2];
                tempChild[1][p2] = temp;

                System.arraycopy(tempChild[0], 0, child[contro].Gen, 0, tempChild[0].length);
                System.arraycopy(tempChild[1], 0, child[++contro].Gen, 0, tempChild[1].length);
                contro++;
            } else{
                //Nếu skill-factor khác nhau ta xét tham số rmp
                //Input: 2 cá thể cha mẹ có skillfactor khác nhau
                //Output: 2 con
                int i = genRandom(M.length)-1;
                double µ = M[i];
                double rmp = randomGauss(µ,σ); //rmp là tỉ lệ lai ghép các cá thể có tác vụ khác nhau
                while(rmp>=1||rmp<=0) rmp = randomGauss(µ,σ);
                if(genRandomDouble()<rmp){
                    //Lai ghép + đột biến
                    tempChild = laiGhepOX(dsNST[theHe-1][parentA].Gen,dsNST[theHe-1][parentB].Gen);

                    int p1,p2;
                    p1=genRandom(totalCities);
                    p2=genRandom(totalCities);
                    while (p1==p2) p2=genRandom(totalCities);

                    int temp;
                    temp = tempChild[0][p1];
                    tempChild[0][p1] = tempChild[0][p2];
                    tempChild[0][p2] = temp;
                    temp = tempChild[1][p1];
                    tempChild[1][p1] = tempChild[1][p2];
                    tempChild[1][p2] = temp;

                    System.arraycopy(tempChild[0], 0, child[contro].Gen, 0, tempChild[0].length);
                    tinhKhoangCach(child[contro]);
                    child[contro].skill_factor = genRandom(2);
                    System.arraycopy(tempChild[1], 0, child[++contro].Gen, 0, tempChild[1].length);
                    tinhKhoangCach(child[contro]);
                    child[contro].skill_factor = genRandom(2);
                    Delta = tinhDelta(dsNST[theHe-1][parentA],dsNST[theHe-1][parentB],child[contro-1],child[contro]);
                    if(Delta>0){
                        S[dem1]=rmp;
                        δ[dem1]=Delta;
                        dem1++;
                    }
                    contro++;
                } else{
                    //Lựa chọn các cá thể có cùng tác vụ với cha mẹ để lai ghép
                    //Mỗi cặp chỉ lấy 1 con
                    int parentA2; //Lựa chọn 1 cá thể A2 cùng skillfactor với cá thể A
                    parentA2 = LuaChonChaMe();
                    while(parentA2==parentA || dsNST[theHe-1][parentA].skill_factor != dsNST[theHe-1][parentA2].skill_factor){
                        parentA2 = LuaChonChaMe();
                    }
                    tempChild = laiGhepOX(dsNST[theHe-1][parentA].Gen,dsNST[theHe-1][parentA2].Gen);
                    int p1,p2;
                    p1=genRandom(totalCities);
                    p2=genRandom(totalCities);
                    while (p1==p2) p2=genRandom(totalCities);
                    int temp;
                    temp = tempChild[0][p1];
                    tempChild[0][p1] = tempChild[0][p2];
                    tempChild[0][p2] = temp;
                    System.arraycopy(tempChild[0], 0, child[contro].Gen, 0, tempChild[0].length);
                    tinhKhoangCach(child[contro]);
                    child[contro].skill_factor = dsNST[theHe-1][parentA].skill_factor;
                    contro++;

                    int parentB2;
                    parentB2 = LuaChonChaMe(); //Lựa chọn 1 cá thể B2 cùng skillfactor với cá thể B
                    while(parentB2==parentB || dsNST[theHe-1][parentB].skill_factor != dsNST[theHe-1][parentB2].skill_factor){
                        parentB2 = LuaChonChaMe();
                    }
                    tempChild = laiGhepOX(dsNST[theHe-1][parentB].Gen,dsNST[theHe-1][parentB2].Gen);
                    p1=genRandom(totalCities);
                    p2=genRandom(totalCities);
                    while (p1==p2) p2=genRandom(totalCities);
                    temp = tempChild[0][p1];
                    tempChild[0][p1] = tempChild[0][p2];
                    tempChild[0][p2] = temp;
                    System.arraycopy(tempChild[0], 0, child[contro].Gen, 0, tempChild[0].length);
                    tinhKhoangCach(child[contro]);
                    child[contro].skill_factor = dsNST[theHe-1][parentB].skill_factor;
                    contro++;
                    Delta = tinhDelta(dsNST[theHe-1][parentA],dsNST[theHe-1][parentB],child[contro-2],child[contro-1]);
                    if(Delta>0){
                        S[dem1]=rmp;
                        δ[dem1]=Delta;
                        dem1++;
                    }
                }
            }
        }
        danhGiaCaThe(child);
        updateM(S,δ,dem1);

        for(int i=0;i<maxCaThe;i++){
            double maxfitness=0;
            int eNST=0;
            for(int j=0;j<child.length;j++){
                if(maxfitness<child[j].scalar_fitness&&child[j].cathemoi==false){
                    maxfitness = child[j].scalar_fitness;
                    eNST = j;
                }
            }
            System.arraycopy(child[eNST].Gen, 0, dsNST[theHe][i].Gen, 0, child[eNST].Gen.length);
            child[eNST].cathemoi=true;
        }
    }
}
