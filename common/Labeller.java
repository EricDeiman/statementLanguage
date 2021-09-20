/*
  The statementLanguage programming language
  Copyright 2016 Eric J. Deiman

  This file is part of the statementLanguage programming language.
  The statementLanguage programming language is free software: you can redistribute it
  and/ormodify it under the terms of the GNU General Public License as published by the
  Free Software Foundation, either version 3 of the License, or (at your option) any
  later version.
  
  The statementLanguage programming language is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with the
  statementLanguage programming language. If not, see <https://www.gnu.org/licenses/>
*/

package common;

/**
 * A simple class to return unique labels. Never returns the same one twice.
 */
public class Labeller {

    public Labeller() {
        counter = 0;
        label = "label";
    }

    public Labeller(String prefix) {
        counter = 0;
        label = prefix;
    }

    public String make() {
        return make(this.label);
    }

    public String make(String prefix) {
        return String.format("%s%d", prefix, counter++);
    }

    private long counter;
    private String label;
}
