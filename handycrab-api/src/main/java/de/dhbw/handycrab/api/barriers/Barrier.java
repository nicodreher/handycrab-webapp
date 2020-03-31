package de.dhbw.handycrab.api.barriers;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public class Barrier {

  public UUID id;
  public UUID userid;
  public String title;
  public double longitude;
  public double latitude;
  public URL picture;
  public String description;
  public String postCode;
  public List<Solution> solutions;
  public int upvotes;
  public int downvotes;
}
