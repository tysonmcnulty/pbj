package com.vmware.example;

import java.util.Collection;

public abstract class Pantry {
  protected Collection<Food> foods;

  public Collection<Food> getFoods() {
    return foods;
  }
}
