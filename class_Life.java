import java.util.*;

class Life {

  public String add = "none";
  public int botCount = 0;
  public int epoch = 0;
  public int maxAge = 0;
  public int steps = 0;
  public List<int[]> bestGen;
  public List<String[]> history = new ArrayList<>();
  public int rnd;
  public List<String[]> field = new ArrayList<>();
  public List<Bot> bot;
  String[] h = {"", "", "", ""};

  public Life(int height, int width) {
    for (int i = 0; i < height; i++) {
      field.add(new String[width]);
      for (int j = 0; j < width; j++) {
        rnd = (int) Math.round(100 * Math.random());
        if (rnd < 7) {
          field.get(i)[j] = "food";
        }
        else if (rnd < 14) {
          field.get(i)[j] = "toxic";
        }
        else if (rnd < 16) {
          field.get(i)[j] = "wall";
        }
        else {
          field.get(i)[j] = "free";
        }
        if (i == 0 || j == 0 || i == (height - 1) || j == (width - 1)) field.get(i)[j] = "wall";
      }
    }
  }
    
  public void populate(int numOfBots) {
    int i = 0;
    int x, y;
    bot = new ArrayList<>();
    List<String[]> f = field;
    while(i < numOfBots) {
      y = (int) Math.round((f.size() - 1) * Math.random());
      x = (int) Math.round((f.get(0).length - 1) * Math.random());
      if (f.get(y)[x] == "free") {
        f.get(y)[x] = Integer.toString(i);
        bot.add(new Bot(x, y));
        i++;
      }
    }
    field = f;
  }
    
  public void createGenom() {
    for (int j = 0; j < bot.size(); j++) {
      for (int i = 0; i < 64; i++) {
        bot.get(j).gen[i] = (int) Math.round(63 * Math.random());
      }
    }
  }
    
