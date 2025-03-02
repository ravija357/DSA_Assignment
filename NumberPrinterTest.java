//6.a)


import java.util.concurrent.Semaphore;

class NumberPrinter {
    public void printZero() {
        System.out.print(0);
    }

    public void printEven(int x) {
        System.out.print(x);
    }

    public void printOdd(int x) {
        System.out.print(x);
    }
}

class ThreadController {
    private int n;
    private NumberPrinter printer;
    private Semaphore zeroSemaphore, evenSemaphore, oddSemaphore;

    public ThreadController(int n, NumberPrinter printer) {
        this.n = n;
        this.printer = printer;
        zeroSemaphore = new Semaphore(1); // Start with zeroSemaphore open
        evenSemaphore = new Semaphore(0);
        oddSemaphore = new Semaphore(0);
    }

    public void zeroThread() {
        for (int i = 1; i <= n; i++) {
            try {
                zeroSemaphore.acquire();
                printer.printZero();
                if (i % 2 == 0) {
                    evenSemaphore.release();
                } else {
                    oddSemaphore.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void evenThread() {
        for (int i = 2; i <= n; i += 2) {
            try {
                evenSemaphore.acquire();
                printer.printEven(i);
                zeroSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void oddThread() {
        for (int i = 1; i <= n; i += 2) {
            try {
                oddSemaphore.acquire();
                printer.printOdd(i);
                zeroSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class NumberPrinterTest {
    public static void main(String[] args) {
        int n = 5; // Change the value to test with different numbers
        NumberPrinter printer = new NumberPrinter();
        ThreadController controller = new ThreadController(n, printer);

        Thread t1 = new Thread(() -> controller.zeroThread());
        Thread t2 = new Thread(() -> controller.evenThread());
        Thread t3 = new Thread(() -> controller.oddThread());

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
