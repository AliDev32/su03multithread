package ali.su.cft2j02;

public class BackgroundJob extends Thread {
    Runnable task;
    private int jobIntervalMillis;
    public BackgroundJob(Runnable task, int jobIntervalMillis) {
        this.task = task;
        this.jobIntervalMillis = jobIntervalMillis;
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        do {
            if (!interrupted()) {
                try {
                    sleep(jobIntervalMillis);
                    executeTask();
                } catch (InterruptedException e) {
                    return;
                }
            }
        } while (true);
    }

    public void executeTask() {
        task.run();
    }

    public void setJobIntervalMillis(int jobIntervalMillis) {
        this.jobIntervalMillis = jobIntervalMillis;
    }
}