  public void doHint(int num) {
    if (bot.get(num).state == "dead") return;
    int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1};
    int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0};
    int maxX = field.get(0).length - 1;
    int maxY = field.size() - 1;
    int px = bot.get(num).posX;
    int py = bot.get(num).posY;
    int x, y;
    int i = 0, s = 0;
    String status = "1";
    while (i < 10 && status != "hint") {
      int code = bot.get(num).gen[bot.get(num).step];
      // Запись истории 30-го бота
      if (num == 30) {
        String[] hint = {
          Integer.toString(bot.get(num).step),
          Integer.toString(code),
          "1",
          " // "
      };
        history.add(hint);
      }
      if (code < 8) { // Команда шаг
        s = code + bot.get(num).course;
        if (s > 7) s -= 8;
        x = px + dx[s]; y = py + dy[s];
        if(num == 30) {
          history.get(history.size() - 1)[1] = "Step" + Integer.toString(s);
          history.get(history.size() - 1)[2] = field.get(y)[x];
          history.get(history.size() - 1)[3] = " // ";
        }
        switch(field.get(y)[x]) {
          case "free":
            field.get(y)[x] = Integer.toString(num);
            field.get(py)[px] = "free";
            bot.get(num).posX = x;
            bot.get(num).posY = y;
            break;
          case "wall": bot.get(num).step++; break;
          case "food": 
            bot.get(num).energy += 10;
            field.get(y)[x] = Integer.toString(num);
            field.get(py)[px] = "free";
            bot.get(num).step += 2;
            bot.get(num).posX = x;
            bot.get(num).posY = y;
            add = "food";
            break;
          case "toxic":
            bot.get(num).energy = 0;
            break;
          default: bot.get(num).step += 3;
        }
        status = "hint";
      } // Команда схватить
      else if (code < 16) {
        s = code - 8 + bot.get(num).course;
        if (s > 7) s -= 8;
        x = px + dx[s]; y = py + dy[s];
        if (num == 30) {
          history.get(history.size() - 1)[1] = "Get" + Integer.toString(s);
          history.get(history.size() - 1)[2] = field.get(y)[x];
          history.get(history.size() - 1)[3] = " // ";
        }
        switch(field.get(y)[x]) {
          case "free": break;
          case "wall": bot.get(num).step++; break;
          case "food": 
            bot.get(num).energy += 10;
            bot.get(num).step += 2;
            field.get(y)[x] = "free";
            add = "food";
            break;
          case "toxic":
            bot.get(num).step += 3;
            field.get(y)[x] = "food";
            break;
          default: bot.get(num).step += 4;
        }
        status = "hint";
      } // Команда посмотреть
      else if (code < 24) {
        s = code - 16 + bot.get(num).course;
        if (s > 7) s -= 8;
        x = px + dx[s]; y = py + dy[s];
        if (num == 30) {
          history.get(history.size() - 1)[1] = "Look" + Integer.toString(s);
          history.get(history.size() - 1)[2] = field.get(y)[x];
          if (i == 9) history.get(history.size() - 1)[3] = " // ";
        }
        switch(field.get(y)[x]) {
          case "free": break;
          case "wall": bot.get(num).step++; break;
          case "food": bot.get(num).step += 2; break;
          case "toxic": bot.get(num).step += 3; break;
          default: bot.get(num).step += 4;
        }
      } //Команда поворот
      else if(code < 32) {
        bot.get(num).course += code - 24;
        if (num == 30) {
          history.remove(history.size() - 1);
          h[0] = "?";
          history.add(h);
          h[0] = " // ";
          if (i == 9) history.add(h);
        }
        if (bot.get(num).course > 7) bot.get(num).course -= 8;
      }  // Команда переход
      else {
        if (num == 30) {
          history.remove(history.size() - 1);
          h[0] = ">";
          history.add(h);
          h[0] = " // ";
          if (i == 9) history.add(h);
        }
        bot.get(num).step += code;
      }
      // Последние проверки
      bot.get(num).step++;
      if (bot.get(num).step > 63) bot.get(num).step -= 64;
      i++;
    }
    bot.get(num).age++;
    bot.get(num).energy--;
    if (bot.get(num).energy < 1) {
      bot.get(num).state = "dead";
      botCount++;
      field.get(bot.get(num).posY)[bot.get(num).posX] = "free";
    }
    if (bot.get(num).energy > 90) bot.get(num).energy = 90;
  }
    
  public void addFood() {
    String food;
    double FF = Math.random();
    if (FF < 0.5) food = "food"; else food = "toxic";
    while (add != "none") {
      int y = (int) Math.round((field.size() - 1) * Math.random());
      int x = (int) Math.round((field.get(1).length - 1) * Math.random());
      if (field.get(y)[x] == "free") {
        field.get(y)[x] = "food";
        add = "none";
      }
    }
  }
    
  public void selection() {
    int i = 0;
    bestGen = new ArrayList<>();
    for (int j = 0; j < 4; j++) {
      while (bot.get(i).state != "alive") {
        i++;
      }
      bestGen.add(bot.get(i).gen);
      maxAge = bot.get(i).age;
      field.get(bot.get(i).posY)[bot.get(i).posX] = "free";
      i++;
    }
  }
    
  public void mutation() {
    int mutGen, mutVal, mutLen, g;
    for (int j = 0; j < bot.size(); j++) {
      g = (int) Math.round(3 * Math.random());
      bot.get(j).gen = bestGen.get(g).clone();
    }
    for (int f = 0; f < 6; f++) {
      mutLen = (int) (3 * Math.random());
      for (int b = 0; b < mutLen; b++) {
        mutGen = (int) Math.round(63 * Math.random());
        mutVal = (int) Math.round(63 * Math.random());
        bot.get(f).gen[mutGen] = mutVal;
      }
    }
  }
    
  public String point() {
    String document = "";
    document += "<meta charset=\"utf-8\">";
    document += "<body style=\"background-color:black\">";
    document += "<button style=\"font: 70px Arial; margin: 20px\" onclick=\"start()\">Start</button>";
    document += "<button style=\"font: 70px Arial; margin: 20px\" onclick=\"stop()\">Stop</button>";
    document += "<button style=\"font: 30px Arial; margin: 10px\">N° " + epoch + "</button>";
    document += "<button style=\"font: 30px Arial; margin: 10px\">Age " + maxAge + "</button>";
    document += "<button style=\"font: 30px Arial; margin: 10px\">Step " + steps + "</button>";
    document += "<table style=\"background-color:white\">";
    for (int i = 0; i < field.size(); i++) {
      document += "<tr>";
      for (int j = 0; j < field.get(1).length; j++) {
        switch(field.get(i)[j]) {
          case "free": document += "<td height=\"50px\" width=\"50px\" style=\"background-color:black\"></td>"; break;
          case "wall": document += "<td height=\"50px\" width=\"50px\" style=\"background-color:gray\"></td>"; break;
          case "food": document += "<td height=\"50px\" width=\"50px\" style=\"background-color:green\"></td>"; break;
          case "toxic": document += "<td height=\"50px\" width=\"50px\" style=\"background-color:red\"></td>"; break;
          default: document += "<td height=\"50px\" width=\"50px\" style=\"background-color:blue; font:30px Arial; color:white\">" + Integer.toString(bot.get(Integer.valueOf(field.get(i)[j])).energy) + "</td>";
        }
      }
      document += "</tr>";
    }
    document += "</table>";
    document += "<script>";
    document += "setTimeout(() => {document.location.reload()}, 200)";
    document += "</script>";
    return document;
  }
    
}
