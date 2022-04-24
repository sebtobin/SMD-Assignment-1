package snakeladder.game;

import ch.aplu.jgamegrid.Actor;

public class CosmeticDie extends Actor
{
  private NavigationPane np;
  private int nb;
  boolean last;
  CosmeticDie(int nb, NavigationPane np, boolean last)
  {
    super("sprites/pips" + nb + ".gif", 7);
    this.nb = nb;
    this.np = np;
    this.last = last;
  }

  public void act()
  {
    showNextSprite();
    if (getIdVisible() == 6)
    {
      setActEnabled(false);
      if (last){ // only start moving the die if it's the last one
        np.startMoving();
      }


    }

  }

}
