/**
 * Copyright 2007 Wei-ju Wu
 *
 * This file is part of TinyUML.
 *
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.tinyuml.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.umldraw.structure.ClassElement;
import org.tinyuml.umldraw.structure.ComponentElement;
import org.tinyuml.umldraw.structure.PackageElement;
import org.tinyuml.umldraw.structure.StructureDiagram;

/**
 * RF-001: Cuenta los elementos de un diagrama agrupados por categoria
 * (Package, Class, Component, ...) y los formatea para mostrar en la
 * barra de estado.
 *
 * Esta clase no depende de Swing: recibe el diagrama y devuelve un mapa
 * o un string. Es independiente de la UI para poder ser probada con
 * tests unitarios.
 */
public class DiagramElementCounter {

  /**
   * Categorias soportadas. Agregar un nuevo tipo se reduce a:
   *   1) extender este enum con la nueva entrada
   *   2) hacer que el elemento implemente DiagramElement (ya lo hace)
   * No es necesario tocar la barra de estado ni el formato.
   */
  public enum Category {
    PACKAGE("Package", PackageElement.class),
    CLASS("Class", ClassElement.class),
    COMPONENT("Component", ComponentElement.class);

    private final String label;
    private final Class<? extends DiagramElement> type;

    Category(String label, Class<? extends DiagramElement> type) {
      this.label = label;
      this.type = type;
    }

    public String getLabel() { return label; }

    public boolean matches(DiagramElement element) {
      return type.isInstance(element);
    }
  }

  /**
   * Cuenta los elementos del diagrama agrupados por categoria.
   * Devuelve un mapa con TODAS las categorias (las no encontradas en 0)
   * para que el formato sea estable.
   *
   * @param diagram el diagrama a inspeccionar (puede ser null)
   * @return mapa categoria -> cantidad, en el orden del enum
   */
  public Map<Category, Integer> count(StructureDiagram diagram) {
    Map<Category, Integer> counts = new LinkedHashMap<Category, Integer>();
    for (Category c : Category.values()) {
      counts.put(c, 0);
    }
    if (diagram == null) return counts;

    for (DiagramElement element : diagram.getChildren()) {
      for (Category c : Category.values()) {
        if (c.matches(element)) {
          counts.put(c, counts.get(c) + 1);
          break;
        }
      }
    }
    return counts;
  }

  /**
   * Formatea un mapa de conteos al texto del RF-001.
   * Ejemplo: "Total Items : 06; Package: 02, Class: 02, Component: 02".
   *
   * El formato es uniforme: cada categoria usa el mismo separador
   * para que agregar/quitar categorias no requiera tocar este metodo.
   *
   * @param counts mapa devuelto por {@link #count(StructureDiagram)}
   * @return texto listo para mostrar en la barra de estado
   */
  public String format(Map<Category, Integer> counts) {
    int total = 0;
    for (Integer n : counts.values()) total += n;

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("Total Items : %02d;", total));
    boolean first = true;
    for (Map.Entry<Category, Integer> e : counts.entrySet()) {
      sb.append(first ? " " : ", ")
        .append(e.getKey().getLabel())
        .append(": ")
        .append(String.format("%02d", e.getValue()));
      first = false;
    }
    return sb.toString();
  }

  /**
   * Conveniencia: cuenta y formatea en un solo paso.
   *
   * @param diagram el diagrama a inspeccionar
   * @return texto listo para mostrar
   */
  public String countAndFormat(StructureDiagram diagram) {
    return format(count(diagram));
  }
}
