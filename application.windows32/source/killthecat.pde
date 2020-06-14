import ddf.minim.*;
Minim player;


PFont font;
PImage imgS, spray;
String temp;
Game g;
AudioPlayer music,winSound,gameOver,spraySound, coinSound;

void setup() {
  size(1280, 720);
  font = createFont("scratch.ttf", 100);
  imgS = loadImage("student0.png");
  spray = loadImage("spray.png");
  player = new Minim(this);
  g=new Game();
  
}
void draw() {
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
    if (frameCount%((3-int(g.lv))+200)==0) {
      if (g.cntCat<5*(int(g.lv)+1)) {
        g.addCat();
      } else {
        if (g.lastWave == false) {
          for (int i=0; i< 3*int(g.lv); i++) {
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
        image(imgS, int(mouseX/160)*160, 160 + (2*(int(mouseY-240)/160) + 1)*80, 160, 160, 0, 0, 200, 160);
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
    if (int(g.lv)<=2) {
      temp=str(int(g.lv)+1);
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


void mouseClicked() {
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


void mouseReleased() {
  g.dropStudent();
  g.dropSray();
}
