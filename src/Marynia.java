import java.util.Scanner;

public class Marynia {
    public static void main(String[] args) {
        //zadanie
        //nauczyciel musi wystawic uczniom oceny z kolokwium
        //program, który pobiera od użytkownika procent z testu, a wypisuje ocenę na podstawie wpisanego procenta
        /*
        0-49    -   2
        50-59   -   3
        60-69   -   3,5
        70-79   -   4
        80-89   -   4,5
        90-95   -   5
        96-99   -   5,5
        100     -   6
         */
        int procent;
        float ocena =   0.0f;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wpisz procent:\t");
        procent =   scanner.nextInt();

        if(procent>=0   &&  procent<=49){
            ocena=2.0f;
        }else if(procent>=50    &&  procent<=59){
            ocena=3.0f;
        }else if(procent>=60    &&  procent<=69){
            ocena=3.5f;
        }else if(procent>=70    &&  procent<=79){
            ocena=4.0f;
        }else if(procent>=80    &&  procent<=89){
            ocena=4.5f;
        }else if(procent>=90    &&  procent<=95){
            ocena=5.0f;
        }else if(procent>=96    &&  procent<=99){
            ocena=5.5f;
        }else if(procent==100){
            ocena=6.0f;
        }


        if(procent>100 ||  procent<0){
            System.out.println("procent nieprawidłowy! >.<");
        }else{
            System.out.println("Dla procentu\t->\t"+procent+"\tocena wynosi\n\t->\t"+ocena);
        }


    }
}
