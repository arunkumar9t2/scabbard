package dev.arunkumar.scabbard.coffee;

import dagger.Component;

import javax.inject.Singleton;

public class CoffeeApp {
  public static void main(String[] args) {
    CoffeeShop coffeeShop = DaggerCoffeeApp_CoffeeShop.builder().build();
    coffeeShop.maker().brew();
  }

  @Singleton
  @Component(modules = {DripCoffeeModule.class})
  public interface CoffeeShop {
    CoffeeMaker maker();
  }
}