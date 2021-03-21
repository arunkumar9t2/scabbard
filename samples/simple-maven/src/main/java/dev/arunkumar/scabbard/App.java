package dev.arunkumar.scabbard;


import dagger.Component;

import javax.inject.Inject;


public class App {

  public static void main(String[] args) {
    DaggerApp_MavenSampleComponent
        .create()
        .hello().say();
  }

  @Component
  interface MavenSampleComponent {
    Hello hello();

    @Component.Factory
    interface Factory {
      MavenSampleComponent create();
    }
  }

  static class Hello {
    @Inject
    Hello() {
    }

    void say() {
      System.out.println("Hello World!");
    }
  }
}
