/*
 * Copyright 2021 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
