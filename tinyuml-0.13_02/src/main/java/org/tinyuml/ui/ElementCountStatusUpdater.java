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

import javax.swing.JLabel;
import org.tinyuml.ui.diagram.DiagramEditor;
import org.tinyuml.ui.diagram.EditorMouseEvent;
import org.tinyuml.ui.diagram.EditorStateListener;

/**
 * RF-001: Actualiza el JLabel de la barra de estado con el conteo
 * de elementos del diagrama cada vez que se agrega o elimina uno.
 *
 * Implementa {@link EditorStateListener} para suscribirse al editor
 * y reaccionar automaticamente. Asi se evita que AppFrame tenga que
 * recordar invocar al contador en cada flujo que modifica el modelo.
 */
public class ElementCountStatusUpdater implements EditorStateListener {

  private final JLabel statusLabel;
  private final DiagramElementCounter counter;

  /**
   * @param statusLabel el label de la barra de estado a actualizar
   * @param counter el contador a usar (inyectado para poder mockearlo)
   */
  public ElementCountStatusUpdater(JLabel statusLabel,
                                   DiagramElementCounter counter) {
    this.statusLabel = statusLabel;
    this.counter = counter;
  }

  /**
   * Refresca inmediatamente el label con el conteo del editor dado.
   * Llamar al conectar un editor nuevo (newModel/openModel).
   */
  public void refresh(DiagramEditor editor) {
    if (editor == null) {
      statusLabel.setText(counter.countAndFormat(null));
    } else {
      statusLabel.setText(counter.countAndFormat(editor.getDiagram()));
    }
  }

  // ---- EditorStateListener -----------------------------------------

  public void elementAdded(DiagramEditor editor) {
    refresh(editor);
  }

  public void elementRemoved(DiagramEditor editor) {
    refresh(editor);
  }

  /** No-op: no afecta al conteo. */
  public void stateChanged(DiagramEditor editor) { }

  /** No-op: no afecta al conteo. */
  public void mouseMoved(EditorMouseEvent event) { }
}
