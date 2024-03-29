/*
 * Copyright 2022 Arunkumar
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

package dev.arunkumar.scabbard.coffee;

import javax.inject.Inject;

class ElectricHeater implements Heater {
  boolean heating;

  @Inject
  public ElectricHeater() {
  }

  @Override
  public void on() {
    System.out.println("~ ~ ~ heating ~ ~ ~");
    this.heating = true;
  }

  @Override
  public void off() {
    this.heating = false;
  }

  @Override
  public boolean isHot() {
    return heating;
  }
}