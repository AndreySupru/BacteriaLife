import java.time.*;
import java.util.*;

class Index {
  
  public int POPUL = 32;
  public Life d = new Life(26, 17);
  public Timer simulate, draw;
  public int count = 0;
  public LifeServer server;

  public static void main(String[] args) {
    Index life = new Index();
    life.start();
    System.out.println("end");
    System.exit(0);
  }

  public void start() {
    int simTime, drawTime;
    d.populate(POPUL);
    server = new LifeServer();
    /*
    if (confirm("Fast speed?")) {
      drawTime = 1000;
      simTime = 5;
      simulate = setInterval(
        () => {
          for (int b = 0; b < 5000; b++) {
            process();
          }
        },
        simTime
      );*/
    //} else {
      drawTime = 150;
      simTime = 50;
      simulate = new Timer();
      simulate.schedule(new TimerTask() {
        public void run() {
          process();
        }
      }, simTime, 2*simTime);
    //}
    draw = new Timer();
    draw.schedule(new TimerTask() {
      public void run() {
        server.setResponse(d.point());
      }
    }, drawTime, 2*drawTime);
    server.start(50);
  }
/*
  public void stop() {
    clearInterval(simulate);
    clearInterval(draw);
    alert(d.history);
  }
*/
  public void process() {
    d.doHint(count);
    d.addFood();
    if(d.botCount > (d.bot.size() - 5)) {
      d.selection();
      d.populate(POPUL);
      d.mutation();
      d.botCount = 0;
      d.epoch++;
      d.steps = 0;
      d.history = new ArrayList<>();
    }
    count++;
    if (count > (d.bot.size() - 1)) {
      count = 0;
      d.steps++;
    }
  }

}
