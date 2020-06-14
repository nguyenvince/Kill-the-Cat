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
  void display() {
    x += vx;
    if (vx<=0) {
      image(img, x-w/2, y-h/2, w, h, int(f)*w, 0, (int(f)+1)*w, h);
    } else {
      image(img, x-w/2, y-h/2, w, h, (int(f)+1)*w, 0, int(f)*w, h);
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
      f = (f+0.5)%F;
    } else {
      f = (f+0.15)%F;
    }
  }


  //#Check lose condition (when a cat manages to pass the left boundary of the screen)
  void checkLose() {
    if (x - r <0) {
      g.state="gameover";
      fill(50, 150);
      rect(0, 0, width, height);
    }
  }
}
