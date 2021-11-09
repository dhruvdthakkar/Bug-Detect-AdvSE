// Program to demonstrate the NumberFormatException
class Sample_NumberFormat {
    public static void main(String args[]) {
        try {
            // "Test" is not a number
            int n = Integer.parseInt ("Test") ;
            System.out.println(n);
        } catch(NumberFormatException e) {
            System.out.println("Number format exception");
        }
    }
}