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


  float distance(Creature target) {
    if (this.y == target.y) {
      return abs(this.x - target.x);
    } else {
      return 10000;
    }
  }
}
