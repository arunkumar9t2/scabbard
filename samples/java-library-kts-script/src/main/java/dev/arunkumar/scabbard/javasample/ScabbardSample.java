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

package dev.arunkumar.scabbard.javasample;

import javax.inject.Inject;

import dagger.Component;

public class ScabbardSample {

  public static void main(String[] args) {
    DaggerScabbardSample_ScabbardSampleComponent
        .create()
        .hello().world.say();
  }

  @Component
  interface ScabbardSampleComponent {
    Hello hello();

    @Component.Factory
    interface Factory {
      ScabbardSampleComponent create();
    }
  }

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
}