package ali.su.cft2j02;

public class BackgroundJob extends Thread {
    Runnable task;
    public BackgroundJob(Runnable task) {
        this.task = task;
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        do {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                return;
            }
            task.run();
        } while (true);
    }
}
