class Plate extends Creature {
  Plate(float x, float y, float r, float vx, float hp, int w, int h, String img, int Frame) {
    super(x, y, r, vx, hp, w, h, img, Frame);
  }
  
  void display() {
    x+=vx;
    this.f = (this.f+0.75)%this.F;
    image(img, x-40, y-h/2, w, h, int(f)*w, 0, (int(f)+1)*w, h);
  }
  //#Remove the plate after passing through the boundary of the screen    
  
}
