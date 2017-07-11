package util.thread;

public class ThreadUtil {

	private static void stopToVerifyAll(Thread[] threads) throws InterruptedException
	{

		for (Thread thread : threads) {
			thread.join();
		}
		
		
	}
	public static void startThread(Thread t, boolean ativo)
	{
		if(ativo){
			t.start();
		}else{
			t.run();
		}
	}
	
	public static void waitThreads(Thread[] threads, boolean ativo) throws InterruptedException
	{
		if(ativo){
			util.thread.ThreadUtil.stopToVerifyAll(threads);
		}
	}
	
	
}
