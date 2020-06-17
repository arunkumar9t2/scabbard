package dev.arunkumar.scabbard.javasample;

import dagger.Component;

import javax.inject.Inject;

public class ScabbardSample {

  static class World {
    @Inject
    World() {
    }

    void say() {
      System.out.println("Hello world");
    }
  }

  static class Hello {
    World world;

    @Inject
    Hello(World world) {
      this.world = world;
    }
  }

  @Component
  interface ScabbardSampleComponent {
    Hello hello();

    @Component.Factory
    interface Factory {
      ScabbardSampleComponent create();
    }
  }

  public static void main(String[] args) {
    DaggerScabbardSample_ScabbardSampleComponent
        .create()
        .hello().world.say();
  }
}