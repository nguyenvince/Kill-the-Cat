import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class killthecat extends PApplet {


Minim player;


PFont font;
PImage imgS, spray;
String temp;
Game g;
AudioPlayer music,winSound,gameOver,spraySound, coinSound;

public void setup() {
  
  font = createFont("scratch.ttf", 100);
  imgS = loadImage("student0.png");
  spray = loadImage("spray.png");
  player = new Minim(this);
  g=new Game();
  
}
public void draw() {
  frameRate(60);
  if (g.state == "menu") {
    image(g.menu, 0, 0);
    textFont(font);
    textSize(100);
    textAlign(CENTER);
    fill(150, 50);
    //#The animation of the peeking cat when mouse is hovered above the play button
    image(g.peekcat, width/2-125, height-g.ypeekcat);
    if (mouseX >= width/2-100 && mouseX <= width/2+100 && mouseY >= 2*height/3-80 && mouseY <= 2*height/3+50) {
      fill(255);
      if (g.ypeekcat<200) {
        g.ypeekcat+=50;
      }
      else if (g.ypeekcat>=200 && g.ypeekcat<=240) {
        g.ypeekcat+=10;
      }
    } else {
      if (g.ypeekcat>0) {
        g.ypeekcat-=20;
      }
    }

    text("play", width/2, 2*height/3);
  } else if (g.state == "play") {
    background(0);
    thread("mouseClicked");
    g.display();
    textSize(40);
    fill(255);
    textAlign(RIGHT);
    text("Level "+g.lv, width-60, 45);
    //#Level 1, 2, 3: 
    //Adding a cat into the game every fixed number of frames
    //  #until the number of cats added reach a certain number.
    //  #The higher the level, the more cats in total and the shorter the inteval (the harder)
    //  #Then, add a number of cats at the same time (as the last wave)
    //  #The number of cats being added also depends on the level difficulty
    if (frameCount%((3-PApplet.parseInt(g.lv))+200)==0) {
      if (g.cntCat<5*(PApplet.parseInt(g.lv)+1)) {
        g.addCat();
      } else {
        if (g.lastWave == false) {
          for (int i=0; i< 3*PApplet.parseInt(g.lv); i++) {
            g.addCat();
          }
          g.lastWave=true;
        }
        if (g.cats.size()==0) {
          g.state="win";
          fill(50, 150);
          rect(0,0,width,height);
        }
      }
    }


    if (g.buyNewStudent == true) {
      image(imgS, mouseX - 80, mouseY - 80, 160, 160, 0, 0, 200, 160);
      //#Draw the potential student being placed into a slot
      if (mouseY>240) {
        tint(255, 100);
        image(imgS, PApplet.parseInt(mouseX/160)*160, 160 + (2*(PApplet.parseInt(mouseY-240)/160) + 1)*80, 160, 160, 0, 0, 200, 160);
        noTint();
      }
    }
    if (g.buyNewSpray == true) {
      image(spray, mouseX - 80, mouseY - 60, 160, 160, 0, 0, 160, 160);
    }
  } else if (g.state == "win") {
    //#We only develop 3 levels, so if the current level is level 3
    //  #the game will be restared from level 1
    //  #Only play winning sound for the individual level
    //  #If the player win the whole game, display congrat text
    if (PApplet.parseInt(g.lv)<=2) {
      temp=str(PApplet.parseInt(g.lv)+1);
      g=new Game();
      g.lv=temp;
      g.state="play";
      music.pause();
      winSound.play();
      winSound.rewind();
    } else {
      fill(255);
      textAlign(CENTER);
      textSize(50);
      text("You Have Defeated All The Cats!!!", width/2, 2*height/3);
      text("Click Anywhere To Restart", width/2, 5*height/6);
      music.pause();
    }
  } else if (g.state == "gameover") {
    music.pause();
    music.rewind();
    gameOver.play();
    fill(200, 0, 0);
    textAlign(CENTER);
    textSize(100);
    text("The Cats Stole Your Food!!!", width/2, height/2);
    textSize(50);
    text("Click Anywhere To Restart", width/2, 5*height/6);
  }
}


public void mouseClicked() {
  if (g.state=="menu") {
    if (mouseX >= width/2-100 && mouseX <= width/2+100 && mouseY >= 2*height/3-80 && mouseY <= 2*height/3+50) {
      g.state="play";
    }
  }
  else if (mousePressed==true) {
    g.collectDirham();
    g.buyStudent();
    g.buySpray();
    if (mouseButton == RIGHT){
      g.buyNewStudent=false;
      g.buyNewSpray=false;
    }
  }
  else if (g.state=="gameover") {
    g=new Game();
    g.lv="1";
  }
  else if (g.state=="win" && g.lv=="3") {
    g=new Game();
    g.lv="1";
  }
}


public void mouseReleased() {
  g.dropStudent();
  g.dropSray();
}
class Cat extends Creature {
  AudioPlayer catSound;
  float l, v0, cnt,f=0;
  Cat(float x, float y, float r, float vx, float hp, int w, int h, String img, int Frame) {
    super(x, y, r, vx, hp, w, h, img, Frame);
    l = hp;
    v0 = vx;
    catSound = player.loadFile("cat.mp3");
  }

  //#Update the position of the cat and also detect collision with the plate
  public void display() {
    x += vx;
    if (vx<=0) {
      image(img, x-w/2, y-h/2, w, h, PApplet.parseInt(f)*w, 0, (PApplet.parseInt(f)+1)*w, h);
    } else {
      image(img, x-w/2, y-h/2, w, h, (PApplet.parseInt(f)+1)*w, 0, PApplet.parseInt(f)*w, h);
    }

    //#Draw hp bar (based on type of cat)
    stroke(255);
    noFill();
    strokeWeight(1);
    rect(x - l/2, y - r - 30, l, 10, 5);
    //  #fill color to indicate the hp (hp > 2/3: green, 1/3 < hp < 2/3: yellow, hp <1/3: red)
    if (hp > l*2/3) {
      fill(0, 255, 0);
    } else if (hp <= l*2/3 && hp > l/3) {
      fill(255, 255, 0);
    } else if (hp <= l/3) {
      fill(255, 0, 0);
    }
    rect(x - l/2, y - r - 30, hp, 10, 5);

    if (vx>0) {
      cnt+=1;
      if (cnt>25) {
        cnt=0;
        vx=v0;
      }
      f = (f+0.5f)%F;
    } else {
      f = (f+0.15f)%F;
    }
  }


  //#Check lose condition (when a cat manages to pass the left boundary of the screen)
  public void checkLose() {
    if (x - r <0) {
      g.state="gameover";
      fill(50, 150);
      rect(0, 0, width, height);
    }
  }
}
//#Create class Creature (class Cat and class Student inherit from this class)
class Creature {
  float x, y, r, vx, hp,f;
  int w, h, F;
  PImage img;
  Creature(float X, float Y, float R, float Vx, float Hp, int W, int H, String Img, int Frame) {
    x=X;
    y=Y;
    r=R;
    vx=Vx;
    hp=Hp;
    w=W;
    h=H;
    img = loadImage(Img);
    F=Frame;
  }


  public float distance(Creature target) {
    if (this.y == target.y) {
      return abs(this.x - target.x);
    } else {
      return 10000;
    }
  }
}
class Dirham extends Creature {
  float vy, rate;
  Dirham(float x, float y, float r, float vx, float hp, int w, int h, String img, int Frame) {
    super(x, y, r, vx, hp, w, h, img, Frame);
    vy=vx;
    rate=vx;
  }
  public void display() {
    y += vy;
    //#The faster the dirham falls, the faster it animates through the sequence of images
    f = (f+map(rate, 0.5f, 2, 0.2f, 0.3f))%F;
    image(img, x-w/2, y-h/2, w, h, PApplet.parseInt(f)*w, 0, (PApplet.parseInt(f)+1)*w, h);
  }
}
class Game {
  int money= 0, cntCat=0, frameForDirham=PApplet.parseInt(random(75, 150));
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

  public void display() {
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
      fps=str(PApplet.parseInt(frameRate));
    }
    fill(50, 50);
    textSize(30);
    textAlign(CENTER);
    text("FPS: "+fps, width-50, height-30);

    //#Drop a new dirham randomly
    if (frameCount%frameForDirham==0) {
      dirhams.add(new Dirham(random(400, width-25), 0, 25, random(2, 5), 0, 50, 50, "dirham.png", 15));
      frameForDirham=PApplet.parseInt(random(75, 150));
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
        if (c.y == s.y && s.f>5 && s.f<5.2f) {
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
  public void addCat() {
    int cat, f=0, w=0, h=0, yPos, cnt;
    float hp=0, vx=0;
    cat = PApplet.parseInt(random(0, 9));
    if (0<=cat && cat<5) {
      cat=0;
      f = 7;
      w = 200;
      h = 153;
      hp = 50;
      vx = -1.5f;
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
      vx = -0.5f;
    }
    //#Make sure that there are no more than 2 cats on the same line
    yPos=240+80*(2*PApplet.parseInt(random(0, 3))+1);
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
  public void buyStudent() {
    if (mouseX > 0 && mouseX < 160 && mouseY > 0 && mouseY < 160 && money >= 5*students.size()) {
      buyNewStudent = true;
    }
  }
  public void buySpray() {
    if (mouseX > 160 && mouseX < 320 && mouseY > 0 && mouseY < 160 && money >= 5) {
      buyNewSpray = true;
    }
  }

  //#Check if there is already a student in a slot and drop it down if the slot is empty        
  public void dropStudent() {
    if (mouseX > 0 && mouseX < width && mouseY > 160 && mouseY < height && buyNewStudent == true) { 
      for (Student s : g.students) {
        if (s.x == PApplet.parseInt(mouseX/160)*160 && s.y == (2*(PApplet.parseInt(mouseY-240)/160)+1)*80+240) {
          return;
        }
      }
      students.add(new Student(PApplet.parseInt(mouseX/160)*160, 240 + (2*(PApplet.parseInt(mouseY-240)/160) + 1)*80, 40, 0, 100, 200, 160, "student0.png", 8));
      buyNewStudent = false;
      money -= 5*(students.size()-1);
    }
  }

  public void dropSray() {
    for (Cat c : g.cats) {
      if (mouseX>= c.x-1.5f*c.r && mouseX<= c.x+1.5f*c.r && mouseY>= c.y-1.5f*c.r && mouseY<= c.y+1.5f*c.r && buyNewSpray == true) {
        c.vx=20;
        spraySound.play();
        spraySound.rewind();
        buyNewSpray = false;
        money -= 5;
      }
    }
  }
  //#Increase money inventory whenever a dirham is collected        
  public void collectDirham() {
    for (int i = g.dirhams.size()-1; i >=0; i--) {
      Dirham d = dirhams.get(i);
      if (mouseX >= d.x-1.5f*d.r && mouseX <= d.x+1.5f*d.r && mouseY >= d.y-1.5f*d.r && mouseY <= d.y+1.5f*d.r) {
        money+=1;
        coinSound.play();
        coinSound.rewind();
        dirhams.remove(i);
        break;
      }
    }
  }
}
class Plate extends Creature {
  Plate(float x, float y, float r, float vx, float hp, int w, int h, String img, int Frame) {
    super(x, y, r, vx, hp, w, h, img, Frame);
  }
  
  public void display() {
    x+=vx;
    this.f = (this.f+0.75f)%this.F;
    image(img, x-40, y-h/2, w, h, PApplet.parseInt(f)*w, 0, (PApplet.parseInt(f)+1)*w, h);
  }
  //#Remove the plate after passing through the boundary of the screen    
  
}
class Student extends Creature {
  AudioPlayer scream = player.loadFile("scream.wav");
  Student(float x, float y, float r, float vx, float hp, int w, int h, String img, int Frame) {
    super(x, y, r, vx, hp, w, h, img, Frame);
  }

  public void display() {
    //#The student will animate only if there is a cat on the line of the student
    for (Cat c : g.cats) {
      if (c.y == this.y) {
        this.f = (this.f+0.15f)%this.F;
        break;
      }
    }
    image(img, x-40, y-h/2, w, h, PApplet.parseInt(f)*w, 0, (PApplet.parseInt(f)+1)*w, h);
  }
}
  public void settings() {  size(1280, 720); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "killthecat" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
