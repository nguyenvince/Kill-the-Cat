class Game {
  int money= 0, cntCat=0, frameForDirham=int(random(75, 150));
  ArrayList<Student> students;
  ArrayList<Cat> cats;
  ArrayList<Plate> plates;
  ArrayList<Dirham> dirhams;
  boolean buyNewStudent = false, buyNewSpray = false, lastWave=false;
  String fps;

  PImage bg=loadImage("bg.png"), menu=loadImage("menu.png"), peekcat=loadImage("peekcat.png"), spray = loadImage("spray.png");
  private PImage imgS = loadImage("student0.png"), imgD = loadImage("dirham.png");

  float ypeekcat=0;

  String state="menu", lv="1";

  Game() {
    students = new ArrayList<Student>();
    cats = new ArrayList<Cat>();
    dirhams = new ArrayList<Dirham>();
    plates = new ArrayList<Plate>();

    music = player.loadFile("background.mp3");
    music.loop();
    winSound = player.loadFile("win.mp3");
    gameOver  = player.loadFile("gameover.mp3");
    spraySound  = player.loadFile("spray.wav");
    coinSound = player.loadFile("coin.mp3");
  }

  void display() {
    image(bg, 0, 0);
    stroke(0, 100);
    strokeWeight(2);
    for (int i=0; i<3; i++) {
      line(0, 240+i*160, width, 240+i*160);
    }
    for (int i=0; i<9; i++) {
      line(160*i, 240, 160*i, height);
    }
    //#Draw the box for additional student
    strokeWeight(5);
    fill(0, 150);
    stroke(255, 150);
    rect(0, 0, 160, 160);
    if (money<5*students.size()) {
      tint(255, 100);
    }
    image(imgS, 0, 0, 160, 160, 0, 0, 200, 160);
    textSize(25);
    textAlign(RIGHT);
    fill(255);
    noTint();
    image(imgD, 130, 130, 25, 25, 400, 0, 450, 50);
    text(str(5*students.size())+"x", 125, 150);

    //#Draw the box for spray can
    fill(0, 150);
    stroke(255, 150);
    rect(160, 0, 160, 160);
    if (money<5) {
      tint(255, 100);
    }
    image(spray, 160, 0, 160, 160, 0, 0, 160, 160);
    textSize(25);
    fill(255);
    noTint();
    image(imgD, 290, 130, 25, 25, 400, 0, 450, 50);
    text("5x", 285, 150);

    //#Display money counter
    fill(0, 150);
    rect(width-225, 0, 225, 160);
    image(imgD, width-70, 55, 50, 50, 400, 0, 450, 50);
    fill(255);
    textSize(75);
    textAlign(RIGHT);
    text(str(money)+"x", width-70, 100);

    if (frameCount%30==0) {
      fps=str(int(frameRate));
    }
    fill(50, 50);
    textSize(30);
    textAlign(CENTER);
    text("FPS: "+fps, width-50, height-30);

    //#Drop a new dirham randomly
    if (frameCount%frameForDirham==0) {
      dirhams.add(new Dirham(random(400, width-25), 0, 25, random(2, 5), 0, 50, 50, "dirham.png", 15));
      frameForDirham=int(random(75, 150));
    }
    for (int i = g.dirhams.size()-1; i >=0; i--) {
      Dirham d = dirhams.get(i);
      d.display();
      if (d.y - d.r >= height) {
        g.dirhams.remove(i);
      }
    }

    for (int i = g.cats.size()-1; i >=0; i--) {
      Cat c = cats.get(i);
      c.display();
      c.checkLose();
      if (c.hp<=0) {
        c.catSound.play();
        c.catSound.rewind();
        g.cats.remove(i);
        return;
      }
    }
    for (int i = g.students.size()-1; i >=0; i--) {
      Student s = students.get(i);
      s.display();
      for (Cat c : g.cats) {
        //Detect collision
        if (s.distance(c) <= s.r + c.r + c.w/2) {
          s.scream.play();
          s.hp -= 1;
          c.vx = 0;
        }
        if (c.y == s.y && s.f>5 && s.f<5.2) {
          g.plates.add(new Plate(s.x+120, s.y, 25, 5, 0, 50, 50, "plate0.png", 16));
          break;
        }
      }
      if (s.hp <= 0) {
        for (Cat c : g.cats) {
          if (c.y == s.y && c.vx==0) {
            c.vx=c.v0;
            s.scream.pause();
          }
        }
        g.students.remove(i);
        return;
      }
    }
    for (int i = g.plates.size()-1; i >=0; i--) {
      Plate p = plates.get(i);
      p.display();
      for (Cat c : cats) {
        if (c.distance(p) <= c.r + p.r) { 
          c.hp -= 10;
          g.plates.remove(i);
          break;
        }
      }
      if (p.x-p.r > width) {
        g.plates.remove(this);
        return;
      }
    }
  }

  //#Randomly add new cats into the game
  //#(with probability of the type of cat screws towards those who have lower health    
  void addCat() {
    int cat, f=0, w=0, h=0, yPos, cnt;
    float hp=0, vx=0;
    cat = int(random(0, 9));
    if (0<=cat && cat<5) {
      cat=0;
      f = 7;
      w = 200;
      h = 153;
      hp = 50;
      vx = -1.5;
    } else if (5<=cat && cat<8) {
      cat=1;
      f = 13;
      w = 250;
      h = 250;
      hp = 100;
      vx = -1;
    } else if (8<=cat && cat<10) {
      cat=2;
      f = 7;
      w = 200;
      h = 153;
      hp = 150;
      vx = -0.5;
    }
    //#Make sure that there are no more than 2 cats on the same line
    yPos=240+80*(2*int(random(0, 3))+1);
    cnt=0;
    for (Cat c : g.cats) {
      if (c.y == yPos) {
        cnt+=1;
      }
    }
    if (cnt<3) {
      cats.add(new Cat(width, yPos, 40, vx, hp, w, h, "cat" + str(cat) +".png", f));
      cntCat+=1;
    }
  }

  //#check if the player has enough dirhams to buy a new student
  void buyStudent() {
    if (mouseX > 0 && mouseX < 160 && mouseY > 0 && mouseY < 160 && money >= 5*students.size()) {
      buyNewStudent = true;
    }
  }
  void buySpray() {
    if (mouseX > 160 && mouseX < 320 && mouseY > 0 && mouseY < 160 && money >= 5) {
      buyNewSpray = true;
    }
  }

  //#Check if there is already a student in a slot and drop it down if the slot is empty        
  void dropStudent() {
    if (mouseX > 0 && mouseX < width && mouseY > 160 && mouseY < height && buyNewStudent == true) { 
      for (Student s : g.students) {
        if (s.x == int(mouseX/160)*160 && s.y == (2*(int(mouseY-240)/160)+1)*80+240) {
          return;
        }
      }
      students.add(new Student(int(mouseX/160)*160, 240 + (2*(int(mouseY-240)/160) + 1)*80, 40, 0, 100, 200, 160, "student0.png", 8));
      buyNewStudent = false;
      money -= 5*(students.size()-1);
    }
  }

  void dropSray() {
    for (Cat c : g.cats) {
      if (mouseX>= c.x-1.5*c.r && mouseX<= c.x+1.5*c.r && mouseY>= c.y-1.5*c.r && mouseY<= c.y+1.5*c.r && buyNewSpray == true) {
        c.vx=20;
        spraySound.play();
        spraySound.rewind();
        buyNewSpray = false;
        money -= 5;
      }
    }
  }
  //#Increase money inventory whenever a dirham is collected        
  void collectDirham() {
    for (int i = g.dirhams.size()-1; i >=0; i--) {
      Dirham d = dirhams.get(i);
      if (mouseX >= d.x-1.5*d.r && mouseX <= d.x+1.5*d.r && mouseY >= d.y-1.5*d.r && mouseY <= d.y+1.5*d.r) {
        money+=1;
        coinSound.play();
        coinSound.rewind();
        dirhams.remove(i);
        break;
      }
    }
  }
}
