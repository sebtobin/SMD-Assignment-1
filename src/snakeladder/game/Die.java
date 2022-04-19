package snakeladder.game;

import ch.aplu.jgamegrid.Actor;
import snakeladder.utility.ServicesRandom;

public class Die extends Actor
{
  private NavigationPane np;
  private int nb;

  Die(int nb, NavigationPane np)
  {
    super("sprites/pips" + nb + ".gif", 7);
    this.nb = nb;
    this.np = np;
  }

  public void act()
  {
    showNextSprite();
    if (getIdVisible() == 6)
    {
      setActEnabled(false);
      np.checkNextRoll();

    }
  }

}
