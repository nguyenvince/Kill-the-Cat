class Student extends Creature {
  AudioPlayer scream = player.loadFile("scream.wav");
  Student(float x, float y, float r, float vx, float hp, int w, int h, String img, int Frame) {
    super(x, y, r, vx, hp, w, h, img, Frame);
  }

  void display() {
    //#The student will animate only if there is a cat on the line of the student
    for (Cat c : g.cats) {
      if (c.y == this.y) {
        this.f = (this.f+0.15)%this.F;
        break;
      }
    }
    image(img, x-40, y-h/2, w, h, int(f)*w, 0, (int(f)+1)*w, h);
  }
}
