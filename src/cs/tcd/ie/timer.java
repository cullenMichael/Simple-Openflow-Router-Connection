package cs.tcd.ie;

public class timer implements Runnable {

	boolean isHit = false;
	int timer;
	int num;
	String s;
	Client c;

	timer(Client c, String s, int number) {
		this.num = number;
		this.s = s;
		this.c = c;
		timer = 0;
		isHit = false;
	}

	public void run() {
		try {
			
			c.check = false;
			Thread.sleep(4000);
			if (c.check == false) {
				c.sending(s.getBytes(), num);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	boolean getHit() {
		return this.isHit;
	}
}
