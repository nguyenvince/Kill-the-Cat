class Dirham extends Creature {
  float vy, rate;
  Dirham(float x, float y, float r, float vx, float hp, int w, int h, String img, int Frame) {
    super(x, y, r, vx, hp, w, h, img, Frame);
    vy=vx;
    rate=vx;
  }
  void display() {
    y += vy;
    //#The faster the dirham falls, the faster it animates through the sequence of images
    f = (f+map(rate, 0.5, 2, 0.2, 0.3))%F;
    image(img, x-w/2, y-h/2, w, h, int(f)*w, 0, (int(f)+1)*w, h);
  }
}
